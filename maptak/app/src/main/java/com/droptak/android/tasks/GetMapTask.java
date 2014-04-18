package com.droptak.android.tasks;

import android.os.AsyncTask;

import com.droptak.android.data.MapID;

/** Gets a single map specified by a given ID from the server and stores it in the local database. */
public class GetMapTask extends AsyncTask<Void, Void, Void> {

    private static final String BASE_URL = "http://mapitapps.appspot.com/api/v1/map/";

    private MapID mapToGet;

    public GetMapTask(MapID id) {
        this.mapToGet = id;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Construct the URL
        String url = BASE_URL + mapToGet.toString();



        return null;
    }



}
