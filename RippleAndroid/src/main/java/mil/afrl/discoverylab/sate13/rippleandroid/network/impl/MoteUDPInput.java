package mil.afrl.discoverylab.sate13.rippleandroid.network.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import mil.afrl.discoverylab.sate13.rippleandroid.network.RippleMoteMessage;
import mil.afrl.discoverylab.sate13.rippleandroid.network.skeletons.MoteInterface;

/**
 * Created by harmonbc on 1/6/14.
 */
public class MoteUDPInput implements MoteInterface {
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
    private LinkedBlockingQueue<RippleMoteMessage> queue;

    public MoteUDPInput(InetAddress address, int port, LinkedBlockingQueue queue) {
        this.address = address;
        this.listenPort = port;
        this.queue = queue;
    }


    private void init() throws IOException {
        // initialize socket(must do it this way for bind() to succeed)
        this.socket = DatagramChannel.open().socket();
        // No using socket to broadcast
        this.socket.setBroadcast(false);
        // Bind to local port
        this.socket.bind(new InetSocketAddress(this.address, this.listenPort));
        // initialize receive buffer
        this.receiveBuffer = new byte[RECEIVE_BUF_SIZE];
        // initialize receive packet
        this.receivePacket = new DatagramPacket(this.receiveBuffer, this.receiveBuffer.length);
    }


    @Override
    public void stop() {
        this.socket.close();
    }

    @Override
    public void run(){
        try{
            this.init();
        }catch(Exception e){
            //TODO: add exception handling
            return;
        }

        while(true)
        {
            try{
                // Attempt receive (blocking call)
                this.socket.receive(this.receivePacket);
                // get sender info from socket address
                InetSocketAddress sockAddr = ((InetSocketAddress) this.receivePacket.getSocketAddress());
                // Send packet to the queue thread
                queue.put(RippleMoteMessage.parse(sockAddr, Arrays.copyOf(this.receivePacket.getData(), this.receivePacket.getLength()), System.currentTimeMillis()));
                // Reset packet length to buffer max
                this.receivePacket.setLength(this.receiveBuffer.length);
            }catch (IOException e){
                //TODO: add excetion
            } catch (InterruptedException e) {
                //TODO: add excetion
            }
        }
    }
}
