package online.hualin.ipmsg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import online.hualin.ipmsg.R;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        Intent intent=new Intent(getApplicationContext(),MyFeiGeActivity.class);
        startActivity(intent);

        finish();
    }
}
