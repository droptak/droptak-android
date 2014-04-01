package edu.purdue.maptak.admin.tasks;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.loopj.android.http.*;
import android.content.*;
import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.purdue.maptak.admin.managers.TakFragmentManager;


public class LoginTask extends AsyncTask<Void,Void,String> {

    Activity callingActivity;
    GoogleApiClient mGoogleApiClient;
    SharedPreferences settings;
    ProgressDialog progressDialog;
    FragmentManager fragMan;

    public LoginTask(Activity activity, GoogleApiClient mGoogleApiClient) {
        this.callingActivity = activity;
        this.mGoogleApiClient = mGoogleApiClient;
        settings = activity.getSharedPreferences("settings",0);
        progressDialog = new ProgressDialog(activity);

    }

    public void onPreExecute(){
        this.progressDialog.setMessage("Processing...");
        this.progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String token = null;
        Bundle appActivities = new Bundle();
        appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES,
                "http://schemas.google.com/AddActivity");
        String scopes = "oauth2:https://www.googleapis.com/auth/plus.login";
        String code = null;
        try {
            code = GoogleAuthUtil.getToken(
                    callingActivity,                                            // Context context
                    Plus.AccountApi.getAccountName(mGoogleApiClient),           // String accountName
                    scopes,                                                     // String scope
                    appActivities                                               // Bundle bundle
            );

        } catch (IOException transientEx) {
            // network or server error, the call is expected to succeed if you try again later.
            // Don't attempt to call again immediately - the request is likely to
            // fail, you'll hit quotas or back-off.
        } catch (UserRecoverableAuthException e) {
            // Requesting an authorization code will always throw
            // UserRecoverableAuthException on the first call to GoogleAuthUtil.getToken
            // because the user must consent to offline access to their data.  After
            // consent is granted control is returned to your activity in onActivityResult
            // and the second call to GoogleAuthUtil.getToken will succeed.
            callingActivity.startActivityForResult(e.getIntent(), 0);
        } catch (GoogleAuthException authEx) {
            // Failure. The call is not expected to ever succeed so it should not be
            // retried.
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String personName = "";
        String email = "";
        String id = "";
        // Toast.makeText(this.getActivity(), "User is connected!", Toast.LENGTH_LONG).show();
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            personName = currentPerson.getDisplayName();
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            id = currentPerson.getId();
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name",personName);
        editor.commit();
        AsyncHttpClient client = new AsyncHttpClient();
        personName = personName.replace(" ", "%20");
        client.post("http://mapitapps.appspot.com/api/login?storeToken="+code+"&name="+personName+"&email="+email+"&id="+id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                JSONObject jObject = null;
                try {
                     jObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String id = null;
                try {
                     id = jObject.getString("uuid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("debug","Got id="+id);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("id",id);
                editor.commit();
            }
        });
        return code;
    }

    @Override
    protected void onPostExecute(String token) {
        Log.i("Token", "Access token retrieved:" + token);
        progressDialog.dismiss();
        TakFragmentManager.switchToMainMenu(callingActivity);
    }


}
