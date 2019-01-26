package com.vijay.saurabh.getconnect;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GeofenceReceiver extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private DatabaseReference ref;

    public GeofenceReceiver() {
        super("GeofenceReceiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ref= FirebaseDatabase.getInstance().getReference();
        GeofencingEvent geoEvent = GeofencingEvent.fromIntent(intent);
        if (geoEvent.hasError()) {
            Log.d("Hack36", "Error GeofenceReceiver.onHandleIntent");
        } else {
            Log.d("Hack36", "GeofenceReceiver : Transition -> "
                    + geoEvent.getGeofenceTransition());

            int transitionType = geoEvent.getGeofenceTransition();

            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER
                    || transitionType == Geofence.GEOFENCE_TRANSITION_DWELL
                    || transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                List<Geofence> triggerList = geoEvent.getTriggeringGeofences();

                for (Geofence geofence : triggerList) {
                    SimpleGeofence sg = SimpleGeofenceStore.getInstance()
                            .getSimpleGeofences().get(geofence.getRequestId());

                    String transitionName = "";
                    switch (transitionType) {
                        case Geofence.GEOFENCE_TRANSITION_DWELL:
                            transitionName = "dwell";
                            ref.child("status").setValue("2");
                            break;

                        case Geofence.GEOFENCE_TRANSITION_ENTER:
                            transitionName = "enter";
                            ref.child("status").setValue("1");
                            break;

                        case Geofence.GEOFENCE_TRANSITION_EXIT:
                            transitionName = "exit";
                            ref.child("status").setValue("0");
                            break;
                    }
                }
            }
        }
    }
}