package com.example.bbs_frontend.activity;

import static com.example.bbs_frontend.util.Tools.uriToFileApiQ;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.bbs_frontend.R;
import com.example.bbs_frontend.adapter.SelectPlotAdapter;
import com.example.bbs_frontend.util.Apis;
import com.example.bbs_frontend.util.DraftDetail;
import com.example.bbs_frontend.util.GlideEngine;
import com.example.bbs_frontend.util.Global;
import com.example.bbs_frontend.util.PostDetail;
import com.example.bbs_frontend.util.PostId;
import com.example.bbs_frontend.util.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PublicActivity extends AppCompatActivity {

    private static final String TAG = "PublicActivity";
    @BindView(R.id.edit_title)
    EditText edittitle;
    @BindView(R.id.edit_content)
    EditText editcontent;
    @BindView(R.id.rcv_img)
    RecyclerView recycler;
    @BindView(R.id.top_panel)
    Toolbar toolbar;
    @BindView(R.id.currentpos)
    TextView currentpos;

    @BindView(R.id.currentmusic)
    TextView currentmusic;

    @BindView(R.id.currentvideo)
    TextView currentvideo;
    private String filename;
    private String id;
    private Retrofit mRetrofit;
    private List<String> UploadResult = new ArrayList<>() ;
    private String type = "文字";
    private SelectPlotAdapter adapter;
    private ArrayList<String> allSelectList;//所有的图片集合
    private ArrayList<String> categoryLists;//查看图片集合
    private List<LocalMedia> selectList = new ArrayList<>();

    private List<String> filepathList = new ArrayList<>();
    private File file = null;
    private String currentloc = "";
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = null;
    public AMapLocationClientOption mLocationOption = null;

    public Handler timehandler;
    public Runnable timeRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        edittitle.setText(intent.getStringExtra("drafttitle"));
        editcontent.setText(intent.getStringExtra("draftcontent"));
        mRetrofit = new Retrofit.Builder().baseUrl(Global.SERVER_URL).build();
        if (null == allSelectList) {
            allSelectList = new ArrayList();
        }
        if (null == categoryLists) {
            categoryLists = new ArrayList();
        }
        Tools.requestPermissions(PublicActivity.this);
        try {
            initmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        timehandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                timehandler.postDelayed(this, 5000);
                editDraft();
            }
        };
        timehandler.postDelayed(timeRunnable,5000);
        initAdapter();
    }


    public void deleteDraft(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody build = new FormBody.Builder()
                        .add("id",id)
                        .add("user_id", Global.user_id)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.SERVER_URL + "/draft/delete/")
                        .post(build)
                        .build();
                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    Log.e("success_delete",response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void editDraft(){
        String title = edittitle.getText().toString();
        String content = editcontent.getText().toString();
        if(!TextUtils.isEmpty(title)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(id == null){
                            FormBody body = new FormBody.Builder()
                                    .add("user_id",Global.user_id)
                                    .add("title",title)
                                    .add("content",content)
                                    .build();
                            Response<ResponseBody> response = mRetrofit.create(Apis.class)
                                    .uploadDraft(body)
                                    .execute();
                            if(response.isSuccessful()){
                                List<DraftDetail> post_list = new Gson().
                                        fromJson(response.body().string(),new TypeToken<List<DraftDetail>>(){}.getType());
                                id = post_list.get(0).id;
                                Log.v("response",id);
                                Looper.prepare();
                                Toast.makeText(PublicActivity.this, "已自动保存", Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        }
                        else{
                            FormBody body = new FormBody.Builder()
                                    .add("id",id)
                                    .add("user_id",Global.user_id)
                                    .add("title",title)
                                    .add("content",content)
                                    .build();
                            Response<ResponseBody> response = mRetrofit.create(Apis.class)
                                    .editDraft(body)
                                    .execute();
                            if(response.isSuccessful()){
                                Log.v("response",response.body().string());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    private void initmap() throws Exception {
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
        mLocationListener = aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) {
                currentloc = aMapLocation.getAddress();
                //可在其中解析amapLocation获取相应内容。
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        };
        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setNeedAddress(true);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    private void initAdapter() {
        //最多9张图片
        adapter = new SelectPlotAdapter(this, 9);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        adapter.setImageList(allSelectList);
        recycler.setAdapter(adapter);
        adapter.setListener(new SelectPlotAdapter.CallbackListener() {
            @Override
            public void add() {
                //可添加的最大张数=9-当前已选的张数
                type = "图文";
                currentmusic.setText("");
                currentvideo.setText("");
                filepathList.clear();
                int size = 9 - allSelectList.size();
                Tools.galleryPictures(PublicActivity.this, size);
            }

            @Override
            public void delete(int position) {
                allSelectList.remove(position);
                categoryLists.remove(position);
                adapter.setImageList(allSelectList);//再set所有集合
            }

            @Override
            public void item(int position, List<String> mList) {
                selectList.clear();
                for (int i = 0; i < allSelectList.size(); i++) {
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setPath(allSelectList.get(i));
                    selectList.add(localMedia);
                }
                //①、图片选择器自带预览
                PictureSelector.create(PublicActivity.this)
                        .themeStyle(R.style.picture_default_style)
                        .isNotPreviewDownload(true)//是否显示保存弹框
                        .imageEngine(GlideEngine.createGlideEngine()) // 选择器展示不出图片则添加
                        .openExternalPreview(position, selectList);
            }
        });
    }

    @OnClick(R.id.top_panel)
    public void onClickReturnTo() {
        String title = edittitle.getText().toString();
        Log.v("title", String.valueOf(TextUtils.isEmpty(title)));
        String content = editcontent.getText().toString();
        if(!TextUtils.isEmpty(title)){
            AlertDialog.Builder defaultBuilder = new AlertDialog.Builder(PublicActivity.this);
            defaultBuilder.setTitle("退出编辑");
            defaultBuilder.setMessage("是否保存当前文字内容保存到草稿箱");
            defaultBuilder.setNegativeButton("否",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(id != null){
                        deleteDraft();
                    }
                    Intent intent = new Intent(PublicActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            defaultBuilder.setPositiveButton("是",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editDraft();
                    Intent intent = new Intent(PublicActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog alertDialog = defaultBuilder.create();
            alertDialog.show();
        }
        else{
            Intent intent = new Intent(PublicActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.v("1", requestCode + "");
            switch (requestCode) {
                case 2:
                    filepathList.clear();
                    allSelectList.clear();
                    adapter.setImageList(allSelectList);
                    Uri uri = data.getData();
                    if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                        return;
                    }
                    file = uriToFileApiQ(uri, this);
                    filepathList.add(file.getPath());
                    filename = file.getName();

                    Log.v("name",file.getName());
                    if(type == "音频"){
                        currentmusic.setText(filename);
                        currentvideo.setText("");
                    }else if (type == "视频") {
                        currentmusic.setText("");
                        currentvideo.setText(filename);
                    }
                    break;
                case PictureConfig.CHOOSE_REQUEST:
                    // 结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    showSelectPic(selectList);
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showSelectPic(List<LocalMedia> result) {
        for (int i = 0; i < result.size(); i++) {
            String path;
            //判断是否10.0以上
            if (Build.VERSION.SDK_INT >= 29) {
                path = result.get(i).getAndroidQToPath();
            } else {
                path = result.get(i).getPath();
            }
            allSelectList.add(path);
            filepathList.add(path);
            categoryLists.add(path);
            Log.e(TAG, "图片链接: " + path);
        }
        adapter.setImageList(allSelectList);
    }

    @OnClick({R.id.menu_button, R.id.addmusic, R.id.addpos, R.id.addvideo,R.id.draft_button})
    public void onClick(View view) {
        String title = edittitle.getText().toString();
        String content = editcontent.getText().toString();
        switch (view.getId()) {
            case R.id.draft_button:
                Log.v("title", String.valueOf(TextUtils.isEmpty(title)));
                if(!TextUtils.isEmpty(title)){
                    AlertDialog.Builder defaultBuilder = new AlertDialog.Builder(PublicActivity.this);
                    defaultBuilder.setTitle("退出编辑");
                    defaultBuilder.setMessage("是否保存当前文字内容保存到草稿箱");
                    defaultBuilder.setNegativeButton("否",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(id != null){
                                deleteDraft();
                            }
                            Intent intent1 = new Intent(PublicActivity.this, DraftActivity.class);
                            startActivity(intent1);
                        }
                    });
                    defaultBuilder.setPositiveButton("是",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editDraft();
                            Intent intent1 = new Intent(PublicActivity.this, DraftActivity.class);
                            startActivity(intent1);
                        }
                    });
                    AlertDialog alertDialog = defaultBuilder.create();
                    alertDialog.show();
                }
                else{
                    Intent intent1 = new Intent(PublicActivity.this, DraftActivity.class);
                    startActivity(intent1);}
                break;
            case R.id.menu_button:
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(this, "请输入内容标题", Toast.LENGTH_LONG).show();
                    return;
                }
                new UploadThread(filepathList).start();
                if(id != null){
                    deleteDraft();
                }
                Log.e(TAG, "标题: " + title);
                Log.e(TAG, "内容: " + content);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.addmusic:
                type = "音频";
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.setType("audio/*");
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent2, 2);
                break;
            case R.id.addpos:
                currentpos.setText(currentloc);
                Toast.makeText(this, currentloc, Toast.LENGTH_LONG).show();
                break;
            case R.id.addvideo:
                type = "视频";
                Intent intent3 = new Intent(Intent.ACTION_GET_CONTENT);
                intent3.setType("video/*");
                intent3.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent3, 2);
                Toast.makeText(this, "2", Toast.LENGTH_LONG).show();
                break;
        }

    }



    private MultipartBody convertToRequestBody(String path,String post_id) {
        MultipartBody body;
        if(path != null) {
            File file = new File(path);
            Log.e("path", file.getPath());
            RequestBody requestFile = RequestBody.Companion.create(MediaType.parse("multipart/form-data"), file);

            body =
                    new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("post_id", post_id)
                            .addFormDataPart("user_id", Global.user_id)
                            .addFormDataPart("title", edittitle.getText().toString())
                            .addFormDataPart("content", editcontent.getText().toString())
                            .addFormDataPart("image", file.getName(), requestFile)
                            .addFormDataPart("location",currentloc)
                            .addFormDataPart("type",type)
                            .build();
        }
        else{
            RequestBody requestBody = RequestBody.create(null,"null");
            body =
                    new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("post_id", post_id)
                            .addFormDataPart("user_id", Global.user_id)
                            .addFormDataPart("title", edittitle.getText().toString())
                            .addFormDataPart("content", editcontent.getText().toString())
                            .addFormDataPart("file","null",requestBody)
                            .addFormDataPart("location",currentloc)
                            .addFormDataPart("type",type)
                            .build();
        }
        return body;
    }



    class UploadThread extends Thread {
        private List<String> mLocalMedia;
        private int mIndex = 0;
        private String mpost_id = "init";
        Response<ResponseBody> response;
        public UploadThread(List<String> localMedia) {
            mLocalMedia = localMedia;
        }

        @Override
        public void run() {
            try {
                if(mLocalMedia.isEmpty()){
                    response = mRetrofit.create(Apis.class)
                            .upload(convertToRequestBody(null, mpost_id))
                            .execute();
                }
                else{
                    response = mRetrofit.create(Apis.class)
                            .upload(convertToRequestBody(mLocalMedia.get(mIndex), mpost_id))
                            .execute();
                }
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        PostId post =gson.fromJson(response.body().string(),new TypeToken<PostId>(){}.getType());
                        String post_id = post.id;
                        UploadResult.add(post_id);
                        Log.v("response1", UploadResult.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (mIndex = 1; mIndex < mLocalMedia.size(); mIndex++) {
                mpost_id = UploadResult.get(0);
                Log.e("response2", mpost_id);
                try {
                    response = mRetrofit.create(Apis.class)
                            .upload(convertToRequestBody(mLocalMedia.get(mIndex), mpost_id))
                            .execute();
                    if (response.isSuccessful()) {
                        try {
                            response.body().string();
                            Log.v("response1", UploadResult.get(0));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
