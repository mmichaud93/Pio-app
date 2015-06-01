package app.com.pio.models;

/**
 * Created by mmichaud on 5/29/15.
 */
public class WelcomePageModel {

    String message;
    int image;

    public WelcomePageModel(String message, int image) {
        this.message = message;
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
