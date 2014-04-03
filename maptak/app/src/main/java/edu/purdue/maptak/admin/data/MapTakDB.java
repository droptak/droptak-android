package edu.purdue.maptak.admin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.internal.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.purdue.maptak.admin.activities.MainActivity;

public class MapTakDB extends SQLiteOpenHelper {

    /** Database name and version */
    private Context context;
    public static final String DB_NAME = "database_cached_taks";
    public static final int DB_VERSION = 1;

    /** Tables */
    public static final String TABLE_MAPS = "t_maps";
    public static final String TABLE_MAPS_ADMINS = "t_maps_admins";
    public static final String TABLE_TAKS = "t_taks";

    /** Columns - TABLE_MAPS */
    public static final String MAP_ID = "_id";
    public static final String MAP_LABEL = "map_label";

    /** Columns - TABLE_MAPS_ADMINS */
    public static final String MAPADMINS_ID = "_id";
    public static final String MAPADMINS_MAP_ID = "map_id";
    public static final String MAPADMINS_NAME = "name";

    /** Columns - TABLE_TAKS */
    public static final String TAK_ID = "_id";
    public static final String TAK_MAP_ID = "map_id";
    public static final String TAK_LABEL = "tak_label";
    public static final String TAK_LAT = "tak_lat";
    public static final String TAK_LNG = "tak_lng";

    /** Default constructor */
    public MapTakDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /** Called when the database is first created. */
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Strings which create tables
        Log.d(MainActivity.LOG_TAG, "Creating new database.");

        String create_table_maps = "CREATE TABLE " + TABLE_MAPS + " (" +
                MAP_ID + " TEXT, " +
                MAP_LABEL + " TEXT );";

        String create_table_maps_admins = "CREATE TABLE " + TABLE_MAPS_ADMINS + " (" +
                MAPADMINS_ID + " TEXT, " +
                MAPADMINS_MAP_ID + " TEXT, " +
                MAPADMINS_NAME + " TEXT );";

        String create_table_taks = "CREATE TABLE " + TABLE_TAKS + " ( " +
                TAK_ID + " TEXT, " +
                TAK_MAP_ID + " TEXT, " +
                TAK_LABEL + " TEXT, " +
                TAK_LAT + " DOUBLE, " +
                TAK_LNG + " DOUBLE );";

        // Create the tables from the strings provided
        sqLiteDatabase.execSQL(create_table_maps);
        sqLiteDatabase.execSQL(create_table_maps_admins);
        sqLiteDatabase.execSQL(create_table_taks);

        sqLiteDatabase.close();
    }

    /** Called when the database is upgraded from one DB_VERSION to the next */
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.d(MainActivity.LOG_TAG, "Upgrading database from " + i + " to " + i2);
    }

    /** Destroys the local database and creates a new one. */
    public void destroy() {
        Log.d(MainActivity.LOG_TAG, "Dropping all tables in database.");
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            db.execSQL("DROP TABLE " + TABLE_MAPS);
            db.execSQL("DROP TABLE " + TABLE_MAPS_ADMINS);
            db.execSQL("DROP TABLE " + TABLE_TAKS);
            onCreate(db);
        }

    }

    /** Adds a map and all of its taks to the database.
     *  Does no sanity checking for if the map already exists. */
    public void addMap(MapObject map) {
        // Add the map to the local database
        Log.d(MainActivity.LOG_TAG, "Adding map to database.");
        Log.d(MainActivity.LOG_TAG, "\tID: " + map.getID() + "\tName: " + map.getLabel());

        ContentValues values = new ContentValues();
        values.put(MAP_ID, map.getID().toString());
        values.put(MAP_LABEL, map.getLabel());

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            getWritableDatabase().insert(TABLE_MAPS, null, values);
        }


        // The map also contains taks, so add those as well
        for (TakObject t : map.getTakList()) {
            addTak(t, map.getID());
        }
    }

    /** Adds a new tak for a given map to the database.
     *  Does no sanity checking for whether or not the tak already exists or if the map it
     *  pertains to even exists. */
    public void addTak(TakObject tak, MapID map) {
        // Add the tak to the database
        Log.d(MainActivity.LOG_TAG, "Adding tak to database with ID: " + tak.getID());

        ContentValues values = new ContentValues();
        values.put(TAK_ID, tak.getID().toString());
        values.put(TAK_MAP_ID, map.toString());
        values.put(TAK_LABEL, tak.getLabel());
        values.put(TAK_LAT, tak.getLatitude());
        values.put(TAK_LNG, tak.getLongitude());

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            getWritableDatabase().insert(TABLE_TAKS, null, values);
        }

    }

    /** Adds an administrator ID to be associated with a given map */
    public void addAdmin(UserID admin, MapID map) {
        Log.d(MainActivity.LOG_TAG, "Adding administrator " + admin.getName() + " to the db.");

        ContentValues values = new ContentValues();
        values.put(MAPADMINS_ID, admin.getID());
        values.put(MAPADMINS_MAP_ID, map.toString());
        values.put(MAPADMINS_NAME, admin.getName());

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.insert(TABLE_MAPS_ADMINS, null, values);
        }

    }

    /** Changes the ID of a map associated with "oldID" to "newID" */
    public void setMapID(MapID oldID, MapID newID) {
        Log.d(MainActivity.LOG_TAG, "Modifying mapID from " + oldID + " to " + newID);
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_MAPS + " SET " + MAP_ID + " = " + newID + " WHERE " + MAP_ID + " = " + oldID + ";");
        }

    }

    /** Deletes a map associated with a given map ID from the local database.
     *  Returns true if successful, otherwise false */
    public void deleteMap(MapID map) {
        Log.d(MainActivity.LOG_TAG, "Deleting map from database with ID: " + map);
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("DELETE FROM " + TABLE_MAPS + " WHERE " + MAP_ID + "=\"" + map.toString() + "\";");
        }

    }

    /** Removes a tak associated with a given ID from the local database.
     *  Returns true if successful, otherwise false. */
    public void deleteTak(TakID tak) {
        Log.d(MainActivity.LOG_TAG, "Deleting tak from database with ID: " + tak);
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("DELETE FROM " + TABLE_TAKS + " WHERE " + TAK_ID + "=\"" + tak.toString() + "\";");
        }

    }

    /** Deletes an administrator with the given ID from the local database */
    public void deleteAdmin(UserID admin) {
        Log.d(MainActivity.LOG_TAG, "Deleting administartor " + admin.getName() + " from the local database");

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("DELETE FROM " + TABLE_MAPS_ADMINS + " WHERE " + MAPADMINS_ID + "=\"" + admin.getID() + "\"");
        }

    }

    /** Returns a list of the maps the user has cached on the device. */
    public List<MapObject> getUsersMaps() {
        List<MapObject> results = new ArrayList<MapObject>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        if (db != null) {
            c = db.query(TABLE_MAPS, null, null, null, null, null, null);
        }

        // Statically generate all the column indexes as a performance improvement
        int COL_MAP_ID = c.getColumnIndex(MAP_ID);
        int COL_MAP_LABEL = c.getColumnIndex(MAP_LABEL);

        if (c.moveToFirst()) {
            do {
                // Create the ID
                String id = c.getString(COL_MAP_ID);
                MapID mapID = new MapID(id);

                // Create the label
                String label = c.getString(COL_MAP_LABEL);

                // Get the taks for the map
                List<TakObject> taks = getTaks(mapID);

                // Create the map object
                MapObject map = new MapObject(label, mapID, taks, false);

                // Add to the list
                results.add(map);

            } while (c.moveToNext());
        }


        return results;
    }

    /** Returns a specific map with a given UUID, or null if it doesn't exist in the cache */
    public MapObject getMap(MapID mapID) {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery(
                    "SELECT * FROM " + TABLE_MAPS + " WHERE " + MAP_ID + "=\"" + mapID.toString() + "\";", null);
            if (c.moveToFirst()) {
                String id = c.getString(c.getColumnIndex(MAP_ID));
                String label = c.getString(c.getColumnIndex(MAP_LABEL));
                List<TakObject> taks = getTaks(mapID);
                MapObject map = new MapObject(label, mapID, taks, false);
                return map;
            }
        }

        return null;
    }

    /** Returns a list of taks associated with a given mapid */
    public List<TakObject> getTaks(MapID mapID) {

        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery(
                    "SELECT * FROM " + TABLE_TAKS + " WHERE " + TAK_MAP_ID + "=\"" + mapID.toString() + "\";", null);

            List<TakObject> results = new LinkedList<TakObject>();
            if (c.moveToFirst()) {
                do {
                    String takID = c.getString(c.getColumnIndex(TAK_ID));
                    String takLabel = c.getString(c.getColumnIndex(TAK_LABEL));
                    double takLat = c.getDouble(c.getColumnIndex(TAK_LAT));
                    double takLng = c.getDouble(c.getColumnIndex(TAK_LNG));
                    TakObject t = new TakObject(new TakID(takID), takLabel, takLat, takLng);
                    results.add(t);
                } while (c.moveToNext());
            }


            return results;
        }


        return null;
    }

    /** Returns a specific tak with a given ID, or null if it doesn't exist in the cache
     *  Returns null if the object does not exist in the database. */
    public TakObject getTak(TakID takID) {

        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery(
                    "SELECT * FROM " + TABLE_TAKS + " WHERE " + TAK_ID + "\"" + takID.toString() + "\";", null);

            if (c.moveToFirst()) {
                String takIDStr = c.getString(c.getColumnIndex(TAK_ID));
                String takLabel = c.getString(c.getColumnIndex(TAK_LABEL));
                double takLat = c.getDouble(c.getColumnIndex(TAK_LAT));
                double takLng = c.getDouble(c.getColumnIndex(TAK_LNG));
                TakObject obj = new TakObject(new TakID(takIDStr), takLabel, takLat, takLng);


                return obj;
            }
        }


        return null;
    }

    /** Returns all of the administrators associated with a given map.
     *  Returns null if a grevious error has occured. Returns an empty list if no admins exist. */
    public List<UserID> getAdmins(MapID mapID) {

        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery(
                    "SELECT * FROM " + TABLE_MAPS_ADMINS + " WHERE " + MAPADMINS_MAP_ID + "=\"" + mapID.toString() + "\"", null);

            List<UserID> results = new LinkedList<UserID>();
            if (c.moveToFirst()) {
                do {
                    String userID = c.getString(c.getColumnIndex(MAPADMINS_ID));
                    String userName = c.getString(c.getColumnIndex(MAPADMINS_NAME));
                    UserID uid = new UserID(userID, userName);
                    results.add(uid);
                } while (c.moveToNext());
            }


            return results;
        }


        return null;
    }


}
