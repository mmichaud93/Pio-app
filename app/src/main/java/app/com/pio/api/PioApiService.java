package app.com.pio.api;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by mmichaud on 6/5/15.
 */
public interface PioApiService {

    @GET("/users/new")
    public void newUser(@Query("email") String email, @Query("pass") String pass, @Query("type") String type,
                        @Query("device_name") String name, @Query("device_os") String deviceOs, @Query("device_app_ver") String deviceAppVer,
                        @Query("device_screen_width") int width, @Query("device_screen_height") int height, @Query("device_screen_ppi") float ppi,
                        Callback<PioApiResponse> callback);

    @GET("/users/exist")
    public void userExist(@Query("email") String email, Callback<PioApiResponse> callback);

    @GET("/users/login")
    public void loginUser(@Query("email") String email, @Query("pass") String pass,
                          @Query("device_name") String name, @Query("device_os") String deviceOs, @Query("device_app_ver") String deviceAppVer,
                          @Query("device_screen_width") int width, @Query("device_screen_height") int height, @Query("device_screen_ppi") float ppi,
                          Callback<PioApiResponse> callback);
}
