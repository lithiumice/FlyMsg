package online.hualin.flymsg.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.DaoSession;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.activity.VideoPickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import es.dmoral.toasty.Toasty;
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

import static com.vincent.filepicker.Constant.REQUEST_CODE_PICK_VIDEO;
import static com.vincent.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;
import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;
import static droidninja.filepicker.FilePickerConst.REQUEST_CODE_DOC;
import static droidninja.filepicker.FilePickerConst.REQUEST_CODE_PHOTO;
import static online.hualin.flymsg.utils.CommonUtils.BitMapToString;
import static online.hualin.flymsg.utils.CommonUtils.getLocalIpAddress;

public class ChatActivity extends BaseActivity implements OnClickListener, ReceiveMsgListener
        , MessagesListAdapter.OnMessageClickListener, MessagesListAdapter.OnMessageLongClickListener, MessagesListAdapter.OnLoadMoreListener
        , DateFormatter.Formatter, PopupMenu.OnMenuItemClickListener {
    public static final String TAG = "BaseActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_PHOTO = 2;
    static final int REQUEST_PERMS = 3;
    static final int RECEIVE_IMAGE = 4;
    static final int RECEIVE_StrMsg = 5;
    ImageLoader imageLoader = (ImageView imageView, @Nullable String url, @Nullable Object payload) -> {
        {
//            if (url == "receiver") {
//                Glide.with(ChatActivity.this).load(BitmapFactory.decodeResource(res, R.drawable.ic_1)).into(imageView);
//
//            } else if (url == "sender") {
//                Glide.with(ChatActivity.this).load(BitmapFactory.decodeResource(res, R.drawable.ic_2)).into(imageView);
//
//            }
        }
    };
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
    private ImageView chatPlus;
    private TextView chatTitle;
    private App app;
    private String deviceName;
    private int requestPermCode = 1;
    private String shareText = "";
    private String sharePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        deviceName = android.os.Build.DEVICE;
        res = getResources();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        receiverName = bundle.getString("receiverName");
        receiverIp = bundle.getString("receiverIp");
        receiverGroup = bundle.getString("receiverGroup");
        selfName = App.getDeviceName();
        selfGroup = App.getGroupName();
        selfIp = getLocalIpAddress();
        shareText = bundle.getString("shareText");
        sharePath = bundle.getString("sharePath");

        initMsgList();
        initView();
        initChatInput();
        if (shareText!=null){
            chat_input.getInputEditText().setText(shareText);
        }
        if (sharePath!=null){
            sendFileByPaths(new String[]{sharePath}, selfName, selfGroup, receiverIp);

        }

        netThreadHelper.addReceiveMsgListener(this);    //注册到listeners
    }

    private void initMsgList() {

        Iterator<ChatMessage> it = netThreadHelper.getReceiveMsgQueue().iterator();
        while (it.hasNext()) {
            //循环消息队列，获取队列中与本聊天activity相关信息
            ChatMessage temp = it.next();
            if (receiverIp.equals(temp.getSenderIp())) {
                msgList.add(temp);    //添加到显示list
                it.remove();        //将本消息从消息队列中移除
            }
        }
    }

    private void initView() {
        chatTitle = findViewById(R.id.main_titile);
        chatTitle.setText(receiverName);

        toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar, 1);

    }

    private void initChatInput() {
        chat_input = findViewById(R.id.chat_input);
        msgListView = findViewById(R.id.messagesList);
        chat_input.setInputListener((CharSequence input) -> {
            String msgStr = chat_input.getInputEditText().getText().toString().trim();
            sendAndAddMessage(msgStr);
            adapter.addToStart(addMessage(input.toString(), selfIp, selfName, "sender"), true);

            insertChatHisData("127.0.0.1", selfName, msgStr);

            return true;

        });
        chat_input.setAttachmentsListener(() -> {
            filePickIntent("Image");

        });

        adapter = new MessagesListAdapter<>(selfIp, imageLoader);
        adapter.addToEnd(msgList, false);
        msgListView.setAdapter(adapter);
    }

    private void filePickIntent(String type) {
        switch (type) {
            case "Image":
                Intent intent1 = new Intent(this, ImagePickActivity.class);
                intent1.putExtra(IS_NEED_CAMERA, true);
                intent1.putExtra(Constant.MAX_NUMBER, 9);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
            case "Video":
                Intent intent2 = new Intent(this, VideoPickActivity.class);
                intent2.putExtra(IS_NEED_CAMERA, true);
                intent2.putExtra(Constant.MAX_NUMBER, 9);
                startActivityForResult(intent2, REQUEST_CODE_PICK_VIDEO);
                break;
            case "Audio":
                Intent intent3 = new Intent(this, AudioPickActivity.class);
                intent3.putExtra(IS_NEED_RECORDER, true);
                intent3.putExtra(Constant.MAX_NUMBER, 9);
                startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
                break;
            case "File":
                Intent intent4 = new Intent(this, NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 9);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                break;
        }
    }

    private void insertChatHisData(String senderIp, String senderName, String msgStr) {
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        ChatHistoryDao chatHistoryDao = daoSession.getChatHistoryDao();

        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setSenderIp(senderIp);
        chatHistory.setSenderName(senderName);
        chatHistory.setSendMsg(msgStr);
        chatHistory.setTime(new Date().toString());
        chatHistoryDao.insert(chatHistory);
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

        if (receiverIp.equals(msg.getSenderIp())) {
            //若消息与本activity有关，则接收

            ChatActivity.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.addToStart((ChatMessage) msg, true);

                }
            });

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
//            case R.id.send_file:
//
//                checkAndRequirePerms(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
//                        , Manifest.permission.READ_EXTERNAL_STORAGE});
//                filePickIntent("Image");
//                PopupMenu popupMenu=new PopupMenu(this,send_button);
//                MenuInflater inflater=popupMenu.getMenuInflater();
//                inflater.inflate(R.menu.file_popup,popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(this);
//                popupMenu.show();
//                break;
            case R.id.clear_msg:
                adapter.clear();
                makeTextShort("已清除所有消息");
                break;
            case R.id.select_image:
                Toasty.warning(this, "Image", Toasty.LENGTH_SHORT).show();
                filePickIntent("Image");
                break;
            case R.id.select_audio:
                Toasty.warning(this, "Audio", Toasty.LENGTH_SHORT).show();
                filePickIntent("Audio");

                break;
            case R.id.select_file:
                Toasty.warning(this, "File", Toasty.LENGTH_SHORT).show();
                filePickIntent("File");

                break;
            case R.id.select_vedio:
                Toasty.warning(this, "Video", Toasty.LENGTH_SHORT).show();
                filePickIntent("Video");

                break;
            default:
                break;
        }
        return true;
    }


    public void checkAndRequirePerms(String[] permList) {
        for (String perm : permList) {
            if (ContextCompat.checkSelfPermission(ChatActivity.this, perm) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{perm}, requestPermCode);
            }
        }
    }

    private void openFileManager(String type, int REQUEST_FILE_CODE) {
        if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMS);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType(type + "/*");
            startActivityForResult(intent, REQUEST_FILE_CODE);
        }
    }

    private void openFile() {

        FilePickerBuilder.getInstance()
                .setActivityTheme(R.style.LibAppTheme)
                .enableCameraSupport(true)
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

            ArrayList<String> tmpPaths = new ArrayList<>();

            switch (requestCode) {
                case Constant.REQUEST_CODE_PICK_VIDEO:
                case Constant.REQUEST_CODE_PICK_FILE:
                case Constant.REQUEST_CODE_PICK_AUDIO:
                case Constant.REQUEST_CODE_PICK_IMAGE: {

                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (list == null) {
                        Toasty.warning(getApplicationContext(), "文件为空").show();
                        return;
                    }
                    for (ImageFile file : list) {
                        tmpPaths.add(file.getPath());
                    }

                    sendFileByPaths(arrayToString(tmpPaths), selfName, selfGroup, receiverIp);
                    return;

                }
                case REQUEST_IMAGE_PHOTO:
                case REQUEST_CODE_DOC:
                case REQUEST_CODE_PHOTO: {
                    tmpPaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    String[] filePathArray = arrayToString(tmpPaths);
                    sendFileByPaths(filePathArray, selfName, selfGroup, receiverIp);
                    break;

                }
//                    tmpPaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
//                    String[] filePathArray = arrayToString(tmpPaths);
//
//                    IpMessageProtocol sendPro = new IpMessageProtocol();
//                    sendPro.setVersion("" + IpMessageConst.VERSION);
//                    sendPro.setCommandNo(IpMessageConst.IPMSG_SENDMSG | IpMessageConst.IPMSG_FILEATTACHOPT);
//                    sendPro.setSenderName(selfName);
//                    sendPro.setSenderHost(selfGroup);
//                    String msgStr = "";    //发送的消息
//
//                    StringBuffer additionInfoSb = new StringBuffer();    //用于组合附加文件格式的sb
//                    for (String path : filePathArray) {
//                        File file = new File(path);
//                        additionInfoSb.append("0:");
//                        additionInfoSb.append(file.getName() + ":");
//                        additionInfoSb.append(Long.toHexString(file.length()) + ":");        //文件大小十六进制表示
//                        additionInfoSb.append(Long.toHexString(file.lastModified()) + ":");    //文件创建时间，现在暂时已最后修改时间替代
//                        additionInfoSb.append(IpMessageConst.IPMSG_FILE_REGULAR + ":");
//                        byte[] bt = {0x07};        //用于分隔多个发送文件的字符
//                        String splitStr = new String(bt);
//                        additionInfoSb.append(splitStr);
//                    }
//
//                    sendPro.setAdditionalSection(msgStr + "\0" + additionInfoSb.toString() + "\0");
//
//                    InetAddress sendto = null;
//                    try {
//                        sendto = InetAddress.getByName(receiverIp);
//                    } catch (UnknownHostException e) {
//
//                        Log.e("ChatActivity", "发送地址有误");
//                    }
//                    if (sendto != null)
//                        netThreadHelper.sendUdpData(sendPro.getProtocolString(), sendto, IpMessageConst.PORT);
//
//                    //监听2425端口，准备接受TCP连接请求
//                    Thread netTcpFileSendThread = new Thread(new NetTcpFileSendThread(filePathArray));
//                    netTcpFileSendThread.start();    //启动线程
//
//                    break;
            }
        }
    }

    private String[] arrayToString(ArrayList<String> arrayList) {
        String[] filePathArray = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            filePathArray[i] = arrayList.get(i);
        }
        return filePathArray;
    }

    @Override
    public void onMessageClick(IMessage message) {
        makeTextShort("you click:" + message.getText());
    }


    @Override
    public void onMessageLongClick(IMessage message) {
        Log.d(TAG, "message lonf click");
        String copyText = message.toString();
        CommonUtils.setClipboard(getApplicationContext(), copyText);
        globalToast("已复制" + copyText);
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {

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

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_image:
                Toasty.warning(this, "Image", Toasty.LENGTH_SHORT).show();
                filePickIntent("Image");
                break;
            case R.id.select_audio:
                Toasty.warning(this, "Audio", Toasty.LENGTH_SHORT).show();
                filePickIntent("Audio");

                break;
            case R.id.select_file:
                Toasty.warning(this, "File", Toasty.LENGTH_SHORT).show();
                filePickIntent("File");

                break;
            case R.id.select_vedio:
                Toasty.warning(this, "Video", Toasty.LENGTH_SHORT).show();
                filePickIntent("Video");

                break;
            default:
                break;
        }
        return false;
    }
}

