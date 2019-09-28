package online.hualin.flymsg.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class FileHistory {
    @Id
    private Long id;

    private String fileName;
    private String filePath;
    private String fileSenderName;
    private String fileSenderIp;
    @Generated(hash = 1193766006)
    public FileHistory(Long id, String fileName, String filePath,
            String fileSenderName, String fileSenderIp) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSenderName = fileSenderName;
        this.fileSenderIp = fileSenderIp;
    }
    @Generated(hash = 454477763)
    public FileHistory() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileSenderName() {
        return this.fileSenderName;
    }
    public void setFileSenderName(String fileSenderName) {
        this.fileSenderName = fileSenderName;
    }
    public String getFileSenderIp() {
        return this.fileSenderIp;
    }
    public void setFileSenderIp(String fileSenderIp) {
        this.fileSenderIp = fileSenderIp;
    }
}
