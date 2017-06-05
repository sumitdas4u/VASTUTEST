package com.utsavmobileapp.utsavapp;

/**
 * Created by Bibaswann on 03-05-2017.
 */

public class ChatExtraData {
    String uid,uUnread;
    Long uLastMsg;
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
