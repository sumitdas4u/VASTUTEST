package com.utsavmobileapp.utsavapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utsavmobileapp.utsavapp.adapter.ChatterAdapter;
import com.utsavmobileapp.utsavapp.parser.ParseMultipleChatterJSON;
import com.utsavmobileapp.utsavapp.parser.ParseSingleChatterJSON;
import com.utsavmobileapp.utsavapp.service.ChatCachingAPI;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;
import com.utsavmobileapp.utsavapp.service.SettingsAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ChatListActivity extends AppCompatActivity {

    public static final String      USERS_CHILD = "messages";
    SettingsAPI set;
    LoginCachingAPI lcp;
    LatLonCachingAPI llc;
    Common common;
    RecyclerView lv;
    ProgressBar clp;
    String mLat, mLon;
    List<String> uName = new ArrayList<>();
    List<String> uNameFinal = new ArrayList<>();
    List<String> uId = new ArrayList<>();
    List<String> uIdFinal = new ArrayList<>();
    List<String> uGender = new ArrayList<>();
    List<String> uDistance = new ArrayList<>();
    List<String> uAge = new ArrayList<>();
    List<String> uImg = new ArrayList<>();
    List<String> uImgFinal = new ArrayList<>();
    List<String> uLastMsg = new ArrayList<>();
    List<String> uLastMsgFinal = new ArrayList<>();
    List<Long> uLastMsgTime = new ArrayList<>();
    List<String> uUnread = new ArrayList<>();
    List<String> uUnreadFinal = new ArrayList<>();
    List<ChatExtraData> ced = new ArrayList<>();
    private String mUsername;
    private String mUserId;
    private String mPhotoUrl;
    private ChatterAdapter cAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        set = new SettingsAPI(this);
        lcp = new LoginCachingAPI(this);
        common = new Common(this);
        llc = new LatLonCachingAPI(this);

        mLat = llc.readLat();
        mLon = llc.readLng();

        lv = (RecyclerView) findViewById(R.id.my_chats);
        clp = (ProgressBar) findViewById(R.id.chat_list_progress);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("important", "onResume: "+lcp.readSetting("login"));
        if (lcp.readSetting("login").equals("true")) {
            mUserId = lcp.readSetting("id");
            mUsername = lcp.readSetting("name");
            mPhotoUrl = lcp.readSetting("photo");

            uUnread.clear();
            uLastMsgTime.clear();
            uLastMsg.clear();

            cAdapter = new ChatterAdapter(this, uIdFinal, uNameFinal, uImgFinal, uLastMsgFinal, uUnreadFinal);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            lv.setLayoutManager(mLayoutManager);
           // lv.setItemAnimator(new DefaultItemAnimator());
            lv.setAdapter(cAdapter);
//            lv.removeAllViews();
//            lv.invalidate();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("mode", "login");
            intent.putExtra("method", "force");
            startActivity(intent);
            return;
        }

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USERS_CHILD);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.v("important", "got "+dataSnapshot.getKey()+"-->"+dataSnapshot.getValue());
                String[] datas = dataSnapshot.getValue().toString().split("[}][}],");

                List<String> user_list = new ArrayList<String>();
                ChatCachingAPI cca = new ChatCachingAPI(ChatListActivity.this);
                for (String data : datas) {
                    Integer unreadCount = 0;
                    Long lastTime = 0L;
                    String lastDateTime = "";
                    try {
//                    Log.e("important", data);
                        String chatters = data.replace("}", "").replace("{", "").split("=")[0].trim();
                        String user1 = chatters.split("-")[0];
                        String user2 = chatters.split("-")[1];
//                    Log.e("important", "this chat is between "+user1+" and "+user2);

//                        for (DataSnapshot ds :dataSnapshot.getChildren()) {
//                            Log.e("important", "got "+ds.getKey()+"-->"+ds.getValue());
//                        }

                        if (mUserId.equals(user1)) {
                            Integer numOfMsg = 1;
                            for (String str : data.split("=[{]-")[1].replace("{", "").replace("}", "").split(",")) {
//                                Log.e("important", "onDataChange: "+str);
                                if (str.split("=")[0].trim().equals("timestamp")) {
                                    if (Long.valueOf(str.split("=")[1].trim()) > lastTime) {
                                        lastTime = Long.valueOf(str.split("=")[1].trim());
                                        //SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy ',' h:mm a", new Locale("en", "IN"));
                                        lastDateTime = Common.getTimeAgo(lastTime.toString());
                                    }
                                }
                                if (str.split("=")[0].trim().equals("uid")) {
                                    if (!str.split("=")[1].trim().equals(mUserId))
                                        numOfMsg++;
                                }
                            }
                            unreadCount = numOfMsg - Integer.parseInt(cca.readCount(user2));
                            user_list.add(user2);
                            uUnread.add(unreadCount.toString());
                            uLastMsg.add(lastDateTime);
                            uLastMsgTime.add(lastTime);
                            ced.add(new ChatExtraData(user2, lastTime, unreadCount.toString()));
                        } else if (mUserId.equals(user2)) {
                            Integer numOfMsg = 1;
                            for (String str : data.split("=[{]-")[1].replace("{", "").replace("}", "").split(",")) {
//                                Log.e("important", "onDataChange: "+str);
                                if (str.split("=")[0].trim().equals("timestamp")) {
                                    if (Long.valueOf(str.split("=")[1].trim()) > lastTime) {
                                        lastTime = Long.valueOf(str.split("=")[1].trim());
                                        //SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy ',' h:mm a", new Locale("en", "IN"));
                                        lastDateTime = Common.getTimeAgo(lastTime.toString());
                                    }
                                }
                                if (str.split("=")[0].trim().equals("uid")) {
                                    if (!str.split("=")[1].trim().equals(mUserId))
                                        numOfMsg++;
                                }
                            }
                            unreadCount = numOfMsg - Integer.parseInt(cca.readCount(user1));
                            user_list.add(user1);
                            uUnread.add(unreadCount.toString());
                            uLastMsg.add(lastDateTime);
                            uLastMsgTime.add(lastTime);
                            ced.add(new ChatExtraData(user2, lastTime, unreadCount.toString()));
                        }

                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
//                user_list.clear();
//                for (int i = 1; i < 200; i++)
//                    user_list.add(String.valueOf(i));
                ParseMultipleChatterJSON prnpj = new ParseMultipleChatterJSON(ChatListActivity.this.getString(R.string.uniurl) + "/api/user.php?lat=" + mLat + "&long=" + mLon + "&type=SINGLE&user_id_lists=" + android.text.TextUtils.join(",", user_list), ChatListActivity.this);
                prnpj.fetchJSON();
                while (prnpj.parsingInComplete) ;

                uName.clear();
                uId.clear();
                uGender.clear();
                uDistance.clear();
                uAge.clear();
                uImg.clear();

                uName.addAll(prnpj.getuName());
                uId.addAll(prnpj.getuId());
                uGender.addAll(prnpj.getuGender());
                uDistance.addAll(prnpj.getuDistance());
                uAge.addAll(prnpj.getuAge());
                uImg.addAll(prnpj.getuImg());

                //Sort time

                uIdFinal.clear();
                uLastMsgFinal.clear();
                uUnreadFinal.clear();
                uNameFinal.clear();
                uImgFinal.clear();

                ArrayList<Long> indexOfUid = new ArrayList<Long>(uLastMsgTime);
                Collections.sort(uLastMsgTime);
                int[] indexes = new int[uLastMsgTime.size()];
                for (int n = 0; n < uLastMsgTime.size(); n++) {
                    indexes[n] = indexOfUid.indexOf(uLastMsgTime.get(n));
                }
                for (int i : indexes) {
//                    Log.e("important", "adding "+user_list.get(i));
                    if (user_list.size() > i) {
                        if (!uIdFinal.contains(user_list.get(i))) {
                            uIdFinal.add(user_list.get(i));
                            uLastMsgFinal.add(uLastMsg.get(i));
                            uUnreadFinal.add(uUnread.get(i));
                        }
                    }
                }

                Collections.reverse(uIdFinal);
                Collections.reverse(uLastMsgFinal);
                Collections.reverse(uUnreadFinal);

                for (String id : uIdFinal) {
                    int index = uId.indexOf(id);
                    uNameFinal.add(uName.get(index));
                    uImgFinal.add(uImg.get(index));
                }

                if (uId.size() > 0) {
                    cAdapter.notifyDataSetChanged();
                }
                clp.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Log.e("important","The read failed: " + databaseError.getCode());
            }
        });
    }
}
