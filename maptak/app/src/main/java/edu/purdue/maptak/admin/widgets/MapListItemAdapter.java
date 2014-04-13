package edu.purdue.maptak.admin.widgets;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.fragments.dialogs.MapInfoDialog;

public class MapListItemAdapter extends BaseAdapter {

    private Activity c;
    private List<MapObject> maps;

    public MapListItemAdapter(Activity c, List<MapObject> maps) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.adapter_map_item, viewGroup, false);

        TextView tvLabel = (TextView) v.findViewById(R.id.maplistadapter_tv_maplabel);
        tvLabel.setText(maps.get(i).getName());

        Button buInfo = (Button) v.findViewById(R.id.maplistadapter_bu_info);
        buInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MapInfoDialog d = new MapInfoDialog(maps.get(i));
                d.show(c.getFragmentManager(), "map_info_dialog");
            }
        });

        return v;
    }
}
