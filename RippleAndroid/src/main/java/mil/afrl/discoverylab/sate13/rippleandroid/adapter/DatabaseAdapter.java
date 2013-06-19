package mil.afrl.discoverylab.sate13.rippleandroid.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        PATIENT, VITALS, INTERVENTIONS, TRAUMA
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
            "id INTEGER(4) PRIMARY KEY AUTOINCREMENT,\n" +
            "ip_addr VARCHAR(15) NOT NULL,\n" +
            "first_name VARCHAR(20),\n" +
            "last_name VARCHAR(20),\n" +
            "ssn VARCHAR(11),\n" +
            "age INTEGER(3),\n" +
            "sex VARCHAR(6),\n" +
            "nbc_contamination INTEGER(1),\n" +
            "type VARCHAR(10));";

    private static final int DATABASE_VERSION = 1;
    private final Context context;

    /**
     * Helper instance for accessing the SQLlite database
     */
    private DatabaseHelper helper;

    public DatabaseAdapter(Context context) {
        this.helper = new DatabaseHelper(context);
        this.context = context;
    }

    /**
     * Inserts all data associated with a scan into the table storing Patient
     * information
     *
     * @return true, if insertion was successful - false, otherwise
     */
    public synchronized boolean storeScanData(int id,
                                              String ip_addr,
                                              String first_name,
                                              String last_name,
                                              String ssn,
                                              int age,
                                              String sex,
                                              int nbc_contamination,
                                              String type) {
        int numrows = 9;
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();

        ContentValues map = new ContentValues(numrows);
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
        Log.d(Common.LOG_TAG,
              TableType.PATIENT.name() + " table insertion - " + numrows + " rows inserted");

        return true;
    }

    /**
     * Gets all information for a patient specified by their id as a string.
     *
     * @return
     */
    public synchronized String getPatientString(int id) {
        StringBuilder str = new StringBuilder();

        // query database
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(true, TableType.PATIENT.name(), null,
                                 "ID = " + Integer.toString(id),
                                 null, null, null, null, null);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {

            str.append(Integer.toString(cursor.getInt(0)));
            str.append(", ");
            for (int i = 1; i < 4; i++) {
                str.append(cursor.getString(i));
                str.append(", ");
            }

            str.append(Integer.toString(cursor.getInt(4)));
            str.append(", ");

            str.append(cursor.getString(5));
            str.append(", ");

            str.append(Integer.toString(cursor.getInt(6)));

        }
        cursor.close();
        db.close();

        return str.toString();
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
        SQLiteDatabase db = helper.getWritableDatabase();
        switch (type) {
            case PATIENT:
                db.execSQL("DELETE FROM " + TableType.PATIENT.name());
                Log.d(Common.LOG_TAG,
                      TableType.PATIENT.name() + " table clear finished - all rows removed");
                db.close();
                return true;
            default:
                Log.e(Common.LOG_TAG, "Table type not supported for clear module.");
                db.close();
                return false;
        }
    }

    /**
     * Helper class for creating and accessing the database. If database is
     * accessed for the first time the schema will be initialized by creating
     * the required tables.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(Common.LOG_TAG, "Creating SQlite " + TableType.PATIENT.name() + " table");
            db.execSQL(PATIENT_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(Common.LOG_TAG, "Upgrading database from version "
                                  + oldVersion + " to " + newVersion
                                  + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TableType.PATIENT.name());
            onCreate(db);
        }
    }

}
