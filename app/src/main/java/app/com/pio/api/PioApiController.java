package app.com.pio.api;

import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * Created by mmichaud on 6/5/15.
 */
public class PioApiController {

    static PioApiService pioApiService;

    static {
        if (pioApiService==null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://pio-api.herokuapp.com/api")
                    .setLogLevel(RestAdapter.LogLevel.BASIC)
                    .build();

            pioApiService = restAdapter.create(PioApiService.class);
        }
    }

    public static void sendNewUser(String email, String pass, String type, Callback<PioApiResponse> callback) {
        pioApiService.newUser(email, pass, type, callback);
    }

    public static void userExists(String email, Callback<PioApiResponse> callback) {
        pioApiService.userExist(email, callback);
    }
}
