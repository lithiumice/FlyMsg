package online.hualin.flymsg.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.DaoSession;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import online.hualin.flymsg.App;
import online.hualin.flymsg.R;
import online.hualin.flymsg.data.ChatMessage;
import online.hualin.flymsg.data.User;
import online.hualin.flymsg.db.ChatHistory;
import online.hualin.flymsg.interfaces.ReceiveMsgListener;
import online.hualin.flymsg.net.NetTcpFileSendThread;
import online.hualin.flymsg.utils.CommonUtils;
import online.hualin.flymsg.utils.IpMessageConst;
import online.hualin.flymsg.utils.IpMessageProtocol;
import online.hualin.flymsg.utils.UsedConst;

import static droidninja.filepicker.FilePickerConst.REQUEST_CODE_DOC;
import static droidninja.filepicker.FilePickerConst.REQUEST_CODE_PHOTO;
import static online.hualin.flymsg.utils.CommonUtils.BitMapToString;
import static online.hualin.flymsg.utils.CommonUtils.closeInputBoard;
import static online.hualin.flymsg.utils.CommonUtils.getLocalIpAddress;

public class ChatActivity extends BaseActivity implements OnClickListener, ReceiveMsgListener
        , MessagesListAdapter.OnMessageClickListener, MessagesListAdapter.OnMessageLongClickListener, MessagesListAdapter.OnLoadMoreListener
        , DateFormatter.Formatter {
    public static final String TAG = "BaseActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_PHOTO = 2;
    static final int REQUEST_PERMS = 3;
    static final int RECEIVE_IMAGE = 4;
    static final int RECEIVE_StrMsg = 5;
    private ImageView chat_item_head;    //头像
    private TextView chat_name;            //名字及IP
    private TextView chat_mood;            //组名
    private MessagesList msgListView;       //消息
    private MessageInput chat_input;        //聊天输入框
    private ActionBar actionBar;
    private Toolbar toolbar;
    private List<ChatMessage> msgList = new ArrayList<>();    //用于显示的消息list
    private String receiverName;            //要接收本activity所发送的消息的用户名字
    private String receiverIp;            //要接收本activity所发送的消息的用户IP
    private String receiverGroup;            //要接收本activity所发送的消息的用户组名
    private String selfName;
    private String selfGroup;
    private String selfIp;
    private String selfMac;
    private User user;
    private MessagesListAdapter<ChatMessage> adapter;
    private Bitmap imageBitmap;
    private String imageString;
    private Resources res;
    private ArrayList<String> photoPaths;
    private ArrayList<String> docPaths;
    private ImageView chatPlus;
    private TextView chatTitle;
    private App app;
    private Handler chatHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            adapter.addToStart((ChatMessage) msg.obj, true);
        }
    };

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        res = getResources();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        receiverName = bundle.getString("receiverName");
        receiverIp = bundle.getString("receiverIp");
        receiverGroup = bundle.getString("receiverGroup");
        selfName = getApplicationContext().getString(R.string.default_device_name);
        selfGroup = getApplicationContext().getString(R.string.default_device_group);
        selfIp = getLocalIpAddress();
        selfMac = getLocalMacAddress();

        chatTitle = findViewById(R.id.main_titile);
        chatTitle.setText(receiverName);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Iterator<ChatMessage> it = netThreadHelper.getReceiveMsgQueue().iterator();
        while (it.hasNext()) {    //循环消息队列，获取队列中与本聊天activity相关信息
            ChatMessage temp = it.next();
            //若消息队列中的发送者与本activity的消息接收者IP相同，则将这个消息拿出，添加到本activity要显示的消息list中
            if (receiverIp.equals(temp.getSenderIp())) {
                msgList.add(temp);    //添加到显示list
                it.remove();        //将本消息从消息队列中移除
            }
        }

        ImageLoader imageLoader = (ImageView imageView, @Nullable String url, @Nullable Object payload) -> {
            {
                if (url == "receiver") {
                    Glide.with(ChatActivity.this).load(BitmapFactory.decodeResource(res, R.drawable.ic_1)).into(imageView);

                } else if (url == "sender") {
                    Glide.with(ChatActivity.this).load(BitmapFactory.decodeResource(res, R.drawable.ic_2)).into(imageView);

                }
            }
        };

        chat_input = findViewById(R.id.chat_input);
        msgListView = findViewById(R.id.messagesList);
        chat_input.setInputListener((CharSequence input) -> {
            String msgStr = chat_input.getInputEditText().getText().toString().trim();
            sendAndAddMessage(msgStr);
            adapter.addToStart(addMessage(input.toString(), selfIp, selfName, "sender"), true);
            DaoSession daoSession = ((App) getApplication()).getDaoSession();
            ChatHistoryDao chatHistoryDao = daoSession.getChatHistoryDao();

            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setSenderIp("127.0.0.1");
            chatHistory.setSenderName(selfName);
            chatHistory.setSendMsg(msgStr);
            chatHistoryDao.insert(chatHistory);
            chatHistory.setTime(new Date().toString());

            Log.d("DaoExample", "Inserted new chat history, ID: " + chatHistory.getId());

            return true;

        });
        chat_input.setAttachmentsListener(() -> {
            dispatchTakePictureIntent();
//                new ChatPopup(ChatActivity.getCurrentActivity().getApplicationContext()).showPopupWindow();

        });
        chat_input.setTypingListener(new MessageInput.TypingListener() {
            @Override
            public void onStartTyping() {
            }

            @Override
            public void onStopTyping() {

            }
        });

        adapter = new MessagesListAdapter<>(selfIp, imageLoader);

//        for (ChatMessage msg : msgList)
//            adapter.addToStart(msg, true);
        adapter.addToEnd(msgList, false);

        msgListView.setAdapter(adapter);
        msgListView.setOnScrollChangeListener(new MessagesList.OnScrollChangeListener(){
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                closeInputBoard(getCurrentActivity());
            }
        });
        netThreadHelper.addReceiveMsgListener(this);    //注册到listeners
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    private ChatMessage addMessage(String msg, String ip, String name, String avatar) {
        user = new User(name, "", ip, "", avatar);
        return new ChatMessage(ip, name, msg, new Date(), user);//String senderIp, String senderName, String msg, Date time,User user
    }

    public ChatMessage getImageMessage(String ip, String name, String avatar) {
        user = new User(name, "", ip, "", avatar);
        ChatMessage message = new ChatMessage(ip, name, "", new Date(), user);
        message.setImage(new ChatMessage.Image(BitMapToString(imageBitmap)));
        return message;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_toolbar, menu);
        return true;
    }


    @Override
    public void processMessage(Message msg) {
        Log.d(TAG, String.valueOf(msg.what));
        switch (msg.what) {
            case IpMessageConst.IPMSG_SENDMSG:
                adapter.notifyDataSetChanged();    //刷新ChatKit UI
                break;

            case IpMessageConst.IPMSG_RELEASEFILES: { //拒绝接受文件,停止发送文件线程
                if (NetTcpFileSendThread.server != null) {
                    try {
                        NetTcpFileSendThread.server.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
            break;

            case UsedConst.FILESENDSUCCESS: {    //文件发送成功
                makeTextLong("文件发送成功");
            }
            break;

        }
    }

    @Override
    public boolean receive(ChatMessage msg) {

        if (receiverIp.equals(msg.getSenderIp())) {    //若消息与本activity有关，则接收
            Message message = new Message();
            message.obj = msg;
            chatHandler.sendMessage(message);

            sendEmptyMessage(IpMessageConst.IPMSG_SENDMSG); //使用handle通知，来更新UI
            BaseActivity.playMsg();

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.send_file:
                openFile();
            case R.id.clear_msg:
                adapter.clear();
                makeTextShort("已清除所有消息");
            default:
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_file:
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openFile();
                }
                break;
            case R.id.clear_msg:

//            case R.id.chat_plus:
//                new ChatPopup(ChatActivity.this).showPopupWindow();
//                break;
        }
    }

    private void openFileManager() {
        if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMS);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PHOTO);
        }
    }

    private void openFile() {
        FilePickerBuilder.getInstance()
//                        .setSelectedFiles("/mnt/sdcard/download/")
                .setActivityTheme(R.style.LibAppTheme)
//                .enableCameraSupport(true)
                .enableSelectAll(true)
                .enableVideoPicker(true)
                .enableImagePicker(true)
                .showFolderView(true)
                .showGifs(true)
                .pickFile(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFile();
                } else {
                    globalToastLong("You denied the permission");
                }
                break;
            default:
        }
    }


    private void sendAndAddMessage(String msgStr) {
        if (!"".equals(msgStr)) {
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

                Log.e("ChatActivity", "发送地址有误");
            }
            if (sendto != null)
                netThreadHelper.sendUdpData(sendMsg.getProtocolString(), sendto, IpMessageConst.PORT);
            ChatMessage selfMsg = new ChatMessage(selfIp, selfName, msgStr, new Date(), user);//String senderIp, String senderName, String msg, Date time,User user
            msgList.add(selfMsg);

        } else {
            makeTextShort("不能发送空内容");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    assert extras != null;
                    imageBitmap = (Bitmap) extras.get("data");
                    adapter.addToStart(getImageMessage(selfIp, selfName, "sender"), true);
                    break;

                case REQUEST_IMAGE_PHOTO:
                case REQUEST_CODE_DOC:
                case REQUEST_CODE_PHOTO:
                    ArrayList<String> tmpPaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    String[] filePathArray = new String[tmpPaths.size()];
                    for (int i = 0; i < tmpPaths.size(); i++) {
                        filePathArray[i] = tmpPaths.get(i);
                    }

                    //发送传送文件UDP数据报
                    IpMessageProtocol sendPro = new IpMessageProtocol();
                    sendPro.setVersion("" + IpMessageConst.VERSION);
                    sendPro.setCommandNo(IpMessageConst.IPMSG_SENDMSG | IpMessageConst.IPMSG_FILEATTACHOPT);
                    sendPro.setSenderName(selfName);
                    sendPro.setSenderHost(selfGroup);
                    String msgStr = "";    //发送的消息

                    StringBuffer additionInfoSb = new StringBuffer();    //用于组合附加文件格式的sb
                    for (String path : filePathArray) {
                        File file = new File(path);
                        additionInfoSb.append("0:");
                        additionInfoSb.append(file.getName() + ":");
                        additionInfoSb.append(Long.toHexString(file.length()) + ":");        //文件大小十六进制表示
                        additionInfoSb.append(Long.toHexString(file.lastModified()) + ":");    //文件创建时间，现在暂时已最后修改时间替代
                        additionInfoSb.append(IpMessageConst.IPMSG_FILE_REGULAR + ":");
                        byte[] bt = {0x07};        //用于分隔多个发送文件的字符
                        String splitStr = new String(bt);
                        additionInfoSb.append(splitStr);
                    }

                    sendPro.setAdditionalSection(msgStr + "\0" + additionInfoSb.toString() + "\0");

                    InetAddress sendto = null;
                    try {
                        sendto = InetAddress.getByName(receiverIp);
                    } catch (UnknownHostException e) {

                        Log.e("ChatActivity", "发送地址有误");
                    }
                    if (sendto != null)
                        netThreadHelper.sendUdpData(sendPro.getProtocolString(), sendto, IpMessageConst.PORT);

                    //监听2425端口，准备接受TCP连接请求
                    Thread netTcpFileSendThread = new Thread(new NetTcpFileSendThread(filePathArray));
                    netTcpFileSendThread.start();    //启动线程

            }
        }
    }


    @Override
    public void onMessageClick(IMessage message) {
        makeTextShort("you click:" + message.getText());
    }


    @Override
    public void onMessageLongClick(IMessage message) {
        String copyText = message.toString();
        CommonUtils.setClipboard(getApplicationContext(), copyText);
        globalToast("已复制" + copyText);
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
//    if (totalItemsCount < this.total) {
////        loadMessages(...);
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return DateFormatter.format(date, DateFormatter.Template.TIME);
        } else if (DateFormatter.isYesterday(date)) {
            return getString(R.string.date_header_yesterday);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }
}

