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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.discoverylab.ripple.android.config.Common;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage the MQTTClient
 */
public class MQTTClientService extends Service {
    // Log tag
    private final static String TAG = "MQTTClientService";
    // Set to true for debug messages
    private final static boolean DEBUG = true;
    // interval to retry connecting in ms
    private final static long INTERVAL_RETRY = 10 * 1000;

    // handles service->manger(s) comms
    private List<Messenger> mClients = new ArrayList<Messenger>();
    // handles manager(s)->service comms
    private final Messenger mMessenger = new Messenger(new IncomingHandler(this));
    // Handler for messages from MQTTClient
    private final MQTTClientHandler mHandler = new MQTTClientHandler(this);

    // Reference to client
    private MQTTClient mqttClient;

    // Receiver for connectivity changes
    private final BroadcastReceiver connectivityChanged = new ConnectivityChangedReceiver();
    // Broker information
    private String brokerHost;
    private String brokerPort;
    // is the service started
    private boolean mIsStarted;
    // is a connect operation in progress
    private volatile boolean isConnecting = false;
    // reference to connection thread
    private Thread connectThread;

    /**
     * Class handles messages from the MQTT client
     */
    private static class MQTTClientHandler extends Handler {
        // Weak reference to the service
        private final WeakReference<MQTTClientService> serviceInstance;

        public MQTTClientHandler(MQTTClientService service) {
            this.serviceInstance = new WeakReference<MQTTClientService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MQTTClientService service = serviceInstance.get();
            if (service != null) {
                switch (msg.what) {
                    case MQTTClient.MQTTCLIENT_CONNECTION_ESTABLISHED:
                        if (DEBUG) {
                            Log.d(TAG, "Connection established.");
                        }
                        service.send(Message.obtain(null, MQTTServiceConstants.MSG_CONNECTED));
                        break;
                    case MQTTClient.MQTTCLIENT_CONNECTION_TERMINATED:
                        if (DEBUG) {
                            Log.d(TAG, "Connection terminated!!");
                        }
                        service.send(Message.obtain(null, MQTTServiceConstants.MSG_DISCONNECTED));
                        break;
                    case MQTTClient.MQTTCLIENT_CONNECTION_LOST:
                        if (DEBUG) {
                            Log.d(TAG, "Connection lost!!!");
                        }
                        if (service.isNetworkAvailable()) {
                            // try now if network is available
                            service.reconnectIfNecessary();
                        } else {
                            // notify that there is no network to connect to(leave reconnect for when connectivity is restored)
                            service.send(Message.obtain(null, MQTTServiceConstants.MSG_NO_NETWORK));
                        }
                        break;
                    case MQTTClient.MQTTCLIENT_MESSAGE_ARRIVED:
                        // forward message to service manager
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

    /**
     * Class manages messages from the service manager
     */
    private static class IncomingHandler extends Handler {
        // weak reference to service instance
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
                        // TODO: find cleaner way to notify a new client that the connection is already established?
                        try {
                            if (service.mqttClient != null && service.mqttClient.isConnected()) {
                                // notify immediately this messager if we are already connected
                                msg.replyTo.send(Message.obtain(null, MQTTServiceConstants.MSG_CONNECTED));
                            }
                        } catch (RemoteException e) {
                            Log.e(TAG, "Client: " + msg.replyTo.toString() + " is dead, removing him from the list");
                            service.mClients.remove(msg.replyTo);
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
                    case MQTTServiceConstants.MSG_PUBLISH_TO_TOPIC:
                        service.publishToTopic(msg.getData().getString(MQTTServiceConstants.MQTT_TOPIC), msg.getData().getString(MQTTServiceConstants.MQTT_MESSAGE));
                        break;
                    case MQTTServiceConstants.MSG_GET_CONNECTION_STATUS:
                        Message connStatusMsg = Message.obtain(null, MQTTServiceConstants.MSG_CONNECTION_STATUS);
                        // send result as boolean in bundle
                        Bundle info = new Bundle();
                        info.putBoolean(MQTTServiceConstants.MQTT_CONNECTION_STATUS, (service.mqttClient != null && service.isNetworkAvailable() && service.mqttClient.isConnected()));
                        connStatusMsg.setData(info);

                        service.send(connStatusMsg);
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

    /**
     * Class to monitor system connectivity
     */
    private class ConnectivityChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            boolean hasConnectivity = (info != null && info.isConnected());

            if (DEBUG) {
                Log.d(TAG, "Connectivity changed: connection -> " + hasConnectivity);
            }

            if (hasConnectivity) {
                // attempt reconnect now
                reconnectIfNecessary();
            } else if (mqttClient != null) {
                // no connectivity so make sure client is set to disconnected and cancel reconnect attempts
                mqttClient.disconnect();
                cancelReconnect();
            }
        }
    }


    /**
     * Reconnect to the Broker if needed
     */
    private void reconnectIfNecessary() {

        if (mIsStarted && (mqttClient == null || !mqttClient.isConnected())) {
            // Notify that we are attempting to reconnect
            send(Message.obtain(null, MQTTServiceConstants.MSG_RECONNECTING));
            attemptConnectMqttClient();
        }
    }

    /**
     * Attempt connecting the MQTTClient to the Broker.
     * <p/>
     * Another connection attempt will be scheduled if connecting fails.
     * <p/>
     * Synchronized to not create multiple threads at the same time.
     */
    private synchronized void attemptConnectMqttClient() {
        if (isConnecting && connectThread != null && connectThread.isAlive()) {
            // connect already in progress, so do nothing
            Log.d(TAG, "Already connecting to broker.");
            return;
        }

        isConnecting = true;
        // run operation as background thread
        connectThread = new Thread(new ConnectRunnable());
        connectThread.setName("MQTT Connect thread");
        connectThread.start();

    }

    /**
     * Runnable to create MQTT Client and attempt connect to broker.
     */
    private class ConnectRunnable implements Runnable {

        @Override
        public void run() {
            if (mqttClient == null) {
                // get device ID
                String deviceId = Common.RESPONDER_ID;

                // Create client object
                try {
                    mqttClient = new MQTTClient(brokerHost, brokerPort, deviceId, mHandler);
                } catch (MqttException e) {
                    e.printStackTrace();
                    // TODO: something here, not really sure what at the moment
                }
            }

            try {
                mqttClient.connect();
                // We connected, so cancel any pending reconnect attempts
                cancelReconnect();
                if (DEBUG) {
                    Log.d(TAG, "Connect success!");
                }
            } catch (MqttException e) {
                if (DEBUG) {
                    Log.d(TAG, "Connect failed.");
                }
                // schedule reconnect
                if (isNetworkAvailable()) {
                    // schedule reconnect if network is available (otherwise just wait until connectivity is available)
                    scheduleReconnect();
                } else {
                    // notify that there is no network
                    send(Message.obtain(null, MQTTServiceConstants.MSG_NO_NETWORK));
                }
            } finally {
                // no longer attempting connect
                isConnecting = false;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (DEBUG) {
            Log.d(TAG, "Service created");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onStopService();

        if (DEBUG) {
            Log.d(TAG, "Service destroyed");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) {
            Log.d(TAG, "Received start id " + startId);
        }

        if (intent.getAction().equals(MQTTServiceConstants.ACTION_START)) {
            // Handle start action
            brokerHost = intent.getExtras().getString(MQTTServiceConstants.MQTT_BROKER_IP);
            brokerPort = intent.getExtras().getString(MQTTServiceConstants.MQTT_BROKER_PORT);
            onStartService();
        } else if (intent.getAction().equals(MQTTServiceConstants.ACTION_STOP)) {
            // handle stop action
            onStopService();
            stopSelf();
        } else if (intent.getAction().equals(MQTTServiceConstants.ACTION_RECONNECT)) {
            // handle reconnect action
            if (isNetworkAvailable()) {
                reconnectIfNecessary();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent i) {
        return mMessenger.getBinder();
    }

    /**
     * Send msg to all registered clients
     *
     * @param msg Message to send
     */
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

    /**
     * Handle start service actions
     */
    private void onStartService() {

        if (mIsStarted) {
            Log.w(TAG, "Attempt to start connection that is already active.");
            return;
        }

        // register receiver
        getApplicationContext().registerReceiver(connectivityChanged, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // service is started
        mIsStarted = true;
        // new start, so make sure client is recreated
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
        }
        mqttClient = null;

        // Attempt connect is other thread to avoid blocking of activity binding
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // short sleep
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // attempt to connect client
                attemptConnectMqttClient();
            }
        }).start();


    }


    /**
     * Handle stop service actions
     */
    private void onStopService() {

        if (!mIsStarted) {
            // service not running, so do nothing
            Log.w(TAG, "Attempt to stop non-active service.");
            return;
        }

        mIsStarted = false;
        // remove the connectivity receiver
        getApplicationContext().unregisterReceiver(connectivityChanged);

        // cancel any reconnect timers
        cancelReconnect();

        // destroy MQTT client
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
        }
        mqttClient = null;

    }

    /**
     * Schedule reconnect of MQTT client
     */
    private void scheduleReconnect() {
        // Create Intent
        Intent i = new Intent(this, MQTTClientService.class);
        i.setAction(MQTTServiceConstants.ACTION_RECONNECT);
        // Create PendingIntent
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        // Set alarm with PendingIntent
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_RETRY, pi);

        // notify that service is attempting reconnect
        this.send(Message.obtain(null, MQTTServiceConstants.MSG_RECONNECTING));

    }

    /**
     * Remove any scheduled reconnect attempts
     */
    private void cancelReconnect() {
        // Create Intent
        Intent i = new Intent(this, MQTTClientService.class);
        i.setAction(MQTTServiceConstants.ACTION_RECONNECT);
        // Create PendingIntent
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        // Cancel any pending calls to this service
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pi);
    }

    /**
     * Checks if network is available
     *
     * @return true if network is available, false otherwise
     */
    private boolean isNetworkAvailable() {
        NetworkInfo info = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (info != null);
    }

    /**
     * Unsubscribe from specified topic
     *
     * @param topic Topic to unsubscribe from
     */
    private void unsubscribeFromTopic(String topic) {
        try {
            mqttClient.unsubscribeFromTopic(topic);
        } catch (MqttException e) {
            if (DEBUG) {
                Log.d(TAG, "Unable to unsubscribe to " + topic + " : " + e.getMessage());
            }
        }
    }

    /**
     * Subscribe client to specified topic
     *
     * @param topic topic to subscribe to
     */
    private void subscribeToTopic(String topic) {
        try {
            mqttClient.subscribeToTopic(topic);
        } catch (MqttException e) {
            if (DEBUG) {
                Log.d(TAG, "Unable to subscribe to " + topic + " : " + e.getMessage());
            }
        }
    }

    /**
     * Publish message to specified topic
     *
     * @param topic   topic to send message to
     * @param message message to send
     */
    private void publishToTopic(String topic, String message) {
        if ((mqttClient == null) || !mqttClient.isConnected()) {
            Log.w(TAG, "No connection to publish to.");
            // TODO: handle messages so they are not lost (just change QoS in Client?)
        } else {
            try {
                mqttClient.publishToTopic(topic, message);
            } catch (MqttException e) {
                if (DEBUG) {
                    Log.d(TAG, "Unable to publish message.");
                }
            }
        }
    }
}
