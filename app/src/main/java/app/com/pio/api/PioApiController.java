package app.com.pio.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * Created by mmichaud on 6/5/15.
 */
public class PioApiController {

    private static String TAG = "PioApiController";

    static PioApiService pioApiService;

    static {
        if (pioApiService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://pio-api.herokuapp.com/api")
                    .setLogLevel(RestAdapter.LogLevel.BASIC)
                    .build();

            pioApiService = restAdapter.create(PioApiService.class);
        }
    }

    public static void sendNewUser(Activity activity, String email, String pass, String type, Callback<PioApiResponse> callback) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        try {
            pioApiService.newUser(email, pass, type, Build.MODEL, "android " + Build.VERSION.RELEASE,
                    activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName,
                    size.x, size.y, activity.getResources().getDisplayMetrics().density, callback);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "error creating user", e);
        }
    }

    public static void userExists(String email, Callback<PioApiResponse> callback) {
        pioApiService.userExist(email, callback);
    }

    public static void loginUser(Activity activity, String email, String pass, Callback<PioApiResponse> callback) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        try {
            pioApiService.loginUser(email, pass, Build.MODEL, "android " + Build.VERSION.RELEASE,
                    activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName,
                    size.x, size.y, activity.getResources().getDisplayMetrics().density, callback);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
