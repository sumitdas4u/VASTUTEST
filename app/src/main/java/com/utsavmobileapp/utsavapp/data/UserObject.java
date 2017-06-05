package com.utsavmobileapp.utsavapp.data;

import java.io.Serializable;

public class UserObject implements Serializable {
    private String id, name, photo, review, primg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getPrimg() {
        return primg;
    }

    public void setPrimg(String primg) {
        this.primg = primg;
    }
}

