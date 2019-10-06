package online.hualin.flymsg.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceDialogFragmentCompat;

import online.hualin.flymsg.App;
import online.hualin.flymsg.R;

public class ThemeChoiceFragment extends PreferenceDialogFragmentCompat {
    private static final int DEFAULT_VALUE = R.style.Theme7;
    private ImageView mImageView;
    private RadioGroup mGroup1;
    private RadioGroup mGroup2;
    private RadioGroup mGroup3;
    private RadioGroup mGroup4;
    //    已保存的主题
    private int mTheme;
    private SharedPreferences mSp;
    //    style文件中的所有theme
    private int[] mThemes;
    //    所有的radioButton
    private int[] mRdoBtns;
    private boolean changeGroup = false;
    //    现在的value
    private int mCurrentValue;
    //    新的value
    private int mNewValue;
    RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (group != null && checkedId != -1 && changeGroup == false) {
                for (int i = 0; i < mRdoBtns.length; i++) {
                    if (checkedId == mRdoBtns[i]) {
                        mNewValue = mThemes[i];
                        group.check(mRdoBtns[i]);
                        getThemeChoicePreference().setValue(mNewValue);
                        getDialog().cancel();
                    }
                }
            }
        }
    };

    public static ThemeChoiceFragment newInstance(String key) {
        final ThemeChoiceFragment f = new ThemeChoiceFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        f.setArguments(b);
        return f;
    }

    @Override
    protected View onCreateDialogView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.dialog_theme_choice, null);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        Log.d("currentValue", mCurrentValue + "");
        Log.d("newValue", mNewValue + "");

    }

    public ThemeChoicePreference getThemeChoicePreference() {
        return (ThemeChoicePreference) getPreference();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.dialog_theme_choice, null);
        mGroup1 = (RadioGroup) view.findViewById(R.id.group1);
        mGroup2 = (RadioGroup) view.findViewById(R.id.group2);
        mGroup3 = (RadioGroup) view.findViewById(R.id.group3);
        mGroup4 = (RadioGroup) view.findViewById(R.id.group4);
        mThemes = new int[]{R.style.Theme1, R.style.Theme2, R.style.Theme3
                , R.style.Theme4, R.style.Theme5, R.style.Theme6
                , R.style.Theme7, R.style.Theme8, R.style.Theme9
                , R.style.Theme10, R.style.Theme11, R.style.Theme12
                , R.style.Theme13, R.style.Theme14, R.style.Theme15
                , R.style.Theme16, R.style.Theme17, R.style.Theme18
                , R.style.Theme19};
        mRdoBtns = new int[]{R.id.rdobtn_1, R.id.rdobtn_2, R.id.rdobtn_3, R.id.rdobtn_4, R.id.rdobtn_5
                , R.id.rdobtn_6, R.id.rdobtn_7, R.id.rdobtn_8, R.id.rdobtn_9, R.id.rdobtn_10
                , R.id.rdobtn_11, R.id.rdobtn_12, R.id.rdobtn_13, R.id.rdobtn_14, R.id.rdobtn_15
                , R.id.rdobtn_16, R.id.rdobtn_17, R.id.rdobtn_18, R.id.rdobtn_19};
        for (int i = 0; i < mThemes.length; i++) {
            if (mTheme == mThemes[i]) {
                RadioButton radioButton = (RadioButton) view.findViewById(mRdoBtns[i]);
                radioButton.setChecked(true);
                break;
            }
        }
        mGroup1.setOnCheckedChangeListener(mCheckedChangeListener);
        mGroup2.setOnCheckedChangeListener(mCheckedChangeListener);
        mGroup3.setOnCheckedChangeListener(mCheckedChangeListener);
        mGroup4.setOnCheckedChangeListener(mCheckedChangeListener);
        builder.setView(view).setTitle("主题更换").setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mContentView = LayoutInflater.from(getContext()).inflate(
                R.layout.preference_theme_change, container, false);

        return mContentView;
    }
}

