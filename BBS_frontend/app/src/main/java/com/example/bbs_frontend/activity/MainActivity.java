package com.example.bbs_frontend.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.bbs_frontend.R;
import com.example.bbs_frontend.adapter.MainActivityPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.ButterKnife;


/**
 * 登录后进入的主界面
 */
public class MainActivity extends BaseActivity {


    BottomNavigationView navView;
    private ViewPager viewPager;
    private MainActivityPagerAdapter mMainActivityPagerAdapter;
    /*
    private ChatHistoryViewModel chatHistoryViewModel;
    private Runnable mTimeCounterRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("消息列表轮询", "+1");
            refreshData();

            mHandler.postDelayed(this, 2 * 1000);
        }
    };*/

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        navView = findViewById(R.id.nav_view);

        navView.removeBadge(R.id.navigation_conversations);
        navView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_conversations:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return true;
        });


        viewPager = findViewById(R.id.nav_host_fragment);
        mMainActivityPagerAdapter = new MainActivityPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mMainActivityPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                        break;
                    case 1:
                        navView.getMenu().findItem(R.id.navigation_conversations).setChecked(true);
                        break;
                    default:
                        navView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        if(message!=null) {
            Log.v("1",message);
            switch (message) {
                case "chat":
                    viewPager.setCurrentItem(1);
                    navView.getMenu().findItem(R.id.navigation_conversations).setChecked(true);
                    break;
                case "info":
                    viewPager.setCurrentItem(2);
                    navView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
                    break;
                default:
                    viewPager.setCurrentItem(0);
                    navView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
