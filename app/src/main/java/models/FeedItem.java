package models;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;

/**
 * Created by unexpected_err on 20/05/2017.
 */

@IgnoreExtraProperties
public class FeedItem {

    private String id;
    private String username;
    private String userId;
    private String message;
    private String imageUrl;
    private String date;
    private String avatar;
    private HashMap<String, Boolean> likes = new HashMap<>();
    private HashMap<String, Boolean> unlikes = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HashMap<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, Boolean> likes) {
        this.likes = likes;
    }

    public HashMap<String, Boolean> getUnlikes() {
        return unlikes;
    }

    public void setUnlikes(HashMap<String, Boolean> unlikes) {
        this.unlikes = unlikes;
    }
}