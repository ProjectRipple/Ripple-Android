package mil.afrl.discoverylab.sate13.rippleandroid.network;

import android.os.Message;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by harmonbc on 1/6/14.
 */
public class MessageHandler implements Runnable{
    private LinkedBlockingQueue<RippleMoteMessage> queue;
    public MessageHandler(LinkedBlockingQueue<RippleMoteMessage> queue){
        this.queue = queue;
    }

    @Override
    public void run() {

    }
}
