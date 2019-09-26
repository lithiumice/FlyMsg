package online.hualin.ipmsg.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Window;

import online.hualin.ipmsg.R;

public class LoadActivity extends AppCompatActivity {
    private final static int LOAD_ACTIVITY_TIME=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 设置contentFeature,可使用切换动画
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            init_explode();// 分解
//            init_Slide();//滑动进入
            init_fade();//淡入淡出
        }
        setContentView(R.layout.activity_load);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent=new Intent(getApplicationContext(),MyFeiGeActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoadActivity.this).toBundle());
                } else {
                    startActivity(intent);
                }

            }
        },LOAD_ACTIVITY_TIME);
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void init_fade() {
        Transition transition = new Fade().setDuration(200);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);
    }
}
