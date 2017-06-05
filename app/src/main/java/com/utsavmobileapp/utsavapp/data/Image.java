package com.utsavmobileapp.utsavapp.data;

import java.io.Serializable;

/**
 * Created by Bibaswann on 21-05-2016.
 */
public class Image implements Serializable {
    private String name;
    private String small, medium, large;
    private String uploader, place, uploaderDp;
    private Integer totalike, totalcomment;
    private String timestamp;
    private boolean isLiked;

    public Image() {
    }

    public Image(String name, String small, String medium, String large, String timestamp) {
        this.name = name;
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUploaderDp() {
        return uploaderDp;
    }

    public void setUploaderDp(String uploaderDp) {
        this.uploaderDp = uploaderDp;
    }

    public Integer getTotalike() {
        return totalike;
    }

    public void setTotalike(Integer totalike) {
        this.totalike = totalike;
    }

    public Integer getTotalcomment() {
        return totalcomment;
    }

    public void setTotalcomment(Integer totalcomment) {
        this.totalcomment = totalcomment;
    }

    public boolean getLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}

