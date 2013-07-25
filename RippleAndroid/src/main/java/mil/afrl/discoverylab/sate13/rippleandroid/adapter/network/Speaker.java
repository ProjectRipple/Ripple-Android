package mil.afrl.discoverylab.sate13.rippleandroid.adapter.network;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 7/19/13.
 */
public abstract class Speaker {

    private final List<Handler> listeners = new ArrayList<Handler>();

    public void adListener(Handler listener) {
        if (listener != null) {
            synchronized (this.listeners) {
                this.listeners.add(listener);
            }
        }
    }

    public void removeListener(Handler listener) {
        if (listener != null) {
            synchronized (this.listeners) {
                this.listeners.remove(listener);
            }
        }
    }

    private void speak(int what, Object data) {
        synchronized (listeners) {
            for (Handler l : listeners) {
                l.sendMessage(l.obtainMessage(what, data));
            }
        }
    }

}
