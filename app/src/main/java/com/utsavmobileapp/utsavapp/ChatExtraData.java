package com.utsavmobileapp.utsavapp;

/**
 * Created by Bibaswann on 03-05-2017.
 * test f
 */

//this is comment das

    //this comment by sumit das 1
//again by sumit das

public class ChatExtraData {
    private String uid,uUnread;
    private Long uLastMsg;
    ChatExtraData(String id, Long lastTime,String unread)
    {
        uid=id;
        uLastMsg=lastTime;
        uUnread=unread;
    }

    public String getUid() {
        return uid;
    }

    public String getuUnread() {
        return uUnread;
    }

    public Long getuLastMsg() {
        return uLastMsg;
    }
}
