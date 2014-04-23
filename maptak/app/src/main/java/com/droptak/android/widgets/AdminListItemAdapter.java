package com.droptak.android.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.droptak.android.R;
import com.droptak.android.data.User;

import java.util.List;

public class AdminListItemAdapter extends BaseAdapter {

    private Context c;
    private List<User> admins;

    public AdminListItemAdapter(Context c, List<User> adminsToDisplay) {
        this.c = c;
        this.admins = adminsToDisplay;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get admin's name and email
        String name = admins.get(position).getName();
        String email = admins.get(position).getEmail();

        // Inflate the view we will use
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.adapter_admin_item, parent, false);

        // Get elements in the view
        TextView tvName = (TextView) row.findViewById(R.id.admin_list_tv_name);
        TextView tvEmail = (TextView) row.findViewById(R.id.admin_list_tv_email);

        // Set the name and email
        tvName.setText(name);
        tvEmail.setText(email);

        return row;
    }
}
