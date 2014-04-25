package com.droptak.android.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.droptak.android.R;
import com.droptak.android.data.TakMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class TakMetaListItemAdapter extends BaseAdapter {

    private Context c;
    private List<TakMetadata> metadataList;

    public TakMetaListItemAdapter(Context c, Collection<TakMetadata> metadataList) {
        this.c = c;
        this.metadataList = new ArrayList<TakMetadata>(metadataList);
    }

    @Override
    public int getCount() {
        return metadataList.size();
    }

    @Override
    public Object getItem(int position) {
        return metadataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the view we will use
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.adapter_meta_item, parent, false);

        // Get the two edit texts on the row
        TextView tvKey = (TextView) row.findViewById(R.id.takmetadata_tv_key);
        TextView tvValue = (TextView) row.findViewById(R.id.takmetadata_tv_value);

        // Set their content
        tvKey.setText(metadataList.get(position).getKey());
        tvValue.setText(metadataList.get(position).getValue());

        // Return the view
        return row;
    }

}
