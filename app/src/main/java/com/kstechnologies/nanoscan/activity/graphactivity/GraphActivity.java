package com.kstechnologies.nanoscan.activity.graphactivity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.activity.BaseActivity;
import com.kstechnologies.nanoscan.databinding.ActivityGraphBinding;
import com.kstechnologies.nanoscan.databinding.RowGraphListItemBinding;
import com.kstechnologies.nanoscan.model.DataFile;
import com.kstechnologies.nanoscan.model.MeasureDictionary;
import com.kstechnologies.nanoscan.model.MeasurePoint;
import com.kstechnologies.nanoscan.utils.CSVUtil;
import com.kstechnologies.nanoscan.utils.GsonUtil;
import com.kstechnologies.nanoscan.utils.MPAndroidChartUtil;
import com.kstechnologies.nirscannanolibrary.SettingsManager;

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
    ArrayList<GraphListItem> graphDict = new ArrayList<>();

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
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(dataFile.getFileName());
            ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


            binding.viewpager.setOffscreenPageLimit(2);

            // Create a tab listener that is called when the user changes tabs.
            ActionBar.TabListener tl = new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    binding.viewpager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                }
            };

            // Add 3 tabs, specifying the tab's text and TabListener
            for (int i = 0; i < 3; i++) {
                ab.addTab(
                        ab.newTab()
                                .setText(getResources().getStringArray(R.array.graph_tab_index)[i])
                                .setTabListener(tl));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Initialize pager adapter
        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(this);
        binding.viewpager.setAdapter(pagerAdapter);
        binding.viewpager.invalidate();

        //Set page change listener for pager to show proper tab when selected
        binding.viewpager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        ActionBar ab = getActionBar();
                        if (ab != null) {
                            ab.setSelectedNavigationItem(position);
                        }
                    }
                });


        /**
         * 尝试打开csv文件和json文件 不得不说之前的这个打开方式和文件的存储方式以及处理方式是真的很蠢
         */
        try {
            measurePoints = (ArrayList<MeasurePoint>) CSVUtil.ReadMeasurePoints(dataFile.getCsvPath());
            MeasureDictionary jsonDict = GsonUtil.ReadDictFromFile(dataFile.getJsonPath());
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
                mIntensityFloat.add(new Entry(m.getWaveLength(), m.getIntensity()));
                mAbsorbanceFloat.add(new Entry(m.getWaveLength(), m.getAbsorbance()));
                mReflectanceFloat.add(new Entry(m.getWaveLength(), m.getReflectance()));
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

        if (id == R.id.action_email) {
            //实在是用不到这个email功能嗷 不符合中国用户习惯
//            if (findFile(fileName) != null) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("message/rfc822");
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailing) + fileName);
//                i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(findFile(fileName)));
//                try {
//                    startActivity(Intent.createChooser(i, getString(R.string.send_mail)));
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(GraphActivity.this, getString(R.string.no_email_clients), Toast.LENGTH_SHORT)
//                    .show();
//                }
//            } else {
//
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("message/rfc822");
//                //i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailing) + fileName);
//                //i.putExtra(Intent.EXTRA_TEXT, "body of email");
//                InputStream inputStream = getResources().openRawResource(getResources().getIdentifier(fileName,
//                "raw", getPackageName()));
//                File file = new File(getExternalCacheDir(), "sample.csv");
//                try {
//
//                    OutputStream output = new FileOutputStream(file);
//                    try {
//                        try {
//                            // or other buffer size
//                            byte[] buffer = new byte[4 * 1024];
//                            int read;
//
//                            while ((read = inputStream.read(buffer)) != -1) {
//                                output.write(buffer, 0, read);
//                            }
//                            output.flush();
//                        } finally {
//                            output.close();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace(); // handle exception, define IOException and others
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//                try {
//                    startActivity(Intent.createChooser(i, getString(R.string.send_mail)));
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(GraphActivity.this, getString(R.string.no_email_clients), Toast.LENGTH_SHORT)
//                    .show();
//                }
//            }
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 将此Adapter改造为DataBinding使用
     * Custom adapter to control the dictionary items in the listview
     */
    public class ScanListAdapter extends RecyclerView.Adapter<ScanListAdapter.ViewHolder> {

        private List<GraphListItem> mList;

        public ScanListAdapter(List<GraphListItem> mList) {
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
            GraphListItem item = mList.get(position);
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
     * 当图标ViewPager滑动时的处理
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
}
