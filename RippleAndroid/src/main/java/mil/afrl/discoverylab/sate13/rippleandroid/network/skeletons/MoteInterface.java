package mil.afrl.discoverylab.sate13.rippleandroid.network.skeletons;

import java.net.InetAddress;

/**
 * This interface is responsible for the interface the manages the connections
 * with the Mote devices.
 *
 * Created by harmonbc on 1/6/14.
 */
public interface MoteInterface extends Runnable {
    public void stop();
}
