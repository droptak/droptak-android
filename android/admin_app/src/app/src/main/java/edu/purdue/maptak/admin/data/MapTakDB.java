package edu.purdue.maptak.admin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;
import java.util.UUID;

public class MapTakDB extends SQLiteOpenHelper {

    /** Database name and version */
    public static final String DB_NAME = "database_cached_taks";
    public static final int DB_VERSION = 1;

    /** Tables */
    public static final String TABLE_MAPS = "t_maps";
    public static final String TABLE_TAKS = "t_taks";

    /** Columns - TABLE_MAPS */
    public static final String MAP_ID = "_id";
    public static final String MAP_LABEL = "map_label";

    /** Columns - TABLE_TAKS */
    public static final String TAK_ID = "_id";
    public static final String TAK_MAP_ID = "map_id";
    public static final String TAK_LABEL = "tak_label";
    public static final String TAK_LAT = "tak_lat";
    public static final String TAK_LNG = "tak_lng";

    /** Default constructor */
    public MapTakDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /** Called when the database is first created. */
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Strings which create tables

        String create_table_maps = "CREATE TABLE " + TABLE_MAPS + " (" +
                MAP_ID + " TEXT, " +
                MAP_LABEL + " TEXT );";

        String create_table_taks = "CREATE TABLE " + TABLE_TAKS + " ( " +
                TAK_ID + " TEXT, " +
                TAK_MAP_ID + " TEXT, " +
                TAK_LABEL + " TEXT, " +
                TAK_LAT + " DOUBLE, " +
                TAK_LNG + " DOUBLE );";

        // Create the tables from the strings provided

        sqLiteDatabase.execSQL(create_table_maps);
        sqLiteDatabase.execSQL(create_table_taks);

    }

    /** Called when the database is upgraded from one DB_VERSION to the next */
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    /** Destroys the local database and creates a new one. */
    public void destroy() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            db.execSQL("DROP TABLE " + TABLE_MAPS);
            db.execSQL("DROP TABLE " + TABLE_TAKS);
            onCreate(db);
        }
    }

    /** Refreshes the database with information from the server */
    public void refresh() {
        // Implement in sprint 2
    }

    /** Pushes a new map for the user to the remote database then refreshes the local cache */
    public void addMap(String map) {

        // When finally implemented, this will push to the server then refresh the local cache
        // due to the fact that we rely on unique IDs generated by the server

        // For now, it will just add to the local database
        // The ID is simulated with a random UUID

        String thisUUID = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(MAP_ID, thisUUID);
        values.put(MAP_LABEL, map);

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            long rowid = db.insert(TABLE_MAPS, null, values);
        }

    }

    /** Pushes a new tak for a given map to the remote database then refreshes the local cache */
    public void addTak(TakObject tak, String map) {

        // As with addMap(); this will eventually push to the server and refresh the local cache
        // For now, we are using all local information

        String map_uuid = map;
        String tak_uuid = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put(TAK_ID, tak_uuid);
        values.put(TAK_MAP_ID, map_uuid);
        values.put(TAK_LABEL, tak.toString());
        values.put(TAK_LAT, 40);
        values.put(TAK_LNG, -60);

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            long rowid = db.insert(TABLE_TAKS, null, values);
        }

    }

    /** Returns a list of the maps the user is authorized to access. */
    public List<String> getUsersMaps() {
        return null;
    }

    /** Returns a specific map with a given UUID, or null if it doesn't exist in the cache */
    public String getMap(String uuid) {
        return null;
    }

    /** Returns a list of taks associated with a given map */
    public List<TakObject> getTaks(MapID mapID) {
        return null;
    }

    /** Returns a specific tak with a given UUID, or null if it doesn't exist in the cache */
    public TakObject getTak(TakID takID) {
        return null;
    }



}
