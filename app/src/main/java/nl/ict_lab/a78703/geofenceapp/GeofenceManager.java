package nl.ict_lab.a78703.geofenceapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

public class GeofenceManager
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>
{
    public GoogleApiClient googleApiClient;

    public SharedPreferences preferences;
    public Context context;

    private static final String PREFS_TAG = "preferences_tag";
    private static final String REQUEST_ID = "geofence_home_id";

    private static String LATITUDE_KEY = "latitude";
    private static String LONGITUDE_KEY = "longitude";
    private static String RADIUS_KEY = "radius";

    /**
     * Runs the GoogleApiClient builder.
     * @param context Context connected with the MainActivity.
     */
    public GeofenceManager(Context context)
    {
        this.context = context;
        buildGoogleApiClient();
        googleApiClient.connect();
    }

    /**
     * Gets the geofence currently in the SharedPreferences.
     * @returns the saved SharedPreference
     */
    @Nullable
    public String getCurrentGeofence()
    {
        return preferences.getString("request_id", null);
    }

    /**
     * Builds and sets the geofence in the correct place using the latitude and longitude. Saves the parameters in a SharedPreferences.
     * @param geofenceLatitude Latitude of the geofence
     * @param geofenceLongitude Longitude of the geofence
     * @param geofenceRadius Radius of the geofence in meters
     */
    public void setGeofence(double geofenceLatitude, double geofenceLongitude, float geofenceRadius)
    {
        preferences = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);

        Geofence geofence = new Geofence.Builder()
                .setRequestId(REQUEST_ID)
                .setCircularRegion(geofenceLatitude, geofenceLongitude, geofenceRadius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        try
        {
            LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, getGeofencePendingIntent()).setResultCallback(this);

            preferences.edit()
                    .putFloat(LATITUDE_KEY, (float) geofenceLatitude)
                    .putFloat(LONGITUDE_KEY, (float) geofenceLongitude)
                    .putFloat(RADIUS_KEY, geofenceRadius)
                    .apply();
        }
        catch (SecurityException securityException)
        {
            securityException.printStackTrace();
        }
    }

    /**
     * Removes the geofence currently saved in the sharedPreferences.
     */
    public void removeCurrentGeofence()
    {
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, getGeofencePendingIntent());

        preferences.edit()
                .remove(LATITUDE_KEY)
                .remove(LONGITUDE_KEY)
                .remove(RADIUS_KEY)
                .apply();
    }

    /**
     * Builds the GoogleApiClient.
     */
    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.d("GeofenceManager", "Connected");
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.d("GeofenceManager", "Connection failed");
    }

    @Override
    public void onResult(@NonNull Status status)
    {
        if (status.isSuccess())
        {
            Toast.makeText(context, "Geofences Added", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String errorMessage = GeofenceErrorMessages.getErrorString(context, status.getStatusCode());
        }
    }

    private PendingIntent getGeofencePendingIntent()
    {
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        return android.app.PendingIntent.getService(context, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static Intent makeNotificationIntent(Context geofenceService, String notificationDetails)
    {
        return new Intent(geofenceService, MainActivity.class);
    }
}
