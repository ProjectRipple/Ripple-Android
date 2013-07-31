package mil.afrl.discoverylab.sate13.rippleandroid.adapter.network;

import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vital;

public class UdpClient {

    // flag for listener thread
    private volatile boolean listening = false;
    private int port;
    private String serverHost;
    private InetAddress serverAddr;
    // Reference to socket
    private DatagramSocket socket = null;
    // Reference to the current listener thread
    private Thread listenThread = null;
    // List of handlers to send messages
    private final List<Handler> listeners = new ArrayList<Handler>();

    public UdpClient() {
    }

    public void addHandler(Handler handle) {
        if (handle != null) {
            synchronized (this.listeners) {
                this.listeners.add(handle);
            }
        }
    }

    public void removehandler(Handler handle) {
        if (handle != null) {
            synchronized (this.listeners) {
                this.listeners.remove(handle);
            }
        }
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    public void connect(String serverHost, int port) {

        if (port < 0 || port > 65355)
            throw new IllegalArgumentException("port must by in range 0-65355");

        this.setServerHost(serverHost);
        this.setPort(port);

        // Retrieve the ServerName
        try {
            this.serverAddr = InetAddress.getByName(this.getServerHost());
        } catch (UnknownHostException e1) {
            Log.e(Common.LOG_TAG, "UDP: C: Unable to retrieve the ServerName", e1);
        }

        DatagramChannel chan = null;
        try {
            chan = DatagramChannel.open();
        } catch (IOException e1) {
            Log.e(Common.LOG_TAG, "UDP: C: unable to open the DataGram Channel", e1);
        }

        this.socket = chan.socket();
        try {
            this.socket.bind(null);
        } catch (SocketException e1) {
            Log.e(Common.LOG_TAG, "UDP: C: Unable to bind the socket", e1);
        }
        this.socket.connect(this.serverAddr, getPort());

        if (this.listenThread == null) {
            // start thread after first join
            this.listenThread = new Thread(new ListenThread());
            // Set thread as daemon (prevents blocking of JVM exit if thread is still running as JVM can exit if only daemon threads remain)
            this.listenThread.setDaemon(true);
            // Give the thread a name
            this.listenThread.setName("UDPClient listener thread");
            // set flag to true
            this.listening = true;
            // start thread
            this.listenThread.start();
        }
    }

    public void sendMessage(String msg) {
        try {

            Log.d("UDP", "C: Connecting...");
            /* Prepare some data to be sent. */
            byte[] buf = msg.getBytes();

			/*
             * Create UDP-packet with data & destination(url+port)
			 */
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            //Log.d(Common.LOG_TAG, "UDP: C: Sending: '" + new String(buf).trim() + "'");

			/* Send out the packet */
            socket.send(packet);

            Log.d(Common.LOG_TAG, "UDP: C: Packet Sent.");
        } catch (Exception e) {
            Log.e(Common.LOG_TAG, "UDP: C: Error", e);
        }
    }

    /**
     * Closes all connections for this client
     */
    public void disconnect() {
        // set flag to false
        this.listening = false;
        // close socket and set to null
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        // interupt thread and set to null
        if (this.listenThread != null) {
            this.listenThread.interrupt();
        }
    }

    private class ListenThread implements Runnable {
        // constants
        private static final int WHAT = 0;
        private static final int BUF_SIZE = 4096;
        // buffer for receiving data
        private byte[] dataBuffer;
        // Input string
        private String input;
        // packet for receiving data
        private DatagramPacket receivePacket;
        // Vital object to deserialize and send
        private Vital vital;

        @Override
        public void run() {

            Log.d(Common.LOG_TAG, "UDP: S: Receiving...");
            while (listening) {
                try {

						/*
                         * By magic we know, how much data will be waiting for
						 * us
						 */
                    dataBuffer = new byte[BUF_SIZE];

                        /*
                         * Prepare a UDP-Packet that can contain the data we
						 * want to receive
						 */
                    receivePacket = new DatagramPacket(dataBuffer, dataBuffer.length);

						/* Receive the UDP-Packet */
                    socket.receive(receivePacket);

                    // Deserialize the object contained in the data in the newly received packet
                    try {
                        ByteArrayInputStream baos = new ByteArrayInputStream(receivePacket.getData());
                        ObjectInputStream oos = new ObjectInputStream(baos);
                        vital = (Vital) oos.readObject();
                    } catch (Exception e) {
                        Log.e(Common.LOG_TAG, "Unable to deserialize message " + e);
                    }

                    if (vital != null) {

                        // Bundle deserialized object into a message
                        // Send the message to all subscribed handlers
                        synchronized (listeners) {
                            for (Handler l : listeners) {
                                l.sendMessage(l.obtainMessage(WHAT, vital));
                            }
                        }

                        vital = null;
                    }

                    Log.d(Common.LOG_TAG, "UDP: S: Received something:");

                } catch (Exception e) {
                    Log.e(Common.LOG_TAG, "UDP: S: Error", e);
                }
            }
            Log.d(Common.LOG_TAG, "UDP: S: Done.");
        }
    }
}
