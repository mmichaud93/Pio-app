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

import java.text.DecimalFormat;

import app.com.pio.R;
import app.com.pio.database.MVDatabase;
import app.com.pio.ui.main.MainActivity;
import app.com.pio.ui.map.RecordUtil;

/**
 * Created by mmichaud on 6/14/15.
 */
public class LocationUpdateService extends Service {

    private boolean started = false;
    LocationManager locationManager;
    private int NOTIFICATION_ID = 0;
    float kiloSquared = 0;

    public static String AREA_KEY = "area";
    public static String SESSION_KEY = "session";

    DecimalFormat areaFormat = new DecimalFormat("#.###");

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

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            registerLocationChange(location);
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
        if (MVDatabase.storePoint((float) location.getLatitude(), (float) location.getLongitude(), true)) {
            float uncoveredArea = (float) ((8+(Math.random()*2-1))/1000f);
            RecordUtil.recordPoint(uncoveredArea);
            kiloSquared += uncoveredArea;
            NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, buildNotification());
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
