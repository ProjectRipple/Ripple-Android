package mil.afrl.discoverylab.sate13.rippleandroid.network.ingestion;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.network.RippleMoteMessage;

/**
 * Created by harmonbc on 1/6/14.
 */
public class MoteUDPInput implements Runnable{
    // Port server is listening on
    private int listenPort;
    // Address servier is listening on
    private InetAddress address;
    // Socket object that server is bound to
    private DatagramSocket socket = null;
    // Buffer for incoming messages
    private byte[] receiveBuffer;
    // Packet for incoming messages
    private DatagramPacket receivePacket;
    // size in bytes of receive buffer
    private static final int RECEIVE_BUF_SIZE = 400;
    //Queue that holds messages
    private LinkedBlockingQueue<Unparsed> queue;
    //Logging name
    private static final String LOG_NAME = "MOTE_UDP_INPUT";

    public class Unparsed{
        public InetSocketAddress sender;
        public byte[] message;
    }
    public MoteUDPInput(InetAddress address, int port, LinkedBlockingQueue queue) {
        this.address = address;
        this.listenPort = port;
        this.queue = queue;
    }


    private void init() throws IOException {
        // initialize socket(must do it this way for bind() to succeed)
        this.socket = new DatagramSocket(listenPort);
        Log.e(Common.LOG_TAG, this.socket.getLocalSocketAddress().toString());
        // initialize receive buffer
        this.receiveBuffer = new byte[RECEIVE_BUF_SIZE];
        // initialize receive packet
        this.receivePacket = new DatagramPacket(this.receiveBuffer, this.receiveBuffer.length);
    }


    public void stop() {
        this.socket.close();
    }

    public void run(){
        try{
            this.init();
        }catch(Exception e){
            Log.e(LOG_NAME, e.getMessage());
            return;
        }

        while(true)
        {
            try{
                // Attempt receive (blocking call)
                this.socket.receive(this.receivePacket);
                Unparsed msg = new Unparsed();
                // get sender info from socket address
                InetSocketAddress sockAddr = ((InetSocketAddress) this.receivePacket.getSocketAddress());

                msg.sender = sockAddr;
                msg.message = Arrays.copyOfRange(this.receivePacket.getData(), 4, 40);
                 // Send packet to the queue thread
                queue.put(msg);

                Log.e(Common.LOG_TAG, "Queue SIze: "+queue.size());
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Reset packet length to buffer max
            this.receivePacket.setLength(this.receiveBuffer.length);
        }
    }
}
