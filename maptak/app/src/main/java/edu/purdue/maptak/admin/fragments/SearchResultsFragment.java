package edu.purdue.maptak.admin.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.managers.TakFragmentManager;

/*
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SearchResultsFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView mapList;
    ListAdapter listAdapter;
    private static List<MapObject> listOfMaps = null;

    public SearchResultsFragment(List<MapObject> listOfMaps) {
        this.listOfMaps = listOfMaps;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maplist, container, false);
        mapList = (ListView) v.findViewById(R.id.fragment_maplist_listview);
        mapList.setOnItemClickListener(this);
        listAdapter = new MapObjectAdapter(getActivity(), android.R.layout.simple_list_item_1, listOfMaps);
        mapList.setAdapter(listAdapter);
        return v;
    }

    private class MapObjectAdapter extends ArrayAdapter {
        private Context mContext;
        private int id;
        private List<MapObject> mMaps;

        public MapObjectAdapter(Context context, int textViewResourceId, List<MapObject> maps){
            super(context, textViewResourceId, maps);
            mContext = context;
            id = textViewResourceId;
            mMaps = maps;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View row = convertView;
            MapObjectData temp = null;
            if ( row == null ){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.listview_mapobject, parent, false);
                temp = new MapObjectData();
                temp.title = (TextView) row.findViewById(R.id.mapTitle);
                row.setTag(temp);
            } else {
                temp = (MapObjectData)row.getTag();
            }
            MapObject mapToBeDisplayed = mMaps.get(position);
            temp.title.setText(mapToBeDisplayed.getLabel());
            return row;
        }

        class MapObjectData
        {
            TextView title;
        }

    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MapObject selected = listOfMaps.get(i);
        String id = selected.getID().toString();
        getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString(MainActivity.PREF_CURRENT_MAP, id).commit();
        TakFragmentManager.switchToMap(getActivity(), selected);
    }

}
