package online.hualin.flymsg.event;

import online.hualin.flymsg.adapter.UserAdapter;

public class MyEvent {
    public UserAdapter userAdapter;

    public MyEvent(UserAdapter userAdapter){
        this.userAdapter=userAdapter;
    }
}
