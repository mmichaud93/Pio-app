package app.com.pio.ui.friends.friendslist;

/**
 * Created by mmichaud on 3/6/16.
 */
public class FriendsListItem {
    private String name;
    private String userId;
    private String profileImageUrl;
    private int xp;

    public FriendsListItem(String name, String userId, String profileImageUrl, int xp) {
        this.name = name;
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
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

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
