package online.hualin.flymsg.loadsir.callback;

import android.content.Context;
import android.view.View;

import com.kingja.loadsir.callback.Callback;

import online.hualin.flymsg.R;


/**
 * Description:TODO
 * Create Time:2017/9/4 10:22
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */

public class GithubCallback extends Callback {

    @Override
    protected int onCreateView() {
        return R.layout.layout_empty;
    }

    @Override
    protected boolean onRetry(Context context, View view) {
        return true;
    }
}
