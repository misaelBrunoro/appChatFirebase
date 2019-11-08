package com.misael.appchat.model;

public class Message {
    private String text;
    private long timestamp;
    private String fromId;
    private String toId;

    public Message(String text, String fromId, String toId, long timestamp) {
        this.text = text;
        this.timestamp = timestamp;
        this.fromId = fromId;
        this.toId = toId;
    }

    public Message( ) { }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }
}
