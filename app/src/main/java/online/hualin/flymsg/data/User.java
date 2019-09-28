package online.hualin.flymsg.data;


import android.media.Image;

import com.stfalcon.chatkit.commons.models.IUser;

import static online.hualin.flymsg.utils.CommonUtils.textAsBitmapString;

public class User implements IUser {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
        this.id=ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }



    private String alias;        //别名（若为pc，则是登录名）
    private String groupName;    //组名
    private String ip;            //ip地址//id
    private String hostName;    //主机名
    private String mac;            //MAC地址
    private int msgCount;        //未接收消息数

    private String id;
    private String name;    // 用户名
    private String avatar;
    private boolean online;

    public String getLastestMsg() {
        return lastestMsg;
    }

    public void setLastestMsg(String lastestMsg) {
        this.lastestMsg = lastestMsg;
    }

    private String lastestMsg="";

    public User(){}

    public User(String name, String groupName, String ip,String lastestMsg,String avatar) {
        super();
        msgCount = 0;    //初始化为零
        this.alias = "";
        this.groupName = groupName;
        this.hostName = "";
        this.mac = "";
        this.ip = ip;
        this.lastestMsg=lastestMsg;

        this.id=ip;
        this.name = name;
        this.online = true;
        this.avatar=avatar;

//        this.avatar = textAsBitmapString(name.substring(0,1),18);
    }
}
