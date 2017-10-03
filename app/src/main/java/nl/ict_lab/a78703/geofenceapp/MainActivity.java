package nl.ict_lab.a78703.geofenceapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
{
    private Button addGeofencesButton;
    private Button removeGeofencesButton;
    GeofenceManager geofenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geofenceManager = new GeofenceManager(this);

        addGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
        removeGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);

        addGeofencesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                geofenceManager.setGeofence(51.94276047, 4.36713314, 1609);
            }
        });

        removeGeofencesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                geofenceManager.removeCurrentGeofence();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }
}
