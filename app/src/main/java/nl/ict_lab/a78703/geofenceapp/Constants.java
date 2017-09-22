package nl.ict_lab.a78703.geofenceapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class Constants
{
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 5 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 1609;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();

    static
    {
        // San Francisco International Airport.
        LANDMARKS.put("Moscone South", new LatLng(37.783888, -122.4009012));

        // Googleplex.
        LANDMARKS.put("Japantown", new LatLng(37.785281, -122.4296384));

        // Test
        LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));

        // Kapelstraat.
        LANDMARKS.put("Kapelstraat", new LatLng(51.90560028,4.45854619));

        // Leendert Langstraathof.
        LANDMARKS.put("Leendert Langstraathof", new LatLng(51.94276047, 4.36713314));
    }
}
