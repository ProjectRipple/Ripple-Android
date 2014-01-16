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

    private static final String DELIM=",";

    private static final int SIZE_HEADER = 16;
    //Header Parsing
    private static final int SIZE_HEADER_DISPATCH = 8;
    private static final int SIZE_HEADER_RIPPLECOMM_VERSION=4;
    private static final int SIZE_HEADER_MSG_TYPE=4;
    //Body Parsing
    private static final int SIZE_SOURCE_ADDRESS=1;
    private static final int SIZE_SEQ=2;
    private static final int SIZE_EST_AGE=1;
    private static final int SIZE_HOPS=1;
    private static final int SIZE_HR=1;
    private static final int SIZE_SP02=1;
    private static final int SIZE_BPM=1;
    private static final int SIZE_TEMP=1;
    private static final int SIZE_DEVICE_STATUS=2;

    private static final int INDEX_HEADER=0;
    //Header Indexes
    private static final int INDEX_HEADER_DISPATCH_START=0;
    private static final int INDEX_HEADER_DISPATCH_END=7;
    private static final int INDEX_HEADER_RIPPLECOMM_VERSION_START=8;
    private static final int INDEX_HEADER_RIPPLECOMM_VERSION_END=11;
    private static final int INDEX_HEADER_MSG_TYPE_START=12;
    private static final int INDEX_HEADER_MSG_TYPE_END=15;
    //Body Indexes
    private static final int INDEX_SEQ_START=16;
    private static final int INDEX_SEQ_END=INDEX_SEQ_START+SIZE_SEQ-1;
    private static final int INDEX_EST_AGE=INDEX_SEQ_END+1;
    private static final int INDEX_HOPS=INDEX_EST_AGE+1;
    private static final int INDEX_HR=INDEX_HOPS+1;
    private static final int INDEX_SP02=INDEX_HR+1;
    private static final int INDEX_BPM=INDEX_SP02+1;
    private static final int INDEX_TEMP=INDEX_BPM+1;
    private static final int INDEX_STATUS_START=INDEX_TEMP+1;
    private static final int INDEX_STATUS_END=INDEX_STATUS_START+SIZE_DEVICE_STATUS-1;


    // Message information
    public InetSocketAddress senderAddress;
    public long timeReceived;
    public int sequence, hops, estAge,  hr, sp02, bpm, temp, status;
    public String id;

    /**
     * Parse listener observation to a RippleMoteMessage
     * @param message
     * @param time
     * @return
     */
    public static RippleMoteMessage parse(InetSocketAddress address, byte[] message, long time) {

        RippleMoteMessage result = new RippleMoteMessage();

        result.senderAddress = address;
        result.timeReceived = time;

        result.sequence = parseValue(INDEX_SEQ_START, SIZE_SEQ, message);
        result.estAge = parseValue(INDEX_EST_AGE, SIZE_EST_AGE, message);
        result.hops = parseValue(INDEX_HOPS, SIZE_HOPS, message);
        result.hr = parseValue(INDEX_HR,SIZE_HR,message);
        result.sp02 = parseValue(INDEX_SP02, SIZE_SP02, message);
        result.bpm = parseValue(INDEX_BPM, SIZE_BPM, message);
        result.temp = parseValue(INDEX_TEMP, SIZE_TEMP, message);
        result.status = parseValue(INDEX_STATUS_START, SIZE_DEVICE_STATUS, message);

        Log.e(Common.LOG_TAG, result.toString());
        return result;
    }

    public static int parseValue(int startIndex, int size, byte[] message){

        int value=0;
        if(size==1) return (message[startIndex]&0xff);

        else{
            for(int i=startIndex; i < startIndex+size-1; i++){
                value |= (message[i]&0xff);
                value = (value<<8);
            }
            value |= (message[startIndex+size-1] &0xff);
            return value;

        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(300);
        sb.append("[")
                .append(timeReceived).append(DELIM)
                .append(sequence).append(DELIM)
                .append(hops).append(DELIM)
                .append(estAge).append(DELIM)
                .append(hr).append(DELIM)
                .append(sp02).append(DELIM)
                .append(bpm).append(DELIM)
                .append(temp).append(DELIM)
                .append(status).append("]\n");

        return sb.toString();
    }
}