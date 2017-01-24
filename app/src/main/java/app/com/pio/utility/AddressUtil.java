package app.com.pio.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.main.MainActivity;

/**
 * Created by mmichaud on 4/3/16.
 */
public class AddressUtil {

    static Context context;
    static ArrayList<LatLng> addresses = new ArrayList<>();
    static Thread thread = null;
    static boolean running = false;
    static boolean started = false;
    static int i = 0;

    public static void lookupAddress(LatLng latLng, final Context context) {

        if (thread == null) {
            AddressUtil.context = context;
            addresses = new ArrayList<>();
            i = 0;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!arrayIsEmpty()) {

                        LatLng latLng1 = addresses.get(i);
                        Log.d("i","i: "+i+"/"+addresses.size());
                        if (latLng1 == null) {
                            continue;
                        }
                        List<Address> addresses;
                        Geocoder geocoder = new Geocoder(AddressUtil.context);

                        try {
                            addresses = geocoder.getFromLocation(latLng1.latitude, latLng1.longitude, 1);
                            if (addresses.size() > 0) {
                                AddressUtil.addresses.set(i, null);
                                i++;
                                ProfileManager.activeProfile.addCityPoint(
                                        addresses.get(0).getLocality(),
                                        addresses.get(0).getAdminArea(),
                                        addresses.get(0).getCountryName());
                                continue;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        RequestFuture<JSONObject> future = RequestFuture.newFuture();
                        try {
                            JsonRequest<JSONObject> addressRequest = new JsonRequest<JSONObject>(Request.Method.GET,
                                    "http://maps.google.com/maps/api/geocode/json?latlng=" + URLEncoder.encode(latLng1.latitude + "", "utf-8") + "," + URLEncoder.encode(latLng1.longitude + "", "utf-8") + "&sensor=false",
                                    "",
                                    future,
                                    future) {

                                @Override
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    JSONObject jsonResponse = null;
                                    String jsonString = new String(response.data);
                                    if (!TextUtils.isEmpty(jsonString)) {
                                        try {
                                            AddressUtil.addresses.set(i, null);
                                            i++;
                                            jsonResponse = new JSONObject(jsonString);
                                            ProfileManager.activeProfile.addCityPoint(
                                                    jsonResponse.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("long_name"),
                                                    jsonResponse.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(4).getString("long_name"),
                                                    jsonResponse.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("long_name"));
                                        } catch (JSONException e) {
                                            Log.d("ERRO", jsonString);
                                            Log.e("EROR", e.toString(), e);
                                        }
                                    }

                                    return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
                                }
                            };
                            future.setRequest(MainActivity.queue.add(addressRequest));

                            JSONObject response = future.get();
                        } catch (InterruptedException e) {
                        } catch (ExecutionException e) {
                        } catch (UnsupportedEncodingException e) {
                        }

                        if (i >= AddressUtil.addresses.size()) {
                            i = 0;
                        }
                    }
                    AddressUtil.addresses = new ArrayList<>();
                    running = false;
                }
            });
        }
        addresses.add(latLng);
        if (!running) {
            i = 0;
            if (started) {
                thread.run();
            } else {
                started = true;
                thread.start();
            }
            running = true;
        }
    }

    private static boolean arrayIsEmpty() {
        for (LatLng latLng : addresses) {
            if (latLng != null) {
                return false;
            }
        }
        return true;
    }
}
