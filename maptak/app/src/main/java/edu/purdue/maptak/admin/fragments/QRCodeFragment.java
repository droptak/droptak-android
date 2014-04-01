package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.purdue.maptak.admin.R;

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

    public QRCodeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = new Bundle();
        String title = bundle.getString("title");
        TextView qrTitle = (TextView) getActivity().findViewById(R.id.QRCodeTitle);
        qrTitle.setText(title);
        return inflater.inflate(R.layout.fragment_qrcode, container, false);
    }

}
