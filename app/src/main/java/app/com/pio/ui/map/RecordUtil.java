package app.com.pio.ui.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.LocationManager;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import app.com.pio.R;
import app.com.pio.service.LocationUpdateService;
import app.com.pio.utility.PrefUtil;
import app.com.pio.utility.Util;

/**
 * Created by mmichaud on 6/12/15.
 */
public class RecordUtil {

    private static Activity activity;
    private static View dashboard;

    private static boolean isRecording = false;
    private static long sessionTimeStart = 0;
    private static Intent intent;

    private static String location;
    private static float gpsStrength = 100;
    private static float uncoveredKilometersSquared = 0;
    private static float distanceTravelled = 0;
    private static int numberOfPoints = 0;
    private static float speed = 0;

    private static TimerTask timerTask;
    private static Timer timer;

    public static void startRecording(Activity activity, View dashboard) {
        RecordUtil.activity = activity;
        RecordUtil.dashboard = dashboard;

        isRecording = true;
        sessionTimeStart = System.currentTimeMillis();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(RecordUtil.activity!=null) {
                    RecordUtil.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)RecordUtil.dashboard.findViewById(R.id.map_dashboard_session_time)).setText(Util.formatLongToTime(RecordUtil.getRecordingSessionTime()));
                        }
                    });
                }
            }
        };
        startTimer();

        startService(activity);
    }

    public static void stopRecording(Context context) {
        isRecording = false;
        stopTimer();
        if(intent!=null) {
            context.stopService(intent);
        }

        if(activity!=null) {
            float totalArea = PrefUtil.getPref(activity, PrefUtil.PREFS_STATS_AREA_KEY, 0f);
            totalArea += uncoveredKilometersSquared;
            PrefUtil.savePref(activity, PrefUtil.PREFS_STATS_AREA_KEY, totalArea);

            long totalTime = PrefUtil.getPref(activity, PrefUtil.PREFS_STATS_TIME_KEY, 0);
            totalTime += getRecordingSessionTime();
            PrefUtil.savePref(activity, PrefUtil.PREFS_STATS_TIME_KEY, totalTime);

            float totalDistance = PrefUtil.getPref(activity, PrefUtil.PREFS_STATS_DISTANCE_KEY, 0f);
            totalDistance += distanceTravelled;
            PrefUtil.savePref(activity, PrefUtil.PREFS_STATS_DISTANCE_KEY, totalDistance);
        }

        sessionTimeStart = 0;
        uncoveredKilometersSquared = 0;
        numberOfPoints = 0;
        distanceTravelled = 0;
        speed = 0;
    }

    public static void startService(Context context) {
        intent = new Intent(context, LocationUpdateService.class);
        context.startService(intent);
    }

    private static void startTimer() {
        timer = new Timer();

        timer.schedule(timerTask, 0, 1000); //
    }

    public static void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static void recordPoint(float kiloSquared) {
        if(isRecording) {
            uncoveredKilometersSquared+=kiloSquared;
            numberOfPoints++;
        }
    }

    public static long getRecordingSessionTime() {
        if(sessionTimeStart != 0) {
            return System.currentTimeMillis() - sessionTimeStart;
        } else {
            return 0;
        }
    }

    public static float getUncoveredKilometersSquared() {
        return uncoveredKilometersSquared;
    }

    public static void setUncoveredKilometersSquared(float area) {
        uncoveredKilometersSquared = area;
    }

    public static float getGpsStrength() {
        return gpsStrength;
    }

    public static void setGpsStrength(float gpsStrength) {
        RecordUtil.gpsStrength = gpsStrength;
    }

    public static boolean isRecording() {
        return isRecording;
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        RecordUtil.location = location;
    }

    public static long getSessionTimeStart() {
        return sessionTimeStart;
    }

    public static void setSessionTimeStart(long sessionTimeStart) {
        RecordUtil.sessionTimeStart = sessionTimeStart;
    }

    public static float getDistanceTravelled() {
        return distanceTravelled;
    }

    public static void setDistanceTravelled(float distanceTravelled) {
        RecordUtil.distanceTravelled = distanceTravelled;
    }

    public static int getNumberOfPoints() {
        return numberOfPoints;
    }

    public static void setNumberOfPoints(int numberOfPoints) {
        RecordUtil.numberOfPoints = numberOfPoints;
    }

    public static float getSpeed() {
        return speed;
    }

    public static void setSpeed(float speed) {
        RecordUtil.speed = speed;
    }
}
