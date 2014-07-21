package com.discoverylab.ripple.android.activity;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.fragment.PatientBannerFragment;
import com.discoverylab.ripple.android.fragment.ScenarioPatientFragment;
import com.discoverylab.ripple.android.mqtt.MQTTClientService;
import com.discoverylab.ripple.android.mqtt.MQTTServiceConstants;
import com.discoverylab.ripple.android.mqtt.MQTTServiceManager;
import com.discoverylab.ripple.android.mqtt.PublishedMessage;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;

public class ScenarioActivity extends FragmentActivity {

    // MQTT
    private MQTTServiceManager mqttServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerio);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.banner_container, PatientBannerFragment.newInstance())
                    .commit();


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.patient_scenario_container, ScenarioPatientFragment.newInstance())
                    .commit();
        }
        // get MQTT service manager
        this.mqttServiceManager = new MQTTServiceManager(this, MQTTClientService.class, new MQTTHandler(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // bind service if running
        if (this.mqttServiceManager != null && this.mqttServiceManager.isServiceRunning()){
            this.mqttServiceManager.bind();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unbind service
        if (this.mqttServiceManager != null && this.mqttServiceManager.isServiceRunning()) {
            this.mqttServiceManager.unbind();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // pass result to fragments
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * Processes message from MQTT client
     *
     * @param msg MQTT message to process
     */
    private void processPublishedMessage(PublishedMessage msg) {
        String topic = msg.getTopic();
        if (topic.equals(Common.MQTT_TOPIC_VITALPROP) || topic.matches(Common.MQTT_TOPIC_MATCH_VITALCAST)) {
            JsonObject recordJson = Common.GSON.fromJson(msg.getPayload(), JsonObject.class);
            //this.banner.getHandler().obtainMessage(Common.RIPPLE_MSG_RECORD, recordJson).sendToTarget();
            //this.patLeft.getHandler().obtainMessage(Common.RIPPLE_MSG_RECORD, recordJson).sendToTarget();
        } else if (topic.matches(Common.MQTT_TOPIC_MATCH_ECG_STREAM)) {
            //this.patLeft.getHandler().obtainMessage(Common.RIPPLE_MSG_ECG_STREAM, msg).sendToTarget();
        } else {
            Log.d(Common.LOG_TAG, "Unknown MQTT topic recieved:" + topic);
        }
    }

    /**
     * Class to handle messages from MQTT client
     */
    private static class MQTTHandler extends Handler {
        private WeakReference<ScenarioActivity> activityReference;

        public MQTTHandler(ScenarioActivity activity) {
            this.activityReference = new WeakReference<ScenarioActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ScenarioActivity activity = activityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MQTTServiceConstants.MSG_CONNECTED:
                        //activity.subscribeToTopic(Common.MQTT_TOPIC_VITALPROP);
                        //activity.subscribeToTopic(Common.MQTT_TOPIC_VITALCAST.replace(Common.MQTT_TOPIC_ID_STRING, Common.MQTT_TOPIC_WILDCARD_SINGLE_LEVEL));
                        Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
                        // Clear old patient list
                        break;
                    case MQTTServiceConstants.MSG_CANT_CONNECT:
                        Toast.makeText(activity, "Unable to connect", Toast.LENGTH_SHORT).show();
                        break;
                    case MQTTServiceConstants.MSG_PUBLISHED_MESSAGE:
                        activity.processPublishedMessage((PublishedMessage) msg.obj);
                        break;
                    case MQTTServiceConstants.MSG_DISCONNECTED:
                        Toast.makeText(activity, "Disconnected from Broker.", Toast.LENGTH_SHORT).show();
                        break;
                    case MQTTServiceConstants.MSG_NO_NETWORK:
                        Toast.makeText(activity, "No network connection, will attempt to reconnect with broker when network is restored.", Toast.LENGTH_LONG).show();
                        break;
                    case MQTTServiceConstants.MSG_RECONNECTING:
                        Toast.makeText(activity, "Reconnecting to Broker...", Toast.LENGTH_SHORT).show();
                        break;
                    case MQTTServiceConstants.MSG_CONNECTION_STATUS:

                        break;
                    default:
                        Log.d(Common.LOG_TAG, "Unknown message type :" + msg.what);
                        break;
                }
            }
        }
    }
}
