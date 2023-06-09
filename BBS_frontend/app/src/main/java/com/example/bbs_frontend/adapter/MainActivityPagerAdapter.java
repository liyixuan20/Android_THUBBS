package com.example.bbs_frontend.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.bbs_frontend.fragment.chat.ChatFragment;
import com.example.bbs_frontend.fragment.main.DashboardFragment;
import com.example.bbs_frontend.fragment.main.HomeFragment;

import org.jetbrains.annotations.NotNull;

/**
 * 主界面ViewPager适配器
 */
public class MainActivityPagerAdapter extends FragmentStatePagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public MainActivityPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment("all");
            case 1:
                return new ChatFragment();
            default:
                return new DashboardFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NotNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
