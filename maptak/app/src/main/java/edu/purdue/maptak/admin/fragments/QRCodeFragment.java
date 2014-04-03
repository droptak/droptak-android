package edu.purdue.maptak.admin.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.qrcode.IntentIntegrator;
import edu.purdue.maptak.admin.qrcode.IntentResult;
import edu.purdue.maptak.admin.tasks.GetQRMapTask;

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
        GetQRMapTask getQRMapTask = new GetQRMapTask(code,getActivity());
        getQRMapTask.execute();
        return v;
    }
}
