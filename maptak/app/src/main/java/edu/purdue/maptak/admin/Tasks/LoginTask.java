package edu.purdue.maptak.admin.Tasks;

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
import android.app.Fragment;
import android.util.Log;
import java.io.IOException;


public class LoginTask extends AsyncTask<Void,Void,String> {

    Context context;
    GoogleApiClient mGoogleApiClient;

    public LoginTask(Context context, GoogleApiClient mGoogleApiClient){
        this.context = context;
        this.mGoogleApiClient = mGoogleApiClient;
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
                    context,                                              // Context context
                    Plus.AccountApi.getAccountName(mGoogleApiClient),  // String accountName
                    scopes,                                            // String scope
                    appActivities                                      // Bundle bundle
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
            ((Activity)context).startActivityForResult(e.getIntent(),0);
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
        AsyncHttpClient client = new AsyncHttpClient();
        personName = personName.replace(" ", "%20");
        client.post("http://mapitapps.appspot.com/api/login?storeToken="+code+"&name="+personName+"&email="+email+"&id="+id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                /* This will be a json string, with a key "uuid":id
                    This should be stored into the shared_prefs
                 */
                System.out.println("response is "+ response);
            }
        });
        return code;
    }

    @Override
    protected void onPostExecute(String token) {
        Log.i("Token", "Access token retrieved:" + token);
    }


}
