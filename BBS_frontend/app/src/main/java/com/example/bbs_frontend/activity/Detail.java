package com.example.bbs_frontend.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bbs_frontend.R;
import com.example.bbs_frontend.adapter.HomeAdapter;
import com.example.bbs_frontend.adapter.ImageAdapter;
import com.example.bbs_frontend.adapter.PostAdapter;
import com.example.bbs_frontend.adapter.UserListAdapter;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.PostDetail;
import com.example.bbs_frontend.util.PostDetailDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Detail extends AppCompatActivity {

    private String id;
    private TextView name,date,text,thumbs,reply,share,thumb_list;
    private ImageView good,comment;
    private CircleImageView ava;
    private String responseData;
    private Gson gson;
    private Message msg;
    private PostDetailDetail postInfo;
    private RecyclerView recycleView,pictureView;
    private PostAdapter postAdapter;
    private Handler handler;
    private Detail that = this;
    private ImageAdapter imageAdapter;
    private VideoView video;
    private MediaPlayer music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Log.d("TAG", "动态的id是"+id);
        setContentView(R.layout.activity_detail);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        text = findViewById(R.id.text);
        thumbs = findViewById(R.id.good_text);
        reply = findViewById(R.id.comment_text);
        ava = findViewById(R.id.imageButton);
        good = findViewById(R.id.good);
        comment = findViewById(R.id.comment);
        pictureView = findViewById(R.id.pic);
        recycleView = findViewById(R.id.com);
        thumb_list = findViewById(R.id.thumb_list);
        video = findViewById(R.id.video);
        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = text.getText().toString();
                String mimeType = "text/plain";
                ShareCompat.IntentBuilder
                        .from(that)
                        .setType(mimeType)
                        .setChooserTitle("分享")
                        .setText(txt)
                        .startChooser();
            }
        });
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("id", id)
                                    .add("type", "1")
                                    .add("user_id", Global.user_id)
                                    .add("reply_type", "2");
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/operator/edit/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            msg = new Message();
                            handler.sendMessage(msg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
                handler = new Handler(){ //创建Handler
                    @Override
                    public void handleMessage(Message msg) {
                        that.refresh();
                    }
                };
            }
        });
        thumbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("id", id)
                                    .add("type", "1")
                                    .add("user_id", Global.user_id)
                                    .add("reply_type", "2");
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/operator/edit/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            msg = new Message();
                            handler.sendMessage(msg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
                handler = new Handler(){ //创建Handler
                    @Override
                    public void handleMessage(Message msg) {
                        that.refresh();
                    }
                };
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputServer = new EditText(that);
                inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                AlertDialog.Builder builder = new AlertDialog.Builder(that);
                builder.setTitle("请输入回复内容").setIcon(android.R.drawable.ic_menu_add).setView(inputServer);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FormBody.Builder builder = new  FormBody.Builder()
                                            .add("reply_id", id)
                                            .add("content", inputServer.getText().toString())
                                            .add("author", Global.user_id);
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder()
                                            .url(Global.SERVER_URL + "/reply/post/")
                                            .post(builder.build())
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    msg = new Message();
                                    handler.sendMessage(msg);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        handler = new Handler(){ //创建Handler
                            @Override
                            public void handleMessage(Message msg) {
                                that.refresh();
                            }
                        };
                    }
                });
                builder.show();
            }
        });
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputServer = new EditText(that);
                inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                AlertDialog.Builder builder = new AlertDialog.Builder(that);
                builder.setTitle("请输入回复内容").setIcon(android.R.drawable.ic_menu_add).setView(inputServer);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FormBody.Builder builder = new  FormBody.Builder()
                                            .add("reply_id", id)
                                            .add("content", inputServer.getText().toString())
                                            .add("author", Global.user_id);
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder()
                                            .url(Global.SERVER_URL + "/reply/post/")
                                            .post(builder.build())
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    msg = new Message();
                                    handler.sendMessage(msg);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        handler = new Handler(){ //创建Handler
                            @Override
                            public void handleMessage(Message msg) {
                                that.refresh();
                            }
                        };
                    }
                });
                builder.show();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder().add("id", id).add("user_id", Global.user_id);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.SERVER_URL + "/post/detail/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    postInfo = gson.fromJson(responseData,new TypeToken<PostDetailDetail>(){}.getType());
                    name.setText(postInfo.sendname);
                    date.setText(postInfo.date);
                    text.setText(postInfo.text);
                    thumbs.setText(postInfo.likes);
                    thumb_list.setText(postInfo.thumb_list);
                    if (postInfo.like!=0)
                        thumbs.setTextColor(Color.RED);
                    else
                        thumbs.setTextColor(Color.BLACK);
                    msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        handler = new Handler(){ //创建Handler
            @Override
            public void handleMessage(Message msg) {
                Glide.with(that).load(postInfo.ava).into(ava);
                LinearLayoutManager manager = new LinearLayoutManager(that);
                manager.setOrientation(RecyclerView.VERTICAL);
                recycleView.setLayoutManager(manager);
                postAdapter = new PostAdapter(that,postInfo.response,that);
                recycleView.setAdapter(postAdapter);
                if (postInfo.type.equals("图文")) {
                    LinearLayoutManager manager1 = new LinearLayoutManager(that);
                    manager1.setOrientation(RecyclerView.HORIZONTAL);
                    pictureView.setLayoutManager(manager1);
                    imageAdapter = new ImageAdapter(that, postInfo.pic);
                    pictureView.setAdapter(imageAdapter);
                }
                else
                    pictureView.setVisibility(View.GONE);
                if (postInfo.type.equals("视频")) {
                    playVideo(postInfo.pic.get(0).substring(0,postInfo.pic.get(0).length() - 1));
                }
                else
                    video.setVisibility(View.GONE);
                if (postInfo.type.equals("音频")) {
                    playVideo(postInfo.pic.get(0).substring(0,postInfo.pic.get(0).length() - 1));
                }
                ava.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(ava,postInfo.author);
                    }
                });
            }
        };

    }
    public void playVideo(String url)
    {
        LinearLayout r = findViewById(R.id.videoLayout);
        r.setVisibility(View.VISIBLE);
        video.setKeepScreenOn(true);
        video.setVisibility(View.VISIBLE);
        video.setVideoURI(Uri.parse(url));
        MediaController mediacontroller = new MediaController(this);
        video.setMediaController(mediacontroller);
        video.start();
        video.requestFocus();
    }

    public void refresh()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder().add("id", id).add("user_id", Global.user_id);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.SERVER_URL + "/post/detail/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    postInfo = gson.fromJson(responseData,new TypeToken<PostDetailDetail>(){}.getType());
                    name.setText(postInfo.sendname);
                    date.setText(postInfo.date);
                    text.setText(postInfo.text);
                    thumbs.setText(postInfo.likes);
                    thumb_list.setText(postInfo.thumb_list);
                    if (postInfo.like!=0)
                        thumbs.setTextColor(Color.RED);
                    else
                        thumbs.setTextColor(Color.BLACK);
                    msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        handler = new Handler(){ //创建Handler
            @Override
            public void handleMessage(Message msg) {
                Glide.with(that).load(postInfo.ava).into(ava);
                LinearLayoutManager manager = new LinearLayoutManager(that);
                manager.setOrientation(RecyclerView.VERTICAL);
                recycleView.setLayoutManager(manager);
                postAdapter = new PostAdapter(that,postInfo.response,that);
                recycleView.setAdapter(postAdapter);
                if (postInfo.type.equals("图文")) {
                    LinearLayoutManager manager1 = new LinearLayoutManager(that);
                    manager1.setOrientation(RecyclerView.HORIZONTAL);
                    pictureView.setLayoutManager(manager1);
                    imageAdapter = new ImageAdapter(that, postInfo.pic);
                    pictureView.setAdapter(imageAdapter);
                }
                else
                    pictureView.setVisibility(View.GONE);
                ava.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(ava,postInfo.author);
                    }
                });
            }
        };
    }

    private void showPopupMenu(View view,String id) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.avatar, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.visit:
                        Intent intent = new Intent(view.getContext(), visit.class);
                        intent.putExtra("id",id);
                        view.getContext().startActivity(intent);
                        break;
                    case R.id.follow:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    FormBody.Builder builder = new  FormBody.Builder()
                                            .add("user_id", Global.user_id)
                                            .add("follow_id",id);
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder()
                                            .url(Global.SERVER_URL + "/user/follow/")
                                            .post(builder.build())
                                            .build();
                                    Response response = client.newCall(request).execute();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        Toast.makeText(view.getContext(), "操作成功", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.black:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    FormBody.Builder builder = new  FormBody.Builder()
                                            .add("user_id", Global.user_id)
                                            .add("black_id",id);
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder()
                                            .url(Global.SERVER_URL + "/user/blacklist/")
                                            .post(builder.build())
                                            .build();
                                    Response response = client.newCall(request).execute();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        Toast.makeText(view.getContext(), "拉黑成功", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });

        popupMenu.show();
    }
}