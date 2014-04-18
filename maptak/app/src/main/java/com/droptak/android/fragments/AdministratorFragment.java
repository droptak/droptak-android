package com.droptak.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.droptak.android.R;
import com.droptak.android.activities.MainActivity;
import com.droptak.android.data.MapObject;
import com.droptak.android.data.MapTakDB;
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
public class AdministratorFragment extends Fragment implements View.OnClickListener{

    private MapObject currentMap;
    private ListView adminList;
    private ListAdapter listAdapter;

    public AdministratorFragment(MapObject currentMap) {
        this.currentMap = currentMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_administrator, container, false);
        Button addAdministrator = (Button) v.findViewById(R.id.admin_bu_addadmin);
        addAdministrator.setOnClickListener(this);
        adminList = (ListView) v.findViewById(R.id.admin_listview);
        currentMap.addManager(new UserID("0571", "Michael Hockerman", "mhockerman@gmail.com"));
        currentMap.addManager(new UserID("0129", "Tylor Garrett", "tylorgarrett@gmail.com"));
        listAdapter = new ListViewAdapter<UserID>(getActivity(), android.R.layout.simple_list_item_1, currentMap.getManagers());
        adminList.setAdapter(listAdapter);
        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.admin_bu_addadmin:
                break;

        }

    }


    public class ListViewAdapter<E> extends ArrayAdapter {
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
            UserID currentUser = mAdmins.get(position);
            String name = currentUser.getName();
            String email = currentUser.getEmail();
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

            temp.name.setText(name);
            temp.email.setText(email);
            return row;
        }

        class AdminData{
            TextView name;
            TextView email;
        }
    }





}
