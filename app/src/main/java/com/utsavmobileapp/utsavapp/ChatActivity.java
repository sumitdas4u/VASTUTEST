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
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
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
import com.utsavmobileapp.utsavapp.data.Tools;
import com.utsavmobileapp.utsavapp.service.ChatCachingAPI;
import com.utsavmobileapp.utsavapp.service.Common;
import com.utsavmobileapp.utsavapp.service.LoginCachingAPI;
import com.utsavmobileapp.utsavapp.service.SettingsAPI;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private static final String FRIENDLY_MSG_LENGTH = "100000";

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

    private String mUsername;
    private String mUserId;
    private String mPhotoUrl;
    private String chatNode;
    private String chatNode_1;
    private String chatNode_2;
    private String mGirlFriend;
    private SharedPreferences mSharedPreferences;
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
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    //    private GoogleApiClient mGoogleApiClient;
    SettingsAPI set;
    LoginCachingAPI lcp;
    ChatCachingAPI cca;
    Common common;
    String receivedNode = "";
    private boolean iBlocked = false, meBlocked = false;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        initToolbar();

        set = new SettingsAPI(this);
        lcp = new LoginCachingAPI(this);
        common = new Common(this);
        cca = new ChatCachingAPI(this);

        if (getIntent().getStringExtra("chat_node") != null)
            receivedNode = getIntent().getStringExtra("chat_node");
        if (getIntent().getStringExtra("grlfrnd") != null)
            mGirlFriend = getIntent().getStringExtra("grlfrnd");
//        Log.e(TAG, receivedNode);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();

//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//            return;
//        } else {
//            mUsername = mFirebaseUser.getDisplayName();
//            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
//            mUserEmail = mFirebaseUser.getEmail().replace(".", "").replace("@", "").trim();
//        }
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getIntent().getStringExtra("gfname"));
        actionBar.setSubtitle(getIntent().getStringExtra("last"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
            if (!lcp.readSetting("gfs").equals("null"))
                if (Integer.parseInt(lcp.readSetting("gfs")) > 4)
                    if (lcp.readSetting("subscription").equals("false")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
                        alertDialog.setTitle("Paysa de");
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage("Give me money and I will give you freedom");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Buy",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent i = new Intent(MyProfileActivity.this, SomeClass.class);
//                                        i.putExtra("key", "value");
//                                        startActivity(i);
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Poysa nei",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ChatActivity.this.finish();
                                    }
                                });
                        alertDialog.show();

                    }

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("mode", "login");
            intent.putExtra("method", "force");
            startActivity(intent);
            return;
        }


        chatNode_1 = mUserId + "-" + mGirlFriend;
        chatNode_2 = mGirlFriend + "-" + mUserId;

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
                if (receivedNode.equals("")) {
                    if (snapshot.hasChild(MESSAGES_CHILD + "/" + chatNode_1)) {
                        chatNode = chatNode_1;
                    } else if (snapshot.hasChild(MESSAGES_CHILD + "/" + chatNode_2)) {
                        chatNode = chatNode_2;
                    } else {
                        chatNode = chatNode_1;
                        if (lcp.readSetting("gfs").equals("null"))
                            lcp.addUpdateSettings("gfs", "1");
                        else {
                            int gfs = Integer.parseInt(lcp.readSetting("gfs"));
                            lcp.addUpdateSettings("gfs", String.valueOf(gfs + 1));
                        }
                    }
                } else
                    chatNode = receivedNode;

//                Log.wtf("important","myid "+mUserId+" grlfrnd is "+mGirlFriend);
                mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                        ChatMessage.class,
                        R.layout.item_message,
                        MessageViewHolder.class,
                        mFirebaseDatabaseReference.child(MESSAGES_CHILD + "/" + chatNode)) {

                    @Override
                    protected ChatMessage parseSnapshot(DataSnapshot snapshot) {
                        ChatMessage friendlyMessage = super.parseSnapshot(snapshot);
                        if (friendlyMessage != null) {
                            if (snapshot.getKey().equals("blocked")) {
                                if (mUserId.equals(snapshot.getValue().toString().split("=")[1].replace("}", "").trim())) {
                                    iBlocked = true;
                                    friendlyMessage.setText("You have blocked this person");
                                } else if (mGirlFriend.equals(snapshot.getValue().toString().split("=")[1].replace("}", "").trim())) {
                                    meBlocked = true;
                                    friendlyMessage.setText("This person has blocked you");
                                }
                            }
                            friendlyMessage.setId(snapshot.getKey());
                        }
                        return friendlyMessage;
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
                            FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));

                            // log a view action on it
                            FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));
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

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 1000000);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
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

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean chattable = true;
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
                } else if (numberOfMsgs > 4) {
                    if (lcp.readSetting("subscription").equals("false")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
                        alertDialog.setTitle("Paysa de");
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage("Give me money and I will give you freedom");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Buy",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent i = new Intent(MyProfileActivity.this, SomeClass.class);
//                                        i.putExtra("key", "value");
//                                        startActivity(i);
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Poysa nei",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ChatActivity.this.finish();
                                    }
                                });
                        alertDialog.show();
                        chattable = false;
                    } else
                        chattable = true;
                }
                if (chattable) {
                    final String msgBody = mMessageEditText.getText().toString();
                    final ChatMessage friendlyMessage = new ChatMessage(mMessageEditText.getText().toString(), mUsername, mPhotoUrl, mUserId);
//                Log.e(TAG, "sending to "+MESSAGES_CHILD+"/"+chatNode);
                    mMessageEditText.setText("");
                    mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD + "/" + chatNode).push().setValue(friendlyMessage);
                            try {
                                String urlstr = getString(R.string.uniurl) + "/api/user.php?type=SEND&user_id=" + mGirlFriend + "&msg=" + URLEncoder.encode(msgBody, "UTF-8") + "&title=" + URLEncoder.encode(mUsername, "UTF-8") + "&chat_node=" + chatNode;
//                            Log.e(TAG, urlstr);
                                String sb = Common.HttpURLConnection(urlstr);
//                            Log.e(TAG, "onClick: " + sb);
                            } catch (IOException ignored) {
                            }
                            numberOfMsgs++;
                        }
                    });
                }
            }
        });
    }

    private Action getMessageViewAction(ChatMessage friendlyMessage) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(friendlyMessage.getName(), (MESSAGE_URL + MESSAGES_CHILD + "/").concat(friendlyMessage.getId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(ChatMessage friendlyMessage) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername == friendlyMessage.getName())
                .setName(friendlyMessage.getName())
                .setUrl((MESSAGE_URL + MESSAGES_CHILD + "/").concat(friendlyMessage.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl((MESSAGE_URL + MESSAGES_CHILD + "/").concat(friendlyMessage.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(friendlyMessage.getText())
                .setUrl((MESSAGE_URL + MESSAGES_CHILD + "/").concat(friendlyMessage.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each com.utsavmobileapp.utsavapp.fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
//                        Log.w(TAG, "Error fetching config: " + e.getMessage());
                        applyRetrievedLengthLimit();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
//                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
//                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong("friendly_msg_length");
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        //Log.d(TAG, "FML is: " + friendly_msg_length);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
