package com.example.bbs_frontend.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.example.bbs_frontend.R;
import com.example.bbs_frontend.util.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 注册
 */
public class EditInfoActivity extends BaseActivity {
    /******************************
     ************ 变量 ************
     ******************************/

    /******************************
     ************ 方法 ************
     ******************************/
    @BindView(R.id.logon2_name)
    FormEditText nameEditText;
    private Handler handler;

    @BindView(R.id.logon2_school)
    FormEditText intro_;

    @BindView(R.id.logon)
    Button btn;

    private String name;
    private String intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        ButterKnife.bind(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            name = nameEditText.getText().toString();
                            intro = intro_.getText().toString();
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("id",Global.user_id)
                                    .add("name", name)
                                    .add("intro",intro);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/user/edit/")
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
