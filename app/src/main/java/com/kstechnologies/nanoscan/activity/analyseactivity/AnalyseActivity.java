package com.kstechnologies.nanoscan.activity.analyseactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.activity.BaseActivity;
import com.kstechnologies.nanoscan.databinding.ActivityAnalyseBinding;
import com.kstechnologies.nanoscan.databinding.PageLinechartBinding;
import com.kstechnologies.nanoscan.databinding.RowGraphListItemBinding;
import com.kstechnologies.nanoscan.model.DataFile;
import com.kstechnologies.nanoscan.model.MeasureDictionary;
import com.kstechnologies.nanoscan.model.MeasurePoint;
import com.kstechnologies.nanoscan.utils.CSVUtil;
import com.kstechnologies.nanoscan.utils.GsonUtil;
import com.kstechnologies.nanoscan.utils.MPAndroidChartUtil;
import com.kstechnologies.nanoscan.utils.MathUtil;
import com.kstechnologies.nanoscan.viewmodel.InfoListItem;

import org.apache.commons.math3.fitting.WeightedObservedPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 进行初步数据分析和图像绘制的Activity
 *
 * @author crt106 on 2019/5/15.
 */
public class AnalyseActivity extends BaseActivity {

    private static final String TAG = "AnalyseActivity";

    private ActivityAnalyseBinding binding;

    private AnalyseActivityViewModel viewModel;

    /**
     * 计算Brix值的四个波长定值
     */
    private static final double BRIX_P1 = 900.9161;
    private static final double BRIX_P2 = 904.8474;
    private static final double BRIX_P3 = 996.6378;
    private static final double BRIX_P4 = 1062.341;
    private static final double BRIX_P5 = 1100.897;

    /**
     * 计算的Brix值
     */
    private double brix = 0;

    /**
     * 默认的曲线阶数
     * TODO 实现默认值的存储
     */
    private static final int DEFAULT_CURVE_LEVEL = 1;
    private int curveLevel = DEFAULT_CURVE_LEVEL;

    /**
     * 生成的预测点的个数
     */
    private static final int NUMBER_OF_PREDICT_POINTS = 100;

    /**
     * 最大处理时间 超过时间显示错误(ms)
     */
    private static final int TIME_OUT = 5000;

    /**
     * 本实例进行处理的DataFile
     */
    private DataFile dataFile;

    /**
     * 本实例绘制的线性图表对象们 以及其数据存储对象
     */
    private ArrayList<LineChartPara> lineChartsPara = new ArrayList<>();
    private ArrayList<Entry> Absorbance = new ArrayList<>();
    private ArrayList<Entry> Absorbance_first = new ArrayList<>();
    private ArrayList<Entry> Absorbance_second = new ArrayList<>();

    /**
     * 分析信息们
     */
    private ArrayList<InfoListItem> infoListItems = new ArrayList<>();

    /**
     * 最后一次运算得到的拟合多项式的系数
     */
    private double[] lastPolynomialParas = new double[1];
    /**
     * 最后一次运算得到的拟合多项式的一阶导数系数
     */
    private double[] lastpolynomialParasFirst = new double[1];
    /**
     * 最后一次运算得到的拟合多项式的二阶导数系数
     */
    private double[] lastpolynomialParasSceond = new double[1];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_analyse);
        //从Intent中获取DataFile
        try {
            dataFile = (DataFile) getIntent().getSerializableExtra("dataFile");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: 获取数据失败", e);
            showShortToast("获取处理数据失败");
            finish();
        }

        //初始化ViewModel
        viewModel = new AnalyseActivityViewModel();

        viewModel.calcTime.set(String.valueOf(0));
        viewModel.level.set(String.valueOf(curveLevel));
        binding.setVm(viewModel);

        binding.sbCurveLevel.setOnSeekBarChangeListener(seekBarChangeListener);
        binding.sbCurveLevel.setProgress(curveLevel);

        setSupportActionBar(binding.includeToolbar.toolbar);
        getSupportActionBar().setTitle(dataFile.getFileName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //执行计算任务
        try {
            new CalculateDataTask().execute().get(TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Log.e(TAG, "onCreate: ", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "onCreate: ", e);
        } catch (TimeoutException e) {
            Log.e(TAG, "onCreate: 分析超时", e);
            showErrorDialog();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_analyse, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.action_share) {
            shareResult();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 异步任务 计算数据嗷
     */
    private class CalculateDataTask extends AsyncTask<Void, Void, Boolean> {

        public CalculateDataTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return dataCalc();
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (!b) {
                showErrorDialog();
            }
            initLineCharts();
        }
    }


    /**
     * 计算页面使用数据
     */
    private boolean dataCalc() {
        try {
            long startTime = System.currentTimeMillis();
            List<MeasurePoint> measurePoints = CSVUtil.readMeasurePoints(dataFile.getCsvPath());
            MeasureDictionary measureDictionary = GsonUtil.readDictFromFile(dataFile.getJsonPath());
            double maxWaveLength = Double.parseDouble(measureDictionary.getSpectralRangeEnd());
            double minWaveLength = Double.parseDouble(measureDictionary.getSpectralRangeStart());

            lineChartsPara.clear();
            Absorbance.clear();
            Absorbance_first.clear();
            Absorbance_second.clear();

            //构建预测值列表
            ArrayList<Double> predicXValues = new ArrayList<>();
            double step = (maxWaveLength - minWaveLength) / NUMBER_OF_PREDICT_POINTS;
            for (int i = 0; i < NUMBER_OF_PREDICT_POINTS; i++) {
                predicXValues.add(minWaveLength + i * step);
            }
            predicXValues.add(BRIX_P1);
            predicXValues.add(BRIX_P2);
            predicXValues.add(BRIX_P3);
            predicXValues.add(BRIX_P4);
            predicXValues.add(BRIX_P5);

            List<WeightedObservedPoint> wps = new ArrayList<>();
            double p1 = 0, p2 = 0, p3 = 0, p4 = 0, p5 = 0;
            for (MeasurePoint m : measurePoints) {
                wps.add(new WeightedObservedPoint(1, m.getWavelength(), m.getIntensity()));
                double x = m.getWavelength();

                //TODO 优化这坨shi
                if (Math.abs(x - BRIX_P1) < 0.01 ||
                        Math.abs(x - BRIX_P2) < 0.01 ||
                        Math.abs(x - BRIX_P3) < 0.01 ||
                        Math.abs(x - BRIX_P4) < 0.01 ||
                        Math.abs(x - BRIX_P5) < 0.01)
                {
//                    double p = -1 / log(10) * (y_second * y - pow(y_first, 2)) / (pow(y, 2));
                    double p = m.getIntensity();
                    if (Math.abs(x - BRIX_P1) < 0.01) {
                        p1 = p;
                    }
                    if (Math.abs(x - BRIX_P2) < 0.01) {
                        p2 = p;
                    }
                    if (Math.abs(x - BRIX_P3) < 0.01) {
                        p3 = p;
                    }
                    if (Math.abs(x - BRIX_P4) < 0.01) {
                        p4 = p;
                    }
                    if (Math.abs(x - BRIX_P5) < 0.01) {
                        p5 = p;
                    }
                }
            }
            double[] polynomialParas = MathUtil.polynomialFit(wps, curveLevel);
            if (polynomialParas.length == 1) {
                return false;
            }
            lastPolynomialParas = polynomialParas;
            double[] polynomialParas_first = MathUtil.polynomialDerivate(polynomialParas, 1);
            double[] polynomialParas_second = MathUtil.polynomialDerivate(polynomialParas, 2);
            lastpolynomialParasFirst = polynomialParas_first;
            lastpolynomialParasSceond = polynomialParas_second;

            //求拟合后的波长预测值
            List<Double> predicYValues = MathUtil.polynomialPredict(polynomialParas, predicXValues);
            List<Double> predicYValues_first = MathUtil.polynomialPredict(polynomialParas_first, predicXValues);
            List<Double> predicYValues_second = MathUtil.polynomialPredict(polynomialParas_second, predicXValues);

            //构建画图数据
            for (int i = 0; i < predicXValues.size(); i++) {
                double x = predicXValues.get(i);
                double y = predicYValues.get(i);
                double y_first = predicYValues_first.get(i);
                double y_second = predicYValues_second.get(i);

                //构建Brix值计算系数
//                if (x == BRIX_P1 || x == BRIX_P2 || x == BRIX_P3 || x == BRIX_P4 || x == BRIX_P5) {
////                    double p = -1 / log(10) * (y_second * y - pow(y_first, 2)) / (pow(y, 2));
//                    double p = y;
//                    if (x == BRIX_P1) {
//                        p1 = p;
//                    }
//                    if (x == BRIX_P2) {
//                        p2 = p;
//                    }
//                    if (x == BRIX_P3) {
//                        p3 = p;
//                    }
//                    if (x == BRIX_P4) {
//                        p4 = p;
//                    }
//                    if (x == BRIX_P5) {
//                        p5 = p;
//                    }
//                } else
                {
                    Absorbance.add(new Entry((float) x, (float) y));
                    Absorbance_first.add(new Entry((float) x, (float) y_first));
                    Absorbance_second.add(new Entry((float) x, (float) y_second));
                }
            }
            brix = MathUtil.getBrix(p1, p2, p3, p4, p5);
            long endTime = System.currentTimeMillis();
            viewModel.calcTime.set(String.valueOf(endTime - startTime));
            infoListItems.add(new InfoListItem(String.format("Brix(level:%s)", viewModel.level.get()),
                    String.valueOf(brix)));
            return true;
        } catch (IOException e) {
            Log.e(TAG, "dataCalc:", e);
            return false;
        }
    }

    /**
     * 初始化图表们 这里创建图表信息就行了嗷 因为实际的图表处理是在Adapter中完成的
     */
    private void initLineCharts() {
        LineChartPara AbsorbanceChart = new LineChartPara(String.format("强度拟合曲线(level:%s)", viewModel.level.get()),
                Absorbance,
                MPAndroidChartUtil.ChartType.ABSORBANCE);
        LineChartPara AbsorbanceChart_first = new LineChartPara("强度一阶导数", Absorbance_first,
                MPAndroidChartUtil.ChartType.ABSORBANCE_FIRST_DERIVATIVE);
        LineChartPara AbsorbanceChart_second = new LineChartPara("强度二阶导数", Absorbance_second,
                MPAndroidChartUtil.ChartType.ABSORBANCE_SECOND_DERIVATIVE);
        lineChartsPara.add(AbsorbanceChart);
        lineChartsPara.add(AbsorbanceChart_first);
        lineChartsPara.add(AbsorbanceChart_second);

        LineChartsAdapter lAdapter = new LineChartsAdapter(lineChartsPara);
        binding.rvAnalyseCharts.setAdapter(lAdapter);
        binding.rvAnalyseCharts.setLayoutManager(new LinearLayoutManager(this));

        AnalyseInfoAdapter iAdapter = new AnalyseInfoAdapter(infoListItems);
        binding.rvAnalyseInfo.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAnalyseInfo.setAdapter(iAdapter);
    }

    /**
     * 图表RecyclerView的Adapter
     */
    private class LineChartsAdapter extends RecyclerView.Adapter<LineChartsAdapter.ViewHolder> {

        List<LineChartPara> mList;

        public LineChartsAdapter(List<LineChartPara> list) {
            super();
            mList = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PageLinechartBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.page_linechart, parent,
                    false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            LineChart mChart = holder.mBinding.lcLinechart;
            LineChartPara para = mList.get(position);
            String dataLabel = para.getDataLabel();
            List<Entry> yvalues = para.getyValues();
            MPAndroidChartUtil.ChartType type = para.getChartType();
            MPAndroidChartUtil.setLineChart(mChart, dataLabel, yvalues, type);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            PageLinechartBinding mBinding;

            public ViewHolder(@NonNull PageLinechartBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }

    }

    /**
     * 承载分析显示信息的Adapter
     */
    private class AnalyseInfoAdapter extends RecyclerView.Adapter<AnalyseInfoAdapter.ViewHolder> {
        private List<InfoListItem> mList;

        public AnalyseInfoAdapter(List<InfoListItem> infoListItems) {
            super();
            mList = infoListItems;
        }

        @NonNull
        @Override
        public AnalyseInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RowGraphListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.row_graph_list_item, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull AnalyseInfoAdapter.ViewHolder holder, int position) {
            holder.mBinding.setItem(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            RowGraphListItemBinding mBinding;

            public ViewHolder(@NonNull RowGraphListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    /**
     * 本页面SeekBar改变值事件
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(TAG, "onProgressChanged: seekBar值变化");
            viewModel.level.set(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "onStartTrackingTouch: 开始滑动");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "onStopTrackingTouch: 结束滑动");
            try {
                curveLevel = Integer.parseInt(viewModel.level.get());
                new CalculateDataTask().execute().get(TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (ExecutionException e) {
                Log.e(TAG, "onStopTrackingTouch: ", e);
            } catch (InterruptedException e) {
                Log.e(TAG, "onStopTrackingTouch: ", e);
            } catch (TimeoutException e) {
                Log.e(TAG, "onStopTrackingTouch: 分析超时", e);
                showErrorDialog();
            }
        }
    };

    /**
     * 分享分析结果 构成字符串内容直接发送
     */
    private void shareResult() {
        StringBuilder resultBuilder = null;
        try {
            resultBuilder = new StringBuilder();
            Object[] Paras = new Object[]{lastPolynomialParas, lastpolynomialParasFirst, lastpolynomialParasSceond};
            for (int j = 0; j < Paras.length; j++) {
                double[] thisa = (double[]) Paras[j];
                switch (j) {
                    case 0: {
                        resultBuilder.append(String.format("吸收率拟合曲线(level:%s)系数\n", viewModel.level.get()));
                        break;
                    }
                    case 1: {
                        resultBuilder.append("吸收率一阶导数系数\n");
                        break;
                    }
                    case 2: {
                        resultBuilder.append("吸收率二阶导数系数\n");
                        break;
                    }
                    default:
                }
                for (int i = 0; i < thisa.length; i++) {
                    if (i == 0) {
                        resultBuilder.append("[" + thisa[i] + ",\n");
                    } else if (i == thisa.length - 1) {
                        resultBuilder.append(thisa[i] + "]\n");
                    } else {
                        resultBuilder.append(thisa[i] + ",\n");
                    }
                }
                resultBuilder.append("\n");
            }
            resultBuilder.append("\n");
            for (InfoListItem i : infoListItems) {
                resultBuilder.append(i.getTitle() + ": " + i.getContent());
                resultBuilder.append("\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "shareResult: ", e);
            showShortToast("分享时遇到错误，请检查数据");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, resultBuilder.toString());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "选择发送对象"));
    }

    /**
     * 当分析无法进行时，展示错误对话框
     */
    private void showErrorDialog() {
        new MaterialAlertDialogBuilder(this, R.style.CommonDialog)
                .setTitle(getString(R.string.error_status))
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.analyse_error_msg))
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                    dialog.dismiss();
                    this.finish();
                })
                .setCancelable(false)
                .create()
                .show();
    }

}
