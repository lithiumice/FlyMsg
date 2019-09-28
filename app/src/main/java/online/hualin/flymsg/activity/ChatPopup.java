package online.hualin.flymsg.activity;

import android.content.Context;
import android.view.View;

import online.hualin.flymsg.R;
import razerdp.basepopup.BasePopupWindow;

public class ChatPopup extends BasePopupWindow {
    public ChatPopup(Context context){
        super(context);
    }
    @Override
    public View onCreateContentView() {
        return createPopupById(R.id.chat_popup);
    }
}
