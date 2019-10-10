package online.hualin.flymsg.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.DaoSession;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import online.hualin.flymsg.App;
import online.hualin.flymsg.R;
import online.hualin.flymsg.anim.ScaleInAnimation;
import online.hualin.flymsg.db.ChatHistory;
import online.hualin.flymsg.View.OnMoveAndSwipeListener;

import static online.hualin.flymsg.App.getApplication;
import static online.hualin.flymsg.utils.CommonUtils.setClipboard;

public class ChatHisAdapter extends RecyclerView.Adapter<ChatHisAdapter.ViewHolder> implements OnMoveAndSwipeListener {
    private final View.OnTouchListener listener;
    protected Resources res;
    private List<ChatHistory> chatHisList;
    private Context mContext;
    private View view;
    private View mView;
    private ScaleInAnimation mSelectAnimation = new ScaleInAnimation();
    private DaoSession daoSession = ((App) getApplication()).getDaoSession();
    private View parentView;

    private final int TYPE_NORMAL = 1;
    private final int TYPE_FOOTER = 2;
    private final int TYPE_HEADER = 3;
    private final String FOOTER = "footer";
    private final String HEADER = "header";

    public ChatHisAdapter(List<ChatHistory> chatHisList, View.OnTouchListener listener, View view) {
        this.chatHisList = chatHisList;
        if (this.chatHisList==null){
            this.chatHisList=new ArrayList<>();
        }
        this.listener = listener;
        this.view = view;
    }
    public void setItems(List<ChatHistory> data) {
        this.chatHisList=data;
        notifyDataSetChanged();
    }
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        parentView = parent;
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
            if (listener != null) {
            view.setOnTouchListener(listener);
        }
            return new ViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_footer, parent, false);
            return new FooterViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_header, parent, false);
            return new HeaderViewHolder(view);
        }
//        View view = LayoutInflater.from(mContext).inflate(R.layout.history_card, parent, false);
//        final ViewHolder holder = new ViewHolder(view);
//        if (listener != null) {
//            view.setOnTouchListener(listener);
//        }
//        return holder;
    }
    private class FooterViewHolder extends ChatHisAdapter.ViewHolder {
        private ProgressBar progress_bar_load_more;

        private FooterViewHolder(View itemView) {
            super(itemView);
            progress_bar_load_more = itemView.findViewById(R.id.progress_bar_load_more);
        }
    }

    private class HeaderViewHolder extends ChatHisAdapter.ViewHolder {
        private TextView header_text;

        private HeaderViewHolder(View itemView) {
            super(itemView);
            header_text = itemView.findViewById(R.id.header_text);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (position>=chatHisList.size()){
            return TYPE_FOOTER;
        }else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("adapter","loading:"+position);

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_recycler_item_show);
        holder.mView.startAnimation(animation);
//
//        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
//        aa.setDuration(500);

        ChatHistory chatHis = chatHisList.get(position);
        holder.sendIp.setText(chatHis.getSenderIp());
        holder.sendName.setText(chatHis.getSenderName());
        holder.sendMsg.setText(chatHis.getSendMsg());
        try {
            holder.time.setText(chatHis.getTime().replace("GMT+08:00",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position <= chatHisList.size()-1) {
                    Log.d("dismiss",position+"");

                    onItemDismiss(position);
                }
            }
        });
        holder.shareImage.setOnClickListener(v -> setClipboard(mContext, chatHis.getSendMsg()));
    }

    @Override
    public int getItemCount() {
        return chatHisList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(chatHisList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(final int position) {

        ChatHistory tmpChatHistory=chatHisList.get(position);

        chatHisList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();

        ChatHistoryDao chatHistoryDao = daoSession.getChatHistoryDao();
        chatHistoryDao.delete(tmpChatHistory);

        Snackbar.make(view, mContext.getString(R.string.delete_his_item), Snackbar.LENGTH_LONG).setAction("UNDO", v -> addItem(position, tmpChatHistory)).show();
    }

    private void addItem(int position, ChatHistory chatHistory) {
        chatHisList.add(position,chatHistory);
        notifyItemChanged(position);
        notifyDataSetChanged();

        ChatHistoryDao chatHistoryDao = daoSession.getChatHistoryDao();
        chatHisList.add(chatHistory);
        chatHistoryDao.insertOrReplace(chatHistory);
    }

//    @Override
//    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        addAnimation(holder);
//    }
//
//    private void addAnimation(ViewHolder holder) {
//        for (Animator anim : mSelectAnimation.getAnimators(holder.itemView)) {
//            anim.setDuration(300).start();
//            anim.setInterpolator(new LinearInterpolator());
//        }
//    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView sendIp;
        TextView sendName;
        TextView sendMsg;
        TextView time;
        ImageView deleteImage;
        ImageView shareImage;
        private View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            cardView = (CardView) view;
            sendIp = view.findViewById(R.id.sender_ip);
            sendName = view.findViewById(R.id.sender_name);
            sendMsg = view.findViewById(R.id.send_msg);
            time = view.findViewById(R.id.send_time);
            deleteImage = view.findViewById(R.id.delete_one);
            shareImage = view.findViewById(R.id.share_one);
        }

    }
}
