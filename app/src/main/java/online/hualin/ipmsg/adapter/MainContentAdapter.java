package online.hualin.ipmsg.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import online.hualin.ipmsg.fragment.HomeFrag;
import online.hualin.ipmsg.fragment.lottie_frag;


public class MainContentAdapter extends FragmentPagerAdapter {
    public final int COUNT = 3;
    private Context context;
    private String[] tabTitiles = new String[]{"设备", "历史", "文件"};
    private FragmentManager fragmentManager;

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

            default:
                return new lottie_frag();
        }
//        return new HomeFrag();
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

