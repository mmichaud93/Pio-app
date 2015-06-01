package app.com.pio.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

/**
 * Created by mmichaud on 5/28/15.
 */
public class PrefUtil {

    public static final String PREFS_LOGIN_TYPE_KEY = "TYPE";
    public static final String PREFS_LOGIN_EMAIL_KEY = "EMAIL";
    public static final String PREFS_LOGIN_PASSWORD_KEY = "PASSWORD";

    public enum LoginTypes {
        GOOGLE, EMAIL
    }


    public static void savePrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //Log.d("PIO", "saved: "+value+", encrpyt: "+encryptText(value));
        prefs.edit().putString(key, encryptText(value)).apply();
    }

    public static String getStringPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            //Log.d("PIO", "loaded: "+sharedPrefs.getString(key, defaultValue)+", decrpyt: "+decryptText(sharedPrefs.getString(key, defaultValue)));
            return decryptText(sharedPrefs.getString(key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    private static final String KEY = "tomlisi-makipilley-matthewmichaud";

    private static String encryptText(String text) {
        return new String(Base64.encode(xor(text.getBytes()), 0));
    }

    private static  String decryptText(String hash) {
        try {
            return new String(xor(Base64.decode(hash.getBytes(), 0)), "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static byte[] xor(final byte[] input) {
        final byte[] output = new byte[input.length];
        final byte[] secret = KEY.getBytes();
        int spos = 0;
        for (int pos = 0; pos < input.length; ++pos) {
            output[pos] = (byte) (input[pos] ^ secret[spos]);
            spos += 1;
            if (spos >= secret.length) {
                spos = 0;
            }
        }
        return output;
    }
}
