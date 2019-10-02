package online.hualin.flymsg;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;


//可用于获取打开以下文件的intent
//PDF,PPT,WORD,EXCEL,CHM,HTML,TEXT,AUDIO,VIDEO

//错误示例:
//这个不行，可能是因为PDF.apk程序没有权限访问其它APK里的asset资源文件,又或者是路径写错?
//Intent it = getPdfFileIntent("file:///android_asset/helphelp.pdf");

//下面这些都OK
//Intent it = getHtmlFileIntent("/mnt/sdcard/tutorial.html");//SD卡主目录
//Intent it = getHtmlFileIntent("/sdcard/tutorial.html");//SD卡主目录,这样也可以
//Intent it = getHtmlFileIntent("/system/etc/tutorial.html");//系统内部的etc目录
//Intent it = getPdfFileIntent("/system/etc/helphelp.pdf");
//Intent it = getWordFileIntent("/system/etc/help.doc");
//Intent it = getExcelFileIntent("/mnt/sdcard/Book1.xls")
//Intent it = getPptFileIntent("/mnt/sdcard/download/Android_PPT.ppt");//SD卡的download目录下
//Intent it = getVideoFileIntent("/mnt/sdcard/ice.avi");
//Intent it = getAudioFileIntent("/mnt/sdcard/ren.mp3");
//Intent it = getImageFileIntent("/mnt/sdcard/images/001041580.jpg");
//Intent it = getTextFileIntent("/mnt/sdcard/hello.txt",false); startActivity( it );


public class Intent {

    private void dispatchTakePictureIntent(Activity context,int REQUEST_IMAGE_CAPTURE) {
        android.content.Intent takePictureIntent = new android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //android获取一个用于打开HTML文件的intent
    public static android.content.Intent getHtmlFileIntent(String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //android获取一个用于打开图片文件的intent
    public static android.content.Intent getImageFileIntent(String param) {
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    //android获取一个用于打开PDF文件的intent
    public static android.content.Intent getPdfFileIntent(String param) {
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    //android获取一个用于打开文本文件的intent
    public static android.content.Intent getTextFileIntent(String param, boolean paramBoolean) {
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    //android获取一个用于打开音频文件的intent
    public static android.content.Intent getAudioFileIntent(String param) {
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //android获取一个用于打开视频文件的intent
    public static android.content.Intent getVideoFileIntent(String param) {
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //android获取一个用于打开CHM文件的intent
    public static android.content.Intent getChmFileIntent(String param) {
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //android获取一个用于打开Word文件的intent
    public static android.content.Intent getWordFileIntent(String param) {
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static android.content.Intent getExcelFileIntent(String param) {
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static android.content.Intent getPptFileIntent(String param) {
        android.content.Intent intent = new android.content.Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }
}