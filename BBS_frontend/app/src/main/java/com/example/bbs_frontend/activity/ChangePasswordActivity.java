package com.example.bbs_frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.andreabaccega.widget.FormEditText;
import com.bumptech.glide.Glide;
import com.example.bbs_frontend.R;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.Valid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity {


    @BindView(R.id.logon1_password)
    FormEditText passwordEditText;

    @BindView(R.id.logon)
    Button btn;
    private Handler handler;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        // 添加验证
        passwordEditText.addValidator(new Valid.PasswordValidator());
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.testValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            password = passwordEditText.getText().toString();
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("password", password)
                                    .add("id",Global.user_id);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/user/change_password/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        handler = new Handler(){ //创建Handler
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){ //区分不同的消息，对不同进度条组件执行操作
                    case 1:
                        btn.setText("修改成功");
                    default:
                        break;
                }
            }
        };
    }
}
