package online.hualin.flymsg.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import es.dmoral.toasty.Toasty;
import online.hualin.flymsg.R;

import static online.hualin.flymsg.activity.BaseActivity.globalToastLong;

public class LoadActivity extends AppCompatActivity {
    private static final int REQUEST_PERMS = 1;
    private CountDownTimer countDownTimer = new CountDownTimer(500, 500) {
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
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
    }

    public void checkAndRequirePerms(String[] permList) {
        for (String perm : permList) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, REQUEST_PERMS);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toasty.info(getApplicationContext(),"已打开权限^_^",Toasty.LENGTH_SHORT).show();
                    countDownTimer.start();
                } else {
                    Toasty.error(getApplicationContext(),"你拒绝了权限,为正常使用你需要打开必须的权限",Toasty.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
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



