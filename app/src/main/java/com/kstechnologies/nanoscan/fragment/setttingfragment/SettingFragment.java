package com.kstechnologies.nanoscan.fragment.setttingfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.kstechnologies.nanoscan.CApplication;
import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.activity.ConfigureActivity;
import com.kstechnologies.nanoscan.activity.InfoActivity;
import com.kstechnologies.nanoscan.activity.mainactivity.MainActivity;
import com.kstechnologies.nanoscan.databinding.FragmentSettingBinding;
import com.kstechnologies.nanoscan.event.ActionGattDisconnectedEvent;
import com.kstechnologies.nanoscan.event.ActionNotifyDoneEvent;
import com.kstechnologies.nanoscan.fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * app系列设置入口页面
 * 同时该页面承担了连接到Nano和管理Nano信息的功能的入口
 *
 * @author crt106 on 2019/5/18
 */
public class SettingFragment extends BaseFragment {

    private MainActivity activityConnect;
    private FragmentSettingBinding binding;
    private SettingFragViewModel viewModel = new SettingFragViewModel();


    public SettingFragment() {

    }


    /**
     * 更多信息点击事件
     */
    private View.OnClickListener moreInfoClick = (v) -> {
        Intent intent = new Intent(activityConnect, InfoActivity.class);
        startActivity(intent);
    };

    /**
     * 切换语言点击
     */
    private View.OnClickListener languageClick = (v) -> {

    };


    /**
     * 连接/断开连接 按钮事件
     */
    private View.OnClickListener btnConnectClick = (v) -> {
        if (!viewModel.getConnected().get()) {
            activityConnect.startScan();
        } else {
            activityConnect.disconnect();
        }
    };

    /**
     * 查看设备状态按钮点击事件
     */
    private View.OnClickListener btnStatusClick = (v) -> {
        Intent intent = new Intent(activityConnect, ConfigureActivity.class);
        startActivity(intent);
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        binding.setViewmodel(viewModel);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        activityConnect = (MainActivity) getActivity();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //绑定点击事件
        binding.llMoreInfo.setOnClickListener(moreInfoClick);
        binding.llLanguage.setOnClickListener(languageClick);
        binding.btnConnectNano.setOnClickListener(btnConnectClick);
        binding.btnNanoStatus.setOnClickListener(btnStatusClick);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.connected.set(CApplication.connected);
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.connected.set(CApplication.connected);
    }


    //region EventBus控制

    /**
     * 接收所有Characteristic监听通知设置完毕时的事件处理
     * 触发此事件代表所有的连接和准备都完成了
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(ActionNotifyDoneEvent event) {
        viewModel.connected.set(CApplication.connected);
        Snackbar.make(binding.getRoot(), "Nano设备已准备就绪", Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 断开连接事件触发
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(ActionGattDisconnectedEvent event) {
        viewModel.connected.set(CApplication.connected);
        Snackbar.make(binding.getRoot(), "Nano已断开连接", Snackbar.LENGTH_SHORT).show();
    }
    //endregion


}
