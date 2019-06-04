package com.kstechnologies.nanoscan.activity.mainactivity;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.kstechnologies.nanoscan.CApplication;
import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.activity.BaseActivity;
import com.kstechnologies.nanoscan.databinding.ActivityMainBinding;
import com.kstechnologies.nanoscan.databinding.MainTabItemBinding;
import com.kstechnologies.nanoscan.event.ActionNotifyDoneEvent;
import com.kstechnologies.nanoscan.event.RefConfDataEvent;
import com.kstechnologies.nanoscan.event.ScanConfDataEvent;
import com.kstechnologies.nanoscan.fragment.HomeFragment;
import com.kstechnologies.nanoscan.fragment.ScanListFragment;
import com.kstechnologies.nanoscan.fragment.setttingfragment.SettingFragment;
import com.kstechnologies.nanoscan.service.NanoBLEService;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;
import com.kstechnologies.nirscannanolibrary.SettingsManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.kstechnologies.nanoscan.constant.Constant.DEVICE_NAME;

/**
 * 应用主Activity 同时承担与最shit的服务{@link com.kstechnologies.nanoscan.service.NanoBLEService}的连接
 *
 * @author crt106 on 2019/5/15
 */
@RuntimePermissions
public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;
    private SparseArray<Fragment> fragmentMap = new SparseArray<>();

    //region 服务和蓝牙配置

    private NanoBLEService mNanoBLEService;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    //endregion

    /**
     * 优先使用的设备名称
     * TODO 调整同时有多个设备的策略
     */
    private String preferredDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViewPager();
        setSupportActionBar(binding.includeToolbar.toolbar);
        EventBus.getDefault().register(this);

        /**
         * 绑定整个app中最糟心的Service
         * Bind to the service. This will start it, and call the start command function
         */
        Intent gattServiceIntent = new Intent(this, NanoBLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
    }

    /**
     * ViewPager初始化过程
     */
    private void initViewPager() {
        fragmentMap.append(0, new HomeFragment());
        fragmentMap.append(1, new ScanListFragment());
        fragmentMap.append(2, new SettingFragment());
        MainPageAdapter mainFragmentAdapter = new MainPageAdapter(getSupportFragmentManager());
        binding.viewpagerMain.setAdapter(mainFragmentAdapter);
        binding.tbMain.setupWithViewPager(binding.viewpagerMain);
        binding.tbMain.setTabMode(TabLayout.MODE_FIXED);
        binding.viewpagerMain.setCanScroll(false);

        //这里与ViewPager联动之后重新初始化一下tab 达到使用自定义tabItem的效果
        MainTabItemBinding itemBinding_home = MainTabItemBinding.inflate(LayoutInflater.from(this), binding.tbMain,
                false);
        itemBinding_home.setDrawable(getDrawable(R.drawable.ic_home_24dp));
        MainTabItemBinding itemBinding_list = MainTabItemBinding.inflate(LayoutInflater.from(this), binding.tbMain,
                false);
        itemBinding_list.setDrawable(getDrawable(R.drawable.ic_view_list_24dp));
        MainTabItemBinding itemBinding_setting = MainTabItemBinding.inflate(LayoutInflater.from(this), binding.tbMain
                , false);
        itemBinding_setting.setDrawable(getDrawable(R.drawable.ic_settings_24dp));

        binding.tbMain.getTabAt(0).setCustomView(itemBinding_home.getRoot());
        binding.tbMain.getTabAt(1).setCustomView(itemBinding_list.getRoot());
        binding.tbMain.getTabAt(2).setCustomView(itemBinding_setting.getRoot());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    /**
     * 主页面ViewPagerAdapter
     */
    private class MainPageAdapter extends FragmentPagerAdapter {

        public MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentMap.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentMap.get(position);
        }
    }


    /**
     * 与服务{@link NanoBLEService }的连接管理
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            //Get a reference to the service from the service connection
            mNanoBLEService = ((NanoBLEService.LocalBinder) service).getService();

            //initialize bluetooth, if BLE is not available, then finish
            if (!mNanoBLEService.initialize()) {
                showLongToast("未启用蓝牙");
                return;
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mNanoBLEService = null;
        }
    };

    /**
     * 虚假的开始扫描233 为了间接调用PermissionsDispatcher的方法
     */
    public void startScan() {
        MainActivityPermissionsDispatcher.dostartScanWithPermissionCheck(this);

    }

    /**
     * 真正的 触发开始扫描设备
     */
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void dostartScan() {

        showLoadingView();
        //初始化相关蓝牙组件
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (mBluetoothLeScanner == null) {
            showShortToast(getString(R.string.toast_ensure_ble));
            return;
        }
        mHandler = new Handler();
        //看有没有配置优先扫描的设备 调用不同的扫描方法
        //TODO 调整优先设备的策略
        if (SettingsManager.getStringPref(getBaseContext(), SettingsManager.SharedPreferencesKeys.preferredDevice,
                null) != null)
        {
            preferredDevice = SettingsManager.getStringPref(getBaseContext(),
                    SettingsManager.SharedPreferencesKeys.preferredDevice, null);
            scanPreferredLeDevice(true);
        } else {
            scanLeDevice(true);
        }
    }


    /**
     * Callback function for Bluetooth scanning. This function provides the instance of the
     * Bluetooth device {@link BluetoothDevice} that was found, it's rssi, and advertisement
     * data (scanRecord).
     * <p>
     * When a Bluetooth device with the advertised name matching the
     * string DEVICE_NAME {@link com.kstechnologies.nanoscan.constant.Constant#DEVICE_NAME} is found, a call is made
     * to connect
     * to the device. Also, the Bluetooth should stop scanning, even if
     * the {@link NanoBLEService#SCAN_PERIOD} has not expired
     */
    private final ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            if (name != null) {
                if (device.getName().equals(DEVICE_NAME)) {
                    mNanoBLEService.connect(device.getAddress());
                    scanLeDevice(false);
                }
            }
        }
    };

    /**
     * Callback function for preferred Nano scanning. This function provides the instance of the
     * Bluetooth device {@link BluetoothDevice} that was found, it's rssi, and advertisement
     * data (scanRecord).
     * <p>
     * When a Bluetooth device with the advertised name matching the
     * string DEVICE_NAME {@link com.kstechnologies.nanoscan.constant.Constant#DEVICE_NAME} is found, a call is made
     * to connect
     * to the device. Also, the Bluetooth should stop scanning, even if
     * the {@link NanoBLEService#SCAN_PERIOD} has not expired
     */
    private final ScanCallback mPreferredLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            if (name != null) {
                if (device.getName().equals(DEVICE_NAME)) {
                    if (device.getAddress().equals(preferredDevice)) {
                        mNanoBLEService.connect(device.getAddress());
                        scanPreferredLeDevice(false);
                    }
                }
            }
        }
    };

    /**
     * Scans for Bluetooth devices on the specified interval {@link NanoBLEService#SCAN_PERIOD}.
     * This function uses the handler {@link #mHandler} to delay call to stop
     * scanning until after the interval has expired. The start and stop functions take an
     * LeScanCallback parameter that specifies the callback function when a Bluetooth device
     * has been found {@link #mLeScanCallback}
     * 开始扫描BLE设备 并且在一段时间后未扫描到则停止扫描
     *
     * @param enable Tells the Bluetooth adapter {@link KSTNanoSDK#mBluetoothAdapter} if
     *               it should start or stop scanning
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                if (mBluetoothLeScanner != null) {
                    mBluetoothLeScanner.stopScan(mLeScanCallback);
                    if (!CApplication.connected) {
                        notConnectedDialog();
                    }
                }
            }, NanoBLEService.SCAN_PERIOD);

            if (mBluetoothLeScanner != null) {
                mBluetoothLeScanner.startScan(mLeScanCallback);
            } else {
                showShortToast(getString(R.string.toast_ensure_ble));
                return;
            }
        } else {
            mBluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    /**
     * Scans for preferred Nano devices on the specified interval {@link NanoBLEService#SCAN_PERIOD}.
     * This function uses the handler {@link #mHandler} to delay call to stop
     * scanning until after the interval has expired. The start and stop functions take an
     * LeScanCallback parameter that specifies the callback function when a Bluetooth device
     * has been found {@link #mPreferredLeScanCallback}
     *
     * @param enable Tells the Bluetooth adapter {@link KSTNanoSDK#mBluetoothAdapter} if
     *               it should start or stop scanning
     */
    private void scanPreferredLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                mBluetoothLeScanner.stopScan(mPreferredLeScanCallback);
                if (!CApplication.connected) {
                    scanLeDevice(true);
                }
            }, NanoBLEService.SCAN_PERIOD);
            mBluetoothLeScanner.startScan(mPreferredLeScanCallback);
        } else {
            mBluetoothLeScanner.stopScan(mPreferredLeScanCallback);
        }
    }

    /**
     * Dialog that tells the user that a Nano is not connected. The activity will finish when the
     * user selects ok
     * 展示未连接对话框
     */
    private void notConnectedDialog() {
        hideLoadingView();
        new MaterialAlertDialogBuilder(this, R.style.CommonDialog)
                .setTitle(getResources().getString(R.string.not_connected_title))
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.not_connected_message))
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    //region 动态权限处理


    //endregion

    //region EventBus事件处理

    /**
     * 接收扫描配置的事件处理
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(ScanConfDataEvent event) {
        byte[] smallArray = event.getScanData();
        byte[] addArray = new byte[smallArray.length * 3];
        byte[] largeArray = new byte[smallArray.length + addArray.length];

        System.arraycopy(smallArray, 0, largeArray, 0, smallArray.length);
        System.arraycopy(addArray, 0, largeArray, smallArray.length, addArray.length);

        Log.w("_JNI", "largeArray Size: " + largeArray.length);
        KSTNanoSDK.ScanConfiguration scanConf =
                KSTNanoSDK.KSTNanoSDK_dlpSpecScanReadConfiguration(event.getScanData());

        CApplication.activeConf = scanConf;
        SettingsManager.storeStringPref(getBaseContext(), SettingsManager.SharedPreferencesKeys.scanConfiguration,
                scanConf.getConfigName());
    }

    /**
     * 当参考数据接收完毕时的事件处理方法
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(RefConfDataEvent event) {
        byte[] refCoeff = event.getRefCoeff();
        byte[] refMatrix = event.getRefMatrix();
        ArrayList<KSTNanoSDK.ReferenceCalibration> refCal = new ArrayList<>();
        refCal.add(new KSTNanoSDK.ReferenceCalibration(refCoeff, refMatrix));
        KSTNanoSDK.ReferenceCalibration.writeRefCalFile(getBaseContext(), refCal);
    }


    /**
     * 接收所有Characteristic监听通知设置完毕时的事件处理
     * 触发此事件代表所有的连接和准备都完成了
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(ActionNotifyDoneEvent event) {
        hideLoadingView();
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(KSTNanoSDK.SET_TIME));
    }

    //endregion

}
