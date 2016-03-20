package app.com.pio.features.profiles;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import app.com.pio.models.ProfileModel;
import app.com.pio.ui.monuments.CityItem;
import app.com.pio.utility.PrefUtil;

/**
 * Created by mmichaud on 7/24/15.
 */
public class ProfileManager {
    public static ProfileModel activeProfile;
    public static Context context;

    public static boolean loadActiveProfile(Context context) {
        ProfileManager.context = context;

        Set<String> monumentsSet = PrefUtil.getPrefSet(context, "PROFILE_MONUMENTS", new HashSet<String>());
        ArrayList<String> monuments = new ArrayList<String>(monumentsSet);


        Set<String> cityIdsAndCountsSet = PrefUtil.getPrefSet(context, "PROFILE_CITY_POINTS", new HashSet<String>());
        ArrayList<String> cityIdsAndCounts = new ArrayList<>(cityIdsAndCountsSet);
        ArrayList<ProfileModel.StatsModel> stats = new ArrayList<>();
        for (String string : cityIdsAndCounts) {
            String[] splitString = string.split(":");
            if (splitString.length > 1 && splitString[0] != null && splitString[1] != null) {
                stats.add(new ProfileModel.StatsModel(splitString[0], Integer.parseInt(splitString[1])));
            }
        }

        activeProfile = new ProfileModel(
                PrefUtil.getPref(context, "PROFILE_NAME", null),
                PrefUtil.getPref(context, "PROFILE_EMAIL", null),
                PrefUtil.getPref(context, "PROFILE_PASS", null),
                PrefUtil.getPref(context, "PROFILE_FACEBOOK_USER_ID", null),
                PrefUtil.getPref(context, "PROFILE_IMAGE", null),
                PrefUtil.getPref(context, "PROFILE_PREMIUM", false),
                stats,
                monuments,
                PrefUtil.getPref(context, "PROFILE_XP", 0),
                PrefUtil.getPref(context, "PROFILE_CREATED_AT", 0l),
                PrefUtil.getPref(context, "PROFILE_LAST_UPDATED", 0l)
        );


        return true;
    }

    public static boolean loadActiveProfile() {
        if (context != null) {
            return loadActiveProfile(context);
        } else {
            return false;
        }
    }

    public static boolean saveActiveProfile(Context context) {
        ProfileManager.context = context;

        PrefUtil.savePref(context, "PROFILE_NAME", activeProfile.getName());
        PrefUtil.savePref(context, "PROFILE_EMAIL", activeProfile.getEmail());
        PrefUtil.savePref(context, "PROFILE_PASS", activeProfile.getPass());
        PrefUtil.savePref(context, "PROFILE_FACEBOOK_USER_ID", activeProfile.getFacebook().getUserId());
        PrefUtil.savePref(context, "PROFILE_IMAGE", activeProfile.getImage());
        PrefUtil.savePref(context, "PROFILE_PREMIUM", activeProfile.isPremium());
        PrefUtil.savePref(context, "PROFILE_MONUMENTS", new HashSet<String>(activeProfile.getMonuments()));
        PrefUtil.savePref(context, "PROFILE_CITY_POINTS", new HashSet<String>(activeProfile.getCityIdsAndCounts()));
        PrefUtil.savePref(context, "PROFILE_XP", activeProfile.getXp());
        PrefUtil.savePref(context, "PROFILE_CREATED_AT", activeProfile.getCreatedAt());
        PrefUtil.savePref(context, "PROFILE_LAST_UPDATED", activeProfile.getLastUpdated());

        return true;
    }

    public static boolean saveActiveProfile() {
        if (context != null) {
            return saveActiveProfile(context);
        } else {
            return false;
        }
    }

    public static boolean monumentIsUnlocked(String monumentId) {
        if (activeProfile != null) {
            return activeProfile.getMonuments().contains(monumentId);
        }
        return false;
    }
}
