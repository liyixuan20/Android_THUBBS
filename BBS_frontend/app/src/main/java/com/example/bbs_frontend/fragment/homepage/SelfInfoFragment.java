package com.example.bbs_frontend.fragment.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.androidapp.R;
import com.example.androidapp.util.HomeDetail;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 展示信息界面1
 */
public class SelfInfoFragment extends Fragment {

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.account)
    TextView account;

    @BindView(R.id.introduction)
    TextView introduction;

    private Unbinder unbinder;
    private HomeDetail info;

    public SelfInfoFragment(HomeDetail info) { this.info = info;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_self_info, container, false);
        unbinder = ButterKnife.bind(this, view);
       // setInfo();
        account.setText(info.account);
        name.setText(info.name);
        introduction.setText(info.intro);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //if (getActivity() instanceof MainActivity) setInfo();
    }

    public void setInfo() {
        //Activity activity = getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

