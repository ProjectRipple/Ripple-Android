package mil.afrl.discoverylab.sate13.rippleandroid.network;

import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.LinkedBlockingQueue;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.network.analytics.AnalyticsEngine;
import mil.afrl.discoverylab.sate13.rippleandroid.network.analytics.AnalyticsResponse;
import mil.afrl.discoverylab.sate13.rippleandroid.network.ingestion.MoteUDPInput;
import mil.afrl.discoverylab.sate13.rippleandroid.network.ingestion.UDPMessageHandler;

/**
 * Created by harmonbc on 1/15/14.
 */
public class Controller implements Runnable{
    private static InetAddress address;
    private static final int port = 10911;

    @Override
    public void run() {
        try {

            address = InetAddress.getByName(getLocalIpAddress());
            Log.e(Common.LOG_TAG, getLocalIpAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LinkedBlockingQueue<MoteUDPInput.Unparsed> queue = new LinkedBlockingQueue<MoteUDPInput.Unparsed>();


        MoteUDPInput ingestion = new MoteUDPInput(address, port,queue);
        new Thread(ingestion).start();
        new Thread(new UDPMessageHandler(queue)).start();
    }



    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(Common.LOG_TAG, ex.toString());
        }
        return null;
    }
}
