package com.utsavmobileapp.utsavapp.service;

import com.utsavmobileapp.utsavapp.data.StoryObject;

import java.util.List;

/**
 * Created by Sumit on 04-07-2016.
 */
public interface AsyncResponseStoryBoard {
    void processFinish(List<StoryObject> output);
}