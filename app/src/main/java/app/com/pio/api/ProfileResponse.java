package app.com.pio.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import app.com.pio.models.ProfileModel;

/**
 * Created by mmichaud on 6/5/15.
 */
public class ProfileResponse extends PioApiResponse {

    @Expose
    @SerializedName("profile")
    ProfileModel profile;

    public ProfileResponse(ProfileModel profile) {
        this.profile = profile;
    }

    public ProfileModel getProfile() {
        return profile;
    }

    public void setProfile(ProfileModel profile) {
        this.profile = profile;
    }
}
