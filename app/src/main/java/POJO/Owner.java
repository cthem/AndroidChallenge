package POJO;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by sissy on 14/7/2017.
 */

public class Owner extends RealmObject
{
    private int id;

    private String login;

    @SerializedName("avatar_url")
    private String avatarUrl;

    private String url;

    private String type;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
