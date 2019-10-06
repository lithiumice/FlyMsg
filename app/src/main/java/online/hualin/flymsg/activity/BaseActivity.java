package online.hualin.flymsg.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;
import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.DaoSession;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;

import es.dmoral.toasty.Toasty;
import online.hualin.flymsg.App;
import online.hualin.flymsg.R;
import online.hualin.flymsg.db.ChatHistory;
import online.hualin.flymsg.net.NetTcpFileReceiveThread;
import online.hualin.flymsg.net.NetTcpFileSendThread;
import online.hualin.flymsg.net.NetThreadHelper;
import online.hualin.flymsg.utils.IpMessageConst;
import online.hualin.flymsg.utils.IpMessageProtocol;
import online.hualin.flymsg.utils.ThemeUtils;
import online.hualin.flymsg.utils.UsedConst;

public abstract class BaseActivity extends AppCompatActivity {
    protected static LinkedList<BaseActivity> queue = new LinkedList<BaseActivity>();
    protected static NetThreadHelper netThreadHelper;
    private static String notification_id_send = "flymsgSend";
    private static String notification_id_receive = "flymsgReceive";
    private static String notify_channel_receive = "接受文件通知";
    private static String notify_channel_send = "发送文件通知";
    private static Ringtone ringTone;
    private static SharedPreferences pref = App.getPref();
//    private static App app;
private int theme;

    private static Handler handler = new Handler() {

        @Override
        public void handleMessage(@org.jetbrains.annotations.NotNull Message msg) {
            if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toasty.error(getCurrentActivity(),"未正确设置储存权限",Toasty.LENGTH_LONG).show();
                return;
            }

            switch (msg.what) {

                case IpMessageConst.IPMSG_SENDMSG | IpMessageConst.IPMSG_FILEATTACHOPT: {
                    //收到发送文件请求
                    final String[] extraMsg = (String[]) msg.obj;    //得到附加文件信息,字符串数组，分别放了  IP，附加文件信息,发送者名称，包ID
                    Log.d("receive file....", "receive file from :" + extraMsg[2] + "(" + extraMsg[0] + ")");
                    Log.d("receive file....", "receive file info:" + extraMsg[1]);
                    byte[] bt = {0x07};        //用于分隔多个发送文件的字符
                    String splitStr = new String(bt);
                    final String[] fileInfos = extraMsg[1].split(splitStr);    //使用分隔字符进行分割

                    Log.d("feige", "收到文件传输请求,共有" + fileInfos.length + "个文件");

                    String infoStr = "发送者IP:\t" + extraMsg[0] + "\n" +
                            "文件信息:\t" + extraMsg[1].split(":")[1] + "\n" +
                            "发送者名称:\t" + extraMsg[2] + "\n" +
                            "文件总数:\t" + fileInfos.length;

                    //TODO
                    insertChatHisData(extraMsg[0], extraMsg[2], extraMsg[1]);

                    if (pref.getBoolean("AutoReceive", false)) {
                        Thread fileReceiveThread = new Thread(new NetTcpFileReceiveThread(extraMsg[3], extraMsg[0], fileInfos));    //新建一个接受文件线程
                        fileReceiveThread.start();    //启动线程

                        Toasty.info(getCurrentActivity(), "开始接收文件", Toast.LENGTH_SHORT).show();
                        queue.getLast().showNotification("开始接收文件", "开始接收文件", -1);    //显示notification
                    } else {
                        new AlertDialog.Builder(queue.getLast())
                                .setIcon(R.drawable.ic_about_white_24dp)
                                .setTitle("收到文件传输请求")
                                .setMessage(infoStr)
                                .setPositiveButton("接收",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Thread fileReceiveThread = new Thread(new NetTcpFileReceiveThread(extraMsg[3], extraMsg[0], fileInfos));    //新建一个接受文件线程
                                                fileReceiveThread.start();    //启动线程

                                                Toasty.info(getCurrentActivity(), "开始接收文件", Toast.LENGTH_SHORT).show();
                                                queue.getLast().showNotification("开始接收文件", "开始接收文件", -1);    //显示notification
                                            }
                                        })
                                .setNegativeButton("取消",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //发送拒绝报文
                                                //构造拒绝报文
                                                IpMessageProtocol ipmsgSend = new IpMessageProtocol();
                                                ipmsgSend.setVersion("" + IpMessageConst.VERSION);    //拒绝命令字
                                                ipmsgSend.setCommandNo(IpMessageConst.IPMSG_RELEASEFILES);
                                                ipmsgSend.setSenderName(App.getDeviceName());
                                                ipmsgSend.setSenderHost("127.0.0.1");
                                                ipmsgSend.setAdditionalSection(extraMsg[3] + "\0");    //附加信息里是确认收到的包的编号

                                                InetAddress sendAddress = null;
                                                try {
                                                    sendAddress = InetAddress.getByName(extraMsg[0]);
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                }
                                                netThreadHelper.sendUdpData(ipmsgSend.getProtocolString(), sendAddress, IpMessageConst.PORT);

                                            }
                                        })
                                .setNeutralButton("自动接收文件", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pref.edit().putBoolean("AutoReceive", true).apply();
                                        Toasty.warning(getCurrentActivity(), "自动接收文件已打开,你可以在设置界面关闭", Toasty.LENGTH_LONG).show();
                                        Thread fileReceiveThread = new Thread(new NetTcpFileReceiveThread(extraMsg[3], extraMsg[0], fileInfos));    //新建一个接受文件线程
                                        fileReceiveThread.start();    //启动线程

                                        queue.getLast().showNotification("开始接收文件", "开始接收文件", -1);    //显示notification
                                    }
                                })
                                .show();
                    }

                }
                break;

                case UsedConst.FILERECEIVEINFO: {    //更新接收文件进度条
//                    int[] sendedPer = (int[]) msg.obj;    //得到信息
                    String[] sendInfo = (String[]) msg.obj;
                    BaseActivity oneActivity = queue.getLast();
                    oneActivity.showNotification("文件" + sendInfo[0] + "接受中:" + sendInfo[1] + "%", "", Integer.parseInt(sendInfo[1]));

                }
                break;

                case UsedConst.FILESENDINFO: {
//                    int[] sendedPer = (int[]) msg.obj;
                    String[] sendInfo = (String[]) msg.obj;

                    BaseActivity oneActivity = queue.getLast();
                    oneActivity.showNotification("文件" + sendInfo[0] + "发送中:" + sendInfo[1] + "%", "", Integer.parseInt(sendInfo[1]));

                }
                break;

                case UsedConst.FILERECEIVESUCCESS: {    //成功接受文件
                    String info = (String) msg.obj;

                    BaseActivity oneActivity = queue.getLast();
                    oneActivity.showNotificationSuccess("文件接收成功", info + "接收成功", -1, 0, null);
                }

                case UsedConst.FILESENDSUCCESS: {
                    String info = (String) msg.obj;
                    BaseActivity oneActivity = queue.getLast();
                    oneActivity.showNotificationSuccess("文件发送成功", info + "发送成功", -1, 1, null);
                }
                break;

                case UsedConst.UDPPORTFAIL:
                    globalToast("绑定2425端口失败,请检查有无软件占用改端口");

                case UsedConst.FILERECEIVEFAIL:
                    globalToast("文件接受失败!");

                default:
                    if (queue.size() > 0)
                        queue.getLast().processMessage(msg);
                    break;
            }
        }

    };

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private void onPreCreate() {
        theme=pref.getInt("theme_change", R.style.Theme7);
        setTheme(theme);
    }
    private NotificationManager notificationManager;
    private Notification notification;

    private static void insertChatHisData(String senderIp, String senderName, String msgStr) {
        DaoSession daoSession = App.getDaoSession();
        ChatHistoryDao chatHistoryDao = daoSession.getChatHistoryDao();

        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setSenderIp(senderIp);
        chatHistory.setSenderName(senderName);
        chatHistory.setSendMsg(msgStr);
        chatHistory.setTime(new Date().toString());
        chatHistoryDao.insert(chatHistory);
    }

    public static BaseActivity getActivity(int index) {
        if (index < 0 || index >= queue.size())
            throw new IllegalArgumentException("out of queue");
        return queue.get(index);
    }

    public static BaseActivity getCurrentActivity() {
        return queue.getLast();
    }

    public static void sendMessage(int cmd, Object text) {//customise
        Message msg = new Message();
        msg.obj = text;
        msg.what = cmd;
        sendMessage(msg);
    }

    public static void globalToast(String msg) {
        Toasty.info(App.getContext(), msg, Toast.LENGTH_SHORT).show();

    }

    public static void globalToastLong(String msg) {
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_LONG).show();

    }

    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public static void sendEmptyMessage(int what) {
        handler.sendEmptyMessage(what);
    }

    public static void playMsg() {
        boolean isPlayNotify = pref.getBoolean("switch_notify", true);
        if (isPlayNotify) {
            ringTone.play();
        }
    }

    public static void sendFileByPaths(String[] filePathArray, String senderName, String senderGroup, String receiverIp) {

        //发送传送文件UDP数据报
        IpMessageProtocol sendPro = new IpMessageProtocol();
        sendPro.setVersion("" + IpMessageConst.VERSION);
        sendPro.setCommandNo(IpMessageConst.IPMSG_SENDMSG | IpMessageConst.IPMSG_FILEATTACHOPT);
        sendPro.setSenderName(senderName);
        sendPro.setSenderHost(senderGroup);
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
        Thread netTcpFileSendThread = new Thread(new NetTcpFileSendThread(filePathArray));
        netTcpFileSendThread.start();    //启动线程

    }

    public void setToolbar(Toolbar toolbar, int indicator) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                if (indicator == 0) {
                    indicator = R.drawable.ic_menu;
                } else if (indicator == 1) {
                    indicator = R.drawable.ic_arrow_back;
                }
                getSupportActionBar().setHomeAsUpIndicator(indicator);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netThreadHelper = NetThreadHelper.newInstance();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (!queue.contains(this))
            queue.add(this);
        ringTone = RingtoneManager.getRingtone(getApplicationContext()
                , Uri.fromFile(new File("/system/media/audio/ringtones/luna.ogg")));
        onPreCreate();
        ThemeUtils.initStatusBarColor(getCurrentActivity(), ThemeUtils.getPrimaryDarkColor(getCurrentActivity()));

    }

    public void makeTextShort(String text) {
        Snackbar.make(getWindow().getDecorView(), text, Snackbar.LENGTH_SHORT).show();
    }

    public void makeTextLong(String text) {
        Snackbar.make(getWindow().getDecorView(), text, Snackbar.LENGTH_LONG).show();
    }

    public abstract void processMessage(Message msg);

    public void exit() {
        while (queue.size() > 0)
            queue.getLast().finish();
    }

    @Override
    public void finish() {

        super.finish();
        queue.removeLast();
    }

    public void showNotification(String title, String content, int progress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationChannel mChannel = new NotificationChannel(notification_id_send, notify_channel_receive, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            if (progress > 0) {
                notification = new Notification.Builder(this, notify_channel_receive)
                        .setChannelId(notification_id_send)
                        .setSmallIcon(R.drawable.ic_bird_f)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bird_f))
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pi)
                        .setProgress(100, progress, false)
                        .build();
            } else {
                notification = new Notification.Builder(this, notify_channel_receive)
                        .setChannelId(notification_id_send)
                        .setSmallIcon(R.drawable.ic_bird_f)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bird_f))
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pi)
                        .build();
            }
            getNotificationManager().notify(1, notification);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationCompat.Builder notBuild = new NotificationCompat.Builder(this, "FlyMsg");
            notBuild.setSmallIcon(R.drawable.ic_bird_f);
            notBuild.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bird_f));
            notBuild.setContentIntent(pi);
            notBuild.setContentTitle(title);
            notBuild.setContentText(content);

            getNotificationManager().notify(1, notBuild.build());
        }
    }


    public void showNotificationSuccess(String title, String content, int progress, int type, String filePath) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        String notification_id;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            StringBuilder notify_channel = new StringBuilder();
            if (type == 0) {
                notify_channel = new StringBuilder(notify_channel_receive);
                notification_id=notification_id_receive;
            } else {
                notify_channel = new StringBuilder(notify_channel_send);
                notification_id=notification_id_send;

            }

            NotificationChannel mChannel = new NotificationChannel(notification_id, notify_channel.toString(), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
            if (progress > 0) {
                notification = new Notification.Builder(this, notify_channel.toString())
                        .setChannelId(notification_id)
                        .setSmallIcon(R.drawable.ic_bird_f)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bird_f))
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pi)
                        .setProgress(100, progress, false)
                        .build();
            } else {
                notification = new Notification.Builder(this, notify_channel.toString())
                        .setChannelId(notification_id)
                        .setSmallIcon(R.drawable.ic_bird_f)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bird_f))
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pi)
                        .build();
            }
            getNotificationManager().notify(1, notification);
        } else {
            NotificationCompat.Builder notBuild = new NotificationCompat.Builder(this, "FlyMsg");
            notBuild.setSmallIcon(R.drawable.ic_bird_f);
            notBuild.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bird_f));
            notBuild.setContentIntent(pi);
            notBuild.setContentTitle(title);
            notBuild.setContentText(content);

            getNotificationManager().notify(1, notBuild.build());
        }

        playMsg();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int newTheme = pref.getInt("theme_change", theme);
        if (newTheme != theme) {
            recreate();
        }
    }
}
