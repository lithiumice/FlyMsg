package online.hualin.flymsg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import online.hualin.flymsg.R;

public class LoadActivity extends AppCompatActivity {
    CountDownTimer countDownTimer = new CountDownTimer(500, 500) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            Intent intent = new Intent(LoadActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(-1, R.anim.activity_exit_alpha);
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
    }

    @Override
    protected void onResume() {
        countDownTimer.cancel();
        countDownTimer.start();
        super.onResume();
    }

    @Override
    protected void onStop() {
        countDownTimer.cancel();
        super.onStop();
    }
}



