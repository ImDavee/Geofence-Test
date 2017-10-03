package nl.ict_lab.a78703.geofenceapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService
{
    protected static final String TAG = "GeofenceTransitionsIS";
    public static final int GEOFENCE_NOTIFICATION_ID = 0;

    public GeofenceTransitionsIntentService()
    {
        super(TAG);
    }

    /**
     * Sends notification when a geofence is entered.
     * @param intent Displays everything on other activities.
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError())
        {
            String errorMessage = GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);

            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        }
        else
        {
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    /**
     * Gets the transition details.
     * @param geofenceTransition The transition that triggers a geofence.
     * @param triggeringGeofences List of geofences which are triggered by a transition.
     * @returns the name of the geofence which is triggered by a transition.
     */
    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences)
    {
        String geofenceTransitionString = getTransitionString(geofenceTransition);
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();

        for (Geofence geofence : triggeringGeofences)
        {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }

        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Gets the string for the fitting transition.
     * @param transitionType Type of transition.
     * @returns a string for the fitting transition.
     */
    private String getTransitionString(int transitionType)
    {
        switch (transitionType)
        {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    /**
     * Sends the built notification.
     * @param notificationDetails Transition type + name of the triggered geofence.
     */
    private void sendNotification(String notificationDetails)
    {
        Intent notificationIntent = GeofenceManager.makeNotificationIntent(getApplicationContext(), notificationDetails);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(GeofenceManager.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(GEOFENCE_NOTIFICATION_ID, createNotification(notificationDetails, notificationPendingIntent));
    }

    /**
     * Builds a notification.
     * @param notificationDetails Transition type + name of the triggered geofence.
     * @param notificationPendingIntent Displays everything on other activities.
     * @returns the built notification.
     */
    private Notification createNotification(String notificationDetails, PendingIntent notificationPendingIntent)
    {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Geofence Notification!")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }
}
