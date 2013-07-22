package mil.afrl.discoverylab.sate13.rippleandroid.data.provider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.util.ColumnMetadata;

/**
 * This class was generated by the ContentProviderCodeGenerator project made by Foxykeep
 * <p>
 * (More information available https://github.com/foxykeep/ContentProviderCodeGenerator)
 */
public abstract class RippleContent {

    public static final Uri CONTENT_URI = Uri.parse("content://" + RippleProvider.AUTHORITY);

    private RippleContent() {
    }

    /**
     * Created in version 1
     */
    public static final class db_patient extends RippleContent {

        private static final String LOG_TAG = db_patient.class.getSimpleName();

        public static final String TABLE_NAME = "db_patient";
        public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/ripple-db_patient";
        public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/ripple-db_patient";

        public static final Uri CONTENT_URI = Uri.parse(RippleContent.CONTENT_URI + "/" + TABLE_NAME);

        public static enum Columns implements ColumnMetadata {
            PID(BaseColumns._ID, "integer"),
            IP_ADDR("ip_addr", "text"),
            FIRST_NAME("first_name", "text"),
            LAST_NAME("last_name", "text"),
            SSN("ssn", "text"),
            DOB("dob", "text"),
            SEX("sex", "text"),
            NBC_CONTAMINATION("nbc_contamination", "text"),
            TYPE("type", "text");

            private final String mName;
            private final String mType;

            private Columns(String name, String type) {
                mName = name;
                mType = type;
            }

            @Override
            public int getIndex() {
                return ordinal();
            }

            @Override
            public String getName() {
                return mName;
            }

            @Override
            public String getType() {
                return mType;
            }
        }

        public static final String[] PROJECTION = new String[] {
                Columns.PID.getName(),
                Columns.IP_ADDR.getName(),
                Columns.FIRST_NAME.getName(),
                Columns.LAST_NAME.getName(),
                Columns.SSN.getName(),
                Columns.DOB.getName(),
                Columns.SEX.getName(),
                Columns.NBC_CONTAMINATION.getName(),
                Columns.TYPE.getName()
        };

        private db_patient() {
            // No private constructor
        }

        public static void createTable(SQLiteDatabase db) {
            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_patient | createTable start");
            }
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + Columns.PID.getName() + " " + Columns.PID.getType()+ " PRIMARY KEY AUTOINCREMENT" + ", " + Columns.IP_ADDR.getName() + " " + Columns.IP_ADDR.getType() + ", " + Columns.FIRST_NAME.getName() + " " + Columns.FIRST_NAME.getType() + ", " + Columns.LAST_NAME.getName() + " " + Columns.LAST_NAME.getType() + ", " + Columns.SSN.getName() + " " + Columns.SSN.getType() + ", " + Columns.DOB.getName() + " " + Columns.DOB.getType() + ", " + Columns.SEX.getName() + " " + Columns.SEX.getType() + ", " + Columns.NBC_CONTAMINATION.getName() + " " + Columns.NBC_CONTAMINATION.getType() + ", " + Columns.TYPE.getName() + " " + Columns.TYPE.getType() + ");");

            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_patient | createTable end");
            }
        }

        // Version 1 : Creation of the table
        public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_patient | upgradeTable start");
            }

            if (oldVersion < 1) {
                Log.i(LOG_TAG, "Upgrading from version " + oldVersion + " to " + newVersion
                        + ", data will be lost!");

                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
                createTable(db);
                return;
            }


            if (oldVersion != newVersion) {
                throw new IllegalStateException("Error upgrading the database to version "
                        + newVersion);
            }

            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_patient | upgradeTable end");
            }
        }

        static String getBulkInsertString() {
            return new StringBuilder("INSERT INTO ").append(TABLE_NAME).append(" ( ").append(Columns.IP_ADDR.getName()).append(", ").append(Columns.FIRST_NAME.getName()).append(", ").append(Columns.LAST_NAME.getName()).append(", ").append(Columns.SSN.getName()).append(", ").append(Columns.DOB.getName()).append(", ").append(Columns.SEX.getName()).append(", ").append(Columns.NBC_CONTAMINATION.getName()).append(", ").append(Columns.TYPE.getName()).append(" ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)").toString();
        }

        static void bindValuesInBulkInsert(SQLiteStatement stmt, ContentValues values) {
            int i = 1;
            String value;
            value = values.getAsString(Columns.IP_ADDR.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.FIRST_NAME.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.LAST_NAME.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.SSN.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.DOB.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.SEX.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.NBC_CONTAMINATION.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.TYPE.getName());
            stmt.bindString(i++, value != null ? value : "");
        }
    }

    /**
     * Created in version 1
     */
    public static final class db_vital extends RippleContent {

        private static final String LOG_TAG = db_vital.class.getSimpleName();

        public static final String TABLE_NAME = "db_vital";
        public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/ripple-db_vital";
        public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/ripple-db_vital";

        public static final Uri CONTENT_URI = Uri.parse(RippleContent.CONTENT_URI + "/" + TABLE_NAME);

        public static enum Columns implements ColumnMetadata {
            VID(BaseColumns._ID, "integer"),
            PID("pid", "integer"),
            SERVER_TIMESTAMP("server_timestamp", "text"),
            SENSOR_TIMESTAMP("sensor_timestamp", "integer"),
            SENSOR_TYPE("sensor_type", "text"),
            VALUE_TYPE("value_type", "text"),
            VALUE("value", "integer");

            private final String mName;
            private final String mType;

            private Columns(String name, String type) {
                mName = name;
                mType = type;
            }

            @Override
            public int getIndex() {
                return ordinal();
            }

            @Override
            public String getName() {
                return mName;
            }

            @Override
            public String getType() {
                return mType;
            }
        }

        public static final String[] PROJECTION = new String[] {
                Columns.VID.getName(),
                Columns.PID.getName(),
                Columns.SERVER_TIMESTAMP.getName(),
                Columns.SENSOR_TIMESTAMP.getName(),
                Columns.SENSOR_TYPE.getName(),
                Columns.VALUE_TYPE.getName(),
                Columns.VALUE.getName()
        };

        private db_vital() {
            // No private constructor
        }

        public static void createTable(SQLiteDatabase db) {
            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_vital | createTable start");
            }
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + Columns.VID.getName() + " " + Columns.VID.getType()+ " PRIMARY KEY AUTOINCREMENT" + ", " + Columns.PID.getName() + " " + Columns.PID.getType() + ", " + Columns.SERVER_TIMESTAMP.getName() + " " + Columns.SERVER_TIMESTAMP.getType() + ", " + Columns.SENSOR_TIMESTAMP.getName() + " " + Columns.SENSOR_TIMESTAMP.getType() + ", " + Columns.SENSOR_TYPE.getName() + " " + Columns.SENSOR_TYPE.getType() + ", " + Columns.VALUE_TYPE.getName() + " " + Columns.VALUE_TYPE.getType() + ", " + Columns.VALUE.getName() + " " + Columns.VALUE.getType() + ");");

            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_vital | createTable end");
            }
        }

        // Version 1 : Creation of the table
        public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_vital | upgradeTable start");
            }

            if (oldVersion < 1) {
                Log.i(LOG_TAG, "Upgrading from version " + oldVersion + " to " + newVersion
                        + ", data will be lost!");

                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
                createTable(db);
                return;
            }


            if (oldVersion != newVersion) {
                throw new IllegalStateException("Error upgrading the database to version "
                        + newVersion);
            }

            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_vital | upgradeTable end");
            }
        }

        static String getBulkInsertString() {
            return new StringBuilder("INSERT INTO ").append(TABLE_NAME).append(" ( ").append(Columns.PID.getName()).append(", ").append(Columns.SERVER_TIMESTAMP.getName()).append(", ").append(Columns.SENSOR_TIMESTAMP.getName()).append(", ").append(Columns.SENSOR_TYPE.getName()).append(", ").append(Columns.VALUE_TYPE.getName()).append(", ").append(Columns.VALUE.getName()).append(" ) VALUES (?, ?, ?, ?, ?, ?)").toString();
        }

        static void bindValuesInBulkInsert(SQLiteStatement stmt, ContentValues values) {
            int i = 1;
            String value;
            stmt.bindLong(i++, values.getAsLong(Columns.PID.getName()));
            value = values.getAsString(Columns.SERVER_TIMESTAMP.getName());
            stmt.bindString(i++, value != null ? value : "");
            stmt.bindLong(i++, values.getAsLong(Columns.SENSOR_TIMESTAMP.getName()));
            value = values.getAsString(Columns.SENSOR_TYPE.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.VALUE_TYPE.getName());
            stmt.bindString(i++, value != null ? value : "");
            stmt.bindLong(i++, values.getAsLong(Columns.VALUE.getName()));
        }
    }

    /**
     * Created in version 1
     */
    public static final class db_intervention extends RippleContent {

        private static final String LOG_TAG = db_intervention.class.getSimpleName();

        public static final String TABLE_NAME = "db_intervention";
        public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/ripple-db_intervention";
        public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/ripple-db_intervention";

        public static final Uri CONTENT_URI = Uri.parse(RippleContent.CONTENT_URI + "/" + TABLE_NAME);

        public static enum Columns implements ColumnMetadata {
            IID(BaseColumns._ID, "integer"),
            PID("pid", "integer"),
            TYPE("type", "text"),
            DETAILS("details", "text");

            private final String mName;
            private final String mType;

            private Columns(String name, String type) {
                mName = name;
                mType = type;
            }

            @Override
            public int getIndex() {
                return ordinal();
            }

            @Override
            public String getName() {
                return mName;
            }

            @Override
            public String getType() {
                return mType;
            }
        }

        public static final String[] PROJECTION = new String[] {
                Columns.IID.getName(),
                Columns.PID.getName(),
                Columns.TYPE.getName(),
                Columns.DETAILS.getName()
        };

        private db_intervention() {
            // No private constructor
        }

        public static void createTable(SQLiteDatabase db) {
            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_intervention | createTable start");
            }
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + Columns.IID.getName() + " " + Columns.IID.getType()+ " PRIMARY KEY AUTOINCREMENT" + ", " + Columns.PID.getName() + " " + Columns.PID.getType() + ", " + Columns.TYPE.getName() + " " + Columns.TYPE.getType() + ", " + Columns.DETAILS.getName() + " " + Columns.DETAILS.getType() + ");");

            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_intervention | createTable end");
            }
        }

        // Version 1 : Creation of the table
        public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_intervention | upgradeTable start");
            }

            if (oldVersion < 1) {
                Log.i(LOG_TAG, "Upgrading from version " + oldVersion + " to " + newVersion
                        + ", data will be lost!");

                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
                createTable(db);
                return;
            }


            if (oldVersion != newVersion) {
                throw new IllegalStateException("Error upgrading the database to version "
                        + newVersion);
            }

            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_intervention | upgradeTable end");
            }
        }

        static String getBulkInsertString() {
            return new StringBuilder("INSERT INTO ").append(TABLE_NAME).append(" ( ").append(Columns.PID.getName()).append(", ").append(Columns.TYPE.getName()).append(", ").append(Columns.DETAILS.getName()).append(" ) VALUES (?, ?, ?)").toString();
        }

        static void bindValuesInBulkInsert(SQLiteStatement stmt, ContentValues values) {
            int i = 1;
            String value;
            stmt.bindLong(i++, values.getAsLong(Columns.PID.getName()));
            value = values.getAsString(Columns.TYPE.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.DETAILS.getName());
            stmt.bindString(i++, value != null ? value : "");
        }
    }

    /**
     * Created in version 1
     */
    public static final class db_trauma extends RippleContent {

        private static final String LOG_TAG = db_trauma.class.getSimpleName();

        public static final String TABLE_NAME = "db_trauma";
        public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/ripple-db_trauma";
        public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/ripple-db_trauma";

        public static final Uri CONTENT_URI = Uri.parse(RippleContent.CONTENT_URI + "/" + TABLE_NAME);

        public static enum Columns implements ColumnMetadata {
            TID(BaseColumns._ID, "integer"),
            PID("pid", "integer"),
            LOCATION("location", "text"),
            TYPE("type", "text"),
            STATUS("status", "text");

            private final String mName;
            private final String mType;

            private Columns(String name, String type) {
                mName = name;
                mType = type;
            }

            @Override
            public int getIndex() {
                return ordinal();
            }

            @Override
            public String getName() {
                return mName;
            }

            @Override
            public String getType() {
                return mType;
            }
        }

        public static final String[] PROJECTION = new String[] {
                Columns.TID.getName(),
                Columns.PID.getName(),
                Columns.LOCATION.getName(),
                Columns.TYPE.getName(),
                Columns.STATUS.getName()
        };

        private db_trauma() {
            // No private constructor
        }

        public static void createTable(SQLiteDatabase db) {
            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_trauma | createTable start");
            }
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + Columns.TID.getName() + " " + Columns.TID.getType()+ " PRIMARY KEY AUTOINCREMENT" + ", " + Columns.PID.getName() + " " + Columns.PID.getType() + ", " + Columns.LOCATION.getName() + " " + Columns.LOCATION.getType() + ", " + Columns.TYPE.getName() + " " + Columns.TYPE.getType() + ", " + Columns.STATUS.getName() + " " + Columns.STATUS.getType() + ");");

            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_trauma | createTable end");
            }
        }

        // Version 1 : Creation of the table
        public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_trauma | upgradeTable start");
            }

            if (oldVersion < 1) {
                Log.i(LOG_TAG, "Upgrading from version " + oldVersion + " to " + newVersion
                        + ", data will be lost!");

                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
                createTable(db);
                return;
            }


            if (oldVersion != newVersion) {
                throw new IllegalStateException("Error upgrading the database to version "
                        + newVersion);
            }

            if (RippleProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "db_trauma | upgradeTable end");
            }
        }

        static String getBulkInsertString() {
            return new StringBuilder("INSERT INTO ").append(TABLE_NAME).append(" ( ").append(Columns.PID.getName()).append(", ").append(Columns.LOCATION.getName()).append(", ").append(Columns.TYPE.getName()).append(", ").append(Columns.STATUS.getName()).append(" ) VALUES (?, ?, ?, ?)").toString();
        }

        static void bindValuesInBulkInsert(SQLiteStatement stmt, ContentValues values) {
            int i = 1;
            String value;
            stmt.bindLong(i++, values.getAsLong(Columns.PID.getName()));
            value = values.getAsString(Columns.LOCATION.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.TYPE.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.STATUS.getName());
            stmt.bindString(i++, value != null ? value : "");
        }
    }
}
