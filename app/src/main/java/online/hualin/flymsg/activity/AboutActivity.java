package online.hualin.flymsg.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaeger.library.StatusBarUtil;

import online.hualin.flymsg.Constant;
import online.hualin.flymsg.R;
import online.hualin.flymsg.utils.CommonUtils;

public class AboutActivity extends LiteBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
//        StatusBarUtil.setTransparent(this);

        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        initView();
    }

    public void initView() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.card_slow);
        ScrollView scroll_about = findViewById(R.id.scroll_about);
        scroll_about.startAnimation(animation);

        TextView tv_card_about_2_email = findViewById(R.id.tv_card_about_2_email);
        TextView tv_card_about_2_git_hub = findViewById(R.id.tv_card_about_2_git_hub);
        TextView tv_card_about_2_website = findViewById(R.id.tv_card_about_2_website);
        tv_card_about_2_email.setOnClickListener(this);
        tv_card_about_2_git_hub.setOnClickListener(this);
        tv_card_about_2_website.setOnClickListener(this);

        FloatingActionButton fab = findViewById(R.id.fab_about_share);
        fab.setOnClickListener(this);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setStartOffset(600);

        TextView tv_about_version = findViewById(R.id.tv_about_version);
        tv_about_version.setText(CommonUtils.getVersionName(this));
        tv_about_version.startAnimation(alphaAnimation);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;}
                return true;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        switch (view.getId()) {
            case android.R.id.home:
                finish();
                break;


            case R.id.tv_card_about_2_email:
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(Constant.EMAIL));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_email_intent));
                intent.putExtra(Intent.EXTRA_TEXT, "Hi,");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, getString(R.string.about_not_found_email), Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.tv_card_about_2_git_hub:
                intent.setData(Uri.parse(Constant.GIT_HUB));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                break;

            case R.id.tv_card_about_2_website:
                intent.setData(Uri.parse(Constant.MY_WEBSITE));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                break;

            case R.id.fab_about_share:
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Constant.SHARE_CONTENT);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share with"));
                break;
        }
    }

}
