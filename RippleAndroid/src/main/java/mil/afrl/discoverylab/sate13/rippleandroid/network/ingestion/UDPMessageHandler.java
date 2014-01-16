package mil.afrl.discoverylab.sate13.rippleandroid.network.ingestion;

import android.provider.ContactsContract;
import android.util.Log;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.DatabaseAdapter;
import mil.afrl.discoverylab.sate13.rippleandroid.network.RippleMoteMessage;
import mil.afrl.discoverylab.sate13.rippleandroid.network.analytics.AnalyticsEngine;
import mil.afrl.discoverylab.sate13.rippleandroid.network.analytics.AnalyticsResponse;

/**
 * Created by harmonbc on 1/16/14.
 */
public class UDPMessageHandler implements Runnable {

    //Queue that holds messages
    private LinkedBlockingQueue<MoteUDPInput.Unparsed> queue;
    DatabaseAdapter db = DatabaseAdapter.getInstance();

    public UDPMessageHandler(LinkedBlockingQueue queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        while(true){
            try{

            MoteUDPInput.Unparsed unparsed = queue.take();
            AnalyticsEngine analytics = new AnalyticsEngine();

            //RippleMoteMessage message = RippleMoteMessage.parse(unparsed.sender,unparsed.message, System.currentTimeMillis());
            takeAction(analytics.analyze(unparsed.sender, unparsed.message), unparsed);
            Log.e(Common.LOG_TAG, "Message Analytics Complete");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void takeAction(AnalyticsResponse response, MoteUDPInput.Unparsed message) {
        switch (response.response_code){
            case NO_ATTENTION_NEEDED:
            case LOW_PRIORITY:
            case MEDIUM_PRIORITY:
                addToBatch(message);
                break;
            case HIGH_PRIORITY:
            case IMMEDIATE_INTERVENTION:
                broadcastEmergency(message);
        }
    }

    private void broadcastEmergency(MoteUDPInput.Unparsed message) {

    }

    private void addToBatch(MoteUDPInput.Unparsed message) {
        db.storeVitalData(System.currentTimeMillis(), message.sender.toString(), message.message);
    }
}
