package app.com.pio.ui.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

import app.com.pio.service.LocationUpdateService;

/**
 * Created by mmichaud on 6/12/15.
 */
public class RecordUtil {

    private static boolean isRecording = false;
    private static long sessionTimeStart = 0;
    private static float gpsStrength = 100;
    private static Intent intent;

    private static float uncoveredKilometersSquared = 0;

    public static void startRecording(Context context) {
        isRecording = true;
        uncoveredKilometersSquared = 0;
        sessionTimeStart = System.currentTimeMillis();
        intent = new Intent(context, LocationUpdateService.class);
        context.startService(intent);
    }

    public static long getRecordingSessionTime() {
        return System.currentTimeMillis()-sessionTimeStart;
    }

    public static void stopRecording(Context context) {
        isRecording = false;
        if(intent!=null) {
            context.stopService(intent);
        }
    }

    public static void recordPoint(float kiloSquared) {
        if(isRecording) {
            uncoveredKilometersSquared+=kiloSquared;
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
}
