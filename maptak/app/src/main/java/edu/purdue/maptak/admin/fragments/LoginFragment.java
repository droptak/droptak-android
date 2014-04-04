package edu.purdue.maptak.admin.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.activities.MainActivity;
import edu.purdue.maptak.admin.tasks.LoginTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.content.IntentSender.*;
import android.content.Intent;
import android.widget.Toast;

public class LoginFragment extends Fragment
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {

    /** Track whether the sign-in button has been clicked so that we know to resolve
     *  all issues preventing sign-in without waiting. */
    private boolean mSignInClicked;

    /** Store the connection result from onConnectionFailed callbacks so that we can
     *  resolve them when the user clicks sign-in. */
    private ConnectionResult mConnectionResult;


    /** Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /** Client used to interact with Google APIs. */
    public static GoogleApiClient mGoogleApiClient;

    /** A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents. */
    private boolean mIntentInProgress;

    private PendingIntent mSignInIntent;
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private int mSignInProgress;

    /** Inflates the view for this fragment. */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(MainActivity.LOG_TAG, "Creating LoginFragment and connecting GoogleAPIClient");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity().getBaseContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API, null)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();
        }

        boolean isLoggedIn = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(MainActivity.PREF_IS_LOGGED_IN, false);

        View v = null;
        if (isLoggedIn) {
            v = inflater.inflate(R.layout.fragment_splash, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_login, container, false);
            v.findViewById(R.id.sign_in_button).setOnClickListener(this);
        }

        return v;
    }

    private void resolveSignInError() {
        Log.d(MainActivity.LOG_TAG, "Resolving any signin errors");

        if (mSignInIntent != null) {
            try {
                mSignInProgress = STATE_IN_PROGRESS;
                mConnectionResult.startResolutionForResult(this.getActivity(), RC_SIGN_IN);
            } catch (SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.sign_in_button:
                if (!mGoogleApiClient.isConnecting()) {
                    Log.d(MainActivity.LOG_TAG, "Google+ Signin Pressed");
                    mSignInClicked = true;
                    resolveSignInError();
                }
                break;

        }

    }

    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (mSignInProgress != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            mSignInIntent = result.getResolution();
            mConnectionResult = result;
        }

        if (mSignInProgress == STATE_SIGN_IN) {
            // STATE_SIGN_IN indicates the user already clicked the sign in button
            // so we should continue processing errors until the user is signed in
            // or they click cancel.
            resolveSignInError();
        }
    }


    public void onConnected(Bundle connectionHint) {
        // We've resolved any connection errors.  mGoogleApiClient can be used to
        // access Google APIs on behalf of the user.
        Log.d(MainActivity.LOG_TAG, "Google+ signin complete. UN/Email are now in shared preferences.");

        mSignInClicked = false;
        String personName = "";
        String email = "";

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            personName = currentPerson.getDisplayName();
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);

            // Add them to the shared preferences
            SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(MainActivity.PREF_USER_NAME, personName);
            prefs.edit().putString(MainActivity.PREF_USER_EMAIL, email);
            prefs.edit().putBoolean(MainActivity.PREF_IS_LOGGED_IN, true);
        }

        Toast.makeText(this.getActivity(), "Welcome back, " + personName + " (" + email + ")", Toast.LENGTH_LONG).show();
        initiateMaptakAuthentication();

        // Replace the main view
        getFragmentManager().beginTransaction().replace(R.id.mainview, new LoginFragment()).commit();

    }

    public void initiateMaptakAuthentication() {
        LoginTask loginTask = new LoginTask(this.getActivity(), mGoogleApiClient);
        loginTask.execute();
    }

    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();

    }

    public void onDisconnected() {

    }

    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != Activity.RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e("debug", "User access revoked!");
                            mGoogleApiClient.connect();

                        }

                    });
        }
    }


}

