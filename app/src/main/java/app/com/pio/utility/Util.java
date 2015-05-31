package app.com.pio.utility;

import android.app.Activity;
import android.content.Context;

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
}
