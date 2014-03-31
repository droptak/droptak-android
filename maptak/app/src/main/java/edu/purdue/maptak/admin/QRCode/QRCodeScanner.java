package edu.purdue.maptak.admin.QRCode;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.fragments.QRCodeFragment;

/**
 * Created by tylorgarrett on 3/29/14.
 */
public class QRCodeScanner extends Fragment {

    Activity thisActivity = null;

    public QRCodeScanner(Activity activity){
        thisActivity = activity;
        getQRCode();
    }

    public void getQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(thisActivity);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            QRCodeFragment qrCodeFragment = new QRCodeFragment();
            Bundle args = new Bundle();
            args.putString("title", scanResult.toString());
            qrCodeFragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.activity_map_mapview, qrCodeFragment);
            fragmentTransaction.commit();
        } else {
            // throw an error that said that the scan didn't work
        }
    }

}
