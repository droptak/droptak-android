package com.droptak.android.fragments.dialogs;



import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droptak.android.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class TakMetadataDialog extends DialogFragment {


    public TakMetadataDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tak_metadata_dialog, container, false);
    }


}
