package online.hualin.ipmsg.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class utils {

//    public void makeTextShort(String text) {
//        Snackbar.make(MyApplication.getContext().get, text, Toast.LENGTH_SHORT).show();
//    }

//    public void makeTextShort(String text) {
//        Snackbar.make(MyApplication.getApplication(). getWindow().getDecorView(), text, Snackbar.LENGTH_SHORT).show();
//    }

//    public void makeTextLong(String text) {
//        Snackbar.make(getWindow().getDecorView(), text, Snackbar.LENGTH_LONG).show();
//    }

    public static boolean  isForeground(Context context, String className) {
    if (context == null || TextUtils.isEmpty(className)) {
        return false;
    }

    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
    if (list != null && list.size() > 0) {
        ComponentName cpn = list.get(0).topActivity;
        if (className.equals(cpn.getClassName())) {
            return true;
        }
    }

    return false;
}

    public static Bitmap textAsBitmap(String text, float textSize) {

        TextPaint textPaint = new TextPaint();

        // textPaint.setARGB(0x31, 0x31, 0x31, 0);
        textPaint.setColor(Color.BLACK);

        textPaint.setTextSize(textSize);

        StaticLayout layout = new StaticLayout(text, textPaint, 450,
                Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, true);
        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth() + 20,
                layout.getHeight() + 20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);

        layout.draw(canvas);
        Log.d("textAsBitmap",
                String.format("1:%d %d", layout.getWidth(), layout.getHeight()));
        return bitmap;
    }

    //判断wifi是否打开
    public static boolean isWifiActive(){
        ConnectivityManager mConnectivity = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(mConnectivity != null){
            NetworkInfo[] infos = mConnectivity.getAllNetworkInfo();

            if(infos != null){
                for(NetworkInfo ni: infos){
                    if("WIFI".equals(ni.getTypeName()) && ni.isConnected())
                        return true;
                }
            }
        }

        return false;
    }

    //得到本机IP地址
    public static String getLocalIpAddress(){
        try{
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while(en.hasMoreElements()){
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
                while(enumIpAddr.hasMoreElements()){
                    InetAddress mInetAddress = enumIpAddr.nextElement();
                    if(!mInetAddress.isLoopbackAddress() && mInetAddress instanceof Inet4Address){
                        return mInetAddress.getHostAddress();
                    }
                }
            }
        }catch(SocketException ex){
            Log.e("MyFeiGeActivity", "获取本地IP地址失败");
        }

        return null;
    }

}
