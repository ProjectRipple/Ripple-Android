package com.discoverylab.ripple.android.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import java.lang.Thread;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Message;
import android.widget.Button;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.NfcManager;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.ActionBarActivity;

import com.discoverylab.ripple.android.R;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class NfcActivity extends Activity {

    NfcAdapter nfcAdapter;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc2);
        //t = (TextView) findViewById(R.id.textView2);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC Available", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "NFC Not Available", Toast.LENGTH_LONG).show();
        }

    }
    /*
    @Override
    protected void onStart(){
        super.onStart();

        enableForegroundDispatchSystem();
    }
    */

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }
    /*
    @Override
    protected void onStop() {
        super.onStop();

        disableForegroundDispatchSystem();
    }*/

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
            //t.setText("Please Wait...");

            android.nfc.Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = createNdefMessage("channel=26PanID=234AIP=3FFEF2000234AB00012345678901ABCDENC=78FCF2000234AB00013352678901AB2B");


            writeNdefMessage(tag, ndefMessage);

        }
    }

    /*@Override // use to read tags
    protected void onNewIntent(android.content.Intent intent) {

        Toast.makeText(this, "NFC intent received:", Toast.LENGTH_LONG).show();

        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);

        super.onResume();
    }

    @Override
    protected void onPause() {

        nfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }*/



    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, NfcActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {

        nfcAdapter.disableForegroundDispatch(this);

    }

    private void formatTag(android.nfc.Tag tag, NdefMessage ndefMessage){

        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if(ndefFormatable == null){
                Toast.makeText(this, "Tag Not NDEF formatable:(", Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "NFC Tag Written", Toast.LENGTH_SHORT).show();


        }catch(Exception e){
            Log.e("formatTag", e.getMessage());
        }

    }

    private void writeNdefMessage(android.nfc.Tag tag, NdefMessage ndefMessage){

        try{

            if(tag == null){
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef == null){
                formatTag(tag, ndefMessage);
            }
            else{
                ndef.connect();
                if (!ndef.isWritable()){
                    Toast.makeText(this, "Tag Not writeable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this, "Tag Written!", Toast.LENGTH_SHORT).show();
                //t.setText("Tag Written!");
                //SystemClock.sleep(2000);
                //t.setText("Approach the NFC antenna on a Ripple Sensor Pack");
            }


        } catch (Exception e){
            Log.e("writeNdefMessage", e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content){
        try{
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1+languageSize+textLength);

            payload.write((byte) languageSize & 0x1F);
            payload.write(language, 0, languageSize);
            payload.write(text ,0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e){
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content){

        NdefRecord ndefRecord = createTextRecord(content); // creating a message just creates one record

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] { ndefRecord });

        return ndefMessage;

    }
}