package edu.purdue.maptak.admin.managers;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

/** Class which handles returns a user's location when required.
 *  Create it normally.
 *  Calls to getLat() or getLng() should be enclosed in an if(isLocationAvailable()) statement.
 *  Call disconnect() when you're finished with it. */

public class UserLocationManager
        implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    /** Stores the user's location at all times */
    private LocationClient userLocation;

    /** Whether or not the location is currently good */
    private boolean isAvailable;

    /** Constructor. Pass in context of activity. */
    public UserLocationManager(Context c) {
        userLocation = new LocationClient(c, this, this);
        userLocation.connect();
    }

    /** Returns whether the location is currently available */
    public boolean isLocationAvailable() {
        if (userLocation.getLastLocation() != null) {
            return isAvailable;
        }
        return false;
    }

    /** Call this when you're finished */
    public void disconnect() {
        userLocation.disconnect();
    }

    /** Returns the user's latitude */
    public double getLat() {
        return userLocation.getLastLocation().getLatitude();
    }

    /** Returns the user's longitude */
    public double getLng() {
        return userLocation.getLastLocation().getLongitude();
    }

    @Override
    public void onConnected(Bundle bundle) {
        isAvailable = true;
    }

    @Override
    public void onDisconnected() {
        isAvailable = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        isAvailable = false;
    }
}
