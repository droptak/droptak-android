package com.droptak.android.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.droptak.android.R;
import com.droptak.android.data.MapID;
import com.droptak.android.data.User;
import com.droptak.android.interfaces.OnAdminIDUpdateListener;
import com.droptak.android.interfaces.OnAdminRevokedListener;
import com.droptak.android.tasks.RevokeAdminTask;

import java.util.List;

public class AdminListItemAdapter extends BaseAdapter {

    private Context c;
    private MapID id;
    private List<User> admins;
    private OnAdminRevokedListener listener;

    public AdminListItemAdapter(Context c, MapID id, List<User> adminsToDisplay, OnAdminRevokedListener listener) {
        this.c = c;
        this.id = id;
        this.admins = adminsToDisplay;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return admins.size();
    }

    @Override
    public Object getItem(int position) {
        return admins.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get admin's name and email
        String name = admins.get(position).getName();
        String email = admins.get(position).getEmail();

        // Inflate the view we will use
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.adapter_admin_item, parent, false);

        // Get elements in the view
        TextView tvName = (TextView) row.findViewById(R.id.adminlist_tv_name);
        TextView tvEmail = (TextView) row.findViewById(R.id.adminlist_tv_email);
        TextView buDelete = (TextView) row.findViewById(R.id.adminlist_tvasbu_delete);

        // Set a listener on the button
        buDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RevokeAdminTask task = new RevokeAdminTask(c, id, admins.get(position), listener);
                task.execute();
            }
        });

        // Set the name and email
        tvName.setText(name);
        tvEmail.setText(email);

        return row;
    }
}
