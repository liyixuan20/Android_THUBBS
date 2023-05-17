package com.example.bbs_frontend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.example.bbs_frontend.R;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.Valid;
import com.example.bbs_frontend.util.login_info;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity{
    @BindView(R.id.login_account)
    FormEditText accountEditText;

    @BindView(R.id.login_password)
    FormEditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // 添加验证
        accountEditText.addValidator(new Valid.AccountValidator());
        accountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                accountEditText.testValidity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
        Button btn = findViewById(R.id.login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String account = accountEditText.getText().toString();
                            String password = passwordEditText.getText().toString();
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("password", password)
                                    .add("username",account);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/user/loginfunc/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
                            Gson gson = new Gson();
                            String responseData = response.body().string();
                            login_info my_info = gson.fromJson(responseData,login_info.class);
                            if (my_info.type.equals("ok")) {
                                Global.user_id = my_info.message;
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Button info_btn = findViewById(R.id.info);
                                info_btn.setText(my_info.message);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }


    @OnClick(R.id.logon)
    public void onClickLogon() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
