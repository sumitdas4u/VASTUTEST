package com.utsavmobileapp.utsavapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumit on 08-07-2016.
 */
public class StoryCommonObjectSingleton {
    private static final StoryCommonObjectSingleton storiesIns = new StoryCommonObjectSingleton();
    private List<StoryObject> stories = new ArrayList<>();


    public static StoryCommonObjectSingleton getInstance() {
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



