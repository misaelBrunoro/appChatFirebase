package com.misael.appchat.model;

public class User {
    private String uuid;
    private String username;
    private String profileURL;

    public User() { }

    public User(String uuid, String username, String profileURL) {
        this.uuid = uuid;
        this.username = username;
        this.profileURL = profileURL;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }
}
