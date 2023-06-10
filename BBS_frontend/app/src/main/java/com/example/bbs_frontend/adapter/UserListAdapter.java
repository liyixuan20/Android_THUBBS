package com.example.bbs_frontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.example.bbs_frontend.activity.visit;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.visit_detail;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 主页ViewPager适配器
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    private List<visit_detail> data;
    private Context context;
    private View inflater;
    public Handler hander;
    /*构造函数*/
    public UserListAdapter(Context context, List<visit_detail> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder，绑定每一项的布局为item
        Log.d("TAG", "进入了人UserListAdapter");
        inflater= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        MyViewHolder holder = new MyViewHolder(inflater);
        holder.itemView.findViewById(R.id.avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView text1 = holder.itemView.findViewById(R.id.user_id);
                String userid = text1.getText().toString();
                showPopupMenu(holder.itemView.findViewById(R.id.avatar),userid,hander);
            }
        });
        return holder;
    }

    private void showPopupMenu(View view,String id,Handler hander) {
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
                                    Message msg = new Message();
                                    msg.what = 1;
                                    hander.sendMessage(msg);
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
                                    Message msg = new Message();
                                    msg.what = 2;
                                    hander.sendMessage(msg);
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
        TextView text = holder.itemView.findViewById(R.id.user_id);
        text.setText(data.get(position).user_id);
        //通过点击改变状态
    }

    @Override
    public int getItemCount() {
        //返回数据总条数
        return data.size();
    }

    //内部类，绑定控件
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView user_name,follow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            user_name = itemView.findViewById(R.id.user_name);
            follow = itemView.findViewById(R.id.follow);
        }
    }

    //自定义方法，用于绑定数据
    public void bindView(@NonNull MyViewHolder holder, int position){
        String imageUrl = data.get(position).ava;
        Glide.with(context).load(imageUrl).into(holder.avatar);
        holder.user_name.setText(data.get(position).name);
        holder.follow.setText(data.get(position).follow);
    }

}
