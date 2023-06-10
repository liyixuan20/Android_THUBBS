package com.example.bbs_frontend.fragment.main;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.androidapp.R;
import com.example.androidapp.activity.ChangePasswordActivity;
import com.example.androidapp.activity.EditInfoActivity;
import com.example.androidapp.activity.InfoActivity;
import com.example.androidapp.activity.user_list;
import com.example.androidapp.adapter.HomepagePagerAdapter;
import com.example.androidapp.util.Global;
import com.example.androidapp.util.HomeDetail;
import com.example.androidapp.util.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 主界面主页子页
 */
public class DashboardFragment
        extends Fragment {

    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.homepage_name)
    TextView name;
    @BindView(R.id.num_focus)
    TextView numFocus;
    @BindView(R.id.num_focused)
    TextView numFocused;
    @BindView(R.id.btn4)
    Button button;
    @BindView(R.id.btn3)
    Button button1;
    @BindView(R.id.left)
    LinearLayout left;
    @BindView(R.id.right)
    LinearLayout right;
    @BindView(R.id.btn1)
    Button btn_info;

    private HomepagePagerAdapter pagerAdapter;
    private HomeDetail info;
    private Handler handler;
    private DashboardFragment that = this;
    private int first =0;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, root);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditInfoActivity.class);
                startActivity(intent);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InfoActivity.class);
                startActivity(intent);
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
                        break;
                    default: break;
                }
            }
        };

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), user_list.class);
                intent.putExtra("type","关注");
                getContext().startActivity(intent);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), user_list.class);
                intent.putExtra("type","被关注");
                getContext().startActivity(intent);
            }
        });
        this.registerForContextMenu(imgAvatar);
        return root;
    }

    @Override
    public void onStart() {
        super.onDestroy();
        super.onCreate(null);
        super.onStart();
        show();
    }

    public void show(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder()
                            .add("id",Global.user_id);
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
                    Log.d("1", "进入线程");
                    Log.d("TAG", "返回内容"+responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_changeavatar,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_avatar: //更改头像
                Tools.galleryPicture(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == PictureConfig.CHOOSE_REQUEST){
                LocalMedia newAvatar =  PictureSelector.obtainMultipleResult(data).get(0);
                try {
                    String path = newAvatar.getAndroidQToPath();
                    Glide.with(this).load(path).into(imgAvatar);
                    new UploadThread(path).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public MultipartBody uploadAvatar(String path){
        MultipartBody body = null;
        Log.e("path",path);
        if(path !=null){
            File file = new File(path);
            RequestBody requestfile = RequestBody.Companion.create(MediaType.parse("multipart/form-data"), file);
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id",Global.user_id)
                    .addFormDataPart("image", file.getName(),requestfile)
                    .build();
        }
        return body;
    }

    class UploadThread extends Thread{
        private String mAvatar;

        public UploadThread(String localMedia){mAvatar = localMedia;}

        @Override
        public void run(){
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Global.SERVER_URL + "/user/avatar/")
                    .post(uploadAvatar(mAvatar))
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String responseData = response.body().string();
                Log.e("responseData",responseData);
                show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}


