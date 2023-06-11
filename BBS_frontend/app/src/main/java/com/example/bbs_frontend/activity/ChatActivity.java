package com.example.bbs_frontend.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.bbs_frontend.R;
import com.example.bbs_frontend.entity.chat.Message;
import com.example.bbs_frontend.entity.chat.User;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.message_index;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 聊天的具体界面
 */
public class ChatActivity extends BaseActivity {
    private MessagesListAdapter messagesAdapter;
    private MessagesList messagesList;
    private ImageLoader imageLoader;
    private String id_;
    private Handler handler;
    private List<message_index> post_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageLoader = (imageView, url, payload) -> {
            Picasso.get().load(url).placeholder(R.drawable.ic_avatarholder).into(imageView);
        };

        Log.d("TAG", "聊天界面");
        id_ = getIntent().getStringExtra("user_id");
        messagesList = findViewById(R.id.messagesList);
        messagesAdapter = new MessagesListAdapter<>("0", imageLoader);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder()
                            .add("user_id", Global.user_id)
                            .add("id",id_);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.SERVER_URL + "/message/detail/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    post_list = gson.fromJson(responseData,new TypeToken<List<message_index>>(){}.getType());
                    android.os.Message msg = new android.os.Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        handler = new Handler(){ //创建Handler
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what){ //区分不同的消息，对不同进度条组件执行操作
                    case 1:
                        User user = new User("1");
                        TextView name = findViewById(R.id.name);
                        name.setText(post_list.get(0).sender);
                        ArrayList msgs = new ArrayList<>();
                        for(int i =0;i<post_list.size();i++){
                            Calendar tmp_cal = Calendar.getInstance();
                            tmp_cal.set(post_list.get(i).year,post_list.get(i).mon-1,post_list.get(i).day,
                                    post_list.get(i).hour,post_list.get(i).min);
                            Message tmp= new Message("1",user,post_list.get(i).msg,tmp_cal);
                            msgs.add(tmp);
                        }
                        messagesList.setAdapter(messagesAdapter);
                        messagesAdapter.addToEnd(msgs, false);
                        messagesAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };



    }



}
