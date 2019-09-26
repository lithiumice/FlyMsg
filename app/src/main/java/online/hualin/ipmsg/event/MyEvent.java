package online.hualin.ipmsg.event;

import online.hualin.ipmsg.adapter.UserAdapter;

public class MyEvent {
    public UserAdapter userAdapter;

    public MyEvent(UserAdapter userAdapter){
        this.userAdapter=userAdapter;
    }
}
