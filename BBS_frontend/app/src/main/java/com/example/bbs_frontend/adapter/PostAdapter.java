package com.example.bbs_frontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bbs_frontend.R;
import com.example.bbs_frontend.activity.Detail;
import com.example.bbs_frontend.activity.visit;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.ResponseDetail;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private List<ResponseDetail> data;
    private Context context;
    private View inflater;
    private Detail p;
    private Handler handler;
    private Message msg;
    /*构造函数*/
    public PostAdapter(Context context, List<ResponseDetail> data, Detail p) {
        this.context = context;
        this.data = data;
        this.p = p;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder，绑定每一项的布局为item
        inflater= LayoutInflater.from(context).inflate(R.layout.reply_item,parent,false);
        MyViewHolder holder = new MyViewHolder(inflater);

        holder.itemView.findViewById(R.id.good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("id", holder.reply_id)
                                    .add("type", "1")
                                    .add("user_id", Global.user_id)
                                    .add("reply_type", "4");
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(Global.SERVER_URL + "/operator/edit/")
                                    .post(builder.build())
                                    .build();
                            Response response = client.newCall(request).execute();
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
                        p.refresh();
                    }
                };
            }
        });


        holder.itemView.findViewById(R.id.avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.itemView.findViewById(R.id.avatar),holder.user_id);
            }
        });
        return holder;

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

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        bindView(holder,position);
    }

    @Override
    public int getItemCount() {
        //返回数据总条数
        return data.size();
    }

    //内部类，绑定控件
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView content,user_name,date,thumbs,delete;
        String reply_id,user_id;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            content = itemView.findViewById(R.id.content);
            user_name = itemView.findViewById(R.id.user_name);
            date = itemView.findViewById(R.id.time);
            thumbs = itemView.findViewById(R.id.thumbs);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    //自定义方法，用于绑定数据
    public void bindView(@NonNull MyViewHolder holder, int position){
        String imageUrl = data.get(position).ava;
        Glide.with(context).load(imageUrl).into(holder.avatar);
        holder.content.setText(data.get(position).msg);
        holder.user_name.setText(data.get(position).sendname);
        holder.date.setText(data.get(position).date);
        holder.thumbs.setText(data.get(position).likes);
        holder.reply_id = data.get(position).id;
        holder.user_id = data.get(position).user_id;
        if (data.get(position).like!=0)
            holder.thumbs.setTextColor(Color.RED);
        else
            holder.thumbs.setTextColor(Color.BLACK);

        if (data.get(position).user_id.equals(Global.user_id))
        {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                FormBody.Builder builder = new  FormBody.Builder()
                                        .add("id", holder.reply_id);
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url(Global.SERVER_URL + "/reply/delete/")
                                        .post(builder.build())
                                        .build();
                                Response response = client.newCall(request).execute();
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
                            p.refresh();
                        }
                    };
                }
            });
        }
    }
}
