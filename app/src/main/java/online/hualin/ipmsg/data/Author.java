package online.hualin.ipmsg.data;

import com.stfalcon.chatkit.commons.models.IUser;

public class Author implements IUser {

    private String name;
    private String id;
    private String avatar;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}