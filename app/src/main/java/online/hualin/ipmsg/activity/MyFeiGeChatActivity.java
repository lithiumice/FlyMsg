package online.hualin.ipmsg.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import online.hualin.ipmsg.R;
import online.hualin.ipmsg.data.ChatMessage;
import online.hualin.ipmsg.data.User;
import online.hualin.ipmsg.interfaces.ReceiveMsgListener;
import online.hualin.ipmsg.net.NetTcpFileSendThread;
import online.hualin.ipmsg.utils.IpMessageConst;
import online.hualin.ipmsg.utils.IpMessageProtocol;
import online.hualin.ipmsg.utils.RealPathFromUriUtils;
import online.hualin.ipmsg.utils.UsedConst;

import static online.hualin.ipmsg.utils.utils.getLocalIpAddress;

public class MyFeiGeChatActivity extends MyFeiGeBaseActivity implements OnClickListener, ReceiveMsgListener
        , MessagesListAdapter.OnMessageClickListener, MessagesListAdapter.OnMessageLongClickListener, MessagesListAdapter.OnLoadMoreListener
        , MessagesListAdapter.SelectionListener {

    public static final String TAG = "MyFeiGeBaseActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_PHOTO = 2;
    static final int REQUEST_PERMS = 3;
    static final int RECEIVE_IMAGE = 4;
    static final int RECEIVE_StrMsg= 5;
    public static String FLYMSG_IMAGE_KEY;
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
//            if (msg.arg1==RECEIVE_IMAGE){
//
//            }

            adapter.addToStart((ChatMessage)msg.obj, true);
        }
    };

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        res = getResources();
        FLYMSG_IMAGE_KEY=res.getString(R.string.flymsg_image_key);
//        getWindow().setStatusBarColor(res.getColor(R.color.colorPrimaryDark));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        receiverName = bundle.getString("receiverName");
        receiverIp = bundle.getString("receiverIp");
        receiverGroup = bundle.getString("receiverGroup");
        selfName = getApplicationContext().getString(R.string.default_device_name);
        selfGroup = getApplicationContext().getString(R.string.default_device_group);
        selfIp = getLocalIpAddress();
        selfMac = getLocalMacAddress();

        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        toolbar.setTitle(receiverName);
        toolbar.setTitleMargin(10, 6, 6, 10);
        toolbar.setTitleTextColor(res.getColor(R.color.textIcon));


        Iterator<ChatMessage> it = netThreadHelper.getReceiveMsgQueue().iterator();
        while (it.hasNext()) {    //循环消息队列，获取队列中与本聊天activity相关信息
            ChatMessage temp = it.next();
            //若消息队列中的发送者与本activity的消息接收者IP相同，则将这个消息拿出，添加到本activity要显示的消息list中
            if (receiverIp.equals(temp.getSenderIp())) {
                msgList.add(temp);    //添加到显示list
                it.remove();        //将本消息从消息队列中移除
            }
        }

        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Bitmap bitmap = StringToBitMap(url);
                Glide.with(MyFeiGeChatActivity.this).load(bitmap).into(imageView);
            }
        };

        chat_input = findViewById(R.id.chat_input);
        msgListView = findViewById(R.id.messagesList);
        chat_input.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                String msgStr = chat_input.getInputEditText().getText().toString().trim();

                sendAndAddMessage(msgStr);
                adapter.addToStart(addMessage(input.toString(), selfIp, selfName), true);
                return true;
            }
        });
        chat_input.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                dispatchTakePictureIntent();
            }
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
        adapter.enableSelectionMode(this);

        for (ChatMessage msg : msgList)
            adapter.addToStart(msg, true);

        msgListView.setAdapter(adapter);
        netThreadHelper.addReceiveMsgListener(this);    //注册到listeners
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private ChatMessage addMessage(String msg, String ip, String name) {
        user = new User(name, "", "", ip, "", null, true, "");
        return new ChatMessage(ip, name, msg, new Date(), user);//String senderIp, String senderName, String msg, Date time,User user
    }

    private ChatMessage addMessage(String msg, String ip, String name, String alias, String groupName, String hostName, String mac) {
        user = new User(name, alias, groupName, ip, hostName, null, true, mac);
        return new ChatMessage(ip, name, msg, new Date(), user);//String senderIp, String senderName, String msg, Date time,User user
    }

    public ChatMessage getImageMessage(String ip, String name) {
        user = new User(name, "", ip);
        ChatMessage message = new ChatMessage(ip, name, "", new Date(), user);
        message.setImage(new ChatMessage.Image(BitMapToString(imageBitmap)));
        return message;
    }

    @TargetApi(19)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
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

            String tmpString=msg.getMsg();
            if (tmpString.length()>12){
                if (tmpString.endsWith(FLYMSG_IMAGE_KEY) & tmpString.startsWith(FLYMSG_IMAGE_KEY)){
                    msg.setImage(new ChatMessage.Image(tmpString.substring(6,tmpString.length()-6)));
                    msg.setMsg("");
                    message.arg1=RECEIVE_IMAGE;
                }
            }else{
                message.arg1=RECEIVE_StrMsg;
            }
//            msgList.add(msg);    //将此消息添加到显示list中
            message.obj = msg;

            handler.sendMessage(message);

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.send_file:
                openFile();
            default:
//                finish();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_file:
                if (ContextCompat.checkSelfPermission(MyFeiGeChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyFeiGeChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openFile();
                }
                break;
        }
    }

    /*打开文件管理器*/
    private void openFile() {
        if (ContextCompat.checkSelfPermission(MyFeiGeChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MyFeiGeChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyFeiGeChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMS);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PHOTO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    /* 发送消息并将该消息添加到UI显示*/
    private void sendAndAddMessage(String msgStr) {
//        String msgStr = chat_input.getInputEditText().getText().toString().trim();
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

                Log.e("MyFeiGeChatActivity", "发送地址有误");
            }
            if (sendto != null)
                netThreadHelper.sendUdpData(sendMsg.getProtocolString(), sendto, IpMessageConst.PORT);
//                netThreadHelper.sendUdpData(sendMsg.getProtocolString() + "\0", sendto, IpMessageConst.PORT);

            //添加消息到显示list
            ChatMessage selfMsg = new ChatMessage(selfIp, selfName, msgStr, new Date(), user);//String senderIp, String senderName, String msg, Date time,User user
//            selfMsg.setSelfMsg(true);    //设置为自身消息
            msgList.add(selfMsg);

        } else {
            makeTextShort("不能发送空内容");
        }

//        adapter.notifyDataSetChanged();//更新UI
    }


    /* 接受选择文件返回的路径 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    assert extras != null;
                    imageBitmap = (Bitmap) extras.get("data");
                    imageString=BitMapToString(imageBitmap);
                    String encodeImageString=FLYMSG_IMAGE_KEY+imageString+FLYMSG_IMAGE_KEY;
                    sendAndAddMessage(encodeImageString);

                    adapter.addToStart(getImageMessage(selfIp, selfName), true);
                    break;

                case REQUEST_IMAGE_PHOTO:
                    String uriPath = data.getData().getPath();
                    String[] filePathArray = new String[]{""};
                    filePathArray[0] = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
                    Log.d(TAG, "文件路径" + filePathArray[0]);


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

                        Log.e("MyFeiGeChatActivity", "发送地址有误");
                    }
                    if (sendto != null)
                        netThreadHelper.sendUdpData(sendPro.getProtocolString(), sendto, IpMessageConst.PORT);

                    //监听2425端口，准备接受TCP连接请求
                    Thread netTcpFileSendThread = new Thread(new NetTcpFileSendThread(filePathArray));
                    netTcpFileSendThread.start();    //启动线程

            }
        }
        //得到发送文件的路径
//			Bundle bundle = data.getExtras();
//			String filePaths = bundle.getString("filePaths");	//附加文件信息串,多个文件使用"\0"进行分隔
//			Toast.makeText(this, filePaths, Toast.LENGTH_SHORT).show();
//			String[] filePathArray = filePaths.split("\0");


    }


    @Override
    public void onMessageClick(IMessage message) {
        makeTextShort("you click the text");
    }

    @Override
    public void onMessageLongClick(IMessage message) {
//        String copyText=message.toString();
    }

    @Override
    public void onSelectionChanged(int count) {

    }

    //    MessagesListAdapter.Formatter formatter= MessagesListAdapter.Formatter<ChatMessage>() {
//        @Override
//        public String format(ChatMessage message) {
//            String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
//                    .format(message.getCreatedAt());
//
//            return String.format(Locale.getDefault(), "%s: %s (%s)",
//                    message.getUser().getName(), text, createdAt);
//        }
//    };
    @Override
    public void onLoadMore(int page, int totalItemsCount) {
//    if (totalItemsCount < this.total) {
////        loadMessages(...);
    }
}

