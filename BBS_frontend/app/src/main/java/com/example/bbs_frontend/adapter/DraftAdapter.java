package com.example.bbs_frontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbs_frontend.R;
import com.example.bbs_frontend.activity.PublicActivity;
import com.example.bbs_frontend.util.DraftDetail;
import com.example.bbs_frontend.util.Global;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.MyViewHolder> {
    private List<DraftDetail> data;
    private Context context;
    private View inflater;
    private String host = Global.SERVER_URL;
    Handler handler;
    Runnable runnable;
    MyViewHolder holder;
    public DraftAdapter(Context context, List<DraftDetail> data) {
        this.context =  context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder，绑定每一项的布局为item
        inflater= LayoutInflater.from(context).inflate(R.layout.draft_item,parent,false);
        holder = new MyViewHolder(inflater);

        runnable = new Runnable(){
            public void run(){
                notifyDataSetChanged();
            }
        };
        handler = new Handler();


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        bindView(holder,position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DraftDetail tmp = data.get(holder.getAdapterPosition());
                deletedraft(holder.getAdapterPosition());
                Intent intent = new Intent(view.getContext(), PublicActivity.class);
                intent.putExtra("draftcontent", tmp.content);
                intent.putExtra("drafttitle", tmp.titie);
                view.getContext().startActivity(intent);

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = holder.getAdapterPosition();
                Log.e("num", String.valueOf(num));
                if(num != -1) {
                    deletedraft(num);
                }
            }
        });

    }

    public int getItemCount() {
        //返回数据总条数
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView content,title,delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.draft_title);
            content = itemView.findViewById(R.id.draft_content);
            delete = itemView.findViewById(R.id.draft_delete);
        }
    }

    public void bindView(@NonNull MyViewHolder holder, int position){
        holder.title.setText("<"+data.get(position).titie+">");
        holder.content.setText(data.get(position).content);
    }

    public void deletedraft(int position){
        Log.e("position", String.valueOf(position));
        DraftDetail tmp = data.get( position);

        String id = tmp.id;
        Log.e("deletedata",(tmp.id));
        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody build = new FormBody.Builder()
                        .add("id",id)
                        .add("user_id", Global.user_id)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(host + "/draft/delete/")
                        .post(build)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.e("success_delete",response.body().string());
                    data.remove(position);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(runnable);
            }
        }).start();
    }

}
