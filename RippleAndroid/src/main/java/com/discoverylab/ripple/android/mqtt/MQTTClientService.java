package com.discoverylab.ripple.android.mqtt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.ibm.mqtt.MqttException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Service to manage the MQTTClient
 */
public class MQTTClientService extends Service {
    private final static String TAG = "MQTTClientService";
    private final static boolean DEBUG = false;
    private final static long INTERVAL_RETRY = 5 * 1000;

    private ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // handles service->manger(s) comms
    private final Messenger mMessenger = new Messenger(new IncomingHandler(this)); // hadnles manager(s)->service comms
    private final MQTTClientHandler mHandler = new MQTTClientHandler(this);
    private boolean mIsRunning;

    private ConnectivityManager connectivityManager;
    private MQTTClient mqttClient;

    private static class MQTTClientHandler extends Handler {
        private final WeakReference<MQTTClientService> serviceInstance;

        public MQTTClientHandler(MQTTClientService service) {
            this.serviceInstance = new WeakReference<MQTTClientService>(service);
        }

        public void handleMessage(Message msg) {
            MQTTClientService service = serviceInstance.get();
            if (service != null) {
                switch (msg.what) {
                    case MQTTClient.MQTTCLIENT_CONNECTION_ESTABLISHED:
                        service.send(Message.obtain(null, MQTTServiceConstants.MSG_CONNECTED));
                        break;
                    case MQTTClient.MQTTCLIENT_CONNECTION_TERMINATED:

                        break;
                    case MQTTClient.MQTTCLIENT_CONNECTION_LOST:
                        service.scheduleReconnect(service.mqttClient.getBrokerHostName(),
                                service.mqttClient.getBrokerPort());
                        service.onStopService();
                        break;
                    case MQTTClient.MQTTCLIENT_MESSAGE_ARRIVED:
                        PublishedMessage publishedMsg = new PublishedMessage(msg.getData().getString(MQTTClient.MESSAGE_TOPIC),
                                msg.getData().getString(MQTTClient.MESSSAGE_PAYLOAD));
                        service.send(Message.obtain(null, MQTTServiceConstants.MSG_PUBLISHED_MESSAGE, publishedMsg));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static class IncomingHandler extends Handler {
        private final WeakReference<MQTTClientService> serviceInstance;

        public IncomingHandler(MQTTClientService service) {
            serviceInstance = new WeakReference<MQTTClientService>(service);
        }

        public void handleMessage(Message msg) {
            MQTTClientService service = serviceInstance.get();
            if (service != null) {
                switch (msg.what) {
                    case MQTTServiceConstants.MSG_REGISTER_CLIENT:
                        service.mClients.add(msg.replyTo);
                        if (DEBUG) {
                            Log.d(TAG, "Client registered: " + msg.replyTo);
                        }
                        break;
                    case MQTTServiceConstants.MSG_UNREGISTER_CLIENT:
                        service.mClients.remove(msg.replyTo);
                        if (DEBUG) {
                            Log.d(TAG, "Client unregistered: " + msg.replyTo);
                        }
                        break;
                    case MQTTServiceConstants.MSG_STOP_SERVICE:
                        service.onStopService();
                        if (DEBUG) {
                            Log.d(TAG, "Service stopped by: " + msg.replyTo);
                        }
                        break;
                    case MQTTServiceConstants.MSG_SUBSCRIBE:
                        service.subscribeToTopic(msg.getData().getString(MQTTServiceConstants.MQTT_TOPIC));
                        break;
                    case MQTTServiceConstants.MSG_UNSUBSCRIBE:
                        service.unsubscribeFromTopic(msg.getData().getString(MQTTServiceConstants.MQTT_TOPIC));
                        break;
                    default:
                        if (DEBUG) {
                            Log.d(TAG, "Unknown message type received. Message from: " + msg.replyTo + ". Message info: " + msg.what);
                        }
                        break;
                }
            }
        }
    }

    private final BroadcastReceiver connectivityChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            boolean hasConnectivity = (info != null && info.isConnected());

            if (DEBUG) {
                Log.d(TAG, "Connectivity changed: connection -> " + hasConnectivity);
            }

            if (hasConnectivity) {

            } else {
                if (mqttClient != null) {
                    onStopService();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        if (DEBUG) {
            Log.d(TAG, "Service started");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onStopService();

        if (DEBUG) {
            Log.d(TAG, "Service stopped");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) {
            Log.d(TAG, "Received start id " + startId);
        }
        if (!mIsRunning) {
            onStartService(intent.getExtras().getString(MQTTServiceConstants.MQTT_BROKER_IP),
                    intent.getExtras().getString(MQTTServiceConstants.MQTT_BROKER_PORT));
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent i) {
        return mMessenger.getBinder();
    }

    protected void send(Message msg) {
        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                mClients.get(i).send(msg);
                if (DEBUG) {
                    Log.d(TAG, "Sending message: " + msg.what + ", to client: " + mClients.get(i).toString());
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Client: " + mClients.get(i).toString() + " is dead, removing him from the list");
                mClients.remove(i);
            }
        }
    }

    private void onStartService(String brokerIP, String brokerPort) {
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (deviceId == null) {
            if (DEBUG) {
                Log.d(TAG, "Device ID not found");
            }
        } else {
            try {
                mqttClient = new MQTTClient(brokerIP, brokerPort, deviceId, mHandler);
                mIsRunning = true;
                this.getApplicationContext().registerReceiver(connectivityChanged,  new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (MqttException e) {
                if (DEBUG) {
                    Log.d(TAG, "Mqtt Exception: " + e.getMessage());
                }
                onStopService();
                scheduleReconnect(brokerIP, brokerPort);
            }
        }
    }


    private void onStopService() {
        if (mIsRunning && mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
            mqttClient = null;
        }
        try {
            this.getApplicationContext().unregisterReceiver(connectivityChanged);
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Unable to unregister broadcast receiver: " + e.getMessage());
            }
        }
        mIsRunning = false;
    }

    private void  scheduleReconnect(String brokerIP, String brokerPort) {
        Intent i = new Intent(this, MQTTClientService.class);
        i.putExtra(MQTTServiceConstants.MQTT_BROKER_IP, brokerIP);
        i.putExtra(MQTTServiceConstants.MQTT_BROKER_PORT, brokerPort);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_RETRY, pi);

    }

    private boolean isNetworkAvailable() {
        NetworkInfo info = ((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (info != null);
    }

    private void unsubscribeFromTopic(String topic) {
        try {
            mqttClient.unsubscribeFromTopic(topic);
        } catch (MqttException e) {
            if (DEBUG) {
                Log.d(TAG, "Unable to unsubscribe to " + topic + " : " + e.getMessage());
            }
        }
    }

    private void subscribeToTopic(String topic) {
        try {
            mqttClient.subscribeToTopic(topic);
        } catch (MqttException e) {
            if (DEBUG) {
                Log.d(TAG, "Unable to subscribe to " + topic + " : " + e.getMessage());
            }
        }
    }
}
