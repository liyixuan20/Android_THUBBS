package com.example.bbs_frontend.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.bbs_frontend.R;
import com.example.bbs_frontend.adapter.HomepagePagerAdapter;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.HomeDetail;
import com.example.bbs_frontend.util.visit_detail;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.MalformedURLException;
import java.net.URL;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class visit extends AppCompatActivity {

    private HomepagePagerAdapter pagerAdapter;
    private HomeDetail info;
    private visit_detail info1;
    private Handler handler;
    private visit that = this;
    public String the_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        Intent intent = getIntent();
        the_id = intent.getStringExtra("id");
        show();
        TextView title=findViewById(R.id.visit_homepage_title1);
        title.setText("Ta的主页");
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ImageView imgAvatar = findViewById(R.id.img_avatar);
        TextView name=findViewById(R.id.homepage_name);
        TextView numFocus=findViewById(R.id.num_focus);
        TextView numFocused=findViewById(R.id.num_focused);
        ViewPager viewPager=findViewById(R.id.view_pager);
        tabLayout.addTab(tabLayout.newTab().setText("Ta的动态"));
        tabLayout.addTab(tabLayout.newTab().setText("Ta的信息"));
        tabLayout.setBackgroundColor(Color.WHITE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                JCVideoPlayer.releaseAllVideos();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder()
                            .add("id",the_id).add("user_id",Global.user_id);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.SERVER_URL + "/user/get/detail/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    info1 = gson.fromJson(responseData,new TypeToken<visit_detail>(){}.getType());
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        TextView btn = findViewById(R.id.btn_follow);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("user_id", Global.user_id)
                                    .add("follow_id",the_id);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/user/follow/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            Message msg = new Message();
                            msg.what = 3;
                            handler.sendMessage(msg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


        handler = new Handler(){
            @Override
            public  void handleMessage(Message msg){
                switch (msg.what){
                    case 1:
                        try {
                            URL url = new URL(info.ava);
                            Glide.with(that).load(url).into(imgAvatar);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        numFocus.setText(info.follow);
                        numFocused.setText(info.followed);
                        name.setText(info.name);
                            pagerAdapter = new HomepagePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), info,the_id);
                            viewPager.setAdapter(pagerAdapter);
                        break;
                    case 2:
                        btn.setText(info1.follow);
                        break;
                    case 3:
                        if(btn.getText().toString().equals("已关注"))
                            btn.setText("未关注");
                        else{
                            btn.setText("已关注");
                        }
                    default: break;
                }
            }
        };


    }

    public void show(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder()
                            .add("id",the_id).add("user_id",Global.user_id);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.SERVER_URL + "/user/get/home/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    info = gson.fromJson(responseData,new TypeToken<HomeDetail>(){}.getType());
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


}