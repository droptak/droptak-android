package com.droptak.android.fragments.dialogs;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.droptak.android.R;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakID;
import com.droptak.android.data.TakMetadata;
import com.droptak.android.data.TakObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class TakMetadataDialog extends DialogFragment implements View.OnClickListener {

    ListView listView;
    MyAdapter myAdapter;
    TakID takID;
    Button button;
    EditText key;
    EditText value;

    public TakMetadataDialog(TakID takID) {
        this.takID = takID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AlertDialog.Builder dataList= new AlertDialog.Builder(getActivity());
        dataList.setTitle("Tak MetaData");
        MapTakDB db = MapTakDB.getDB(getActivity());
        TakObject tak = db.getTak(takID);
        Log.d("debug","name="+tak.getName());
        return dataList.create();
    }

    @Override
    public void onClick(View view) {
        //add the metadata to the TakObject and refresh the
    }

    public class MyAdapter extends BaseAdapter{
        private final ArrayList mData;

        public MyAdapter(Map<String, TakMetadata> map){
            mData = new ArrayList();
            mData.addAll(map.entrySet());
        }

        @Override
        public int getCount(){
            return mData.size();
        }

        @Override
        public Map.Entry<String, TakMetadata> getItem(int position){
            return (Map.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int position){
            // TODO: implement with your own logic with ID
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final View result;

            if ( convertView == null ){
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tak_metadata_dialog, parent, false);
            } else {
                result = convertView;
            }
            Map.Entry<String, TakMetadata> item = getItem(position);
            //TODO replace findViewByID by ViewHolder
            ((TextView) result.findViewById(R.id.takmetadata_lv_et_name)).setText(item.getKey());
            ((TextView) result.findViewById(R.id.takmetadata_lv_et_email)).setText(item.getValue().toString());

            return result;
        }

    }

}
