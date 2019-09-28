package online.hualin.flymsg.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import online.hualin.flymsg.fragment.HistoryFrag;
import online.hualin.flymsg.fragment.HomeFrag;
import online.hualin.flymsg.fragment.lottie_frag;


public class MainContentAdapter extends FragmentPagerAdapter {
    public final int COUNT = 3;
    private Context context;
    private String[] tabTitiles = new String[]{"设备", "历史", "文件"};
    private FragmentManager fragmentManager;

    private HomeFrag homeFrag;

    public MainContentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFrag();

            case 1:
                return new HistoryFrag();

            default:
                return new lottie_frag();
        }
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
    }

    public HomeFrag getHomeFrag() {
        return homeFrag;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitiles[position];
    }
}

