package com.droptak.android.fragments.dialogs;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
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
    MapTakDB db;
    MyAdapter myAdapter;
    TakID takID;
    Button button;
    EditText key;
    EditText value;
    AlertDialog.Builder dataList;

    public TakMetadataDialog(TakID takID) {
        this.takID = takID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataList = new AlertDialog.Builder(getActivity());
        dataList.setTitle("Tak MetaData");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_tak_metadata_dialog, null);
        dataList.setView(v);
        db = MapTakDB.getDB(getActivity());
        button = (Button) v.findViewById(R.id.takmetadata_btn_add);
        button.setOnClickListener(this);
        key = (EditText) v.findViewById(R.id.takmetadata_et_key);
        value = (EditText) v.findViewById(R.id.takmetadata_et_value);
        listView = (ListView) v.findViewById(R.id.takmetadata_listview);
        myAdapter = new MyAdapter(db.getTakMetadata(takID));
        listView.setAdapter(myAdapter);
        return dataList.create();
    }

    @Override
    public void onClick(View view) {
        //add the metadata to the TakObject and refresh the page
        String mapKey = key.getText().toString();
        String mapTakID = takID.toString();
        String mapValue = value.getText().toString();
        if ( mapKey != null && mapValue != null ){
            // add to the database
            TakMetadata newData = new TakMetadata(mapTakID, mapKey, mapValue);
            db.addTakMetadata(takID, newData);
        }
        getDialog().cancel();
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
                LayoutInflater inflater = getActivity().getLayoutInflater();
                result = inflater.inflate(R.layout.fragment_tak_metdata_listview, parent, false);
                ((TextView) result.findViewById(R.id.takmetadata_lv_et_key)).setText("Temp Text");
                ((TextView) result.findViewById(R.id.takmetadata_lv_et_value)).setText("Temp Value");
            } else {
                result = convertView;
            }
            Map.Entry<String, TakMetadata> item = getItem(position);

            return result;
        }

    }

}
