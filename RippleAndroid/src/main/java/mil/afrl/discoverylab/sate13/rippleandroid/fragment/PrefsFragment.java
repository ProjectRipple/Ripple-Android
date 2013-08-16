package mil.afrl.discoverylab.sate13.rippleandroid.fragment;

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

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;


public class PrefsFragment extends PreferenceFragment {

    private SharedPreferences prefs;
    private Editor myEditor;
    private EditTextPreference ipTextBox, portNumTextBox, textPortNumTextBox;
    public static final String IP_REG_EXPRESSION = "^((1\\d{2}|2[0-4]\\d|25[0-5]|\\d?\\d)\\.){3}(?:1\\d{2}|2[0-4]\\d|25[0-5]|\\d?\\d)$";
    private static final String IPV6_REG_EXPRESSION = "^([\\dA-F]{1,4}:|((?=.*(::))(?!.*\\3.+\\3))\\3?)([\\dA-F]{1,4}(\\3|:\\b)|\\2){5}(([\\dA-F]{1,4}(\\3|:\\b|$)|\\2){2}|(((2[0-4]|1\\d|[1-9])?\\d|25[0-5])\\.?\\b){4})\\z";

    private static final String IPV6_HEX4DECCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?) ::((?:[0-9A-Fa-f]{1,4}:)*)(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
    private static final String IPV6_6HEX4DEC_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}:){6,6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
    public static final String IPV6_HEXCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)\\z";
    public static final String IPV6_REGEX = "\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z";

    public static final String IP_FROM_PREFS = "ipAddressPref";
    private static final String PORT_NUM_PREFS = "portNumberPref";
    private static final String TEXT_PORT_PREFS = "portTexturePref";


    @Override
    /**
     * The onCreate method handles thing when starting this activity,
     * mainly display the activity_settings.xml.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(!prefs.contains(IP_FROM_PREFS)){
            // set preference if not already there
            Log.d(Common.LOG_TAG, "Setting default ip");
            myEditor = prefs.edit();
            myEditor.putString(IP_FROM_PREFS, WSConfig.DEFAULT_IP);
            myEditor.commit();
        }


        addPreferencesFromResource(R.layout.fragment_settings);
        ipTextBox = (EditTextPreference) getPreferenceScreen().findPreference(IP_FROM_PREFS);
        portNumTextBox = (EditTextPreference) getPreferenceScreen().findPreference(PORT_NUM_PREFS);
        textPortNumTextBox = (EditTextPreference) getPreferenceScreen().findPreference(TEXT_PORT_PREFS);


        ipTextBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                myEditor = prefs.edit();
                Boolean rtnval = true;
                String newValueString = newValue.toString();
                boolean validIPv4 = newValueString.matches(IP_REG_EXPRESSION);
                boolean validIPv6 = newValueString.matches(IPV6_HEXCOMPRESSED_REGEX) || newValueString.matches(IPV6_REGEX);
                if (validIPv4 && newValueString.length() > 0) {
                    myEditor.putString(IP_FROM_PREFS, newValueString);
                    myEditor.commit();
                    WSConfig.ROOT_URL = "http://" + newValueString + ":" + WSConfig.BROKER_PORT + "/" + WSConfig.BROKER_ROOT + "/";
                    WSConfig.WS_QUERY_URL = WSConfig.ROOT_URL + "Query";
                } else if (validIPv6 && newValueString.length() > 0) {
                    myEditor.putString(IP_FROM_PREFS, newValueString);
                    myEditor.commit();
                    WSConfig.ROOT_URL = "http://[" + newValueString + "]:" + WSConfig.BROKER_PORT + "/" + WSConfig.BROKER_ROOT + "/";
                    WSConfig.WS_QUERY_URL = WSConfig.ROOT_URL + "Query";
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
    }
}