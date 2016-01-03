package com.rodions.telenest;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rodion on 27.12.15.
 */
public class NestUserService {

    private static final Logger LOGGER = Logger.getLogger(TelegramService.class.getName());

    private Firebase fb;

    private List<NestThermostat> thermostats;

    public NestUserService(Properties props) {

        String fbUrl = props.getProperty("firebase.url");
        String token = props.getProperty("testuser.nest.token");

        fb = new Firebase(fbUrl);

        fb.auth(token, new Firebase.AuthListener() {
            @Override
            public void onAuthError(FirebaseError firebaseError) {
                LOGGER.log(Level.SEVERE, "Auth error");
            }

            @Override
            public void onAuthSuccess(Object o) {
                LOGGER.log(Level.INFO, "Auth OK");
                fb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        processSnapshot(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        LOGGER.log(Level.WARNING, "Cancelled");
                    }
                });
            }

            @Override
            public void onAuthRevoked(FirebaseError firebaseError) {
                LOGGER.log(Level.SEVERE, "Auth revoked");
            }
        });
    }

    public synchronized List<NestThermostat> getThermostats() {
        return thermostats;
    }

    public String adjustThermostat(Integer value) {
        Firebase thermostatChildren = fb.child("devices").child("thermostats");
        for (NestThermostat thermostat : getThermostats()) {
            thermostatChildren.child(thermostat.getId()).child("target_temperature_f").setValue(
                    value, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                LOGGER.log(Level.SEVERE, firebaseError.getMessage());
                            }
                        }
                    });
        }

        return "Done";
    }

    private synchronized void processSnapshot(DataSnapshot snapShot) {
        thermostats = new ArrayList<NestThermostat>();
        DataSnapshot thermostatChildren = snapShot.child("devices").child("thermostats");
        for (DataSnapshot thermostatChild : thermostatChildren.getChildren()) {
            thermostats.add(new NestThermostat(
                    thermostatChild.child("device_id").getValue().toString(),
                    thermostatChild.child("name").getValue().toString(),
                    thermostatChild.child("target_temperature_f").getValue().toString(),
                    thermostatChild.child("is_online").getValue().toString()
            ));
        }
    }
}
