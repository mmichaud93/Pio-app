package app.com.pio.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import app.com.pio.models.ProfileModel;

/**
 * Created by mmichaud on 6/5/15.
 */
public class FriendsProfileResponse extends PioApiResponse {

    @Expose
    @SerializedName("friends_profile")
    FriendsProfile friendsProfile;

    public FriendsProfileResponse(FriendsProfile friendsProfile) {
        this.friendsProfile = friendsProfile;
    }

    public FriendsProfile getFriendsProfile() {
        return friendsProfile;
    }

    public void setFriendsProfile(FriendsProfile friendsProfile) {
        this.friendsProfile = friendsProfile;
    }

    public class FriendsProfile {
        @Expose
        @SerializedName("name")
        String name;

        @Expose
        @SerializedName("image")
        String image;

        @Expose
        @SerializedName("xp")
        int xp;

        @Expose
        @SerializedName("monuments")
        ArrayList<String> monuments;

        public FriendsProfile(String name, String image, int xp, ArrayList<String> monuments) {
            this.name = name;
            this.image = image;
            this.xp = xp;
            this.monuments = monuments;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getXp() {
            return xp;
        }

        public void setXp(int xp) {
            this.xp = xp;
        }

        public ArrayList<String> getMonuments() {
            return monuments;
        }

        public void setMonuments(ArrayList<String> monuments) {
            this.monuments = monuments;
        }
    }
}
