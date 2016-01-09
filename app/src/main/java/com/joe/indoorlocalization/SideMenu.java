package com.joe.indoorlocalization;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.indoorlocalization.Calibration.CustomImageViewCalibrate;
import com.joe.indoorlocalization.Locate.CustomImageViewLocate;

import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by joe on 20/12/15.
 */
public class SideMenu extends AppCompatActivity {

    static String TAG = SideMenu.class.getSimpleName();

    public ActionBarDrawerToggle mDrawerToggle;

    private String className;
    protected Activity activity;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private DrawerLayout mDrawerLayout;

    private ApplicationState state;
    public SideMenu(Activity activity) {
        this.activity = activity;
    }

    public void initSideMenu(String className, Context context) {
        this.className = className;
        this.state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        mDrawerList = (ListView) activity.findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        Log.d(TAG, mDrawerLayout.toString());
        addSideMenuItems();
        setupSideMenu();
    }

    private void addSideMenuItems() {
        String[] sideMenuListNames = new String[this.state.getBlueprints().size()];
        int index = 0;
        for (HashMap.Entry<Integer, String> entry : this.state.getBlueprints().entrySet()) {
            if(entry.getKey() == 0) {
                sideMenuListNames[index] = "Basement";
            } else {
                sideMenuListNames[index] = "Floor " + entry.getKey();
            }
            index++;
        }
        mAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, sideMenuListNames);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new sideMenuItemClickListener());
    }

    private class sideMenuItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        int floorNumber = Integer.parseInt(this.state.getBlueprints().keySet().toArray()[position].toString());
        this.state.floorChanger.changeFloor(activity, floorNumber, className);
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void setupSideMenu() {
        mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
        mDrawerList.bringToFront();
                mDrawerLayout.requestLayout();
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a sideMenu has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        Log.d(TAG, mDrawerToggle.toString());
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
}
