package online.hualin.flymsg.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import online.hualin.flymsg.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_main_layout);

        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int oldElementIndex, int newElementIndex) {
            }
        });

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
            }
        });

    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Hotels", "Transfer your file without internet connection!",
                Color.parseColor("#678FB4"), R.drawable.intro_1, R.drawable.ic_unlock);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Banks", "SOGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGOOD",
                Color.parseColor("#65B0B4"), R.drawable.intro_2, R.drawable.ic_comment);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Stores", "OHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH",
                Color.parseColor("#9B90BC"), R.drawable.intro_3, R.drawable.ic_explore_title);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        return elements;
    }
}