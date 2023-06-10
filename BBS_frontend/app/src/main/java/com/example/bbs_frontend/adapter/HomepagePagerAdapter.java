package com.example.bbs_frontend.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.bbs_frontend.fragment.homepage.SelfInfoFragment;
import com.example.bbs_frontend.fragment.main.HomeFragment;
import com.example.bbs_frontend.util.HomeDetail;

import org.jetbrains.annotations.NotNull;


/**
 * 主页ViewPager适配器
 */
public class HomepagePagerAdapter extends FragmentStatePagerAdapter {


    int mNumOfTabs;
    HomeDetail info;
    String id;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public HomepagePagerAdapter(@NonNull FragmentManager fm, int NumOfTabs, HomeDetail info, String id) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.info = info;
        this.id = id;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment(id);

            default:
                return new SelfInfoFragment(info);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
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

    public void change_info(HomeDetail info){
        this.info = info;
    }
}
