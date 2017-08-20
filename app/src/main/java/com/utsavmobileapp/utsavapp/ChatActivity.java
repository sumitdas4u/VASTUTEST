package com.utsavmobileapp.utsavapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.utsavmobileapp.utsavapp.data.ChatMessage;
import com.utsavmobileapp.utsavapp.service.ChatCachingAPI;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;
import com.utsavmobileapp.utsavapp.service.SettingsAPI;

import java.io.IOException;
import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private static final String FRIENDLY_MSG_LENGTH = "100000";
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Bundle ExtraNotificationData;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        return Actions.newView("Chat", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    @Override
    public void onStop() {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        super.onStop();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    private static final String TAG = "important";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10000;
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "https://utsavapp.utsavmobileapp.com/";


    //single reference object


    private String mUsername;
    private String mUserId;
    private String mPhotoUrl;
    private String chatNode;
    private String chatNode_1;
    private String chatNode_2;
    private String mChatFriendUserId;
    private int numberOfMsgs = 0;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    //    private FirebaseAuth mFirebaseAuth;
//    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditText mMessageEditText;
 //  private FirebaseRemoteConfig mFirebaseRemoteConfig;
    //    private GoogleApiClient mGoogleApiClient;
    SettingsAPI set;
    LoginCachingAPI lcp;
    ChatCachingAPI cca;
    Common common;
    String receivedNode = "";
    private boolean iBlocked = false, meBlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        FirebaseRemoteConfig.getInstance().setConfigSettings(configSettings);
        FirebaseRemoteConfig.getInstance().setDefaults(R.xml.remote_config_defaults);
        fetchWelcome();
        setContentView(R.layout.activity_chat);

        initToolbar();

        set = new SettingsAPI(this);
        lcp = new LoginCachingAPI(this);
        common = new Common(this);
        cca = new ChatCachingAPI(this);


        if (getIntent().getStringExtra("chatMessageUserId") != null)
            mChatFriendUserId = getIntent().getStringExtra("chatMessageUserId");
     //   Log.d("chatact: ",getIntent().getStringExtra("chatMessageUserId"));
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


    }

    /**
     * Fetch a welcome message from the Remote Config service, and then activate it.
     */
    private void fetchWelcome() {

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        }
                    }
                });
        // [END fetch_config_with_callback]
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getIntent().getStringExtra("chatMessageUserName"));
        actionBar.setSubtitle(getIntent().getStringExtra("chatMessageSubTitle"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        switch (id) {
            case R.id.action_info:
                if (iBlocked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage("Do you want to unblock this person?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mFirebaseDatabaseReference.child(MESSAGES_CHILD + "/" + chatNode + "/blocked").removeValue();
                                    iBlocked = false;
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage("Do you want to block this person?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mFirebaseDatabaseReference.child(MESSAGES_CHILD + "/" + chatNode + "/blocked").push().setValue(mUserId);
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create().show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();


//        Log.e(TAG, "onResume: "+lcp.readSetting("login"));
        if (lcp.readSetting("login").equals("true")) {
            mUserId = lcp.readSetting("id");
            mUsername = lcp.readSetting("name");
            mPhotoUrl = lcp.readSetting("photo");



        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("mode", "login");
            intent.putExtra("method", "force");
            startActivity(intent);
            return;
        }


        chatNode_1 = mUserId + "-" + mChatFriendUserId;
        chatNode_2 = mChatFriendUserId + "-" + mUserId;

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API)
//                .build();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.hasChild(MESSAGES_CHILD + "/" + chatNode_1)) {
                        chatNode = chatNode_1;
                    } else if (snapshot.hasChild(MESSAGES_CHILD + "/" + chatNode_2)) {
                        chatNode = chatNode_2;
                    } else {
                        chatNode = chatNode_1;
                    }

                mProgressBar.setVisibility(ProgressBar.GONE);
//                Log.wtf("important","myid "+mUserId+" chatMessageUserId is "+mChatFriendUserId);
                mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                        ChatMessage.class,
                        R.layout.item_message,
                        MessageViewHolder.class,
                        mFirebaseDatabaseReference.child(MESSAGES_CHILD + "/" + chatNode)) {

                    @Override
                    protected ChatMessage parseSnapshot(DataSnapshot snapshot) {
                        ChatMessage   ChatModelinstance = super.parseSnapshot(snapshot);
                        if (ChatModelinstance != null) {
                            if (snapshot.getKey().equals("blocked")) {
                                Log.e(TAG, "BLOCKED: "+ snapshot.getValue().toString().split("=")[1].replace("}", "").trim());
                                if (mUserId.equals(snapshot.getValue().toString().split("=")[1].replace("}", "").trim())) {
                                    iBlocked = true;
                                    ChatModelinstance.setText("You have blocked this person");
                                } else if (mChatFriendUserId.equals(snapshot.getValue().toString().split("=")[1].replace("}", "").trim())) {
                                    meBlocked = true;
                                    ChatModelinstance.setText("This person has blocked you");
                                }
                            }
                            ChatModelinstance.setId(snapshot.getKey());
                        }
                        return ChatModelinstance;
                    }

                    @Override
                    protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage friendlyMessage, int position) {
                        mProgressBar.setVisibility(ProgressBar.GONE);
                        viewHolder.messageTextView.setText(friendlyMessage.getText());
                        if (!friendlyMessage.getId().equals("blocked")) {
                            viewHolder.messengerTextView.setText(friendlyMessage.getName());
                            if (friendlyMessage.getPhotoUrl() == null) {
                                viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                                        R.drawable.com_facebook_profile_picture_blank_square));
                            } else {
                                Glide.with(ChatActivity.this)
                                        .load(friendlyMessage.getPhotoUrl())
                                        .into(viewHolder.messengerImageView);
                            }
                            numberOfMsgs++;
                            // write this message to the on-device index
                        //    FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));

                            // log a view action on it
                         //   FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));
                        }
                    }
                };


                mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                        int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                        // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                        // to the bottom of the list to show the newly added message.
                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                            mMessageRecyclerView.scrollToPosition(positionStart);
                        }
                    }
                });

                mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
                mMessageRecyclerView.setAdapter(mFirebaseAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Initialize Firebase Measurement.
     mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
      //  mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
              //  .getInt(FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
//// TODO: 8/6/2017 send message smoth
        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view) {
                final String msgBody = mMessageEditText.getText().toString();
                final ChatMessage friendlyMessage =
                        new ChatMessage(mMessageEditText.getText().toString(), mUsername, mPhotoUrl, mUserId);
               // ChatModelinstance=friendlyMessage;
                mMessageEditText.setText("");
                if (isChattable(view)) {
                    new AsyncTask<Void, Void, Void>() {
                        protected void onPreExecute() {
                             }
                        protected Void doInBackground(Void... unused) {

                                mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
                                mFirebaseDatabaseReference.child(MESSAGES_CHILD + "/" + chatNode).push()
                                        .setValue(friendlyMessage);

                                try {
                                String urlstr = getString(R.string.uniurl) + "/api/user.php?type=SEND&user_id="
                                        + mChatFriendUserId + "&msg="
                                        + URLEncoder.encode(msgBody, "UTF-8")
                                        + "&title=" + URLEncoder.encode(mUsername, "UTF-8")
                                        + "&chatMessageUserName=" + URLEncoder.encode(mUsername, "UTF-8")
                                        + "&chatMessageSubTitle=" + URLEncoder.encode(msgBody, "UTF-8")
                                        + "&chatMessageUserId=" + mUserId;
//
                             Common.HttpURLConnection(urlstr);
//
                            }
                            catch (IOException ignored) {
                            }
                            //numberOfMsgs++;

                            return null;
                        }
                        protected void onPostExecute(Void unused) {
                            // Post Code
                        }
                    }.execute();
            }


            }
        });
    }

    private boolean isChattable(View view) {
        //Snackbar.make(view, numberOfMsgs + "no of msg", Snackbar.LENGTH_LONG).show();
        boolean chattable = true;
        int no_of_chat_free=5;
       no_of_chat_free = Integer.parseInt(mFirebaseRemoteConfig.getString("no_of_chat_free"));
    //    Snackbar.make(view, mFirebaseRemoteConfig.getString("no_of_chat_free") + "no of msg", Snackbar.LENGTH_LONG).show();

        if (iBlocked) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setMessage("You blocked this person, do you want to unblock?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD + "/" + chatNode + "/blocked").removeValue();
                            iBlocked = false;
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();
            chattable = false;
        } else if (meBlocked) {
            Snackbar.make(view, "This person has blocked you", Snackbar.LENGTH_LONG).show();
            chattable = false;
        }
        else if (numberOfMsgs > no_of_chat_free) {
            if (lcp.readSetting("subscription").equals("false")) {
                Common.dialogBuy(ChatActivity.this);
                chattable = false;
            } else
                chattable = true;
        }
        return chattable;
    }



}
