package app.com.pio.models;

/**
 * Created by pilleym on 6/1/2015.
 */
public class ProfileModel {
    //information kept in profile: name,  experience (calculate level), prof pic, link to status object (not yet)

    String name;
    String profImage;
    int experience;


    public ProfileModel(String name, String profImage, int experience) {
        this.name = name;
        this.profImage = profImage;
        this.experience = experience;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfImage() {
        return profImage;
    }

    public void setProfImage(String profImage) {
        this.profImage = profImage;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
