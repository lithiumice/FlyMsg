package online.hualin.flymsg.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import online.hualin.flymsg.R;
import online.hualin.flymsg.activity.ChatActivity;
import online.hualin.flymsg.anim.ScaleInAnimation;
import online.hualin.flymsg.data.User;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String TAG = "UserAdapter";
    public static int[] avatars = {
            R.drawable.ic_1,
            R.drawable.ic_2,
            R.drawable.ic_3};

    protected Resources res;
    List<User> mUserList;
    private Context mContext;
    private ScaleInAnimation mSelectAnimation = new ScaleInAnimation();
    private String shareText = "";
    private String sharePath = "";

    public UserAdapter(Context context, List<User> UserList) {
        mUserList = UserList;
        mContext = context;
        res = context.getResources();
    }

    public UserAdapter(Context context, List<User> UserList, String shareText,String sharePath) {
        mUserList = UserList;
        mContext = context;
        res = context.getResources();
        this.shareText = shareText;
        this.sharePath=sharePath;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_view, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                User user = mUserList.get(position);

                Intent intent = new Intent();
                intent.setClass(mContext, ChatActivity.class);
                intent.putExtra("receiverName", user.getName());
                intent.putExtra("receiverIp", user.getIp());
                intent.putExtra("receiverGroup", user.getGroupName());
                intent.putExtra("receiverImage", user.getAvatar());

                if (!shareText.equals("")) {
                    intent.putExtra("shareText", shareText);
                }
                if (!sharePath.equals("")) {
                    intent.putExtra("sharePath", sharePath);
                }
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.userImage.setImageDrawable(res.getDrawable(R.drawable.profile));
        holder.childIp.setText("IP:" + user.getIp());
        holder.childInfos.setText(user.getMsgCount() + "");
        holder.childName.setText("Name:" + user.getName());
        if (!user.getLastestMsg().equals(""))
            holder.unreadMsg.setText("未读:" + user.getLastestMsg());
        else
            holder.unreadMsg.setText("未读消息...");
        holder.groupName.setText("Group:" + user.getGroupName());
        int randomAvatar = avatars[(int) Math.floor(Math.random() * avatars.length)];
        holder.userImage.setImageDrawable(res.getDrawable(randomAvatar));
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CircleImageView userImage;
        TextView childInfos;
        TextView childName;
        TextView childIp;
        TextView groupName;
        TextView unreadMsg;


        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            userImage = view.findViewById(R.id.user_image);
            groupName = view.findViewById(R.id.group_name);
            childInfos = view.findViewById(R.id.unread_count);
            childName = view.findViewById(R.id.child_name);
            childIp = view.findViewById(R.id.child_ip);
            unreadMsg = view.findViewById(R.id.unread_msg);
            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ObjectAnimator downAnimator = ObjectAnimator.ofFloat(view, "translationZ", 16);
                            downAnimator.setDuration(200);
                            downAnimator.setInterpolator(new DecelerateInterpolator());
                            downAnimator.start();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            ObjectAnimator upAnimator = ObjectAnimator.ofFloat(view, "translationZ", 0);
                            upAnimator.setDuration(200);
                            upAnimator.setInterpolator(new AccelerateInterpolator());
                            upAnimator.start();
                            break;
                    }
                    return false;
                }
            });
        }
    }
}
