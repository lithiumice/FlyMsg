//package online.hualin.ipmsg.adapter;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import java.util.List;
//
//import online.hualin.ipmsg.R;
//import online.hualin.ipmsg.data.ChatMessage;
//
///**
// * 聊天activity中的ListView的adapter，实现发送者名称对应TextView字体的颜色变化
// * @author ccf
// *
// * 2012/2/21
// *
// */
//public class ChatListAdapter extends BaseAdapter {
//	protected LayoutInflater mInflater;
//	protected List<ChatMessage> msgList;
//	protected Resources res;
//
//	public ChatListAdapter(Context context, List<ChatMessage> list){
//		super();
//		this.mInflater = LayoutInflater.from(context);
//		this.msgList = list;
//		res = context.getResources();
//	}
//
//	@Override
//	public int getCount() {
//
//		return msgList.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//
//		return msgList.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		View view;
//		if(convertView == null){
//			view = null;
//		}else{
//			view = convertView;
//		}
//		ChatMessage msg = msgList.get(position);
//
//		TextView show_name = view.findViewById(R.id.show_name);
//		show_name.setText(msg.getSenderName());
//		if(msg.isSelfMsg()){	//根据是否是自己的消息更改颜色
//			show_name.setTextColor(res.getColor(R.color.chat_myself));
//		}else{
//			show_name.setTextColor(res.getColor(R.color.chat_other));
//		}
//
//		TextView show_time = view.findViewById(R.id.show_time);
//		show_time.setText(msg.getTimeStr());
//
//		TextView message = view.findViewById(R.id.message);
//		message.setText(msg.getMsg());
//
//		return view;
//	}
//
//}
