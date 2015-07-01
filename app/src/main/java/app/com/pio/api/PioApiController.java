package app.com.pio.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import app.com.pio.R;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by mmichaud on 6/5/15.
 */
public class PioApiController {

    private static String TAG = "PioApiController";

    static PioApiService pioApiService;

    public static void initializeController(final Context context) {
        if (pioApiService == null) {
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestInterceptor.RequestFacade request) {
                    request.addHeader("x-access-token", context.getString(R.string.pio_api_key_prod));
                }
            };
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(context.getString(R.string.pio_api_url_prod))
                    .setLogLevel(RestAdapter.LogLevel.BASIC)
                    .setRequestInterceptor(requestInterceptor)
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
