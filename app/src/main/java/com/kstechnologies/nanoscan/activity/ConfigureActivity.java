package com.kstechnologies.nanoscan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.databinding.ActivityConfigureBinding;
import com.kstechnologies.nanoscan.event.ActionGattDisconnectedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 当设备连接完毕时的管理界面 不做大的修改
 * This activity controls the view for settings once the Nano is connected
 * Four options are presented, each one launching a new activity.
 * Since each option requires the Nano to be connected to perform GATT operations,
 *
 * @author collinmast,crt106
 */
public class ConfigureActivity extends BaseActivity {

    private static Context mContext;
    ActivityConfigureBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_configure);
        EventBus.getDefault().register(this);
        setSupportActionBar(binding.includeToolbar.toolbar);

        mContext = this;

        //Set the action bar title and enable the back button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.configure));
        }

        binding.lvConfigure.setOnItemClickListener((adapterView, view, i, l) ->
        {
            switch (i) {
                case 0:
                    Intent infoIntent = new Intent(mContext, DeviceInfoActivity.class);
                    startActivity(infoIntent);
                    break;
                case 1:
                    Intent statusIntent = new Intent(mContext, DeviceStatusActivity.class);
                    startActivity(statusIntent);
                    break;
                case 2:
                    Intent confIntent = new Intent(mContext, ScanConfActivity.class);
                    startActivity(confIntent);
                    break;
                case 3:
                    Intent scanDataIntent = new Intent(mContext, StoredScanDataActivity.class);
                    startActivity(scanDataIntent);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * On resume, make a call to the super class.
     * Nothing else is needed here besides calling
     * the super method.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * When the activity is destroyed, unregister the BroadcastReceiver
     * handling disconnection events.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Inflate the options menu
     * In this case, there is no menu and only an up indicator,
     * so the function should always return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Handle the selection of a menu item.
     * In this case, there is only the up indicator. If selected, this activity should finish.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * EventBus 接收断开连接信息
     * @param event
     */
    public void onEventReceive(ActionGattDisconnectedEvent event){
        finish();
    }
}
