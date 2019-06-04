package com.kstechnologies.nanoscan.activity.newscanactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.kstechnologies.nanoscan.CApplication;
import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.activity.ActiveScanActivity;
import com.kstechnologies.nanoscan.activity.BaseActivity;
import com.kstechnologies.nanoscan.activity.ConfigureActivity;
import com.kstechnologies.nanoscan.databinding.ActivityNewScanBinding;
import com.kstechnologies.nanoscan.event.ActionGattDisconnectedEvent;
import com.kstechnologies.nanoscan.event.ActionScanStartedEvent;
import com.kstechnologies.nanoscan.event.ScanDataEvent;
import com.kstechnologies.nanoscan.model.DataFile;
import com.kstechnologies.nanoscan.model.MeasureDictionary;
import com.kstechnologies.nanoscan.model.MeasurePoint;
import com.kstechnologies.nanoscan.service.NanoBLEService;
import com.kstechnologies.nanoscan.utils.FileUtil;
import com.kstechnologies.nanoscan.utils.MPAndroidChartUtil;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;
import com.kstechnologies.nirscannanolibrary.SettingsManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.kstechnologies.nanoscan.CApplication.activeConf;
import static com.kstechnologies.nanoscan.CApplication.connected;

/**
 * 进行新扫描的Activity 全面用EventBus替换本地广播
 * Activity controlling the Nano once it is connected
 * This activity allows a user to initiate a scan, as well as access other "connection-only"
 * settings. When first launched, the app will scan for a preferred device
 * for {@link NanoBLEService#SCAN_PERIOD}, if it is not found, then it will start another "open"
 * scan for any Nano.
 * <p>
 * If a preferred Nano has not been set, it will start a single scan. If at the end of scanning, a
 * Nano has not been found, a message will be presented to the user indicating and error, and the
 * activity will finish
 * <p>
 * WARNING: This activity uses JNI function calls for communicating with the Spectrum C library, It
 * is important that the name and file structure of this activity remain unchanged, or the functions
 * will NOT work
 *
 * @author collinmast, crt106
 */
public class NewScanActivity extends BaseActivity {

    private static final String TAG = "NewScanActivity";
    private Context mContext;
    ActivityNewScanBinding binding;
    NewScanActivityViewModel viewModel;

    /**
     * 本次扫描保存的dataFile
     */
    private DataFile dataFile;

    /**
     * 本次扫描的测量点
     */
    private ArrayList<MeasurePoint> measurePoints;

    //三个存储画图数据的数组

    private ArrayList<Entry> mIntensityFloat;
    private ArrayList<Entry> mAbsorbanceFloat;
    private ArrayList<Entry> mReflectanceFloat;


    /**
     * 扫描结果
     */
    private KSTNanoSDK.ScanResults results;


    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        EventBus.getDefault().register(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_scan);
        setSupportActionBar(binding.includeToolbar.toolbar);
        initViewModel();

        binding.llConf.setClickable(true);
        binding.llConf.setOnClickListener(view -> {
            if (activeConf != null) {
                Intent activeConfIntent = new Intent(mContext, ActiveScanActivity.class);
                activeConfIntent.putExtra("conf", activeConf);
                startActivity(activeConfIntent);
            }
        });

        //Set up action bar enable tab navigation
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.new_scan));
            binding.viewpager.setOffscreenPageLimit(2);
        }

        //设置点击事件们

        binding.btnSaveSD.setOnCheckedChangeListener((compoundButton, b) -> viewModel.save2SDCard.set(b));

        binding.btnScan.setOnClickListener(view -> {
            if (connected) {
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(KSTNanoSDK.START_SCAN));
                viewModel.scanBtnText.set(getString(R.string.scanning));
            } else {
                showShortToast("请先连接到Nano");
            }
        });

        measurePoints = new ArrayList<>();
        mIntensityFloat = new ArrayList<>();
        mAbsorbanceFloat = new ArrayList<>();
        mReflectanceFloat = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();
        //Initialize view pager
        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(this);
        binding.viewpager.setAdapter(pagerAdapter);
        binding.viewpager.invalidate();
        binding.tbNewScanCharts.setupWithViewPager(binding.viewpager);
    }

    /**
     * When the activity is destroyed, unregister all broadcast receivers, remove handler callbacks,
     * and store all user preferences
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        /**
         * 保存SharedPreference 这些保存项都是在{@link SettingsManager}中已有的
         * 那还是勉为其难的用吧
         */
        SettingsManager.storeBooleanPref(mContext, SettingsManager.SharedPreferencesKeys.saveOS,
                viewModel.save2Phone.get());
        SettingsManager.storeBooleanPref(mContext, SettingsManager.SharedPreferencesKeys.saveSD,
                viewModel.save2SDCard.get());
        SettingsManager.storeBooleanPref(mContext, SettingsManager.SharedPreferencesKeys.continuousScan,
                viewModel.continueScan.get());
        SettingsManager.storeStringPref(mContext, SettingsManager.SharedPreferencesKeys.prefix,
                viewModel.fileNamePrefix.get());
    }

    /**
     * Inflate the options menu so that user actions are present
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_scan, menu);
        mMenu = menu;
        mMenu.findItem(R.id.action_settings).setEnabled(false);
        return true;
    }

    /**
     * Handle the selection of a menu item.
     * In this case, the user has the ability to access settings while the Nano is connected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent configureIntent = new Intent(mContext, ConfigureActivity.class);
            startActivity(configureIntent);
        }

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化viewModel
     */
    private void initViewModel() {
        viewModel = new NewScanActivityViewModel();
        viewModel.save2Phone.set(SettingsManager.getBooleanPref(mContext, SettingsManager.SharedPreferencesKeys.saveOS,
                false));
        viewModel.save2SDCard.set(SettingsManager.getBooleanPref(mContext, SettingsManager.SharedPreferencesKeys.saveSD,
                false));
        viewModel.continueScan.set(SettingsManager.getBooleanPref(mContext,
                SettingsManager.SharedPreferencesKeys.continuousScan, false));
        viewModel.scanConfig.set(SettingsManager.getStringPref(mContext,
                SettingsManager.SharedPreferencesKeys.scanConfiguration, "Column 1"));
        viewModel.scanBtnText.set(getString(R.string.scan));
        binding.setVm(viewModel);
    }

    /**
     * @see com.kstechnologies.nanoscan.activity.graphactivity.GraphActivity.CustomPagerEnum
     * Pager enum to control tab tile and layout resource
     */
    public enum CustomPagerEnum {

        /**
         * 反射率图表页面
         */
        REFLECTANCE(R.string.reflectance, R.layout.page_graph_reflectance),
        /**
         * 吸收率图表页面
         */
        ABSORBANCE(R.string.absorbance, R.layout.page_graph_absorbance),
        /**
         * 强度图表页面
         */
        INTENSITY(R.string.intensity, R.layout.page_graph_intensity);

        private int mTitleResId;
        private int mLayoutResId;

        CustomPagerEnum(int titleResId, int layoutResId) {
            mTitleResId = titleResId;
            mLayoutResId = layoutResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }
    }

    /**
     * 这个Adapter和{@link com.kstechnologies.nanoscan.activity.graphactivity.GraphActivity.CustomPagerAdapter}一模一样
     * 我真的不知道原来的人怎么这么心大一个300多行的牛皮癣方法还能复制粘贴
     * Custom pager adapter to handle changing chart data when pager tabs are changed
     */
    public class CustomPagerAdapter extends PagerAdapter {

        private final Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), collection, false);
            collection.addView(layout);
            String dataFileName;
            try {
                dataFileName = dataFile.getFileName();
                if (dataFileName == null || dataFileName.isEmpty()) {
                    dataFileName = "New Scan";
                }
            } catch (Exception e) {
                dataFileName = "New Scan";
            }

            if (customPagerEnum.getLayoutResId() == R.layout.page_graph_intensity) {
                LineChart mChart = (LineChart) layout.findViewById(R.id.lineChartInt);
                MPAndroidChartUtil.setLineChart(mChart, dataFileName, mIntensityFloat,
                        MPAndroidChartUtil.ChartType.INTENSITY);
                return layout;
            } else if (customPagerEnum.getLayoutResId() == R.layout.page_graph_absorbance) {
                LineChart mChart = (LineChart) layout.findViewById(R.id.lineChartAbs);
                MPAndroidChartUtil.setLineChart(mChart, dataFileName, mAbsorbanceFloat,
                        MPAndroidChartUtil.ChartType.ABSORBANCE);
                return layout;
            } else if (customPagerEnum.getLayoutResId() == R.layout.page_graph_reflectance) {
                LineChart mChart = (LineChart) layout.findViewById(R.id.lineChartRef);
                MPAndroidChartUtil.setLineChart(mChart, dataFileName, mReflectanceFloat,
                        MPAndroidChartUtil.ChartType.REFLECTANCE);
                return layout;
            } else {
                return layout;
            }
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return CustomPagerEnum.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.reflectance);
                case 1:
                    return getString(R.string.absorbance);
                case 2:
                    return getString(R.string.intensity);
                default:
            }
            return null;
        }

    }

     //region EventBus事件处理

    /**
     * 接收到事件传递的扫描数据的处理方法
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(ScanDataEvent event) {
        hideLoadingView();
        viewModel.scanBtnText.set(getString(R.string.scan));

        byte[] scanData = event.getScanData();
        String scanType = event.getScanType();

        /**
         * 7 bytes representing the current data
         * byte0: uint8_t     year; //< years since 2000
         * byte1: uint8_t     month; /**< months since January [0-11]
         * byte2: uint8_t     day; /**< day of the month [1-31]
         * byte3: uint8_t     day_of_week; /**< days since Sunday [0-6]
         * byte3: uint8_t     hour; /**< hours since midnight [0-23]
         * byte5: uint8_t     minute; //< minutes after the hour [0-59]
         * byte6: uint8_t     second; //< seconds after the minute [0-60]
         */
        String scanDate = event.getScanDate();

        KSTNanoSDK.ReferenceCalibration ref = KSTNanoSDK.ReferenceCalibration.currentCalibration.get(0);
        results = KSTNanoSDK.KSTNanoSDK_dlpSpecScanInterpReference(scanData, ref.getRefCalCoefficients(),
                ref.getRefCalMatrix());

        measurePoints.clear();
        mIntensityFloat.clear();
        mAbsorbanceFloat.clear();
        mReflectanceFloat.clear();

        double[] wavelengthA = results.getWavelength();
        int[] intensityA = results.getIntensity();
        int[] uncalibratedIntensityA = results.getUncalibratedIntensity();

        for (int index = 0; index < results.getLength(); index++) {
            //计算一项result中的三个值
            float intensity = (float) uncalibratedIntensityA[index];
            float absorbance =
                    (-1) * (float) Math.log10((double) uncalibratedIntensityA[index] / (double) intensityA[index]);
            float reflectance = (float) uncalibratedIntensityA[index] / intensityA[index];
            float waveLength = (float) wavelengthA[index];

            MeasurePoint m = new MeasurePoint(waveLength, intensity, absorbance, reflectance);
            measurePoints.add(m);
            mIntensityFloat.add(new Entry(m.getWavelength(), m.getIntensity()));
            mAbsorbanceFloat.add(new Entry(m.getWavelength(), m.getAbsorbance()));
            mReflectanceFloat.add(new Entry(m.getWavelength(), m.getReflectance()));
        }

        //计算最大最小波长 便于构建字典
        float minWavelength = 99999f;
        float maxWavelength = 0f;

        for (MeasurePoint m : measurePoints) {
            if (m.getWavelength() < minWavelength) {
                minWavelength = m.getWavelength();
            }
            if (m.getWavelength() > maxWavelength) {
                maxWavelength = m.getWavelength();
            }
        }
        binding.viewpager.setAdapter(binding.viewpager.getAdapter());
        binding.viewpager.invalidate();

        if (scanType.equals("00")) {
            scanType = "Column 1";
        } else {
            scanType = "Hadamard";
        }

        String fileName;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault());
        String ts = simpleDateFormat.format(new Date());
        if (viewModel.fileNamePrefix.get() == null || viewModel.fileNamePrefix.get().equals("")) {
            fileName = "Nano" + ts;
        } else {
            fileName = viewModel.fileNamePrefix.get() + ts;
        }
        //构建dataFile
        dataFile = new DataFile(fileName, CApplication.scanDataPath + '/' + fileName);


        //调整ActionBar标题
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(fileName);
        }

        //如果要保存到手机
        boolean saveOS = viewModel.save2Phone.get();
        if (saveOS) {
            MeasureDictionary measureDictionary = new MeasureDictionary();
            measureDictionary.setName(fileName);
            measureDictionary.setMethod(scanType);
            measureDictionary.setTimeStamp(ts);
            measureDictionary.setSpectralRangeStart(String.valueOf(minWavelength));
            measureDictionary.setSpectralRangeEnd(String.valueOf(maxWavelength));
            measureDictionary.setNumberofWavelengthPoints(String.valueOf(measurePoints.size()));
            measureDictionary.setNumberofScanstoAverage("1");
            measureDictionary.setDigitalResolution(String.valueOf(measurePoints.size()));

            /**
             * 我无力吐槽了 这个总测量时间和平均测量时间竟然是直接写上去的
             * 原代码:
             * writeCSVDict(ts, scanType, scanDate, String.valueOf(minWavelength), String.valueOf(maxWavelength),
             *                     String.valueOf(results.getLength()), String.valueOf(results.getLength()), "1",
             *                     "2.00", saveOS);
             */
            measureDictionary.setTotalMeasurementTime("2.00");
            try {
                FileUtil.writeData(dataFile, measurePoints, measureDictionary);
                showShortToast(getString(R.string.data_savephone_succeed));
            } catch (IOException e) {
                Log.e(TAG, "onReceive: ", e);
                showShortToast(getString(R.string.file_save_error));
            }
        }

        //如果要连续扫描
        boolean continuous = viewModel.continueScan.get();
        if (continuous) {
            showLoadingView();
            viewModel.scanBtnText.set(getString(R.string.scanning));
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(KSTNanoSDK.SEND_DATA));
        }
    }



    /**
     * 接收到扫描已经开始后的处理方法
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(ActionScanStartedEvent event) {
        hideLoadingView();
        viewModel.scanBtnText.set(getString(R.string.scanning));
    }

    /**
     * 接收到断开连接的事件的处理方法
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(ActionGattDisconnectedEvent event) {
        Toast.makeText(mContext, R.string.nano_disconnected, Toast.LENGTH_SHORT).show();
        finish();
    }

    //endregion

}
