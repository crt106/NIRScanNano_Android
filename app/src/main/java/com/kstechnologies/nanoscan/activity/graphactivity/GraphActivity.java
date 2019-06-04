package com.kstechnologies.nanoscan.activity.graphactivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.activity.BaseActivity;
import com.kstechnologies.nanoscan.activity.analyseactivity.AnalyseActivity;
import com.kstechnologies.nanoscan.constant.Constant;
import com.kstechnologies.nanoscan.databinding.ActivityGraphBinding;
import com.kstechnologies.nanoscan.databinding.RowGraphListItemBinding;
import com.kstechnologies.nanoscan.model.DataFile;
import com.kstechnologies.nanoscan.model.MeasureDictionary;
import com.kstechnologies.nanoscan.model.MeasurePoint;
import com.kstechnologies.nanoscan.utils.CSVUtil;
import com.kstechnologies.nanoscan.utils.GsonUtil;
import com.kstechnologies.nanoscan.utils.MPAndroidChartUtil;
import com.kstechnologies.nanoscan.viewmodel.InfoListItem;
import com.kstechnologies.nirscannanolibrary.SettingsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity controlling the graphing of stored scan files.
 * This activity has to do special processing for the embedded raw files.
 * Currently, both the included raw files and the CSV files can be plotted
 *
 * @author collinmast
 */
public class GraphActivity extends BaseActivity {

    /**
     * 本实例DataBinding
     */
    ActivityGraphBinding binding;

    /**
     * 本实例读取的文件
     */
    private DataFile dataFile;

    /**
     * 读取到的测量点数据
     */
    private ArrayList<MeasurePoint> measurePoints;

    /**
     * 当前文件的信息字典
     */
    ArrayList<InfoListItem> graphDict = new ArrayList<>();

    /**
     * 当前文件提取的波长-强度键值对
     */
    private ArrayList<Entry> mIntensityFloat;
    /**
     * 当前文件提取的波长-吸收率键值对
     */
    private ArrayList<Entry> mAbsorbanceFloat;
    /**
     * 当前文件提取的波长-反射率键值对
     */
    private ArrayList<Entry> mReflectanceFloat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_graph);


        //Get the file name from the intent
        Intent intent = getIntent();
        dataFile = (DataFile) intent.getSerializableExtra("dataFile");

        //Set up action bar title, back button, and navigation tabs
        setSupportActionBar(binding.includeToolbar.toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(dataFile.getFileName());
        }
        binding.viewpager.setOffscreenPageLimit(2);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Initialize pager adapter
        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(this);
        binding.viewpager.setAdapter(pagerAdapter);
        binding.viewpager.invalidate();
        //将Tablayout与viewPager绑定
        binding.tbGraphCharts.setupWithViewPager(binding.viewpager);


        /**
         * 尝试打开csv文件和json文件 不得不说之前的这个打开方式和文件的存储方式以及处理方式是真的很蠢
         */
        try {
            measurePoints = (ArrayList<MeasurePoint>) CSVUtil.readMeasurePoints(dataFile.getCsvPath());
            MeasureDictionary jsonDict = GsonUtil.readDictFromFile(dataFile.getJsonPath());
            graphDict = jsonDict.getDict(this);
        } catch (FileNotFoundException e) {
            //如果指定文件没有找到
            showShortToast("未找到指定文件");
            finish();
        } catch (IOException e) {
            showShortToast("读取文件时发生错误,请检查");
            finish();
        }

        mIntensityFloat = new ArrayList<>();
        mAbsorbanceFloat = new ArrayList<>();
        mReflectanceFloat = new ArrayList<>();
        //生成绘图数据
        for (MeasurePoint m : measurePoints) {
            try {
                mIntensityFloat.add(new Entry(m.getWavelength(), m.getIntensity()));
                mAbsorbanceFloat.add(new Entry(m.getWavelength(), m.getAbsorbance()));
                mReflectanceFloat.add(new Entry(m.getWavelength(), m.getReflectance()));
            } catch (NumberFormatException e) {
                Toast.makeText(GraphActivity.this, "Error parsing float value", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        //构建文件信息展示Adapter
        ScanListAdapter mAdapter = new ScanListAdapter(graphDict);

        //构建RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.lvScanData.setAdapter(mAdapter);
        binding.lvScanData.setLayoutManager(linearLayoutManager);
    }

    /**
     * When the activity is destroyed, nothing is needed except a call to the super class
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Inflate the options menu so that user actions are present
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return true;
    }

    /**
     * Handle the selection of a menu item.
     * In this case, the user has the ability to email a file as well as navigate backwards.
     * When the email icon is clicked, the file is attached and an email template is created
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            this.finish();
        }

        if (id == R.id.action_analyse) {
            //跳转到AnalyseActivity
            Intent intent = new Intent(this, AnalyseActivity.class);
            intent.putExtra("dataFile", dataFile);
            startActivity(intent);
        }

        if (id == R.id.action_share) {
            shareDataFile();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 将此Adapter改造为DataBinding使用
     * Custom adapter to control the dictionary items in the listview
     */
    public class ScanListAdapter extends RecyclerView.Adapter<ScanListAdapter.ViewHolder> {

        private List<InfoListItem> mList;

        public ScanListAdapter(List<InfoListItem> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RowGraphListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.row_graph_list_item, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            InfoListItem item = mList.get(position);
            holder.binding.setItem(item);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            public RowGraphListItemBinding binding;

            public ViewHolder(ViewDataBinding binding) {
                super(binding.getRoot());
                this.binding = (RowGraphListItemBinding) binding;
            }
        }
    }

    /**
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
     * 当图表ViewPager滑动时的处理
     * Custom pager adapter to handle changing chart data when pager tabs are changed
     */
    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), collection, false);
            collection.addView(layout);

            if (customPagerEnum.getLayoutResId() == R.layout.page_graph_intensity) {
                LineChart mChart = (LineChart) layout.findViewById(R.id.lineChartInt);
                MPAndroidChartUtil.setLineChart(mChart, dataFile.getFileName(), mIntensityFloat,
                        MPAndroidChartUtil.ChartType.INTENSITY);
                return layout;
            } else if (customPagerEnum.getLayoutResId() == R.layout.page_graph_absorbance) {
                LineChart mChart = (LineChart) layout.findViewById(R.id.lineChartAbs);
                MPAndroidChartUtil.setLineChart(mChart, dataFile.getFileName(), mAbsorbanceFloat,
                        MPAndroidChartUtil.ChartType.ABSORBANCE);
                return layout;
            } else if (customPagerEnum.getLayoutResId() == R.layout.page_graph_reflectance) {
                LineChart mChart = (LineChart) layout.findViewById(R.id.lineChartRef);
                MPAndroidChartUtil.setLineChart(mChart, dataFile.getFileName(), mReflectanceFloat,
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

    /**
     * Function return the specified frequency in units of frequency or wavenumber
     *
     * @param freq The frequency to convert
     * @return string representing either frequency or wavenumber
     */
    private String getSpatialFreq(String freq) {
        Float floatFreq = Float.parseFloat(freq);
        if (SettingsManager.getBooleanPref(getBaseContext(), SettingsManager.SharedPreferencesKeys.spatialFreq,
                SettingsManager.WAVELENGTH))
        {
            return String.format("%.02f", floatFreq);
        } else {
            return String.format("%.02f", (10000000 / floatFreq));
        }
    }

    /**
     * 分享数据文件到其他地方
     */
    public void shareDataFile() {

        File csvFile = new File(dataFile.getCsvPath());
        if (!csvFile.exists()) {
            showShortToast("分享文件时遇到错误,请检查");
            return;
        }
        Uri fileuri = Uri.EMPTY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileuri = FileProvider.getUriForFile(this, Constant.PROVIDER_NAME, csvFile);
        } else {
            fileuri = Uri.fromFile(csvFile);
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, fileuri);
        sendIntent.setType("application/octet-stream");
        sendIntent.setDataAndType(fileuri, "application/octet-stream");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.addCategory("android.intent.category.DEFAULT");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(sendIntent, "选择分享对象"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
