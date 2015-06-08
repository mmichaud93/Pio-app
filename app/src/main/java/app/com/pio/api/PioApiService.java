package app.com.pio.api;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by mmichaud on 6/5/15.
 */
public interface PioApiService {

    @GET("/users/new")
    public void newUser(@Query("email") String email, @Query("pass") String pass, @Query("type") String type, Callback<PioApiResponse> callback);

    @GET("/users/exist")
    public void userExist(@Query("email") String email, Callback<PioApiResponse> callback);

    @GET("/users/login")
    public void loginUser(@Query("email") String email, @Query("pass") String pass, Callback<PioApiResponse> callback);
}
