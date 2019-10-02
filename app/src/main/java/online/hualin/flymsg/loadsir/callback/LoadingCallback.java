package online.hualin.flymsg.loadsir.callback;

import com.kingja.loadsir.callback.Callback;

import online.hualin.flymsg.R;


/**
 * Description:TODO
 * Create Time:2017/9/4 10:22
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */

public class LoadingCallback extends Callback {

    @Override
    protected int onCreateView() {
        return R.layout.layout_loading;
    }
}
