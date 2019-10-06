package online.hualin.flymsg.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ViewUtils {

    public static void closeInputBoard(Activity activity) {

        try{
            ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity
                            .getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
