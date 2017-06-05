package com.utsavmobileapp.utsavapp.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bibaswann on 15-05-2016.
 */
public class StoryObject implements Serializable {

    private UserObject uob;
    private FestivalObject fob;
    private String lastUpdate;
    private Integer storyId;
    private String userComment;
    private String userRating;
    private Boolean isLiked, isBookmarked;
    private List<String> otherImg = new ArrayList<>();
    private List<String> otherImgNrml = new ArrayList<>();
    private List<String> otherImgBig = new ArrayList<>();
    private List<String> otherImgId = new ArrayList<>();
    private List<String> otherImglk = new ArrayList<>();
    private List<String> otherImgcmt = new ArrayList<>();
    private List<Boolean> otherImgIsLiked = new ArrayList<>();
    private Integer numLike;
    private Integer numComment;

    public UserObject getUob() {
        return uob;
    }

    public void setUob(UserObject uob) {
        this.uob = uob;
    }

    public FestivalObject getFob() {
        return fob;
    }

    public void setFob(FestivalObject fob) {
        this.fob = fob;
    }

    public Integer getStoryId() {
        return storyId;
    }

    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public List<String> getOtherImg() {
        return otherImg;
    }

    public void setOtherImg(List<String> otherImg) {
        this.otherImg = otherImg;
    }

    public List<String> getOtherImgNrml() {
        return otherImgNrml;
    }

    public void setOtherImgNrml(List<String> otherImgNrml) {
        this.otherImgNrml = otherImgNrml;
    }

    public List<String> getOtherImgBig() {
        return otherImgBig;
    }

    public void setOtherImgBig(List<String> otherImgBig) {
        this.otherImgBig = otherImgBig;
    }

    public List<String> getOtherImglk() {
        return otherImglk;
    }

    public void setOtherImglk(List<String> otherImglk) {
        this.otherImglk = otherImglk;
    }

    public List<String> getOtherImgcmt() {
        return otherImgcmt;
    }

    public void setOtherImgcmt(List<String> otherImgcmt) {
        this.otherImgcmt = otherImgcmt;
    }

    public List<String> getOtherImgId() {
        return otherImgId;
    }

    public void setOtherImgId(List<String> otherImgId) {
        this.otherImgId = otherImgId;
    }

    public List<Boolean> getOtherImgIsLiked() {
        return otherImgIsLiked;
    }

    public void setOtherImgIsLiked(List<Boolean> otherImgIsLiked) {
        this.otherImgIsLiked = otherImgIsLiked;
    }

    public Integer getNumLike() {
        return numLike;
    }

    public void setNumLike(Integer numLike) {
        this.numLike = numLike;
    }

    public Integer getNumComment() {
        return numComment;
    }

    public void setNumComment(Integer numComment) {
        this.numComment = numComment;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public Boolean getBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        isBookmarked = bookmarked;
    }
}
