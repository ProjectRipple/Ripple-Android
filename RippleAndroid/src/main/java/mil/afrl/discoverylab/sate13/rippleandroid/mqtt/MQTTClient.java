package mil.afrl.discoverylab.sate13.rippleandroid.mqtt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;

/**
 * MQTTClient handles communication with a MQTT server
 */
public class MQTTClient implements MqttSimpleCallback{
    private static final String TAG = "MQTTClient";
    private static final boolean DEBUG = false;

    private String brokerHostName;
    private String brokerPort;
    private MqttPersistence mqttPersistence = null;
    private boolean mqttCleanStart = true;
    private short mqttKeepAlive = 10000;
    private int[] mqttQoSs = { 0 };
    private int mqttQoS = 0;
    private boolean mqttRetainedPublish = false;
    private IMqttClient mqttClient;

    private String deviceId;
    private final Handler handler;

    public final static int MQTTCLIENT_CONNECTION_ESTABLISHED = 8881;
    public final static int MQTTCLIENT_CONNECTION_TERMINATED = 8882;
    public final static int MQTTCLIENT_CONNECTION_LOST = 8883;
    public final static int MQTTCLIENT_MESSAGE_ARRIVED = 8884;
    public final static String MESSAGE_TOPIC = "MESSAGE_TOPIC";
    public final static String MESSSAGE_PAYLOAD = "MESSAGE_PAYLOAD";

    public MQTTClient(String brokerHostName, String brokerPort, String deviceId, final Handler handler) throws MqttException{
        this.brokerHostName = brokerHostName;
        this.brokerPort = brokerPort;
        this.deviceId = deviceId;
        this.handler = handler;

        final String mqttConnSpec = "tcp://" + brokerHostName + "@" + brokerPort;
        final String clientID = "/" + deviceId;
        this.mqttClient = MqttClient.createMqttClient(mqttConnSpec, mqttPersistence);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mqttClient.connect(clientID, mqttCleanStart, mqttKeepAlive);
                    Thread.sleep(25); // to endasure the handler will send the below message
                    handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_ESTABLISHED);
                    //subscribeToTopic("hello/world");
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        this.mqttClient.registerSimpleHandler(this);

        if (DEBUG) {
            Log.d(TAG, "Connection to " + brokerHostName + " established");
        }


    }

    public void disconnect() {
        try {
            mqttClient.disconnect();
            handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_TERMINATED);
        } catch (MqttPersistenceException e) {
            if (DEBUG) {
                Log.d(TAG, "Failed to disconnect the client: " + e.getMessage());
            }
        }
    }

    public void  subscribeToTopic(String topicName) throws MqttException {
        if (mqttClient == null || mqttClient.isConnected() == false) {
            Log.d(TAG, "Connection status: " + mqttClient.isConnected() + "");
            handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_LOST);
            if (DEBUG) {
                Log.d(TAG, "Connection error: no connection");
            }
        } else {
            String[] topic = { topicName };
            mqttClient.subscribe(topic, mqttQoSs);
        }
    }

    public void unsubscribeFromTopic(String topicName) throws MqttException {
        if (mqttClient == null || mqttClient.isConnected() == false) {
            handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_LOST);
            if (DEBUG) {
                Log.d(TAG, "Connection error: no connnection");
            }
        } else {
            String[] topic = { topicName };
            mqttClient.unsubscribe(topic);
        }
    }

    public void publishToTopic(String topicName, String message) throws MqttException {
        if (mqttClient == null || mqttClient.isConnected() == false) {
            handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_LOST);
            if (DEBUG) {
                Log.d(TAG, "Connection error: no connection");
            }
        } else {
            mqttClient.publish(topicName, message.getBytes(), mqttQoS, mqttRetainedPublish);
        }
    }

    @Override
    public void connectionLost() throws Exception {
        handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_LOST);
        if (DEBUG) {
            Log.d(TAG, "Detected loss of connection.");
        }
    }

    @Override
    public void publishArrived(String topicName, byte[] payload, int qos, boolean retained) throws Exception {
        String s = new String(payload);
        if (DEBUG) {
            Log.d(TAG, "Got message: " + s);
        }
        Message msg = handler.obtainMessage(MQTTCLIENT_MESSAGE_ARRIVED);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_TOPIC, topicName);
        bundle.putString(MESSSAGE_PAYLOAD, s);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    public String getBrokerHostName() {
        return brokerHostName;
    }

    public String getBrokerPort() {
        return brokerPort;
    }
}
