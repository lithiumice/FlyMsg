package online.hualin.ipmsg.activity;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.github.paolorotolo.appintro.ISlideSelectionListener;
import com.github.paolorotolo.appintro.model.SliderPage;

import online.hualin.ipmsg.R;

public class IntroActivity extends AppIntro2 implements ISlideBackgroundColorHolder, ISlideSelectionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFadeAnimation();

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("FlyMsg");
        sliderPage.setDescription("采用ipmsg协议的传输软件");
        sliderPage.setImageDrawable(R.drawable.ic_bird);
        sliderPage.setBgColor(R.color.warm_grey);
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("FlyMsg");
        sliderPage2.setDescription("采用ipmsg协议的传输软件");
        sliderPage2.setImageDrawable(R.drawable.ic_bird);
        sliderPage2.setBgColor(R.color.warm_grey);
        addSlide(AppIntroFragment.newInstance(sliderPage2));
//        addSlide(mFragment);
//        addSlide(getSupportFragmentManager().findFragmentById(R.id.lottie_frag));
//        addSlide(getSupportFragmentManager().findFragmentById(R.id.lottie_logo));

        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
        setVibrate(true);
        setVibrateIntensity(30);

    }

    @Override
    public int getDefaultBackgroundColor() {
        // Return the default background color of the slide.
        return Color.parseColor("#000000");
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        // Set the background color of the view within your slide to which the transition should be applied.
        View layoutContainer=getWindow().getDecorView();
        if (layoutContainer != null) {
            layoutContainer.setBackgroundColor(backgroundColor);
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(Fragment oldFragment, Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
        askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

    }

    @Override
    public void onSlideSelected() {

    }

    @Override
    public void onSlideDeselected() {

    }
}