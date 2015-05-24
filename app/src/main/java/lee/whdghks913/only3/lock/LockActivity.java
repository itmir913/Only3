package lee.whdghks913.only3.lock;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.fragment.LockFragment;
import lee.whdghks913.only3.fragment.MainFragment;
import lee.whdghks913.only3.fragment.SettingsFragment;
import lee.whdghks913.only3.fragment.app.AppFragment;
import lee.whdghks913.only3.lock.fragment.LockMainFragment;
import lee.whdghks913.only3.lock.fragment.WhiteListAppFragment;
import lee.whdghks913.only3.tools.LockTools;
import lee.whdghks913.only3.tools.ServiceTools;

public class LockActivity extends ActionBarActivity {
    Toolbar mToolbar;

    ViewPager mViewPager;
    SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.flat_melon_yellow));
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);

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
                    return LockMainFragment.newInstance();
                case 1:
                    return WhiteListAppFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.fragment_lock_main);
                case 1:
                    return getString(R.string.fragment_lock_white_list);
            }
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return isFinish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isFinish(){
        long finishTime = LockTools.getFinishTime(getApplicationContext());
        long currentTime = System.currentTimeMillis();

        return (currentTime >= finishTime);
    }


}
