package edu.purdue.maptak.admin.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.managers.TakFragmentManager;

/*
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SearchFragment extends Fragment implements View.OnClickListener{

    Button searchButton;
    EditText mapSearch;
    List<MapObject> searchResults;
    String mapName;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mapSearch = (EditText) v.findViewById(R.id.search_et_searchbar);
        searchButton = (Button) v.findViewById(R.id.search_bu_search);
        searchButton.setOnClickListener(this);
        return v;
        //return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_bu_search:
                mapName = mapSearch.getText().toString();
                if ( mapName != null ) {
                    searchForMap(mapName);
                } else {
                    Toast.makeText(getActivity(), "You must have a search term", Toast.LENGTH_LONG);
                }
                break;
        }
    }

    public void searchForMap(String _mapName){
        MapTakDB db = new MapTakDB(getActivity());
        List<MapObject> listOfMaps = db.getUsersMaps();
        for ( int i=0; i<listOfMaps.size(); i++ ){
            String test = listOfMaps.get(i).getLabel().toLowerCase();
            if ( test.equals(_mapName.toLowerCase()) ){
                searchResults.add(listOfMaps.get(i));
                break;
            }
        }
        if ( searchResults != null ){
            TakFragmentManager.switchToSearchResults(getActivity(), searchResults);
        } else {
            // stay on this page and throw an error that says there are no results
        }
    }

}
