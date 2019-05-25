package com.kstechnologies.nanoscan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.kstechnologies.nanoscan.widget.swipemenulistview.SwipeMenu;
import com.kstechnologies.nanoscan.widget.swipemenulistview.SwipeMenuCreator;
import com.kstechnologies.nanoscan.widget.swipemenulistview.SwipeMenuItem;
import com.kstechnologies.nanoscan.widget.swipemenulistview.SwipeMenuListView;
import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.activity.InfoActivity;
import com.kstechnologies.nanoscan.activity.newscanactivity.NewScanActivity;
import com.kstechnologies.nanoscan.activity.SettingsActivity;
import com.kstechnologies.nanoscan.activity.graphactivity.GraphActivity;
import com.kstechnologies.nanoscan.databinding.FragmentScanListBinding;
import com.kstechnologies.nanoscan.model.DataFile;
import com.kstechnologies.nanoscan.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * 改造后是查看数据列表的Fragment/Activity
 * 此Fragment是从原Activity改过来的 有些地方懒得改有点疵
 * 原注释：
 * This activity controls the main launcher view
 * This activity is responsible for generating the splash screen and the main
 * file list view
 * <p>
 * From this activity, the user can begin the scan process {@link NewScanActivity},
 * Go to the info view {@link InfoActivity}, or view old scan data {@link GraphActivity}
 *
 * @author collinmast
 */
public class ScanListFragment extends BaseFragment {

    /**
     * 与Activity的连接
     */
    private AppCompatActivity ActivityConnect;
    FragmentScanListBinding binding;

    /**
     * 可用数据文件集列表
     */
    public ArrayList<DataFile> dataFiles = new ArrayList<>();

    /**
     * 数据文件Adapter 只显示dataFile文件名
     */
    private ArrayAdapter<String> mAdapter;

    /**
     * 单个选项的菜单
     */
    //TODO 更换为RecyclerView

    private SwipeMenuListView lv_csv_files;

    private SwipeMenuCreator unknownCreator = createMenu();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ActivityConnect = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_list, container, false);
        return binding.getRoot();
    }

    /**
     * When the activity is destroyed, make a call to the super class
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * On resume, check for crashes and updates with HockeyApp,
     * and set up the file list,swipe menu, and event listeners
     */
    @Override
    public void onResume() {
        super.onResume();

        dataFiles.clear();

        lv_csv_files = binding.lvCsvFiles;
        populateListView();

        lv_csv_files.setAdapter(mAdapter);
        lv_csv_files.setMenuCreator(unknownCreator);

        /**
         * set the on menu item click for the SwipeMenuListView.
         * In this case, delete the selected file
         */
        lv_csv_files.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {

                switch (index) {
                    case 0:
                        //删除按钮点击时
                        removeFile(dataFiles.get(position));
                        mAdapter.remove(dataFiles.get(position).getFileName());
                        lv_csv_files.setAdapter(mAdapter);
                        break;
                    default:
                }
                return false;
            }
        });

        /**
         * Add item click listener to file listview. This will close an item if it's
         * swipe menu is open
         */
        lv_csv_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lv_csv_files.smoothOpenMenu(position);
            }
        });

        mAdapter.notifyDataSetChanged();

        /**
         * Add item click listener to file listview. Clicking an item will start the graph
         * activity for that file
         */
        lv_csv_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent graphIntent = new Intent(getContext(), GraphActivity.class);
                graphIntent.putExtra("dataFile", dataFiles.get(i));
                startActivity(graphIntent);
            }
        });

        //Get UI reference to Edit text bar for searching through scan names
        EditText searchText = binding.etSearch;

        //Add listener to editText so that the listview is updated as the user starts typing
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Inflate the options menu so that the info, settings, and connect icons are visible
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_scan_list, menu);
    }

    /**
     * Handle the selection of a menu item.
     * In this case, there are three options. The user can go to the info activity,
     * the settings activity, or connect to a Nano
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(ActivityConnect, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_info) {
            Intent infoIntent = new Intent(ActivityConnect, InfoActivity.class);
            startActivity(infoIntent);
            return true;
        } else if (id == R.id.action_scan) {
            Intent graphIntent = new Intent(getContext(), NewScanActivity.class);
            graphIntent.putExtra("file_name", getString(R.string.newScan));
            startActivity(graphIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化可用的csv文件列表
     * Populate the stored scan listview with included files in the raw directory as well as
     * stored CSV files
     */
    public void populateListView() {
        dataFiles = (ArrayList<DataFile>) FileUtil.getAvalibleData();
        ArrayList<String> dataFileNames = new ArrayList<>();
        for (DataFile d : dataFiles) {
            dataFileNames.add(d.getFileName());
        }
        mAdapter = new ArrayAdapter<>(ActivityConnect, android.R.layout.simple_list_item_1, dataFileNames);
    }

    /**
     * Removes a specified file from the external storage directory
     */
    public void removeFile(DataFile dataFile) {
        FileUtil.deleteFile(new File(dataFile.getFilePath()));
    }

    private SwipeMenuCreator createMenu() {
        return new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem settingsItem = new SwipeMenuItem(
                        ActivityConnect.getApplicationContext());
                // set item background
                settingsItem.setBackground(R.color.kst_red);
                // set item width
                settingsItem.setWidth(dp2px(90));
                // set a icon

                settingsItem.setTitleColor(ContextCompat.getColor(ActivityConnect.getBaseContext(), R.color.white));
                settingsItem.setTitleSize(18);
                settingsItem.setTitle(getResources().getString(R.string.delete));

                // add to menu
                menu.addMenuItem(settingsItem);
            }
        };
    }

    /**
     * Function to convert dip to pixels
     *
     * @param dp the number of dip to convert
     * @return the dip units converted to pixels
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
