package app.com.pio.features.monuments;

import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.monuments.CityItem;
import app.com.pio.ui.monuments.MonumentItem;
import app.com.pio.utility.Util;

/**
 * Created by mmichaud on 7/21/15.
 */
public class MonumentManager {

    public static ArrayList<CityItem> cities;
    public static ArrayList<GeoFenceModel> geoFences;
    public static CityItem currentCity;

    public static void initializeMonuments(Context context) {
        try {
            cities = new ArrayList<>();
            geoFences = new ArrayList<>();

            JSONObject citiesObj = new JSONObject(Util.loadJSONFromAsset(context, "cities.json"));
            JSONArray citiesArr = citiesObj.getJSONArray("cities");
            for (int i = 0; i < citiesArr.length(); i++) {
                JSONObject city = citiesArr.getJSONObject(i);

                ArrayList<MonumentItem> monuments = new ArrayList<>();
                JSONArray monumentsArr = city.getJSONArray("monuments");
                for(int r = 0; r < monumentsArr.length(); r++) {
                    JSONObject monument = monumentsArr.getJSONObject(r);
                    monuments.add(new MonumentItem(
                            monument.getString("id"),
                            monument.getString("name"),
                            monument.getString("location"),
                            monument.getInt("xp_value"),
                            context.getResources().getIdentifier(monument.getString("bitmap_locked") + "_large", "drawable", context.getPackageName()),
                            context.getResources().getIdentifier(monument.getString("bitmap_unlocked") + "_large", "drawable", context.getPackageName()),
                            context.getResources().getIdentifier(monument.getString("bitmap_locked")+"_med", "drawable", context.getPackageName()),
                            context.getResources().getIdentifier(monument.getString("bitmap_unlocked")+"_med", "drawable", context.getPackageName()),
                            context.getResources().getIdentifier(monument.getString("bitmap_locked")+"_small", "drawable", context.getPackageName()),
                            context.getResources().getIdentifier(monument.getString("bitmap_unlocked")+"_small", "drawable", context.getPackageName()),
                            monument.getJSONObject("pin").getDouble("lat"),
                            monument.getJSONObject("pin").getDouble("lon"),
                            monument.getJSONObject("pin").getDouble("radius")
                    ));
                    geoFences.add(new GeoFenceModel(
                            monuments.get(monuments.size()-1), new LatLng(
                            monument.getJSONObject("pin").getDouble("lat"),
                            monument.getJSONObject("pin").getDouble("lon")),
                            monument.getJSONObject("pin").getDouble("radius")));
                }

                cities.add(new CityItem(city.getString("name"), monuments));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int getMonumentImage(String monument) {

        for (CityItem cityItem : cities) {
            for (MonumentItem monumentItem : cityItem.getMonumentItems()) {
                if (monumentItem.getId().equals(monument)) {
                    return monumentItem.getBitmapUnlockedLarge();
                }
            }
        }

        return -1;
    }

    public static MonumentItem isInGeoFence(LatLng latLng) {
        for (GeoFenceModel model: geoFences) {
            float[] results = new float[1];
            Location.distanceBetween(
                    model.getLatLng().latitude, model.getLatLng().longitude,
                    latLng.latitude, latLng.longitude, results);
            float distance = results[0];
            if(distance < model.getRadius() && !(model.getMonument().isUnlocked() || (ProfileManager.activeProfile.getMonuments().contains(model.getMonument().getId())))) {
                Log.d("PIO", "[MonumentManager] within range ("+distance+") of "+model.getMonument().getName());
                return model.getMonument();
            }
        }
        return null;
    }
}
