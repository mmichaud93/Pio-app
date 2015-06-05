package app.com.pio.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mmichaud on 6/5/15.
 */
public class PioApiResponse {

    @Expose
    @SerializedName("code")
    int code;

    @Expose
    @SerializedName("msg")
    String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
