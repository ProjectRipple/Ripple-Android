package mil.afrl.discoverylab.sate13.rippleandroid.adapter.network;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;

public class TcpClient {

    /*public interface TcpMessageListener {
        public void onMessage(TcpClient client, String message);
    }*/

    private volatile boolean listening = false;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private Socket socket = null;
    private Thread listenThread = null;
    private List<Handler> listeners = new ArrayList<Handler>();
    private static Gson gson = new GsonBuilder().setDateFormat(Common.DATE_TIME_FORMAT).create();
    //private Thread messageHandlerThread = null;
    //private int userNum = -1;
    //private List<TcpMessageListener> listeners = new ArrayList<TcpMessageListener>();
    //private Queue<String> messageQueue = new LinkedList<String>();
    //private static int queueLimit = 500;

/*    public int getUserNum() {
        return this.userNum;
    }*/

    public TcpClient() {
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

    public boolean connect(String serverIpOrHost, int port) {
        try {

            socket = new Socket(serverIpOrHost, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.listenThread = new Thread() {
                @Override
                public void run() {
                    int charsRead = 0;
                    char[] buff = new char[4096];
                    while (listening && charsRead >= 0) {
                        try {
                            charsRead = in.read(buff);
                            if (charsRead > 0) {

                                // log message for debugging
                                Log.d(Common.LOG_TAG, new String(buff).trim());

                                String input = new String(buff).trim();
                                if (input != null) {
                                    synchronized (listeners) {
                                        for (Handler l : listeners) {
                                            l.sendMessage(l.obtainMessage(0, input));
                                        }
                                    }
                                }

                    /*synchronized (messageQueue) {
                        messageQueue.offer(input);
                    }*/
                                // synchronized (listeners)
                                // {
                                // for (TcpMessageListener l : listeners)
                                // {
                                // l.onMessage(TcpClient.this, input);
                                // }
                                // }
                                //
                                // if (input.toLowerCase().contains("<user_num>"))
                                // {
                                // int index = input.toLowerCase().indexOf("<user_num>");
                                // index += "<user_num>".length();
                                // int index2 = input.toLowerCase().indexOf("</user_num>");
                                // userNum = Integer.parseInt(input.substring(index, index2));
                                //
                                // }
                            }
                        } catch (Exception e) {
                            Log.e(Common.LOG_TAG, "TCP: Exception while reading input stream:" + e.getMessage());
                            listening = false;
                        }

                        // clear buffer before next read
                        while (charsRead > 0) {
                            charsRead--;
                            buff[charsRead] = '\0';
                        }

                        charsRead = 0;
                    }
                    //userNum = -1;
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
                                    Log.d(Common.LOG_TAG, "Queue size exceeded. Size = " + messageQueue.size());
                                }
                            }
                            if (input != null) {
                                synchronized (listeners) {
                                    for (Handler l : listeners) {
                                        //l.onMessage(TcpClient.this, input);
                                    }
                                }

*//*                                if (input.toLowerCase().contains("<user_num>")) {
                                    int index = input.toLowerCase().indexOf("<user_num>");
                                    index += "<user_num>".length();
                                    int index2 = input.toLowerCase().indexOf("</user_num>");
                                    userNum = Integer.parseInt(input.substring(index, index2));

                                }*//*
                            }
                        }
                    }
                }

            });*/

            this.listening = true;

            //this.messageHandlerThread.setDaemon(true);
            //this.messageHandlerThread.setName("TCP Message Handler Thread");
            //this.messageHandlerThread.start();

            //this.listenThread.setDaemon(true);
            this.listenThread.setName("TCP Listener");
            this.listenThread.start();

        } catch (UnknownHostException e) {
            Log.e(Common.LOG_TAG, "TCP: Don't know about host", e);
            return false;
        } catch (IOException e) {
            Log.e(Common.LOG_TAG, "TCP: Couldn't get I/O for the connection", e);
            return false;
        } catch (Exception e) {
            Log.e(Common.LOG_TAG, e.getMessage().toString());
            return false;
        }
        return true;
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
            out.flush();
        }
    }

    public void disconnect() {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (this.listenThread != null) {
                this.listening = false;
                this.listenThread.interrupt();
            }
            /*synchronized (this.messageQueue) {
                this.messageQueue.clear();
            }*/
            /*if (this.messageHandlerThread != null) {
                this.listening = false;
                this.messageHandlerThread.interrupt();
            }*/
            //this.userNum = -1;
        } catch (IOException ioe) {
            Log.e(Common.LOG_TAG, "TCP: I/O error in closing connection.", ioe);
        }
    }

}
