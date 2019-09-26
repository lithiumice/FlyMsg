package online.hualin.ipmsg.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import online.hualin.ipmsg.R;
import online.hualin.ipmsg.net.NetTcpFileReceiveThread;
import online.hualin.ipmsg.net.NetThreadHelper;
import online.hualin.ipmsg.utils.IpMessageConst;
import online.hualin.ipmsg.utils.IpMessageProtocol;
import online.hualin.ipmsg.utils.MyApplication;
import online.hualin.ipmsg.utils.UsedConst;

public abstract class MyFeiGeBaseActivity extends AppCompatActivity {
    protected static LinkedList<MyFeiGeBaseActivity> queue = new LinkedList<MyFeiGeBaseActivity>();
    protected static NetThreadHelper netThreadHelper;
    private static int notification_id = 9786970;
    private static Ringtone ringTone;
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
                            "附加文件信息:\t" + extraMsg[1] + "\n" +
                            "发送者名称:\t" + extraMsg[2] + "\n" +
                            "文件总数:\t" + fileInfos.length;

                    new AlertDialog.Builder(queue.getLast())
                            .setIcon(R.drawable.ic_about)
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
                    MyFeiGeBaseActivity oneActivity = queue.getLast();
                    oneActivity.getNotificationManager().notify(notification_id, oneActivity.getNotification("文件" + (sendedPer[0] + 1) + "接受中:" + sendedPer[1] + "%", sendedPer[1]));

                }
                break;

                case UsedConst.FILERECEIVESUCCESS: {    //成功接受文件
                    int[] successNum = (int[]) msg.obj;
                    StringBuilder notText = new StringBuilder("第" + successNum[0] + "个文件接收成功");
                    queue.getLast().makeTextShort("第" + successNum[0] + "个文件接收成功");
//                    if (successNum[0] == successNum[1]) {
//                        notText = new StringBuilder("所有文件接收成功");
////					queue.getLast().mNotManager.cancel(notification_id);
//                        queue.getLast().makeTextShort("所有文件接收成功");
//                    }
                    MyFeiGeBaseActivity oneActivity = queue.getLast();
                    oneActivity.getNotificationManager().notify(notification_id, oneActivity.getNotification(notText.toString(), 100));
                }
                break;

                case UsedConst.UDPPORTFAIL:
                    Toast.makeText(MyApplication.getContext(), "bind port fail!", Toast.LENGTH_LONG);

//                case IpMessageConst.IPMSG_SENDMSG:
//                    netThreadHelper.getReceiveMsgQueue();
//                    break;
//
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
//    private Tencent mTencent;

//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager=getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//        fragmentTransaction.replace(null,fragment);
//        fragmentTransaction.commit();
//    }
//public void share(View view)
//{
//    Bundle bundle = new Bundle();
////这条分享消息被好友点击后的跳转URL。
//    bundle.putString(Constants.PARAM_TARGET_URL, "http://connect.qq.com/");
////分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_ SUMMARY不能全为空，最少必须有一个是有值的。
//    bundle.putString(Constants.PARAM_TITLE, "我在测试");
////分享的图片URL
//    bundle.putString(Constants.PARAM_IMAGE_URL,
//            "http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg");
////分享的消息摘要，最长50个字
//    bundle.putString(Constants.PARAM_SUMMARY, "测试");
////手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
//    bundle.putString(Constants.PARAM_APPNAME, "??我在测试");
////标识该消息的来源应用，值为应用名称+AppId。
//    bundle.putString(Constants.PARAM_APP_SOURCE, "星期几");
//
//    mTencent.shareToQQ(this, bundle , null);
//}

    public static MyFeiGeBaseActivity getActivity(int index) {
        if (index < 0 || index >= queue.size())
            throw new IllegalArgumentException("out of queue");
        return queue.get(index);
    }

    public static MyFeiGeBaseActivity getCurrentActivity() {
        return queue.getLast();
    }

    public static void sendMessage(int cmd, String text) {
        Message msg = new Message();
        msg.obj = text;
        msg.what = cmd;
        sendMessage(msg);
    }

    public static void globalToast(String msg) {
        Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_LONG);

    }

    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public static void sendEmptyMessage(int what) {
        handler.sendEmptyMessage(what);
    }

    public static void playMsg() {
        ringTone.play();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netThreadHelper = NetThreadHelper.newInstance();
//        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());

//        mNotManager = getNotificationManager();
//        mNotification = new NotificationCompat.Builder(getApplicationContext(), "0")
//                .setProgress(100, 0, false).setContentTitle("接收文件").build();
//        Intent notificationIntent = new Intent(this, MyFeiGeBaseActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        mNotification.contentIntent = contentIntent;

        if (!queue.contains(this))
            queue.add(this);
        ringTone = RingtoneManager.getRingtone(getApplicationContext()
                , Uri.fromFile(new File("/system/media/audio/ringtones/luna.ogg")));
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

    protected void showNotification(String title) {
        Intent intent = new Intent(this, MyFeiGeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notBuild = new NotificationCompat.Builder(this, "0");
        notBuild.setSmallIcon(R.drawable.ic_bird);
        notBuild.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bird));
        notBuild.setContentIntent(pi);
        notBuild.setContentTitle(title);
        getNotificationManager().notify(notification_id, notBuild.build());
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

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MyFeiGeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "0");
        builder.setSmallIcon(R.drawable.ic_bird);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bird));
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void init_fade() {
        Transition transition = new Fade().setDuration(200);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void init_Slide() {
        Transition transition = new Slide().setDuration(200);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void init_explode() {
        Explode explode = new Explode();
        explode.setDuration(200);
        getWindow().setEnterTransition(explode);
    }
}
