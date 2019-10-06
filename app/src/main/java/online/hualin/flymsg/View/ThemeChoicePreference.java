package online.hualin.flymsg.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.fragment.app.DialogFragment;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import online.hualin.flymsg.R;

public class ThemeChoicePreference extends DialogPreference implements  DialogPreference.OnPreferenceClickListener {


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

    @SuppressWarnings("deprecation")
    public ThemeChoicePreference(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mSp = PreferenceManager.getDefaultSharedPreferences(context);
        mTheme = mSp.getInt("theme_change", R.style.Theme7);
    }

    @SuppressWarnings("deprecation")
    public ThemeChoicePreference(final Context context) {
        super(context);
        mSp = PreferenceManager.getDefaultSharedPreferences(context);
        mTheme = mSp.getInt("theme_change", R.style.Theme7);
    }

    public ThemeChoicePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public ThemeChoicePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);
        mSp = PreferenceManager.getDefaultSharedPreferences(context);
        mTheme = mSp.getInt("theme_change", R.style.Theme7);
    }

//
//    @Override
//    protected void onSetInitialValue(Object defaultValue) {
//
//        // Set default state from the XML attribute
//        mCurrentValue = (Integer) defaultValue;
////        if (defaultValue==null)
////            defaultValue=
//        persistInt(mCurrentValue);
//        mNewValue = mCurrentValue;
//
//    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        mNewValue = mCurrentValue;
    }

    public void showSummary() {
//        setSummary(mCurrentValue);
    }

    public void setValue(int value){
        persistInt(value);
        showSummary();
        notifyChanged();
    }
    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }
}
