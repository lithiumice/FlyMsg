package online.hualin.flymsg.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import online.hualin.flymsg.fragment.HistoryFrag;
import online.hualin.flymsg.fragment.HomeFrag;


public class MainContentAdapter extends FragmentPagerAdapter {
    public final int COUNT=2;
    private String[] tabTitiles = new String[]{"设备", "历史"};


    public MainContentAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFrag();

            default:
                return new HistoryFrag();

        }
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
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

