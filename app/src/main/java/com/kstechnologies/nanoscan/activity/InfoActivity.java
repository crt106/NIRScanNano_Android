package com.kstechnologies.nanoscan.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.databinding.ActivityInfoBinding;

import java.util.ArrayList;

/**
 * 展示应用信息Activity 基本不改动
 * This activity controls the information links. Each info item will have a title, message body,
 * and an associated URL. When an item is clicked, the web browser should open the URL
 *
 * @author collinmast,crt106
 */
public class InfoActivity extends BaseActivity {

    ActivityInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_info);

        setSupportActionBar(binding.includeToolbar.toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<InfoManager> infoManagerArrayList = new ArrayList<>();
        int length = getResources().getStringArray(R.array.info_title_array).length;
        int index;
        for (index = 0; index < length; index++) {
            infoManagerArrayList.add(new InfoManager(
                    getResources().getStringArray(R.array.info_title_array)[index],
                    getResources().getStringArray(R.array.info_body_array)[index],
                    getResources().getStringArray(R.array.info_url_array)[index]));
        }

        final InformationAdapter adapter = new InformationAdapter(this, R.layout.row_info_item, infoManagerArrayList);
        binding.lvInfo.setAdapter(adapter);

        //When an info item is clicked, launch the URL using rhe ACTION_VIEW intent
        binding.lvInfo.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse(adapter.getItem(i).getInfoURL()));
            startActivity(webIntent);
        });
    }

    /**
     * Inflate the options menu
     * In this case, inflate the menu resource
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
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
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Class to hold the information items. These objects have a title, body, and an
     * associated URL
     */
    private class InfoManager {

        private String infoTitle;
        private String infoBody;
        private String infoURL;

        public InfoManager(String infoTitle, String infoBody, String infoURL) {
            this.infoTitle = infoTitle;
            this.infoBody = infoBody;
            this.infoURL = infoURL;
        }

        public String getInfoTitle() {
            return infoTitle;
        }

        public String getInfoBody() {
            return infoBody;
        }

        public String getInfoURL() {
            return infoURL;
        }
    }

    /**
     * Custom adapter to hold {@link InfoActivity.InfoManager} objects
     * and add them to the listview
     */
    public class InformationAdapter extends ArrayAdapter<InfoManager> {
        private ViewHolder viewHolder;

        public InformationAdapter(Context context, int textViewResourceId, ArrayList<InfoManager> items) {
            super(context, textViewResourceId, items);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.row_info_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.infoTitle = (TextView) convertView.findViewById(R.id.tv_info_title);
                viewHolder.infoBody = (TextView) convertView.findViewById(R.id.tv_info_body);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final InfoManager item = getItem(position);
            if (item != null) {

                viewHolder.infoTitle.setText(item.getInfoTitle());
                viewHolder.infoBody.setText(item.getInfoBody());
            }
            return convertView;
        }

        /**
         * View holder for {@link InfoActivity.InfoManager} objects
         */
        private class ViewHolder {
            private TextView infoTitle;
            private TextView infoBody;
        }
    }
}
