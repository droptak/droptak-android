package edu.purdue.maptak.admin.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import edu.purdue.maptak.admin.MainActivity;
import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.interfaces.OnTakSelectedListener;
import edu.purdue.maptak.admin.test.DummyData;

public class TakListFragment extends ListFragment {

    /** Listener which is called when a tak is selected */
    private OnTakSelectedListener listener;

    /** The MapObject which owns all of the taks listed in the list */
    private MapID mapID;

    /** Creates a new instance of a TakListFragment and returns it */
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

        MapTakDB db = new MapTakDB(getActivity());
        List<TakObject> takObjects;

        if(mapID == null){
            takObjects = new LinkedList<TakObject>();
        } else{
            takObjects = db.getTaks(mapID);
        }

        List<String> taks = new LinkedList<String>();

        while(!takObjects.isEmpty()){
            taks.add(takObjects.remove(0).getLabel());
        }
        Log.i("TakListFragment", "1");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, taks);
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // do something with the data
    }

}