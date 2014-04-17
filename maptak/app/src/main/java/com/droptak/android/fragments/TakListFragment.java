package com.droptak.android.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import com.droptak.android.R;
import com.droptak.android.data.MapID;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakObject;
import com.droptak.android.interfaces.OnTakSelectedListener;

public class TakListFragment extends ListFragment {

    /** Listener which is called when a tak is selected */
    private OnTakSelectedListener listener;

    /** The MapObject which owns all of the taks listed in the list */
    private MapID mapID;

    /** Creates a new instance of a TakListDialog and returns it */
    public static TakListFragment newInstanceOf(MapID toDisplay, OnTakSelectedListener listener) {
        TakListFragment f = new TakListFragment();
        f.mapID = toDisplay;
        f.listener = listener;
        return f;
    }

    /** Creates the view for the taklistfragment */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_taklist, container, false);
    }

    /** Called when the activity the fragment resides on is created */
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MapTakDB db = MapTakDB.getDB(getActivity());
        List<TakObject> takObjects;

        if(mapID == null){
            takObjects = new LinkedList<TakObject>();
        } else{
            takObjects = db.getTaks(mapID);
        }

        List<String> taks = new LinkedList<String>();

        while(!takObjects.isEmpty()){
            taks.add(takObjects.remove(0).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, taks);
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // do something with the data
    }

}