package app.com.pio.models;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.utility.PrefUtil;

/**
 * Created by pilleym on 6/1/2015.
 */
public class ProfileModel {
    //information kept in profile: name,  xp (calculate level), prof pic, link to status object (not yet)

    @Expose
    @SerializedName("name")
    String name;
    @Expose
    @SerializedName("email")
    String email;
    @Expose
    @SerializedName("pass")
    String pass;
    @Expose
    @SerializedName("facebook")
    FacebookPart facebook;
    @Expose
    @SerializedName("image")
    String image;
    @Expose
    @SerializedName("premium")
    boolean premium;
    @Expose
    @SerializedName("monuments")
    ArrayList<String> monuments;
    @Expose
    @SerializedName("xp")
    int xp;
    @Expose
    @SerializedName("createdAt")
    long createdAt;
    @Expose
    @SerializedName("lastUpdated")
    long lastUpdated;

    public ProfileModel(String name, String email, String pass, FacebookPart facebook, String image, boolean premium, ArrayList<String> monuments, int xp, long createdAt, long lastUpdated) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.facebook = facebook;
        this.image = image;
        this.premium = premium;
        this.monuments = monuments;
        this.xp = xp;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    public ProfileModel(String name, String email, String pass, String facebookUserId, String image, boolean premium, ArrayList<String> monuments, int xp, long createdAt, long lastUpdated) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.facebook = new FacebookPart(facebookUserId);
        this.image = image;
        this.premium = premium;
        this.monuments = monuments;
        this.xp = xp;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    public void saveProfile() {
        this.lastUpdated = System.currentTimeMillis();
        ProfileManager.saveActiveProfile();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        saveProfile();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        saveProfile();
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
        saveProfile();
    }

    public FacebookPart getFacebook() {
        return facebook;
    }

    public void setFacebook(FacebookPart facebook) {
        this.facebook = facebook;
    }

    public void setFacebook(String facebookUserId) {
        this.facebook = new FacebookPart(facebookUserId);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        saveProfile();
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
        saveProfile();
    }

    public ArrayList<String> getMonuments() {
        return monuments;
    }

    public void setMonuments(ArrayList<String> monuments) {
        this.monuments = monuments;
        saveProfile();
    }

    public void addMonument(String monumentId) {
        if (!this.monuments.contains(monumentId)) {
            this.monuments.add(monumentId);
            saveProfile();
        }
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
        saveProfile();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        saveProfile();
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public class FacebookPart {

        @Expose
        @SerializedName("user_id")
        String userId;

        public FacebookPart(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
