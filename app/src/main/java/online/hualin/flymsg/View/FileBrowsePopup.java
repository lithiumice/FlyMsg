package online.hualin.flymsg.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import online.hualin.flymsg.R;
import razerdp.basepopup.BasePopupWindow;

public class FileBrowsePopup extends BasePopupWindow {


    public FileBrowsePopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.file_browser_popup);
//        LayoutInflater.from(getContext()).inflate(R.layout.file_browser_popup);
    }
}
