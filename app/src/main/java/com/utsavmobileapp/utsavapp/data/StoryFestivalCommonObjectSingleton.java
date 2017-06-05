package com.utsavmobileapp.utsavapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 4/18/2017.
 */

public class StoryFestivalCommonObjectSingleton {
    private static final StoryFestivalCommonObjectSingleton storiesIns = new StoryFestivalCommonObjectSingleton();
    private List<StoryObject> stories = new ArrayList<>();


    public static StoryFestivalCommonObjectSingleton getInstance() {
        return storiesIns;
    }

    public List<StoryObject> getStories() {
        return stories;
    }

    public StoryObject get(Integer index) {
        return storiesIns.stories.get(index);
    }

    public void addStories(StoryObject story) {
        storiesIns.stories.add(story);
    }

    public Integer size() {
        return storiesIns.size();
    }

    public void clear() {
        stories.clear();
    }
}