package online.hualin.flymsg.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import es.dmoral.toasty.Toasty;
import online.hualin.flymsg.App;

import static android.content.Context.INPUT_METHOD_SERVICE;

//public static Bitmap urlToImage(String url){
//        Bitmap bitmap=new Bitmap();
//        try {
//        URL url_value = new URL(url);
//        bitmap = BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
//        }catch (Exception e){
//        e.printStackTrace();
//        }
//        return bitmap==null ? null : bitmap;
//        }

public class CommonUtils {
    public static void  globalToastShort(String text){
        Toasty.info(App.getContext(),text,Toasty.LENGTH_SHORT).show();
    }

    public static boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator downAnimator = ObjectAnimator.ofFloat(view, "translationZ", 16);
                downAnimator.setDuration(200);
                downAnimator.setInterpolator(new DecelerateInterpolator());
                downAnimator.start();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ObjectAnimator upAnimator = ObjectAnimator.ofFloat(view, "translationZ", 0);
                upAnimator.setDuration(200);
                upAnimator.setInterpolator(new AccelerateInterpolator());
                upAnimator.start();
                break;
        }
        return false;
    }

    public static boolean checkAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return "V: " + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getScreenWidthDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    public static boolean isGame(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_IS_GAME) == ApplicationInfo.FLAG_IS_GAME) {
                return true;
            }
            if (Build.VERSION.SDK_INT >= 26 && applicationInfo.category == ApplicationInfo.CATEGORY_GAME) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void closeInputBoard(Activity activity) {

        ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(activity
                        .getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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

    public static void setClipboard(Context context, String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toasty.info(context,"已复制:"+text,Toasty.LENGTH_SHORT).show();
    }

    private String handleImageBeforeKitKat(Context context, Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(context, uri, null);
        return imagePath;
    }

    private String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
