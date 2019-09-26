package online.hualin.ipmsg.data;


import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser {
    @Override
    public String getId() {
        return id;
    }

    public void setPlusMsg(){
        this.msgCount++;
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

    public User(){}

    public User(String name, String groupName, String ip) {
        super();
        msgCount = 0;    //初始化为零
        this.alias = "";
        this.groupName = groupName;
        this.hostName = "";
        this.mac = "";
        this.ip = ip;

        this.id=ip;
        this.name = name;
        this.avatar = null;
        this.online = true;
    }


    public User(String name, String alias, String groupName, String ip,
                String hostName, String avatar, boolean online, String mac) {
        super();
        msgCount = 0;    //初始化为零
        this.alias = alias;
        this.groupName = groupName;
        this.hostName = hostName;
        this.mac = mac;
        this.ip = ip;

        this.id=ip;
        this.name = name;
        this.avatar = avatar;
        this.online = online;
    }


}
