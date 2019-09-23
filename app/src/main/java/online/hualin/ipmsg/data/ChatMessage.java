package online.hualin.ipmsg.data;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatMessage implements IMessage {
    private String senderIp;    //消息发送者的ip
    private String senderName;    //消息发送者的名字
    //	private String reciverIp;
    private String msg;            //信息内容
    private Date time;        //发送时间 :格式：
    private boolean selfMsg;    //是否自己发送

    public ChatMessage() {
        this.selfMsg = false;    //默认不是自己
    }

    public ChatMessage(String senderIp, String senderName,
                       String msg, Date time) {
        super();
        this.senderIp = senderIp;
        this.senderName = senderName;
//		this.reciverIp = reciverIp;
        this.msg = msg;
        this.time = time;
        this.selfMsg = false;
    }

    public String getSenderIp() {
        return senderIp;
    }

    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    //	public String getReciverIp() {
//		return reciverIp;
//	}
//	public void setReciverIp(String reciverIp) {
//		this.reciverIp = reciverIp;
//	}
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isSelfMsg() {
        return selfMsg;
    }

    public void setSelfMsg(boolean selfMsg) {
        this.selfMsg = selfMsg;
    }

    public String getTimeStr() {    //返回格式为HH:mm:ss的时间字符串
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        return sdf.format(time);
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public IUser getUser() {
        return null;
    }

    @Override
    public Date getCreatedAt() {
        return null;
    }
}
