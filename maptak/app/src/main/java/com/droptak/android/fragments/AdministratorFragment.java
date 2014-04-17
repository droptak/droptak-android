package com.droptak.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.droptak.android.R;
import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.UserID;

import java.util.List;

/*
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdministratorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdministratorFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AdministratorFragment extends Fragment {

    MapObject currentMap;
    ListView adminList;
    ListAdapter listAdapter;

    public AdministratorFragment(MapObject inMap) {
        this.currentMap = inMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adminList = (ListView) getActivity().findViewById(R.id.fragment_maplist_listview);
        if ( currentMap.getManagers() == null ){
            Log.d(MainActivity.LOG_TAG, "The thingy is null");
        }
        listAdapter = new ListViewAdapter<UserID>(getActivity(), android.R.layout.simple_list_item_1, currentMap.getManagers());
        adminList.setAdapter(listAdapter);
        return inflater.inflate(R.layout.fragment_administrator, container, false);
    }

    public class ListViewAdapter<UserID> extends ArrayAdapter {
        private Context mContext;
        private int id;
        private List<UserID> mAdmins;

        public ListViewAdapter(Context context, int textViewResourceId, List<UserID> admins){
            super(context, textViewResourceId, admins);
            mContext = context;
            id = textViewResourceId;
            mAdmins = admins;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View row = convertView;
            AdminData temp = null;
            if ( row == null ){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.fragment_adminstrator_listview, parent, false);
                temp = new AdminData();
                temp.email = (TextView) row.findViewById(R.id.admin_et_email);
                temp.name = (TextView) row.findViewById(R.id.admin_et_name);
                row.setTag(temp);
            } else {
                temp = (AdminData)row.getTag();
            }
            temp.name.setText("Tylor Garrett");
            temp.email.setText("tylorgarrett@gmail.com");
            return row;
        }

        class AdminData{
            TextView name;
            TextView email;
        }
    }





}
