package com.discoverylab.ripple.android.fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.discoverylab.ripple.android.util.Common;
import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.api.ApiClient;
import com.discoverylab.ripple.android.config.WSConfig;


public class PrefsFragment extends PreferenceFragment {

    private SharedPreferences prefs;
    private Editor myEditor;
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


    @Override
    /**
     * The onCreate method handles thing when starting this activity,
     * mainly display the activity_settings.xml.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!prefs.contains(IP_FROM_PREFS)) {
            // set preference if not already there
            Log.d(Common.LOG_TAG, "Setting default ip");
            myEditor = prefs.edit();
            myEditor.putString(IP_FROM_PREFS, WSConfig.DEFAULT_IP);
            myEditor.commit();
        }

        if (!prefs.contains(PORT_NUM_MQTT_PREFS)) {
            Log.d(Common.LOG_TAG, "Setting default MQTT port");
            myEditor = prefs.edit();
            myEditor.putString(PORT_NUM_MQTT_PREFS, WSConfig.DEFAULT_MQTT_PORT);
            myEditor.commit();
        }

        if (!prefs.contains(PORT_NUM_REST_PREFS)) {
            Log.d(Common.LOG_TAG, "Setting default REST port.");
            myEditor = prefs.edit();
            myEditor.putString(PORT_NUM_REST_PREFS, WSConfig.DEFAULT_REST_PORT);
            myEditor.commit();
        }

        addPreferencesFromResource(R.layout.fragment_settings);
        ipTextBox = (EditTextPreference) getPreferenceScreen().findPreference(IP_FROM_PREFS);
        portNumMqttTextBox = (EditTextPreference) getPreferenceScreen().findPreference(PORT_NUM_MQTT_PREFS);
        portNumRestTextBox = (EditTextPreference) getPreferenceScreen().findPreference(PORT_NUM_REST_PREFS);


        ipTextBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                myEditor = prefs.edit();
                Boolean rtnval = true;
                String newValueString = newValue.toString();
                boolean validIPv4 = newValueString.matches(IP_REG_EXPRESSION);
                boolean validIPv6 = newValueString.matches(IPV6_HEXCOMPRESSED_REGEX) || newValueString.matches(IPV6_REGEX);
                String portNumRest = prefs.getString(PrefsFragment.PORT_NUM_REST_PREFS, WSConfig.DEFAULT_REST_PORT);
                if (validIPv4 && newValueString.length() > 0) {
                    myEditor.putString(IP_FROM_PREFS, newValueString);
                    myEditor.commit();
                    WSConfig.ROOT_URL = "http://" + newValueString + ":" + portNumRest;// + "/" + WSConfig.BROKER_ROOT + "/";
                    WSConfig.WS_QUERY_URL = WSConfig.ROOT_URL + "Query";
                    ApiClient.updateEndPoint();
                } else if (validIPv6 && newValueString.length() > 0) {
                    myEditor.putString(IP_FROM_PREFS, newValueString);
                    myEditor.commit();
                    WSConfig.ROOT_URL = "http://[" + newValueString + "]:" + portNumRest;// + "/" + WSConfig.BROKER_ROOT + "/";
                    WSConfig.WS_QUERY_URL = WSConfig.ROOT_URL + "Query";
                    ApiClient.updateEndPoint();
                } else {
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

        portNumRestTextBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                myEditor = prefs.edit();

                String newPortNum = newValue.toString();
                myEditor.putString(PORT_NUM_REST_PREFS, newPortNum);
                myEditor.commit();

                // update URL
                String ip = prefs.getString(IP_FROM_PREFS, WSConfig.DEFAULT_IP);

                boolean validIPv4 = ip.matches(IP_REG_EXPRESSION);
                boolean validIPv6 = ip.matches(IPV6_HEXCOMPRESSED_REGEX) || ip.matches(IPV6_REGEX);
                if (validIPv4) {
                    WSConfig.ROOT_URL = "http://" + ip + ":" + newPortNum;
                    ApiClient.updateEndPoint();
                } else if (validIPv6) {
                    WSConfig.ROOT_URL = "http://[" + ip + "]:" + newPortNum;
                    ApiClient.updateEndPoint();
                }

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
}