package mil.afrl.discoverylab.sate13.rippleandroid.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.achartengine.model.XYSeries;

import java.io.BufferedWriter;
import java.io.IOException;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;

/**
 * Created by Matt on 6/19/13.
 * <p/>
 * Database Adapter class for accessing an SQLLite database located in the application's
 * private storage. Partially taken from the CarMA project source code.
 * <p/>
 * Flach, T., Mishra, N., Pedrosa, L., Riesz, C., & Govindan, R. (2011, November).
 * CarMA: towards personalized automotive tuning.
 * In Proceedings of the 9th ACM Conference on Embedded Networked Sensor Systems (pp. 135-148). ACM.
 *
 * @author Tobias Flach
 * @author Matt McCartney
 */
public class DatabaseAdapter {

    /**
     * List of supported table types. Each table type is assigned a different
     * table format and may use different methods for accessing and exporting
     * the data.
     */
    public static enum TableType {
        PATIENT, VITAL, INTERVENTION, TRAUMA
    }

    /**
     * Column descriptors for the Patient table
     */
    public static enum PatientTableColumn {
        ID, IP_ADDR, FISRT_NAME, LAST_NAME, SSN, AGE, SEX, NBC_CONTAMINATION, TYPE
    }

    /**
     * Name of the database containing all tables associated with this project
     */
    private static final String DATABASE_NAME = "RIPPLE";

    /**
     * SQL table creation statement for the Patient table.
     */
    private static final String PATIENT_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TableType.PATIENT.name() + "(\n" +
            "ID INTEGER,\n" +
            "IP_ADDR TEXT NOT NULL,\n" +
            "FIRST_NAME TEXT,\n" +
            "LAST_NAME TEXT,\n" +
            "SSN TEXT,\n" +
            "AGE INTEGER,\n" +
            "SEX VARCHAR,\n" +
            "NBC_CONTAMINATION INTEGER,\n" +
            "TYPE TEXT," +
            "PRIMARY KEY(ID));";

    /**
     * SQL table creation statement for the Patient table.
     */
    private static final String VITAL_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TableType.VITAL.name() + "(\n" +
            "VID INTEGER,\n" +
            "TIME INTEGER,\n" +
            "SENSOR_TYPE TEXT,\n" +
            "IP_ADDR TEXT,\n" +
            "VALUE_TYPE TEXT,\n" +
            "VALUE REAL," +
            "PRIMARY KEY(VID));";

    private static final int DATABASE_VERSION = 1;
    private final Context context;

    /**
     * Helper instance for accessing the SQLlite database
     */
    private DatabaseHelper helper;

    private static DatabaseAdapter self;

    /**
     * Get instance of DatabaseAdapter
     *
     * @param context Valid Context on first call, may be null on all later calls
     * @return Instance of DatabaseAdapter or null if DatabaseAdapter cannot be created due to null Context
     */
    public static DatabaseAdapter getInstance(Context context) {
        if (self == null && context != null) {
            // Use global application context instead of passed context
            self = new DatabaseAdapter(context.getApplicationContext());
        }
        return self;
    }

    public static DatabaseAdapter getInstance() {
        return (self != null) ? self : null;
    }

    private DatabaseAdapter(Context context) {
        this.helper = new DatabaseHelper(context);
        this.context = context;
    }

    /**
     * Inserts all data associated with a Patient into the table storing Patient
     * information
     *
     * @return true, if insertion was successful - false, otherwise
     */
    public synchronized boolean storePatientData(int id,
                                                 String ip_addr,
                                                 String first_name,
                                                 String last_name,
                                                 String ssn,
                                                 int age,
                                                 String sex,
                                                 int nbc_contamination,
                                                 String type) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();

        ContentValues map = new ContentValues(9);
        map.put("ID", id);
        map.put("IP_ADDR", ip_addr);
        map.put("FIRST_NAME", first_name);
        map.put("LAST_NAME", last_name);
        map.put("SSN", ssn);
        map.put("AGE", age);
        map.put("SEX", sex);
        map.put("NBC_CONTAMINATION", nbc_contamination);
        map.put("TYPE", type);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        //Log.d(Common.LOG_TAG, TableType.PATIENT.name() + " table insertion - " + 1 + " row inserted");

        return true;
    }

    /**
     * Inserts all data associated with a vital row into the table storing Patient Vital
     * information
     *
     * @return true, if insertion was successful - false, otherwise
     */
    public synchronized boolean storeVitalData(int time,
                                               String sensor_type,
                                               String ip_addr,
                                               String value_type,
                                               double value) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();

        ContentValues map = new ContentValues();
        map.put("TIME", time);
        map.put("SENSOR_TYPE", sensor_type);
        map.put("IP_ADDR", ip_addr);
        map.put("VALUE_TYPE", value_type);
        map.put("VALUE", value);

        if (db.insert(TableType.VITAL.name(), null, map) == -1) {
            Log.e(Common.LOG_TAG, "Failed to insert: " + map.toString());
            db.endTransaction();
            db.close();
            return false;
        } else {
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            //Log.d(Common.LOG_TAG, TableType.VITAL.name() + " table insertion - " + map.toString());
            return true;
        }
    }

    /**
     * For a specific patient ip address and sensor type, store all time-value pairs in the
     * provided XYSeries
     * <p/>
     * Note: Does not check for duplicate rows in the XYSeries
     *
     * @return false if the provided series is null or if an empty set is returned by the query
     */
    public synchronized boolean getVitalXY(String ip_addr,
                                           String sensor_type,
                                           XYSeries data) {
        if (data == null) {
            return false;
        }
        int preItemCount = data.getItemCount();

        // query database
        SQLiteDatabase db = helper.getReadableDatabase();
        String where = "IP_ADDR = '" + ip_addr + "' AND SENSOR_TYPE = '" + sensor_type + "'";
        Cursor cursor = db.query(true,                                  // Distinct
                                 TableType.VITAL.name(),                // Table
                                 new String[]{"TIME", "VALUE"},         // Columns
                                 where,                                 // Selection
                                 null,                                  // Selection Args
                                 null,                                  // Group By
                                 null,                                  // Having
                                 "TIME",                                // Order By
                                 null);                                 // Limit
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            data.add((double) cursor.getInt(cursor.getColumnIndex("TIME")),
                     cursor.getDouble(cursor.getColumnIndex("VALUE")));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return (data.getItemCount() != preItemCount);
    }


    /**
     * Forwards the contents of a database to a writer instance in CSV format.
     * The actual contents forwarded depend on the table type.
     *
     * @param type
     * @param writer
     * @return true, if export was successful - false, otherwise
     */
    public synchronized boolean export(TableType type, BufferedWriter writer) {

        if (writer == null) {
            Log.e(Common.LOG_TAG, "No writer for export available - aborted.");
            return false;
        }

        try {
            switch (type) {
                case PATIENT:
                    return exportPatientTable(writer);
                default:
                    Log.e(Common.LOG_TAG, "Table type not supported for export module.");
                    return false;
            }
        } catch (IOException e) {
            Log.e(Common.LOG_TAG, "Cannot export table: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exports the contents of the patients table to a writer instance in CSV
     * format. The header row identifies column contents:
     *
     * @param writer
     * @return true, if export was successful - false, otherwise
     * @throws IOException
     */
    private synchronized boolean exportPatientTable(BufferedWriter writer) throws IOException {

        // Create header
        String header = "# ID, IP_ADDR, FISRT_NAME, LAST_NAME, SSN, AGE, SEX, NBC_CONTAMINATION, TYPE\n";
        writer.write(header);

        // Fetch data from SESSION table
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TableType.PATIENT.name(), null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            writer.write(Integer.toString(cursor.getInt(0)) + ",\""
                         + cursor.getString(1) + ",\""
                         + cursor.getString(2) + ",\""
                         + cursor.getString(3) + ",\""
                         + Integer.toString(cursor.getInt(4)) + ",\""
                         + cursor.getString(5) + ",\""
                         + Integer.toString(cursor.getInt(6)) + "\"\n");
            cursor.moveToNext();
        }
        writer.flush();
        cursor.close();
        db.close();

        return true;
    }

    public synchronized boolean clear(TableType type) {
        boolean res = true;
        SQLiteDatabase db = helper.getWritableDatabase();
        switch (type) {
            case PATIENT:
                db.execSQL("DELETE FROM " + TableType.PATIENT.name());
                Log.d(Common.LOG_TAG, TableType.PATIENT.name() + " table clear finished - all rows removed");
            case VITAL:
                db.execSQL("DELETE FROM " + TableType.VITAL.name());
                Log.d(Common.LOG_TAG, TableType.VITAL.name() + " table clear finished - all rows removed");
            default:
                Log.e(Common.LOG_TAG, "Table type not supported for clear module.");
                res = false;
        }
        db.close();
        return res;
    }

    /**
     * Helper class for creating and accessing the database. If database is
     * accessed for the first time the schema will be initialized by creating
     * the required tables.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        SQLiteDatabase database;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(Common.LOG_TAG, "Constructing DB Helper");
            database = getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            database = db;

            Log.d(Common.LOG_TAG, "Creating SQlite " + TableType.PATIENT.name() + " table");
            db.execSQL(PATIENT_TABLE_CREATE);
            Log.d(Common.LOG_TAG, "Creating SQlite " + TableType.VITAL.name() + " table");
            db.execSQL(VITAL_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(Common.LOG_TAG, "Upgrading database from version "
                                  + oldVersion + " to " + newVersion
                                  + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TableType.PATIENT.name());
            db.execSQL("DROP TABLE IF EXISTS " + TableType.VITAL.name());
            onCreate(db);
        }
    }

}
