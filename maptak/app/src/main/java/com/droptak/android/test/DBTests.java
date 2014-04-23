package com.droptak.android.test;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public abstract class DBTests {

    /** Saved this code from kyle's implementation of this functionality */
    public static void backupDatabase()  {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
            String currentDBPath = "//data//"+ "com.droptak.android" +"//databases//"+"database_cached_taks";
            String backupDBPath = "database_cahced_taks";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            try {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Toast.makeText(, backupDB.toString(), Toast.LENGTH_LONG).show();
            Log.d("debug", "sql backupmade");

        }
    }

}
