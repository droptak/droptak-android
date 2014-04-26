package com.droptak.android.fragments.dialogs;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.droptak.android.R;
import com.droptak.android.data.MapTakDB;
import com.droptak.android.data.TakID;
import com.droptak.android.data.TakMetadata;
import com.droptak.android.data.TakObject;
import com.droptak.android.tasks.AddMetadataTask;
import com.droptak.android.widgets.TakMetaListItemAdapter;

public class TakMetadataDialog extends DialogFragment implements View.OnClickListener {

    private EditText etKey, etValue;
    private ListView lvMetadata;
    private TakID takID;

    public TakMetadataDialog(TakID takID) {
        this.takID = takID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the tak object passed in
        MapTakDB db = MapTakDB.getDB(getActivity());
        TakObject tak = db.getTak(takID);

        // Create the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(tak.getName() + " Metadata");

        // Inflate a layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_tak_metadata, null, false);
        builder.setView(v);

        // Set up widgets on the screen
        Button buAdd = (Button) v.findViewById(R.id.takmetadata_btn_add);
        etKey = (EditText) v.findViewById(R.id.takmetadata_et_key);
        etValue = (EditText) v.findViewById(R.id.takmetadata_et_value);
        lvMetadata = (ListView) v.findViewById(R.id.takmetadata_listview);

        // Set listeners
        buAdd.setOnClickListener(this);

        // Inflate initial tak metadata
        TakMetaListItemAdapter adapter = new TakMetaListItemAdapter(getActivity(), tak.getMeta().values());
        lvMetadata.setAdapter(adapter);

        return builder.create();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.takmetadata_btn_add:

                // Get the key and value
                String key = etKey.getText().toString();
                String value = etValue.getText().toString();

                // Execute the task
                AddMetadataTask task = new AddMetadataTask(getActivity(), takID, new TakMetadata(null, key, value));
                task.execute();

                // Update the list view
                TakObject tak = MapTakDB.getDB(getActivity()).getTak(takID);
                lvMetadata.setAdapter(new TakMetaListItemAdapter(getActivity(), tak.getMeta().values()));

                // Clear out the edit texts
                etKey.setText("");
                etValue.setText("");

                break;

        }
    }

}
