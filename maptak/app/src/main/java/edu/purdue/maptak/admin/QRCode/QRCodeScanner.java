package edu.purdue.maptak.admin.QRCode;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;

/**
 * Created by tylorgarrett on 3/29/14.
 */
public class QRCodeScanner {

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
            // call the QRCodeFragment
            // display the code that was displayed
        } else {
            // display an error, say the code couldn't be read or something like that
        }
    }

}
