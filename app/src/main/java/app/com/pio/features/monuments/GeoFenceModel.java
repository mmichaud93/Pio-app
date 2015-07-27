package app.com.pio.features.monuments;

import com.google.android.gms.maps.model.LatLng;

import app.com.pio.ui.monuments.MonumentItem;

/**
 * Created by mmichaud on 7/23/15.
 */
public class GeoFenceModel {

    private MonumentItem monument;
    private LatLng latLng;
    private double radius;

    public GeoFenceModel(app.com.pio.ui.monuments.MonumentItem monument, LatLng latLng, double radius) {
        this.monument = monument;
        this.latLng = latLng;
        this.radius = radius;
    }

    public MonumentItem getMonument() {
        return monument;
    }

    public void setMonument(MonumentItem monument) {
        this.monument = monument;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
