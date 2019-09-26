package online.hualin.ipmsg.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import online.hualin.ipmsg.R;
import online.hualin.ipmsg.activity.MyFeiGeChatActivity;
import online.hualin.ipmsg.data.User;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private static final String TAG = "UserAdapter";
    private Context mContext;
    protected Resources res;
    List<User> mUserList;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView userImage;
        TextView childInfos;
        TextView childName;
        TextView childIp;
        TextView groupName;


        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            userImage = (ImageView) view.findViewById(R.id.user_img);
            groupName=view.findViewById(R.id.group_name);
            childInfos = (TextView) view.findViewById(R.id.unread_count);
            childName = (TextView) view.findViewById(R.id.child_name);
            childIp = (TextView) view.findViewById(R.id.child_ip);
        }
    }

    public UserAdapter(Context c,List<User> UserList) {
        mUserList=UserList;
        mContext = c;res = c.getResources();
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
                User user=mUserList.get(position);

                Intent intent = new Intent();
                intent.setClass(mContext, MyFeiGeChatActivity.class);
                intent.putExtra("receiverName", user.getName());
                intent.putExtra("receiverIp", user.getIp());
                intent.putExtra("receiverGroup", user.getGroupName());

                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.userImage.setImageDrawable(res.getDrawable(R.drawable.profile));
        holder.childIp.setText(user.getIp());
        holder.childInfos.setText(user.getMsgCount()+"");
        holder.childName.setText(user.getName());
        holder.groupName.setText(user.getGroupName());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

}
