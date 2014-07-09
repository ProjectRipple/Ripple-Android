package com.discoverylab.ripple.android.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import com.discoverylab.ripple.android.util.Common;

/**
 * Adapter class for accessing external or internal storage.
 * Allows to write data to the storage via a buffered writer (e.g. used by
 * DatabaseAdapter to export table data to a CSV file) or to read from the storage
 * using a buffered reader (e.g. used by image writer to fetch the modified binary
 * file and upload routine)
 *
 * @author Tobias Flach
 */
public class StorageAdapter {

    private final Context context;

    public StorageAdapter(Context context) {
        this.context = context;
    }

    /**
     * Gets a file instance associated with the file name (if the file does not
     * exist yet it is created)
     *
     * @param filename
     * @return File instance, or null if an error occurred
     */
    public File getFile(String filename) {
        if (!isAvailable()) {
            Log.e(Common.LOG_TAG, "External storage is not mounted/available.");
            return null;
        }

        File path = context.getExternalFilesDir(null);
        if (path == null) {
            Log.e(Common.LOG_TAG, "Cannot access external files directory.");
            return null;
        }

        return new File(path, filename);
    }

    /**
     * Gets a buffered writer for writing to a file in the external storage.
     *
     * @param filename
     * @return
     */
    public BufferedWriter getBufferedWriter(String filename) {
        FileOutputStream stream = getFileOutputStream(filename);
        if (stream == null) {
            return null;
        } else {
            return new BufferedWriter(new OutputStreamWriter(stream));
        }
    }

    public FileOutputStream getFileOutputStream(String filename) {
        Log.i(Common.LOG_TAG, "Creating buffered writer for file: " + filename);

        File file = getFile(filename);
        if (file == null) {
            Log.e(Common.LOG_TAG, "Cannot create buffered writer for null-file.");
            return null;
        }

        // create new empty file
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.e(Common.LOG_TAG, "Cannot create new file: " + e.getMessage());
            return null;
        }

        // create output stream
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(Common.LOG_TAG, "Cannot create FileOutputStream: " + e.getMessage());
            return null;
        }

        return stream;
    }

    public InputStream getInputStream(int resource) {
        InputStream stream = context.getResources().openRawResource(resource);
        return stream;
    }

    public FileInputStream getFileInputStream(String filename) {
        Log.i(Common.LOG_TAG, "Creating buffered reader for file: " + filename);

        File file = getFile(filename);
        if (file == null || !file.exists()) {
            Log.e(Common.LOG_TAG, "Cannot create buffered reader for null/non-existent file.");
            return null;
        }

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(Common.LOG_TAG, "Cannot create FileInputStream: " + e.getMessage());
            return null;
        }

        return stream;
    }

    /**
     * Checks if the external storage is available.
     *
     * @return true, if the storage is available (mounted) - false, otherwise
     */
    public boolean isAvailable() {
        return Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState());
    }
}
