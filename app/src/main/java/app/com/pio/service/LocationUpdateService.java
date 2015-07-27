package app.com.pio.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

import app.com.pio.R;
import app.com.pio.database.MVDatabase;
import app.com.pio.features.monuments.MonumentManager;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.main.MainActivity;
import app.com.pio.ui.map.MaskTileProvider;
import app.com.pio.ui.map.RecordUtil;
import app.com.pio.ui.monuments.MonumentItem;
import app.com.pio.utility.Util;

/**
 * Created by mmichaud on 6/14/15.
 */
public class LocationUpdateService extends Service {

    private boolean started = false;
    LocationManager locationManager;
    private int NOTIFICATION_ID = 0;
    private int NOTIFICATION_MONUMENT_ID = 1;
    float kiloSquared = 0;

    public static String AREA_KEY = "area";
    public static String SESSION_KEY = "session";

    DecimalFormat areaFormat = new DecimalFormat("#.###");

    Location previousLocation;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!started) {
            started = true;
            NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NOTIFICATION_ID, buildNotification());

            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, locationListener);
            }
        }
        return Service.START_NOT_STICKY;
    }

    private Notification buildNotification() {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        Intent resultIntent = new Intent(this, MainActivity.class);

        Bundle sessionData = new Bundle();
        sessionData.putFloat(AREA_KEY, kiloSquared);
        resultIntent.putExtra(SESSION_KEY, sessionData);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent).setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setContentTitle(getString(R.string.service_title))
                .setContentText(getString(R.string.service_text, areaFormat.format(kiloSquared)))
                .setOngoing(true);
        return builder.build();
    }

    private Notification buildMonumentNotification(MonumentItem monumentItem) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        Intent resultIntent = new Intent(this, MainActivity.class);

        Bundle sessionData = new Bundle();
        sessionData.putString("monument_id", monumentItem.getId());
        resultIntent.putExtra("monument_key", sessionData);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent).setSmallIcon(monumentItem.getBitmapUnlocked())
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), monumentItem.getBitmapUnlocked()))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Unlocked a new Monument!")
                .setContentText(monumentItem.getName())
                .setOngoing(false);
        return builder.build();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Log.d("PIO-LocUpdateService", "location.speed: " + location.getSpeed());
            int speed = (int) (location.getSpeed()+1);


//                    (float) Math.sqrt((previousLocation.getLatitude()-location.getLatitude())*(previousLocation.getLatitude()-location.getLatitude())+
//                    ( previousLocation.getLongitude()-location.getLongitude())*(previousLocation.getLongitude()-location.getLongitude()));
            RecordUtil.setSpeed(speed);
            if(previousLocation != null) {
                float distance = Util.distanceLatLng(
                        new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude()),
                        new LatLng(location.getLatitude(), location.getLongitude()));

                RecordUtil.setDistanceTravelled(RecordUtil.getDistanceTravelled() + distance);

                if (distance <= 0.001) {
                    if (location.getSpeed() > 10) {
                        for (int i = 0; i < speed; i++) {
                            registerLocationChange(new LatLng(
                                    (float) (previousLocation.getLatitude() + (location.getLatitude() - previousLocation.getLatitude()) / location.getSpeed() * i),
                                    (float) (previousLocation.getLongitude() + (location.getLongitude() - previousLocation.getLongitude()) / location.getSpeed() * i)
                            ));
                        }
                    } else {
                        registerLocationChange(location);
                    }


                } else {
                    registerLocationChange(location);
                }
            } else {
                registerLocationChange(location);
            }


            previousLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public void registerLocationChange(Location location) {
        registerLocationChange(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public void registerLocationChange(LatLng latLng) {
        MonumentItem monumentItem = MonumentManager.isInGeoFence(latLng);
        if(monumentItem != null) {
            // unlocked a monument
            Log.d("PIO", "[LocationUpdateService] unlocked monument "+monumentItem.getName());
            monumentItem.setIsUnlocked(true);
            ProfileManager.activeProfile.addMonument(monumentItem.getId());
            NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_MONUMENT_ID, buildMonumentNotification(monumentItem));
        }
        if (MVDatabase.storePoint((float) latLng.latitude, (float) latLng.longitude, true)) {
            float uncoveredArea = (float) ((8+(Math.random()*2-1))/1000f);
            RecordUtil.recordPoint(uncoveredArea);
            kiloSquared += uncoveredArea;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
