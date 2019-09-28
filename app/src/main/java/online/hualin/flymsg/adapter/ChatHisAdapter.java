package online.hualin.flymsg.adapter;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import online.hualin.flymsg.R;
import online.hualin.flymsg.activity.ChatActivity;
import online.hualin.flymsg.anim.ScaleInAnimation;
import online.hualin.flymsg.data.User;
import online.hualin.flymsg.db.ChatHistory;

public class ChatHisAdapter extends RecyclerView.Adapter<ChatHisAdapter.ViewHolder> {
    protected Resources res;
    List<ChatHistory> chatHisList;
    private Context mContext;

    private ScaleInAnimation mSelectAnimation = new ScaleInAnimation();

    public ChatHisAdapter(List<ChatHistory> chatHisList) {
        this.chatHisList = chatHisList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_card, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatHistory chatHis = chatHisList.get(position);
        holder.sendIp.setText(chatHis.getSenderIp());
        holder.sendName.setText(chatHis.getSenderName());
        holder.sendMsg.setText(chatHis.getSendMsg());
        holder.time.setText(chatHis.getTime());
    }

    @Override
    public int getItemCount() {
        return chatHisList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView sendIp;
        TextView sendName;
        TextView sendMsg;
        TextView time;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            sendIp = view.findViewById(R.id.sender_ip);
            sendName = view.findViewById(R.id.sender_name);
            sendMsg = view.findViewById(R.id.send_msg);
            time = view.findViewById(R.id.send_time);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        addAnimation(holder);
    }

    private void addAnimation(ViewHolder holder) {
        for (Animator anim : mSelectAnimation.getAnimators(holder.itemView)) {
            anim.setDuration(300).start();
            anim.setInterpolator(new LinearInterpolator());
        }
    }
}
