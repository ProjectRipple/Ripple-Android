package com.discoverylab.ripple.android.fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.api.ApiClient;
import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.WSConfig;

/**
 * Fragment to display the application preferences
 */
public class PrefsFragment extends PreferenceFragment {

    // Reference to shared preferences
    private SharedPreferences prefs;
    private EditTextPreference ipTextBox, portNumMqttTextBox, portNumRestTextBox;
    public static final String IP_REG_EXPRESSION = "^((1\\d{2}|2[0-4]\\d|25[0-5]|\\d?\\d)\\.){3}(?:1\\d{2}|2[0-4]\\d|25[0-5]|\\d?\\d)$";
    private static final String IPV6_REG_EXPRESSION = "^([\\dA-F]{1,4}:|((?=.*(::))(?!.*\\3.+\\3))\\3?)([\\dA-F]{1,4}(\\3|:\\b)|\\2){5}(([\\dA-F]{1,4}(\\3|:\\b|$)|\\2){2}|(((2[0-4]|1\\d|[1-9])?\\d|25[0-5])\\.?\\b){4})\\z";

    private static final String IPV6_HEX4DECCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?) ::((?:[0-9A-Fa-f]{1,4}:)*)(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
    private static final String IPV6_6HEX4DEC_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}:){6,6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
    public static final String IPV6_HEXCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)\\z";
    public static final String IPV6_REGEX = "\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z";

    public static final String IP_FROM_PREFS = "ipAddressPref";
    public static final String PORT_NUM_MQTT_PREFS = "portNumberMQTTPref";
    public static final String PORT_NUM_REST_PREFS = "portNumberRESTPref";
    public static final String SEND_IMAGE_BASE64_PREF = "prefSendBase64Image";


    @Override
    /**
     * The onCreate method handles thing when starting this activity,
     * mainly display the activity_settings.xml.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Editor myEditor = prefs.edit();
        if (!prefs.contains(IP_FROM_PREFS)) {
            // set preference if not already there
            Log.d(Common.LOG_TAG, "Setting default ip");
            myEditor.putString(IP_FROM_PREFS, WSConfig.DEFAULT_BROKER_IP);
            myEditor.apply();
        }

        if (!prefs.contains(PORT_NUM_MQTT_PREFS)) {
            // set preference if not already there
            Log.d(Common.LOG_TAG, "Setting default MQTT port");
            myEditor.putString(PORT_NUM_MQTT_PREFS, WSConfig.DEFAULT_MQTT_PORT);
            myEditor.apply();
        }

        if (!prefs.contains(PORT_NUM_REST_PREFS)) {
            // set preference if not already there
            Log.d(Common.LOG_TAG, "Setting default REST port.");
            myEditor.putString(PORT_NUM_REST_PREFS, WSConfig.DEFAULT_REST_PORT);
            myEditor.apply();
        }

        // set preferences
        addPreferencesFromResource(R.xml.fragment_prefs);
        // get text view references
        ipTextBox = (EditTextPreference) getPreferenceScreen().findPreference(IP_FROM_PREFS);
        portNumMqttTextBox = (EditTextPreference) getPreferenceScreen().findPreference(PORT_NUM_MQTT_PREFS);
        portNumRestTextBox = (EditTextPreference) getPreferenceScreen().findPreference(PORT_NUM_REST_PREFS);

        // Set on prefernce change listener for ip address
        ipTextBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Editor myEditor = prefs.edit();
                Boolean rtnval = true;

                // get new IP
                String newValueString = newValue.toString();

                // check if it is valid
                boolean validIPv4 = newValueString.matches(IP_REG_EXPRESSION);
                boolean validIPv6 = newValueString.matches(IPV6_HEXCOMPRESSED_REGEX) || newValueString.matches(IPV6_REGEX);

                // get port number
                String portNumRest = prefs.getString(PrefsFragment.PORT_NUM_REST_PREFS, WSConfig.DEFAULT_REST_PORT);

                // set URL if IP is valid
                if ((validIPv4 || validIPv6) && newValueString.length() > 0) {
                    myEditor.putString(IP_FROM_PREFS, newValueString);
                    myEditor.apply();
                    updateBrokerUrl(newValueString, Integer.valueOf(portNumRest));

                } else {
                    // ALERT user if IP is invalid
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.invalid_input);
                    builder.setMessage(R.string.ip_error_message);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    rtnval = false;
                }
                Log.d(Common.LOG_TAG, "New Root URL:" + WSConfig.ROOT_URL);
                return rtnval;
            }
        });

        // set rest port number's onpreferencechange listener
        portNumRestTextBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Editor myEditor = prefs.edit();
                // set new port number
                String newPortNum = newValue.toString();
                myEditor.putString(PORT_NUM_REST_PREFS, newPortNum);
                myEditor.apply();

                // update URL
                String ip = prefs.getString(IP_FROM_PREFS, WSConfig.DEFAULT_BROKER_IP);

                updateBrokerUrl(ip, Integer.valueOf(newPortNum));

                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (v != null) {
            ListView lv = (ListView) v.findViewById(android.R.id.list);
            lv.setPadding(0, 0, 0, 0);
            ViewGroup parent = (ViewGroup) lv.getParent();
            parent.setPadding(0, 0, 0, 0);
            v.setBackgroundColor(getResources().getColor(R.color.black));
        }
        return v;
    }

    /**
     * Update broker URL setting from new settings
     * @param hostAddress IP address of Broker
     * @param restPort port of Broker's REST API
     */
    private void updateBrokerUrl(String hostAddress, int restPort) {
        boolean validIPv4 = hostAddress.matches(IP_REG_EXPRESSION);
        boolean validIPv6 = hostAddress.matches(IPV6_HEXCOMPRESSED_REGEX) || hostAddress.matches(IPV6_REGEX);
        if (validIPv4) {
            WSConfig.ROOT_URL = "http://" + hostAddress + ":" + restPort;
            ApiClient.updateEndPoint();
        } else if (validIPv6) {
            WSConfig.ROOT_URL = "http://[" + hostAddress + "]:" + restPort;
            ApiClient.updateEndPoint();
        }
    }
}