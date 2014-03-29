package edu.purdue.maptak.admin.QRCode;

import android.app.Activity;

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


}
