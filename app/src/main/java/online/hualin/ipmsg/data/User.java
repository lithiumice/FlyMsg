package online.hualin.ipmsg.data;


public class User {
	private String userName;	// 用户名
	private String alias;		//别名（若为pc，则是登录名）
	private String groupName;	//组名
	private String ip;			//ip地址
	private String hostName;	//主机名
	private String mac;			//MAC地址
	private int msgCount;		//未接收消息数

	public int getGroup_online_num() {
		return group_online_num;
	}

	public void setGroup_online_num(int group_online_num) {
		this.group_online_num = group_online_num;
	}

	private int group_online_num;		//


	public User(){
		msgCount = 0;	//初始化为零
	}

	public User(String userName, String alias, String groupName, String ip,
				String hostName, String mac, int group_online_num) {
		super();
		msgCount = 0;	//初始化为零
		this.group_online_num=0;
		this.userName = userName;
		this.alias = alias;
		this.groupName = groupName;
		this.ip = ip;
		this.hostName = hostName;
		this.mac = mac;
		this.group_online_num=group_online_num;
	}


	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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



}
