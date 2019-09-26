package online.hualin.ipmsg.data;

import androidx.annotation.Nullable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatMessage implements IMessage, MessageContentType.Image,MessageContentType {
    public String getSenderIp() {
        return senderIp;
    }

    public String getStatus() {
        return "Sent";
    }

    public String getTimeStr() {    //返回格式为HH:mm:ss的时间字符串
        return new SimpleDateFormat("HH:mm:ss").format(time);
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
        this.id=senderIp;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
        this.text=msg;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
        this.createdAt=time;
    }

    public boolean isSelfMsg() {
        return selfMsg;
    }

    public void setSelfMsg(boolean selfMsg) {
        this.selfMsg = selfMsg;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    private String senderIp;    //消息发送者的ip
    private String senderName;    //消息发送者的名字
    private String msg;            //信息内容
    private Date time;        //发送时间 :格式：
    private boolean selfMsg;    //是否自己发送

    private String id;
    private String text;
    private Date createdAt;
    private User user;
    private Image image;

    public ChatMessage(String senderIp, String senderName, String msg, Date time,User user) {
//        super();
        this.senderIp = senderIp;
        this.senderName = senderName;
        this.msg = msg;
        this.time = time;
        this.selfMsg = false;

        this.id = senderIp;
        this.text = msg;
        this.user = user;
        this.createdAt = time;
    }

}
