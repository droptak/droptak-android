package edu.purdue.maptak.admin.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapObject;

public class MapListItemAdapter extends BaseAdapter {

    private Context c;
    private List<MapObject> maps;

    public MapListItemAdapter(Context c, List<MapObject> maps) {
        this.c = c;
        this.maps = maps;
    }

    public int getCount() {
        return maps.size();
    }

    public Object getItem(int i) {
        return maps.get(i);
    }

    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.adapter_map_item, viewGroup, false);

        TextView tvLabel = (TextView) v.findViewById(R.id.maplistadapter_tv_maplabel);
        tvLabel.setText(maps.get(i).getLabel());

        return v;
    }
}
