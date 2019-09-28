package online.hualin.flymsg.data;

import org.greenrobot.greendao.annotation.Id;

public class PoetryGson {
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String author;
        private String title;
        private String content;
}
