package com.droptak.android.widgets;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import com.droptak.android.R;
import com.droptak.android.data.MapObject;
import com.droptak.android.fragments.DrawerFragment;
import com.droptak.android.fragments.dialogs.MapInfoDialog;

public class MapListItemAdapter extends BaseAdapter {

    private Activity c;
    private List<MapObject> maps;
    private DrawerLayout drawer;

    public MapListItemAdapter(Activity c, List<MapObject> maps, DrawerLayout containingDrawer) {
        this.c = c;
        this.maps = maps;
        this.drawer = containingDrawer;
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
                drawer.closeDrawers();
                MapInfoDialog d = new MapInfoDialog(maps.get(i));
                d.show(c.getFragmentManager(), "map_info_dialog");
            }
        });

        return v;
    }
}
