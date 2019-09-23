package online.hualin.ipmsg.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import online.hualin.ipmsg.MyApplication;

public class utils {
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
//					final String ipAddress=mInetAddress.getHostAddress();
                    if(!mInetAddress.isLoopbackAddress() && mInetAddress instanceof Inet4Address){
                        return mInetAddress.getHostAddress().toString();
//							if(!mInetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(mInetAddress.getHostAddress())){
//						return mInetAddress.getHostAddress().toString();
                    }
                }
            }
        }catch(SocketException ex){
            Log.e("MyFeiGeActivity", "获取本地IP地址失败");
        }

        return null;
    }

}
