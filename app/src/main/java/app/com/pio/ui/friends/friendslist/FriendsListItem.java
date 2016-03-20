package app.com.pio.ui.friends.friendslist;

import java.util.Arrays;

/**
 * Created by mmichaud on 3/6/16.
 */
public class FriendsListItem {
    private String name;
    private String userId;
    private String profileImageUrl;
    private String[] monuments;
    private int xp;

    public FriendsListItem(String name, String userId, String profileImageUrl, String[] monuments, int xp) {
        this.name = name;
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
        this.monuments = monuments;
        this.xp = xp;
    }

    public FriendsListItem(String name, String userId, String profileImageUrl, String monumentsString, int xp) {
        this.name = name;
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
        String[] monumentsArray = (String[]) Arrays.asList(monumentsString.split("\\s*,\\s*")).toArray();
        if (monumentsArray.length == 1) {
            if (monumentsArray[0].isEmpty()) {
                monumentsArray = new String[]{};
            }
        }
        this.monuments = monumentsArray;
        this.xp = xp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String[] getMonuments() {
        return monuments;
    }

    public String getMonumentsString() {
        String monumentsString = "";
        for (String m : monuments) {
            monumentsString += m + ", ";
        }
        return monumentsString;
    }

    public void setMonuments(String[] monuments) {
        this.monuments = monuments;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
