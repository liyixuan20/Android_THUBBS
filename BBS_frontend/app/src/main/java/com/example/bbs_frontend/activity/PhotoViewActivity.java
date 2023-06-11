package com.example.bbs_frontend.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.bbs_frontend.R;
import com.example.bbs_frontend.adapter.MyImageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoViewActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.mTvImageCount)
    TextView mTvImageCount;
    //点击的下标
    private int currentPosition;
    private List<String> urlLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        if (null == urlLists) {
            urlLists = new ArrayList<>();
        }
        //获得点击的位置
        currentPosition = getIntent().getIntExtra("position", 0);
        //图片集合
        urlLists = getIntent().getStringArrayListExtra("list");
        MyImageAdapter adapter = new MyImageAdapter(this, urlLists);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(currentPosition);
        mTvImageCount.setText(currentPosition + 1 + "/" + urlLists.size());
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTvImageCount.setText(position + 1 + "/" + urlLists.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}



