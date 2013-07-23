package mil.afrl.discoverylab.sate13.rippleandroid.adapter.network;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;

/**
 * Created by james on 7/22/13.
 */
public class MulticastClient {

    // reference to socket
    private MulticastSocket socket = null;

    // Name of Network interface
    private static String NI_NAME = "wlan0";
    // reference to current listener thread
    private Thread listenThread = null;
    // flag for listener thread
    private volatile boolean listening = false;
    // List of Handlers to send messages
    private List<Handler> listeners = new ArrayList<Handler>();

    public MulticastClient()
    {
    }

    public void addHandler(Handler handle)
    {
        if(handle != null)
        {
            synchronized (this.listeners)
            {
                this.listeners.add(handle);
            }
        }
    }

    public void removeHandler(Handler handle)
    {
        if(handle != null)
        {
            synchronized (this.listeners)
            {
                this.listeners.remove(handle);
            }
        }
    }

    /**
     * Joins a multicast group on specified port
     * @param group
     * @param port
     */
    public void joinGroup(InetAddress group, int port)
    {
        try {

            if(this.socket == null)
            {
                // Create new socket
                this.socket = new MulticastSocket(port);
            }

            this.socket.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByName(NI_NAME));

            if(this.listenThread == null)
            {
                // start thread after first join
                this.listenThread = new Thread(new ListenThread());
                // Set thread as daemon (prevents blocking of JVM exit if thread is still running as JVM can exit if only daemon threads remain)
                this.listenThread.setDaemon(true);
                // Give the thread a name
                this.listenThread.setName("MulticastClient listener thread");
                // set flag to true
                this.listening = true;
                // start thread
                this.listenThread.start();
            }

        } catch (IOException e) {
            Log.e(Common.LOG_TAG, "join group Address is not a multicast Address");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Leaves the specific multicast group
     * @param group
     * @param port
     */
    public void leaveGroup(InetAddress group, int port)
    {
        if(this.socket == null)
        {
            // Nothing to do
            return;
        }
        try {
            this.socket.leaveGroup(new InetSocketAddress(group, port), NetworkInterface.getByName(NI_NAME));

        } catch (IOException e) {
            Log.e(Common.LOG_TAG, "leave group Address is not a multicast Address");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Closes all connections for this client
     */
    public void disconnect()
    {
        // set flag to false
        this.listening = false;
        // close socket & set to null
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }

        // interrupt thread & set to null
        if (this.listenThread != null) {
            this.listenThread.interrupt();
            this.listenThread = null;
        }
    }

    private class ListenThread implements Runnable
    {

        // constants
        private static final int BUF_SIZE = 1024;
        // buffer for receiving data
        private byte[] dataBuffer;
        // packet for receiving data
        private DatagramPacket receivePacket;

        @Override
        public void run() {

            // create buffer and packet objects for receiving
            this.dataBuffer = new byte[BUF_SIZE];
            this.receivePacket = new DatagramPacket(this.dataBuffer, this.dataBuffer.length);

            // listen flag is good and close was not called
            while(listening && socket != null)
            {
                try
                {
                    // Reset packet/buffer for reuse
                    // Reset packet length to buffer max
                    this.receivePacket.setLength(this.dataBuffer.length);
                    // Clear packet buffer
                    Arrays.fill(this.dataBuffer, (byte) 0);
                    // wait for message
                    socket.receive(this.receivePacket);

                    // Send input to all listening handlers
                    synchronized (listeners)
                    {
                        for(Handler h : listeners)
                        {
                            h.obtainMessage(Common.RIPPLE_MSG_MCAST, new String(this.receivePacket.getData(), 0, this.receivePacket.getLength())).sendToTarget();
                        }
                    }

                } catch (IOException e) {
                    Log.d(Common.LOG_TAG,"Exception while receiving", e);
                }
            }
        }
    }


}
