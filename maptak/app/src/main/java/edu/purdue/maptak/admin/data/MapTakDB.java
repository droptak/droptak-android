package edu.purdue.maptak.admin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    public static final String TABLE_TAK_METADATA = "t_taks_metadata";

    /** Columns - TABLE_MAPS */
    public static final String MAP_ID = "_id";
    public static final String MAP_LABEL = "map_label";
    public static final String MAP_ISPUBLIC = "map_ispublic";
    public static final String MAP_OWNER_ID = "map_owner_id";
    public static final String MAP_OWNER_STR = "map_owner_string";

    /** Columns - TABLE_MAPS_ADMINS */
    public static final String MAPADMINS_ID = "_id";
    public static final String MAPADMINS_NAME = "name";
    public static final String MAPADMINS_MAP_ID = "map_id";

    /** Columns - TABLE_TAKS */
    public static final String TAK_ID = "_id";
    public static final String TAK_MAP_ID = "map_id";
    public static final String TAK_LABEL = "tak_label";
    public static final String TAK_LAT = "tak_lat";
    public static final String TAK_LNG = "tak_lng";

    /** Columns - TABLE_TAKS_METADATA */
    public static final String TAK_METADATA_ID = "_id";
    public static final String TAK_METADATA_TAKID = "tak_id";
    public static final String TAK_METADATA_KEY = "tak_metadata_key";
    public static final String TAK_METADATA_VALUE = "tak_metadata_value";

    /** Static method that returns the database object you should use */
    private static MapTakDB instance = null;
    public static MapTakDB getDB(Context c) {
        if (instance == null) {
            instance = new MapTakDB(c);
        }
        return instance;
    }

    /** Default constructor */
    private MapTakDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /** Called when the database is first created. */
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Strings which create tables
        Log.d(MainActivity.LOG_TAG, "Creating new database.");

        String create_table_maps = "CREATE TABLE " + TABLE_MAPS + " (" +
                MAP_ID + " TEXT, " +
                MAP_LABEL + " TEXT, " +
                MAP_ISPUBLIC + " INTEGER " +
                MAP_OWNER_ID + " TEXT, " +
                MAP_OWNER_STR + " TEXT );";

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

        String create_table_tak_metadata = "CREATE TABLE " + TABLE_TAK_METADATA + " (" +
                TAK_METADATA_ID + " TEXT, " +
                TAK_METADATA_TAKID + " TEXT, " +
                TAK_METADATA_KEY + " TEXT, " +
                TAK_METADATA_VALUE + " TEXT );";

        // Create the tables from the strings provided
        sqLiteDatabase.execSQL(create_table_maps);
        sqLiteDatabase.execSQL(create_table_maps_admins);
        sqLiteDatabase.execSQL(create_table_taks);
        sqLiteDatabase.execSQL(create_table_tak_metadata);
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
            db.execSQL("DROP TABLE " + TABLE_TAK_METADATA);
            onCreate(db);
        }
    }

    /** Adds a map and all of its metadata to the database.
     *  This includes: Map name, map ID, whether it is public or private, and all of its administrators. */
    public void addMap(MapObject map) {
        // Add the map to the local database
        Log.d(MainActivity.LOG_TAG, "Adding map to database.");
        Log.d(MainActivity.LOG_TAG, "\tID: " + map.getID() + "\tName: " + map.getLabel());

        // Get database
        SQLiteDatabase db = getWritableDatabase();

        // Add the map to the database
        ContentValues valuesMaps = new ContentValues();
        valuesMaps.put(MAP_ID, map.getID().toString());
        valuesMaps.put(MAP_LABEL, map.getLabel());
        valuesMaps.put(MAP_ISPUBLIC, map.isPublic());
        valuesMaps.put(MAP_OWNER_ID, map.getOwner().getID());
        valuesMaps.put(MAP_OWNER_STR, map.getOwner().toString());

        if (db != null) {
            db.insert(TABLE_MAPS, null, valuesMaps);
        }

        // Add the managers
        for (UserID admin : map.getManagerList()) {
            addAdmin(admin, map.getID());
        }

        // Add the taks
        for (TakObject t : map.getTakList()) {
            addTak(t, map.getID());
        }

        if (db != null) {
            db.close();
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

    /** Adds a piece of tak metadata to the database */
    public void addTakMetadata(TakID takID, TakMetadata metadata) {

        ContentValues values = new ContentValues();
        values.put(TAK_METADATA_ID, "1");
        values.put(TAK_METADATA_TAKID, takID.toString());
        values.put(TAK_METADATA_KEY, metadata.getKey());
        values.put(TAK_METADATA_VALUE, metadata.getValue());

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.insert(TABLE_TAK_METADATA, null, values);
        }
    }

    /** Changes the ID of a map associated with "oldID" to "newID" */
    public void setMapID(MapID oldID, MapID newID) {
        Log.d(MainActivity.LOG_TAG, "Modifying mapID from " + oldID + " to " + newID);
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_MAPS + " SET " + MAP_ID + " = \"" + newID + "\" WHERE " + MAP_ID + " = \"" + oldID + "\";");
        }

    }

    /** Changes the name of a map associated with "id" to "name" */
    public void setMapName(MapID id, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_MAPS + " SET " + MAP_LABEL + "=\"" + newName + "\" WHERE " + MAP_ID + " = \"" + id + "\";");
        }
    }

    /** Changes the isPublic status of a map to the given boolean */
    public void setMapIsPublic(MapID id, boolean isPublic) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            int isPub = -1;
            isPub = isPublic ? 1 : 0;
            db.execSQL("UPDATE " + TABLE_MAPS + " SET " + MAP_ISPUBLIC + "=\"" + isPub + "\" WHERE " + MAP_ID + " = \"" + id + "\";");
        }
    }

    /** Changes the owner id and owner name of a given map to the given user ID */
    public void setMapOwner(MapID id, UserID user) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_MAPS + " SET " + MAP_OWNER_ID + "=\"" + user.getID() + "\" WHERE " + MAP_ID + " = \"" + id + "\";");
            db.execSQL("UPDATE " + TABLE_MAPS + " SET " + MAP_OWNER_STR + "=\"" + user.getName() + "\" WHERE " + MAP_ID + " = \"" + id + "\";");
        }
    }

    /** Changes the TakID of a given tak from the old takID to the new takID */
    public void setTakID(TakID oldTak, TakID newTak) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_TAKS + " SET " + TAK_ID + "=\"" + newTak + "\" WHERE " + TAK_ID + " = \"" + oldTak + "\";");
        }
    }

    /** Changes the mapid of a given tak from the old to the new */
    public void setTakMapID(TakID tak, MapID newMap) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_TAKS + " SET " + TAK_MAP_ID + "=\"" + newMap + "\" WHERE " + TAK_ID + " = \"" + tak + "\";");
        }
    }

    /** Changes the tak name of a supplied tak to what is provided */
    public void setTakName(TakID id, String name) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_TAKS + " SET "+ TAK_LABEL + "=\"" + name + "\" WHERE " + TAK_ID + " = \"" + id + "\";");
        }
    }

    /** Changes the tak's latitude and longitude to the given lat/lng pair */
    public void setTakLatLng(TakID id, double lat, double lng) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_TAKS + " SET " + TAK_LAT + "=\"" + lat + "\" WHERE " + TAK_ID + " = \"" + id + "\";");
            db.execSQL("UPDATE " + TABLE_TAKS + " SET " + TAK_LNG + "=\"" + lng + "\" WHERE " + TAK_ID + " = \"" + id + "\";");
        }
    }

    /** Changes the name and ID associated with oldID's user-id to the data in newID */
    public void setMapAdminsUser(UserID oldID, UserID newID) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_MAPS_ADMINS + " SET " + MAPADMINS_ID + "=\"" + newID.getID() + "\" WHERE " + MAPADMINS_ID + " = \"" + oldID.getID() + "\";");
            db.execSQL("UPDATE " + TABLE_MAPS_ADMINS + " SET " + MAPADMINS_NAME + "=\"" + newID.getName() + "\" WHERE " + MAPADMINS_ID + " = \"" + oldID.getID() + "\";");
        }
    }

    /** Changes the mapID a given administrator is associated with */
    public void setMapAdminsMapID(UserID id, MapID newMapID) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("UPDATE " + TABLE_MAPS_ADMINS + " SET " + MAPADMINS_MAP_ID + "=\"" + newMapID.toString() + "\" WHERE " + MAPADMINS_ID + "=\"" + id.getID() + "\";");
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

    /** Deletes the tak metadata associated with a given TakMetadata object (id, specifically) from the database */
    public void deleteTakMetadata(TakMetadata metadata) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("DELETE FROM " + TABLE_TAK_METADATA + " WHERE " + TAK_METADATA_ID + "=\"" + metadata.getID() + "\"");
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

        c.close();
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

                c.close();
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

            c.close();
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

                c.close();
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

            c.close();
            return results;
        }

        return null;
    }


}
