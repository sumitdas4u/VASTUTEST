package com.utsavmobileapp.utsavapp.data;

/**
 * Created by Bibaswann on 24-03-2017.
 */

public class ChatMessage {

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    private  boolean unread;
    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String timestamp;
    private String uid;
    //empty constractor for firebase reference
    public ChatMessage() {
    }

    public ChatMessage(String text, String name, String photoUrl, String uid) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.timestamp=String.valueOf(System.currentTimeMillis() / 1000L);
        this.unread=true;
        this.uid=uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
