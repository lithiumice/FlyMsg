package online.hualin.flymsg.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import online.hualin.flymsg.App;
import online.hualin.flymsg.R;
import online.hualin.flymsg.activity.ChatActivity;

import static android.content.Context.INPUT_METHOD_SERVICE;

//import com.google.android.material.snackbar.Snackbar;

public class CommonUtils {

    public static void closeInputBoard(Activity activity){

        ((InputMethodManager) activity. getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(activity
                        .getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

    }

//    public static Bitmap urlToImage(String url){
////        String name = App.getApplication().getString(url);
//        Bitmap bitmap=new Bitmap();
//        try {
//            URL url_value = new URL(name);
//            Bitmap bitmap = BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return bitmap==null ? null : bitmap;
//    }

    private String handleImageBeforeKitKat(Context context,Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(context, uri, null);
        return imagePath;
    }

    private String getImagePath(Context context,Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = context. getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public static boolean isForeground(Context context, String className) {
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

    public static String textAsBitmapString(String text, float textSize) {

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
        canvas.drawColor(Color.RED);

        layout.draw(canvas);
        Log.d("textAsBitmap",
                String.format("1:%d %d", layout.getWidth(), layout.getHeight()));
        return BitMapToString(bitmap);
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static boolean isWifiActive() {
        ConnectivityManager mConnectivity = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivity != null) {
            NetworkInfo[] infos = mConnectivity.getAllNetworkInfo();

            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if ("WIFI".equals(ni.getTypeName()) && ni.isConnected())
                        return true;
                }
            }
        }

        return false;
    }

    //得到本机IP地址
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress mInetAddress = enumIpAddr.nextElement();
                    if (!mInetAddress.isLoopbackAddress() && mInetAddress instanceof Inet4Address) {
                        return mInetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Utils", "获取本地IP地址失败");
        }
        return null;
    }

    public static void  setClipboard(Context context, String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

}
