package online.hualin.flymsg.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import online.hualin.flymsg.R;

public class LiteBaseActivity extends AppCompatActivity {

    private int theme;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPreCreate();
    }

    private void onPreCreate() {
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        theme=sp.getInt("theme_change", R.style.Theme19);
        setTheme(theme);

    }

    //    当Activity 回调onRestart时（从上一个页面返回），检查当前主题是否已将被更改。
    @Override
    protected void onRestart() {
        super.onRestart();
        int newTheme = sp.getInt("theme_change", theme);
        if (newTheme != theme) {
            recreate();
        }
    }

}
