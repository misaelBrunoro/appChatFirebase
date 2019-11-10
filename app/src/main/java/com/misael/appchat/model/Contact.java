package com.misael.appchat.model;

public class Contact {
    private String uuid;
    private String username;
    private String lastMessage;
    private long timestamp;
    private String photoURL;

    public Contact(String uuid, String username, String lastMessage, long timestamp, String photoURL) {
        this.uuid = uuid;
        this.username = username;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.photoURL = photoURL;
    }

    public Contact() { }

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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
