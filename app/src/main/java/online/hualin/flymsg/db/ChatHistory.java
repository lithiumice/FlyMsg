package online.hualin.flymsg.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class ChatHistory {
    @Id
    private Long id;
    
    private String senderIp;

    private String senderName;
    
    private String sendMsg;

    private String time;

    @Generated(hash = 2001886703)
    public ChatHistory(Long id, String senderIp, String senderName, String sendMsg,
            String time) {
        this.id = id;
        this.senderIp = senderIp;
        this.senderName = senderName;
        this.sendMsg = sendMsg;
        this.time = time;
    }

    @Generated(hash = 546665019)
    public ChatHistory() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderIp() {
        return this.senderIp;
    }

    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSendMsg() {
        return this.sendMsg;
    }

    public void setSendMsg(String sendMsg) {
        this.sendMsg = sendMsg;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

  
  
}
