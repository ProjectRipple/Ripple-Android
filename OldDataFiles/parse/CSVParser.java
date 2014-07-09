package com.discoverylab.ripple.android.parse;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.adapter.DatabaseAdapter;
import com.discoverylab.ripple.android.adapter.StorageAdapter;

public class CSVParser {

    public static boolean initialized = false;

    public static synchronized boolean initializeCSVParser(Context context) {
        boolean result = true;

        if (!initialized) {

            Log.v(Common.LOG_TAG, "Initializing CSV Parser");

            // load data from ECG CSV file
            StorageAdapter store = new StorageAdapter(context);
            BufferedReader reader = new BufferedReader(new InputStreamReader(store.getInputStream(Common.ECG_CSV)));
            result = readCSVFileData(reader);

            try {
                reader.close();
            } catch (IOException e) {
                Log.e(Common.LOG_TAG, "Cannot close reader - error message: " + e.getMessage());
                result = false;
            }
            initialized = result;
        }
        return result;
    }

    public static synchronized boolean readCSVFileData(BufferedReader reader) {
        Log.v(Common.LOG_TAG, "Reading CSV file data from buffered reader");

        boolean result = true;
        int index = 0;
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                index++;
                // Ignore Comments
                if (line.trim().length() == 0 || line.startsWith("#")) {
                    continue;
                }

                if (!parseCSVData(line)) {
                    Log.e(Common.LOG_TAG, "Cannot parse line " + index + " - Is: " + line);
                    result = false;
                    break;
                }
            }
        } catch (IOException e) {
            Log.e(Common.LOG_TAG, "Cannot read CSV file data - error message: " + e.getMessage());
            result = false;
        }

        Log.v(Common.LOG_TAG, "Done reading CSV file data from buffered reader");
        return result;
    }

    private static boolean parseCSVData(String line) {
        String[] tokens = line.split(String.valueOf(Common.CSV_DELIMITER));

        int timestamp = (int) Double.parseDouble(tokens[3]);    // Shimmer 1/Timestamp/CAL/mSecs
        double value = Double.parseDouble(tokens[4]);           // Shimmer 1/ECG RA-LL/CAL/mVolts*

        return DatabaseAdapter.getInstance().storeVitalData(timestamp, "ECG", "localhost", "RA-LL", value);
    }

}
