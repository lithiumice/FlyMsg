package online.hualin.ipmsg.activity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import online.hualin.ipmsg.MyApplication;
import online.hualin.ipmsg.R;
import online.hualin.ipmsg.net.NetTcpFileReceiveThread;
import online.hualin.ipmsg.net.NetThreadHelper;
import online.hualin.ipmsg.utils.IpMessageConst;
import online.hualin.ipmsg.utils.IpMessageProtocol;
import online.hualin.ipmsg.utils.UsedConst;
import online.hualin.ipmsg.utils.DownloadService;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public abstract class MyFeiGeBaseActivity extends AppCompatActivity {
    private static int notification_id = 9786970;
    private NotificationManager mNotManager;
    private Notification mNotification;

    protected static LinkedList<MyFeiGeBaseActivity> queue = new LinkedList<MyFeiGeBaseActivity>();
    private static MediaPlayer player;
    protected static NetThreadHelper netThreadHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        netThreadHelper = NetThreadHelper.newInstance();

        mNotManager = getNotificationManager();
        mNotification = new NotificationCompat.Builder(getApplicationContext(), "0")
                .setProgress(100, 0, false).setContentTitle("接收文件").build();
        Intent notificationIntent = new Intent(this, MyFeiGeBaseActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mNotification.contentIntent = contentIntent;

        if (!queue.contains(this))
            queue.add(this);
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.msg);
            try {
                player.prepare();
            } catch (IllegalStateException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public static MyFeiGeBaseActivity getActivity(int index) {
        if (index < 0 || index >= queue.size())
            throw new IllegalArgumentException("out of queue");
        return queue.get(index);
    }

    public static MyFeiGeBaseActivity getCurrentActivity() {
        return queue.getLast();
    }

    public void makeTextShort(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    public abstract void processMessage(Message msg);

    @Override
    public void finish() {

        super.finish();
        queue.removeLast();
    }

    public static void sendMessage(int cmd, String text) {
        Message msg = new Message();
        msg.obj = text;
        msg.what = cmd;
        sendMessage(msg);
    }

    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public static void sendEmptyMessage(int what) {
        handler.sendEmptyMessage(what);
    }

    private static Handler handler = new Handler() {

        @Override
        public void handleMessage(@org.jetbrains.annotations.NotNull Message msg) {

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
                            "发送者名称:\t" + extraMsg[2] + "\n" +
                            "文件总数:\t" + fileInfos.length + "个";

                    new AlertDialog.Builder(queue.getLast())
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("收到文件传输请求")
                            .setMessage(infoStr)
                            .setPositiveButton("接收",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Thread fileReceiveThread = new Thread(new NetTcpFileReceiveThread(extraMsg[3], extraMsg[0], fileInfos));    //新建一个接受文件线程
                                            fileReceiveThread.start();    //启动线程

                                            Toast.makeText(getCurrentActivity(), "开始接收文件", Toast.LENGTH_SHORT).show();
                                            queue.getLast().showNotification("开始接收文件");    //显示notification
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
                                            ipmsgSend.setSenderName(R.string.default_device_name + "");
                                            ipmsgSend.setSenderHost(R.string.default_device_group + "");
                                            ipmsgSend.setAdditionalSection(extraMsg[3] + "\0");    //附加信息里是确认收到的包的编号

                                            InetAddress sendAddress = null;
                                            try {
                                                sendAddress = InetAddress.getByName(extraMsg[0]);
                                            } catch (UnknownHostException e) {
                                                e.printStackTrace();
                                            }
                                            netThreadHelper.sendUdpData(ipmsgSend.getProtocolString(), sendAddress, IpMessageConst.PORT);

                                        }
                                    }).show();

                }
                break;

                case UsedConst.FILERECEIVEINFO: {    //更新接收文件进度条
                    int[] sendedPer = (int[]) msg.obj;    //得到信息
                    MyFeiGeBaseActivity oneActivity=queue.getLast();
                    oneActivity.getNotificationManager().notify(notification_id,oneActivity.getNotification("文件" + (sendedPer[0] + 1) + "接受中:" + sendedPer[1] + "%",sendedPer[1]));

                }
                break;

                case UsedConst.FILERECEIVESUCCESS: {    //成功接受文件
                    int[] successNum = (int[]) msg.obj;
                    StringBuilder notText=new StringBuilder("第" + successNum[0] + "个文件接收成功");
                    queue.getLast().makeTextShort("第" + successNum[0] + "个文件接收成功");
                    if (successNum[0] == successNum[1]) {
                        notText=new StringBuilder("所有文件接收成功");
//					queue.getLast().mNotManager.cancel(notification_id);
                        queue.getLast().makeTextShort("所有文件接收成功");
                    }
                    MyFeiGeBaseActivity oneActivity=queue.getLast();
                    oneActivity.getNotificationManager().notify(0,oneActivity.getNotification(notText.toString(),100));

                }
                break;
                default:
                    if (queue.size() > 0)
                        queue.getLast().processMessage(msg);
                    break;
            }
        }

    };

    public void exit() {
        while (queue.size() > 0)
            queue.getLast().finish();
    }

    public static void playMsg() {
        player.start();
    }

    protected void showNotification(String title) {
        Intent intent = new Intent(this, MyFeiGeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notBuild = new NotificationCompat.Builder(this,"0");
        notBuild.setSmallIcon(R.mipmap.ic_launcher);
        notBuild.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        notBuild.setContentIntent(pi);
        notBuild.setContentTitle(title);
        getNotificationManager().notify(notification_id, notBuild.build());
    }


    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MyFeiGeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"0");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (progress >= 0) {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
