package com.example.bbs_frontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bbs_frontend.R;
import com.example.bbs_frontend.activity.ChangePasswordActivity;
import com.example.bbs_frontend.activity.Detail;
import com.example.bbs_frontend.activity.EditInfoActivity;
import com.example.bbs_frontend.activity.MainActivity;
import com.example.bbs_frontend.activity.visit;
import com.example.bbs_frontend.fragment.homepage.SelfInfoFragment;
import com.example.bbs_frontend.fragment.main.HomeFragment;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.PostDetail;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * 主页ViewPager适配器
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    private List<PostDetail> data;
    private Context context;
    private View inflater;
    private HomeFragment p;
    private Handler handler;
    private Message msg;
    /*构造函数*/
    public HomeAdapter(Context context, List<PostDetail> data, HomeFragment parent) {
        this.context = context;
        this.data = data;
        this.p = parent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder，绑定每一项的布局为item
        inflater= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        MyViewHolder holder = new MyViewHolder(inflater);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Detail.class);
                TextView text_ = holder.itemView.findViewById(R.id.id);
                String id_ = text_.getText().toString();
                intent.putExtra("id",id_);
                view.getContext().startActivity(intent);
            }
        });

        holder.itemView.findViewById(R.id.good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TextView text_ = holder.itemView.findViewById(R.id.id);
                            String id_ = text_.getText().toString();
                            FormBody.Builder builder = new  FormBody.Builder()
                                    .add("id", id_)
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
                TextView text1 = holder.itemView.findViewById(R.id.user_id);
                String userid = text1.getText().toString();
                showPopupMenu(holder.itemView.findViewById(R.id.avatar),userid);
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
        TextView text = holder.itemView.findViewById(R.id.user_id);
        text.setText(data.get(position).user_id);
        TextView text1 = holder.itemView.findViewById(R.id.id);
        text1.setText(data.get(position).id);

        //通过点击改变状态
    }

    @Override
    public int getItemCount() {
        //返回数据总条数
        return data.size();
    }

    //内部类，绑定控件
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image,avatar;
        TextView content,user_name,date,title,follow,thumbs,location,type;
        String post_id;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            avatar = itemView.findViewById(R.id.avatar);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            user_name = itemView.findViewById(R.id.user_name);
            date = itemView.findViewById(R.id.time);
            thumbs = itemView.findViewById(R.id.thumbs);
            follow = itemView.findViewById(R.id.follow);
            type = itemView.findViewById(R.id.type);
            location = itemView.findViewById(R.id.location);
        }
    }

    //自定义方法，用于绑定数据
    public void bindView(@NonNull MyViewHolder holder, int position){
        String imageUrl = data.get(position).imagePath;
        if (data.get(position).type.equals("图文"))
            Glide.with(context).load(imageUrl).into(holder.image);
        else
            Glide.with(context).load("").into(holder.image);
        imageUrl = data.get(position).avatar;
        Glide.with(context).load(imageUrl).into(holder.avatar);
        holder.title.setText("<"+data.get(position).title+">");
        holder.content.setText(data.get(position).content);
        holder.user_name.setText(data.get(position).sender);
        holder.date.setText(data.get(position).time);
        holder.thumbs.setText(data.get(position).thumbs);
        holder.type.setText(data.get(position).type);
        holder.location.setText(data.get(position).location);
        holder.post_id = data.get(position).id;
        if (data.get(position).thumb!=0)
            holder.thumbs.setTextColor(Color.RED);
        else
            holder.thumbs.setTextColor(Color.BLACK);
        holder.follow.setText(data.get(position).follow);
    }

}
