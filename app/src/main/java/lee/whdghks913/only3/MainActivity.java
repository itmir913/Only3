package lee.whdghks913.only3;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import lee.whdghks913.only3.fragment.app.AppFragment;
import lee.whdghks913.only3.fragment.MainFragment;
import lee.whdghks913.only3.fragment.SettingsFragment;
import lee.whdghks913.only3.tools.Tools;


public class MainActivity extends ActionBarActivity {
    //    ActionBarDrawerToggle mToggle;
//    DrawerLayout mDrawer;
    Toolbar mToolbar;

    ViewPager mViewPager;
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * onCreate()
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.flat_melon_yellow));
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
//        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
//        mDrawer.setDrawerListener(mToggle);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager mFragmentManager) {
            super(mFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MainFragment.newInstance();
                case 1:
                    return AppFragment.newInstance();
                case 2:
                    return SettingsFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.fragment_main);
                case 1:
                    return getString(R.string.fragment_app);
                case 2:
                    return getString(R.string.fragment_settings);
            }
            return null;
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (getServiceRunning()) {
            menu.findItem(R.id.action_start).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // if (mToggle.onOptionsItemSelected(item)) return true;

        *//**
     * 시작 버튼
     *//*
        if (id == R.id.action_start) {
            Start();
            return true;
        }
        *//**
     * 설정 버튼
     *//*
        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
