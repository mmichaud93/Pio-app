package app.com.pio.utility;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.com.pio.R;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by mmichaud on 5/29/15.
 */
public class Util {

    static Style croutonStyle;

    private static void makeStyle() {
        croutonStyle = new Style.Builder().setBackgroundColor(R.color.app_primary_dark).setTextColor(android.R.color.white).build();
    }

    public static void makeCroutonText(String text, Activity activity) {
        if(croutonStyle == null) {
            makeStyle();
        }
        Crouton.makeText(activity, text, croutonStyle, R.id.crouton_handle).show();
    }

    public static float dpToPx(float dp, Context context) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    public static float pxToDp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static String formatLongToTime(long time) {
        time/=1000;
        int hours = (int) (time / 3600);
        int minutes = (int) (time / 60);
        int seconds = (int) (time - minutes * 60 - hours * 3600);

        return (hours < 10 ? hours : hours)+":"+(minutes < 10 ? "0"+minutes : minutes)+":"+(seconds < 10 ? "0"+seconds : ""+seconds);
    }

    public enum ValidateType {
        EMAIL, PASSWORD, TEXT
    }

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PASSWORD_PATTERN = "^" +
            "(?=.*[0-9])" +
            "(?=.*[a-z])" +
            "(?=.*[A-Z])" +
            "(?=\\S+$).{8,}$";

    public static boolean validateText(String text, ValidateType type) {

        Pattern pattern;
        Matcher matcher;
        if(type == ValidateType.EMAIL) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(text);
            return matcher.matches();
        } else if(type == ValidateType.PASSWORD) {
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(text);
            return matcher.matches();
        } else if(type == ValidateType.TEXT) {
            return (text.length() > 0);
        } else {
            return false;
        }

    }

    public static float distanceLatLng(LatLng latLngA, LatLng latLngB)
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(latLngB.latitude-latLngA.latitude);
        double lngDiff = Math.toRadians(latLngB.longitude-latLngA.longitude);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(latLngA.latitude)) * Math.cos(Math.toRadians(latLngB.latitude)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    public static String loadJSONFromAsset(Context context, String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
