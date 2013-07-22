package mil.afrl.discoverylab.sate13.rippleandroid.adapter.network;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;

public class UdpClient {

/*    public interface UdpMessageListener{
        public void onMessage(UdpClient client, String message);
    }*/

    private volatile boolean listening = false;
    private int port;
    private String serverHost;
    private InetAddress serverAddr;
    private DatagramSocket socket = null;
    private Thread listenTread = null;
    private List<Handler> listeners = new ArrayList<Handler>();
    //private Thread messageHandlerThread = null;
    //private Queue<String> messageQueue = new LinkedList<String>();
    //private static int queueLimit = 500;

    public UdpClient() {
    }

    public void addTcpListener(Handler listener) {
        if (listener != null) {
            synchronized (this.listeners) {
                this.listeners.add(listener);
            }
        }
    }

    public void removeTcpListener(Handler listener) {
        if (listener != null) {
            synchronized (this.listeners) {
                this.listeners.remove(listener);
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

        this.listenTread = new Thread() {
            @Override
            public void run() {
                Log.d(Common.LOG_TAG, "UDP: S: Receiving...");
                while (listening) {
                    try {
                        /* Retrieve the ServerName */
                        // InetAddress serverAddr =
                        // InetAddress.getByName(SERVERIP);

                        //Log.d(Common.LOG_TAG, "UDP: S: Connecting...");
                        /* Create new UDP-Socket */
                        // DatagramSocket socket = new
                        // DatagramSocket(SERVERPORT, serverAddr);

						/*
                         * By magic we know, how much data will be waiting for
						 * us
						 */
                        byte[] buf = new byte[4096];
                        /*
                         * Prepare a UDP-Packet that can contain the data we
						 * want to receive
						 */
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);

						/* Receive the UDP-Packet */
                        socket.receive(packet);

                        String input = new String(packet.getData()).trim();

                        /*synchronized(messageQueue) {
                            messageQueue.offer(input);
                        }*/
                        synchronized (listeners) {
                            if (input != null) {
                                for (Handler l : listeners) {
                                    //l.onMessage(UdpClient.this, input);
                                    l.sendMessage(l.obtainMessage(0, input));
                                }
                            }
                        }

                        Log.d(Common.LOG_TAG, "UDP: S: Received something:");// '" + input + "'");

                    } catch (Exception e) {
                        Log.e(Common.LOG_TAG, "UDP: S: Error", e);
                    }
                }
                Log.d(Common.LOG_TAG, "UDP: S: Done.");
            }
        };

        /*this.messageHandlerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String input = "";
                while (listening == true) {
                    while (messageQueue.size() > 0) {

                        synchronized (messageQueue) {
                            input = messageQueue.poll();
                            if (messageQueue.size() > queueLimit) {
                                messageQueue.clear();
                                Log.d(Common.LOG_TAG, "UDP: Queue size exceeded. Size = " + messageQueue.size());
                            }
                        }
                        if (input != null) {
                            synchronized (listeners) {
                                for (UdpMessageListener l : listeners) {
                                    l.onMessage(UdpClient.this, input);
                                }
                            }
                        }
                    }
                }
            }

        });*/

        this.listening = true;

        /*this.messageHandlerThread.setDaemon(true);
        this.messageHandlerThread.setName("UDP Message Handler Thread");
        this.messageHandlerThread.start();*/

        //this.listenTread.setDaemon(true);
        this.listenTread.setName("UDP Listen");
        this.listenTread.start();
    }

    public void sendMessage(String msg) {
        try {

            Log.d("UDP", "C: Connecting...");
            /* Create new UDP-Socket */
            // DatagramSocket socket = new DatagramSocket();

			/* Prepare some data to be sent. */
            byte[] buf = msg.getBytes();

			/*
			 * Create UDP-packet with data & destination(url+port)
			 */
            // DatagramPacket packet = new DatagramPacket(buf, buf.length,
            // this.serverAddr, this.port);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            //Log.d(Common.LOG_TAG, "UDP: C: Sending: '" + new String(buf).trim() + "'");

			/* Send out the packet */
            socket.send(packet);

            Log.d(Common.LOG_TAG, "UDP: C: Sent.");
            Log.d(Common.LOG_TAG, "UDP: C: Done.");
        } catch (Exception e) {
            Log.e(Common.LOG_TAG, "UDP: C: Error", e);
        }
    }

    public void disconnect() {
        this.listening = false;
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }

        if (this.listenTread != null) {
            this.listenTread.interrupt();
        }

        /*synchronized (this.messageQueue) {
            this.messageQueue.clear();
        }
        if (this.messageHandlerThread != null) {
            this.messageHandlerThread.interrupt();
        }*/
    }

}
