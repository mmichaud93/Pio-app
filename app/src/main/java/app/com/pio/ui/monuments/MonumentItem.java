package app.com.pio.ui.monuments;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by mmichaud on 7/21/15.
 */
public class MonumentItem implements Serializable {

    String id;
    String name;
    String location;

    boolean isUnlocked;

    int bitmapLocked;
    int bitmapUnlocked;

    double pinLat;
    double pinLong;
    double radius;

    public MonumentItem(String id, String name, String location, int bitmapLocked, int bitmapUnlocked, double pinLat, double pinLong, double radius) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.bitmapLocked = bitmapLocked;
        this.bitmapUnlocked = bitmapUnlocked;
        this.pinLat = pinLat;
        this.pinLong = pinLong;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getBitmapLocked() {
        return bitmapLocked;
    }

    public void setBitmapLocked(int bitmapLocked) {
        this.bitmapLocked = bitmapLocked;
    }

    public int getBitmapUnlocked() {
        return bitmapUnlocked;
    }

    public void setBitmapUnlocked(int bitmapUnlocked) {
        this.bitmapUnlocked = bitmapUnlocked;
    }

    public double getPinLat() {
        return pinLat;
    }

    public void setPinLat(double pinLat) {
        this.pinLat = pinLat;
    }

    public double getPinLong() {
        return pinLong;
    }

    public void setPinLong(double pinLong) {
        this.pinLong = pinLong;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setIsUnlocked(boolean isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
