package mil.afrl.discoverylab.sate13.rippleandroid.network;
/**
 * Container for Ripple data from motes
 * @author james
 */
import android.util.Log;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.network.Reference.*;



public class RippleMoteMessage {

    // Message information
    public InetSocketAddress senderAddress;
    public long timeReceived;
    public int hops, sequence, hr, sp02, bpm, temp, age;
    public String id;

    /**
     * Parse listener observation to a RippleMoteMessage
     * @param message
     * @param time
     * @return
     */
    public synchronized static RippleMoteMessage parse(InetSocketAddress address, byte[] message, long time) {

        RippleMoteMessage result = new RippleMoteMessage();

        result.senderAddress = address;
        result.timeReceived = time;

        ByteBuffer wrapped = ByteBuffer.wrap(message);

        StringBuilder sb = new StringBuilder();
        for(int i=0; i<16;i++) sb.append(wrapped.getChar()); //First 16 Bytes is the ID
        result.id = sb.toString();
        result.sequence =  wrapped.getShort();
        result.age = wrapped.get();
        result.hops = wrapped.get();
        result.hr = wrapped.get();
        result.sp02 = wrapped.get();
        result.bpm = wrapped.get();
        result.temp = wrapped.get();

        Log.e(Common.LOG_TAG, result.toString());

        return result;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(200);
        sb.append("Mote Message:\n\tSender: ").append(senderAddress.getHostName()).append("\n\tTime In:")
                .append(timeReceived).append("\n\tHops: ").append(hops).append("\n\tSequence: ").append(sequence)
                .append("\n\tHeart Rate: ").append(hr).append("\n\tSp02: ").append(sp02).append("\n\tBPM: ")
                .append(bpm).append("\n\tTemp: ").append(temp).append("\n\tAge: ").append(age);

        return sb.toString();
    }
}