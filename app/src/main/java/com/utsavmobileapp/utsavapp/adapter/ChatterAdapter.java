package com.utsavmobileapp.utsavapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.text.Line;
import com.utsavmobileapp.utsavapp.ChatActivity;
import com.utsavmobileapp.utsavapp.ProfileActivity;
import com.utsavmobileapp.utsavapp.R;
import com.utsavmobileapp.utsavapp.parser.ParseSingleChatterJSON;
import com.utsavmobileapp.utsavapp.service.ChatCachingAPI;
import com.utsavmobileapp.utsavapp.service.LatLonCachingAPI;

import java.util.List;

/**
 * Created by Bibaswann on 06-04-2017.
 */

public class ChatterAdapter extends RecyclerView.Adapter<ChatterAdapter.ChatViewHolder> {
    Context mContext;
    private List<String> userId;
    private List<String> userName;
    private List<String> userPhoto;
    private List<String> lastMsg;
    private List<String> unread;
    ChatCachingAPI cca;

    public ChatterAdapter(Context context, List<String> ids, List<String> names, List<String> photos, List<String> last, List<String> notRead) {
        userId = ids;
        userName = names;
        userPhoto = photos;
        mContext = context;
        lastMsg = last;
        unread = notRead;
        cca = new ChatCachingAPI(context);
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatter, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, final int position) {
        holder.name.setText(userName.get(position));
        holder.last.setText(lastMsg.get(position));
        if (Integer.parseInt(unread.get(position)) > 0)
            holder.notRead.setVisibility(View.VISIBLE);
        else
            holder.notRead.setVisibility(View.GONE);
        if (userPhoto.get(position) == null) {
            holder.thumb.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.com_facebook_profile_picture_blank_square));
        } else {
            Glide.with(mContext).load(userPhoto.get(position)).into(holder.thumb);
        }

        holder.thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPeopoleDetails(userId.get(position));
            }
        });

        holder.total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer prevSv = Integer.parseInt(cca.readCount(userId.get(position)));
                Integer unrd = Integer.parseInt(unread.get(position));
                Integer nowSave = prevSv + unrd;
                cca.addUpdateCount(userId.get(position), nowSave.toString());

                Intent chatent = new Intent(mContext, ChatActivity.class);
                chatent.putExtra("grlfrnd", userId.get(position));
                chatent.putExtra("gfname", userName.get(position));
                chatent.putExtra("last", lastMsg.get(position));
                mContext.startActivity(chatent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (userId != null)
            return userId.size();
        else
            return 0;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView last;
        public LinearLayout notRead;
        ImageView thumb;
        LinearLayout total;

        public ChatViewHolder(View itemView) {
            super(itemView);
            total = (LinearLayout) itemView.findViewById(R.id.chatter_list_item);
            name = (TextView) itemView.findViewById(R.id.user_name);
            last = (TextView) itemView.findViewById(R.id.extra_info);
            notRead = (LinearLayout) itemView.findViewById(R.id.unread);
            thumb = (ImageView) itemView.findViewById(R.id.user_photo);
        }
    }

    private void dialogPeopoleDetails(final String uid) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_contact_info);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LatLonCachingAPI llc = new LatLonCachingAPI(mContext);
        ParseSingleChatterJSON prnpj = new ParseSingleChatterJSON(mContext.getString(R.string.uniurl) + "/api/user.php?lat=" + llc.readLat() + "&long=" + llc.readLng() + "&type=SINGLE&user_id_lists=" + uid, mContext);
        prnpj.fetchJSON();
        while (prnpj.parsingInComplete) ;

        ((TextView) dialog.findViewById(R.id.name)).setText(prnpj.getuName());
        ((TextView) dialog.findViewById(R.id.tvActiveNow)).setText(prnpj.getuLastLogin());
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        Glide.with(mContext).load(prnpj.getuImg()).into(image);
        ((Button) dialog.findViewById(R.id.bt_send_message)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("uid", uid);
                mContext.startActivity(intent);
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
