package com.example.bbs_frontend.fragment.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbs_frontend.R;
import com.example.bbs_frontend.activity.PublicActivity;
import com.example.bbs_frontend.adapter.HomeAdapter;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.PostDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private HomeAdapter homeAdapter;
    private Unbinder unbinder;

    @BindView(R.id.imageButton)
    ImageView imageview;

    private Spinner spinner;
    private String spinner_content;
    private List<PostDetail> post_list;
    private List<PostDetail> temp_list;
    private RecyclerView recycleView;
    private EditText editText;
    private String temp,t1,t2;
    private String responseData;
    private TextView text,all,attention,time,thumbs;
    private Gson gson;
    private String host = Global.SERVER_URL;
    private Switch attention_switch,order_switch;
    private HomeFragment that = this;
    private Handler handler;
    private Message msg;
    public String type;

    public HomeFragment(String type)
    {
        this.type = type;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);
        Initview();
        text = root.findViewById(R.id.selectText);
        all = root.findViewById(R.id.all);
        attention = root.findViewById(R.id.attention);
        time = root.findViewById(R.id.time);
        thumbs = root.findViewById(R.id.thumbs);
        recycleView = root.findViewById(R.id.recycleView);
        editText = root.findViewById(R.id.search_view);
        spinner = root.findViewById(R.id.orderSpinner);
        attention_switch = root.findViewById(R.id.switch_button);
        order_switch = root.findViewById(R.id.order_button);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recycleView.setLayoutManager(manager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FormBody.Builder builder = new  FormBody.Builder().add("user_id", Global.user_id).add("user_type",that.type);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(host + "/operator/search/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    post_list = gson.fromJson(responseData,new TypeToken<List<PostDetail>>(){}.getType());
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
                homeAdapter = new HomeAdapter(getActivity(),post_list,that);
                recycleView.setAdapter(homeAdapter);
            }
        };

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        attention_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    all.setTextColor(Color.BLACK);
                    attention.setTextColor(Color.GREEN);
                }
                else {
                    all.setTextColor(Color.GREEN);
                    attention.setTextColor(Color.BLACK);
                }
                text.callOnClick();
            }
        });

        order_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    time.setTextColor(Color.BLACK);
                    thumbs.setTextColor(Color.GREEN);
                }
                else {
                    time.setTextColor(Color.GREEN);
                    thumbs.setTextColor(Color.BLACK);
                }
                text.callOnClick();
            }
        });

        return root;
    }



    private void Initview(){
        this.registerForContextMenu(imageview);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_publishmenu,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //该方法对菜单的item进行监听
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1: //发布动态
                Intent intent = new Intent();
                intent.setClass(getActivity(), PublicActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void refresh()
    {
        temp = editText.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    spinner_content = spinner.getSelectedItem().toString();
                    if (attention_switch.isChecked())
                        t1 = "1";
                    else
                        t1 = "0";
                    if (order_switch.isChecked())
                        t2 = "1";
                    else
                        t2 = "0";
                    FormBody.Builder builder = new  FormBody.Builder()
                            .add("search", temp)
                            .add("type", spinner_content)
                            .add("user_id", Global.user_id)
                            .add("attention", t1)
                            .add("user_type",that.type)
                            .add("order", t2);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Global.SERVER_URL + "/operator/search/")
                            .post(builder.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    temp_list = gson.fromJson(responseData,new TypeToken<List<PostDetail>>(){}.getType());
                    msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                    text.setText(responseData);
                }
            }
        }).start();
        handler = new Handler(){ //创建Handler
            @Override
            public void handleMessage(Message msg) {
                post_list.clear();
                for (int i = 0;i<temp_list.size();i++)
                {
                    post_list.add(temp_list.get(i));
                }
                homeAdapter.notifyDataSetChanged();
            }
        };
    };

}
