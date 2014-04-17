package com.droptak.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droptak.android.R;
import com.droptak.android.activities.MainActivity;

/*
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QRCodeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QRCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class QRCodeFragment extends Fragment {

    private String code;
    static TextView url = null;

    public static QRCodeFragment newInstance(String code) {
        Bundle args = new Bundle();
        args.putSerializable("EXTRA", code);
        Log.d(MainActivity.LOG_TAG, code);
        QRCodeFragment fragment = new QRCodeFragment();
        fragment.code = code;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qrcode, container, false);
        url = (TextView) v.findViewById(R.id.QRCodeTitle);
        url.setText(this.code);
        return v;
    }
}
