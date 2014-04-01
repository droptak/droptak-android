package edu.purdue.maptak.admin.qrcode;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.fragments.QRCodeFragment;

/**
 * Created by tylorgarrett on 3/29/14.
 */
public class QRCodeScanner extends Fragment {

    Activity thisActivity = null;
    QRCodeFragment qrCodeFragment = new QRCodeFragment();

    public QRCodeScanner(Activity activity){
        thisActivity = activity;
        getQRCode();
    }

    public void getQRCode() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_map_mapview, qrCodeFragment);
        fragmentTransaction.commit();
        IntentIntegrator integrator = new IntentIntegrator(thisActivity);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Bundle args = new Bundle();
            args.putString("title", scanResult.toString());
            qrCodeFragment.setArguments(args);
            Toast.makeText(getActivity(), "This didn't happen", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "This happened", Toast.LENGTH_SHORT).show();
        }
    }

}
