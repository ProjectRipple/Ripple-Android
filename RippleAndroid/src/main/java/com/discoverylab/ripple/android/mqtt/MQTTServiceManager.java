package com.discoverylab.ripple.android.mqtt;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Manages the communication between the MQTTClientService and Activities
 */
public class MQTTServiceManager {
    // Log tag
    private static final String TAG = "MQTTServiceManager";
    // true to print debug messages
    private static final boolean DEBUG = true;

    // Class of MQTT service
    private Class<? extends Service> mServiceClass;
    // context for this object
    private Context mContext;
    // is the service bound
    private boolean mIsBound;
    // Messenger for manager -> service communication
    private Messenger mService = null;
    // Handler for manager -> activity communication
    private Handler mIncomingHandler = null; // handles manager->activity comms
    // Messenger for service to manager communication
    private final Messenger mMessenger = new Messenger(new IncomingHandler(this)); //handler service->manager comms

    /*
     * This class will receive messages sent by service to the service manager. The message
     * will be passed to the activity.
     */
    private static class IncomingHandler extends Handler {
        private WeakReference<MQTTServiceManager> serviceManager;

        public IncomingHandler(MQTTServiceManager manager) {
            this.serviceManager = new WeakReference<MQTTServiceManager>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            MQTTServiceManager service = serviceManager.get();
            if (service != null && service.mIncomingHandler != null) {
                if (DEBUG) {
                    Log.d(TAG, "Incoming message from MQTT service: " + msg);
                }
                service.mIncomingHandler.handleMessage(msg);
            }
        }
    }

    /*
     * Used to monitor state of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);
            if (DEBUG) {
                Log.d(TAG, "Connected to the service");
            }
            try {
                // Register this manager with the service
                Message msg = Message.obtain(null, MQTTServiceConstants.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            if (DEBUG) {
                Log.d(TAG, "Disconnected from the service");
            }
        }
    };

    /**
     * @param context         Context to mind the service to
     * @param serviceClass    Class of service to manage
     * @param incomingHandler Handler to pass messages from service to (usually from an activity)
     */
    public MQTTServiceManager(Context context, Class<? extends Service> serviceClass, Handler incomingHandler) {
        mContext = context;
        mServiceClass = serviceClass;
        mIncomingHandler = incomingHandler;
        if (isServiceRunning()) {
            doBindService();
        }
    }

    /**
     * Start & bind the MQTT service and connect to the specified broker
     *
     * @param brokerIp   Address of Broker
     * @param brokerPort Port of Broker
     */
    public void start(String brokerIp, String brokerPort) {
        doStartService(brokerIp, brokerPort);
        doBindService();
        if (DEBUG) {
            Log.d(TAG, "Service started");
        }
    }

    /**
     * Stop and unbind the MQTT service
     */
    public void stop() {
        try {
            // Send stop signal to service
            send(Message.obtain(null, MQTTServiceConstants.MSG_STOP_SERVICE));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        doUnbindService();
        doStopService();
        if (DEBUG) {
            Log.d(TAG, "Service stopped");
        }
    }

    public void unbind() {
        doUnbindService();
    }

    public void bind() {
        doBindService();
    }

    /**
     * Check if the service is running
     *
     * @return true if service is running, false otherwise
     */
    public boolean isServiceRunning() {
        ActivityManager manger = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manger.getRunningServices(Integer.MAX_VALUE)) {
            if (mServiceClass.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    /**
     * Send a message to the service
     *
     * @param msg Message to send to service
     * @throws RemoteException DeadObjectException if the target Handler no longer exists.
     */
    public void send(Message msg) throws RemoteException {
        if (mIsBound && mService != null) {
            mService.send(msg);
        }
    }

    /**
     * Start service
     *
     * @param brokerIp   Address of Broker
     * @param brokerPort Port of Broker
     */
    private void doStartService(String brokerIp, String brokerPort) {
        Intent i = new Intent(mContext, mServiceClass);
        i.setAction(MQTTServiceConstants.ACTION_START);
        i.putExtra(MQTTServiceConstants.MQTT_BROKER_IP, brokerIp);
        i.putExtra(MQTTServiceConstants.MQTT_BROKER_PORT, brokerPort);
        mContext.startService(i);
    }

    /**
     * Stop service
     */
    private void doStopService() {
        mContext.stopService(new Intent(mContext, mServiceClass));
    }

    /**
     * Unbind the service from the context
     */
    private void doUnbindService() {
        if (mIsBound && mService != null) {
            try {
                Message msg = Message.obtain(null, MQTTServiceConstants.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mContext.unbindService(mConnection);
        mIsBound = false;
    }

    /**
     * Bind the service to the context
     */
    private void doBindService() {
        mContext.bindService(new Intent(mContext, mServiceClass), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
}
