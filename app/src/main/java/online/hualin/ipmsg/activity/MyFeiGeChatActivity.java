package online.hualin.ipmsg.activity;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import online.hualin.ipmsg.R;
import online.hualin.ipmsg.adapter.ChatListAdapter;
import online.hualin.ipmsg.data.ChatMessage;
import online.hualin.ipmsg.interfaces.ReceiveMsgListener;
import online.hualin.ipmsg.net.NetTcpFileSendThread;
import online.hualin.ipmsg.utils.IpMessageConst;
import online.hualin.ipmsg.utils.IpMessageProtocol;
import online.hualin.ipmsg.utils.UsedConst;
import online.hualin.ipmsg.utils.MyIntent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class MyFeiGeChatActivity extends MyFeiGeBaseActivity implements OnClickListener,ReceiveMsgListener{

//	private NetThreadHelper netThreadHelper;


	//	private ImageView chat_item_head;	//头像
	private TextView chat_name;			//名字及IP
	private TextView chat_mood;			//组名
	private Button chat_quit;			//退出按钮
	private ListView chat_list;			//聊天列表
	private EditText chat_input;		//聊天输入框
	private Button chat_send;			//发送按钮

	private List<ChatMessage> msgList;	//用于显示的消息list
	private String receiverName;			//要接收本activity所发送的消息的用户名字
	private String receiverIp;			//要接收本activity所发送的消息的用户IP
	private String receiverGroup;			//要接收本activity所发送的消息的用户组名
	private ChatListAdapter adapter;	//ListView对应的adapter
	private String selfName;
	private String selfGroup;
	private MessagesList messagesList;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);

		chat_input = (EditText) findViewById(R.id.chat_input);
		chat_send = (Button) findViewById(R.id.chat_send);
//		netThreadHelper = NetThreadHelper.newInstance();
		msgList = new ArrayList<ChatMessage>();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		receiverName = bundle.getString("receiverName");
		receiverIp = bundle.getString("receiverIp");
		receiverGroup = bundle.getString("receiverGroup");
		selfName = getApplicationContext().getString(R.string.default_device_name);
		selfGroup = getApplicationContext().getString(R.string.default_device_group);
		chat_send.setOnClickListener(this);

		Iterator<ChatMessage> it = netThreadHelper.getReceiveMsgQueue().iterator();
		while(it.hasNext()){	//循环消息队列，获取队列中与本聊天activity相关信息
			ChatMessage temp = it.next();
			//若消息队列中的发送者与本activity的消息接收者IP相同，则将这个消息拿出，添加到本activity要显示的消息list中
			if(receiverIp.equals(temp.getSenderIp())){
				msgList.add(temp);	//添加到显示list
				it.remove();		//将本消息从消息队列中移除
			}
		}

		adapter = new ChatListAdapter(this, msgList);
		chat_list.setAdapter(adapter);
		MessagesListAdapter<ChatMessage> adapter = new MessagesListAdapter<>(senderId, imageLoader);
		messagesList.setAdapter(adapter);

		netThreadHelper.addReceiveMsgListener(this);	//注册到listeners
	}

	ImageLoader imageLoader = new ImageLoader() {
		@Override
		public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
			Glide.with(MyFeiGeChatActivity.this)
					.load(url)
					.into(imageView);
		}
	};

	@Override
	public void processMessage(Message msg) {

		switch(msg.what){
			case IpMessageConst.IPMSG_SENDMSG:
				adapter.notifyDataSetChanged();	//刷新ListView
				break;

			case IpMessageConst.IPMSG_RELEASEFILES:{ //拒绝接受文件,停止发送文件线程
				if(NetTcpFileSendThread.server != null){
					try {
						NetTcpFileSendThread.server.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
			break;

			case UsedConst.FILESENDSUCCESS:{	//文件发送成功
				makeTextShort("文件发送成功");
			}
			break;


		}	//end of switch
	}

	@Override
	public boolean receive(ChatMessage msg) {

		if(receiverIp.equals(msg.getSenderIp())){	//若消息与本activity有关，则接收
			msgList.add(msg);	//将此消息添加到显示list中
			sendEmptyMessage(IpMessageConst.IPMSG_SENDMSG); //使用handle通知，来更新UI
			MyFeiGeBaseActivity.playMsg();
			return true;
		}

		return false;
	}

	@Override
	public void finish() {
		//一定要移除，不然信息接收会出现问题
		netThreadHelper.removeReceiveMsgListener(this);
		super.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
//			case R.id.chat_file:
//				if(ContextCompat.checkSelfPermission(MyFeiGeChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//					ActivityCompat.requestPermissions(MyFeiGeChatActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//				}else{
//					openFile();
//				}
//				break;
//			case R.id.chat_quit:
//				finish();
//				break;
			case R.id.chat_send:
				sendAndAddMessage();
		}
	}

	/*打开文件管理器*/
	private void openFile(){
		Intent intent=new Intent("android.intent.action.GET_CONTENT");
		intent.setType("image/*");
		startActivityForResult(intent,0);
	}

	/* 发送消息并将该消息添加到UI显示*/
	private void sendAndAddMessage(){
		String msgStr = chat_input.getText().toString().trim();
		if(!"".equals(msgStr)){
			//发送消息
			IpMessageProtocol sendMsg = new IpMessageProtocol();
			sendMsg.setVersion(String.valueOf(IpMessageConst.VERSION));
			sendMsg.setSenderName(selfName);
			sendMsg.setSenderHost(selfGroup);
			sendMsg.setCommandNo(IpMessageConst.IPMSG_SENDMSG);
			sendMsg.setAdditionalSection(msgStr);
			InetAddress sendto = null;
			try {
				sendto = InetAddress.getByName(receiverIp);
			} catch (UnknownHostException e) {

				Log.e("MyFeiGeChatActivity", "发送地址有误");
			}
			if(sendto != null)
				netThreadHelper.sendUdpData(sendMsg.getProtocolString() + "\0", sendto, IpMessageConst.PORT);

			//添加消息到显示list
			ChatMessage selfMsg = new ChatMessage("localhost", selfName, msgStr, new Date());
			selfMsg.setSelfMsg(true);	//设置为自身消息
			msgList.add(selfMsg);

		}else{
			makeTextShort("不能发送空内容");
		}

		chat_input.setText("");
		adapter.notifyDataSetChanged();//更新UI
	}

	/* 接受选择文件返回的路径 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			//得到发送文件的路径
//			Bundle bundle = data.getExtras();
//			String filePaths = bundle.getString("filePaths");	//附加文件信息串,多个文件使用"\0"进行分隔
//			Toast.makeText(this, filePaths, Toast.LENGTH_SHORT).show();
//			String[] filePathArray = filePaths.split("\0");

			String[] filePathArray=new String[]{""};
			filePathArray[0]=data.getData().getPath();


			//发送传送文件UDP数据报
			IpMessageProtocol sendPro = new IpMessageProtocol();
			sendPro.setVersion("" +IpMessageConst.VERSION);
			sendPro.setCommandNo(IpMessageConst.IPMSG_SENDMSG | IpMessageConst.IPMSG_FILEATTACHOPT);
			sendPro.setSenderName(selfName);
			sendPro.setSenderHost(selfGroup);
			String msgStr = "";	//发送的消息

			StringBuffer additionInfoSb = new StringBuffer();	//用于组合附加文件格式的sb
			for(String path:filePathArray){
				File file = new File(path);
				additionInfoSb.append("0:");
				additionInfoSb.append(file.getName() + ":");
				additionInfoSb.append(Long.toHexString(file.length()) + ":");		//文件大小十六进制表示
				additionInfoSb.append(Long.toHexString(file.lastModified()) + ":");	//文件创建时间，现在暂时已最后修改时间替代
				additionInfoSb.append(IpMessageConst.IPMSG_FILE_REGULAR + ":");
				byte[] bt = {0x07};		//用于分隔多个发送文件的字符
				String splitStr = new String(bt);
				additionInfoSb.append(splitStr);
			}

			sendPro.setAdditionalSection(msgStr + "\0" + additionInfoSb.toString() + "\0");

			InetAddress sendto = null;
			try {
				sendto = InetAddress.getByName(receiverIp);
			} catch (UnknownHostException e) {

				Log.e("MyFeiGeChatActivity", "发送地址有误");
			}
			if(sendto != null)
				netThreadHelper.sendUdpData(sendPro.getProtocolString(), sendto, IpMessageConst.PORT);

			//监听2425端口，准备接受TCP连接请求
			Thread netTcpFileSendThread = new Thread(new NetTcpFileSendThread(filePathArray));
			netTcpFileSendThread.start();	//启动线程
		}
	}


}
