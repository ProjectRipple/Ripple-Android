package mil.afrl.discoverylab.sate13.rippleandroid.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import mil.afrl.discoverylab.sate13.rippleandroid.network.impl.MoteUDPInput;
import mil.afrl.discoverylab.sate13.rippleandroid.network.skeletons.MoteInterface;

/**
 * Created by harmonbc on 1/6/14.
 */
public class MoteController implements Runnable {
    private static InetAddress address;
    private static final int port = 10911;

    public MoteController(){
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        LinkedBlockingQueue<RippleMoteMessage> queue = new LinkedBlockingQueue<RippleMoteMessage>();

        //Create needed objects and pass shared queue to both
        MoteInterface moteInterface = new MoteUDPInput(address, port,queue);
        MessageHandler messageHandler = new MessageHandler(queue);

        new Thread(moteInterface).start();
        new Thread(messageHandler).start();
    }
}
