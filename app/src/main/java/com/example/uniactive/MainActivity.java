package com.example.uniactive;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.uniactive.ui.activity.ActDetailActivity;
import com.example.uniactive.ui.home.HomeFragment;
import com.example.uniactive.ui.plaza.PlazaFragment;
import com.example.uniactive.ui.schedule.ScheduleFragment;
import com.example.uniactive.util.JSONGenerator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavView = findViewById(R.id.bottom_nav_view);
        viewPager = findViewById(R.id.view_pager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new PlazaFragment());
        fragments.add(new ScheduleFragment());
        fragments.add(new HomeFragment());

        BottomNavPagerAdapter adapter = new BottomNavPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        NotificationManagerCompat notification = NotificationManagerCompat.from(this);
        boolean isEnabled = notification.areNotificationsEnabled();
        if (!isEnabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示")
                    .setMessage("本软件的活动提醒功能需要开启通知权限，检测到您尚未开启权限，是否设置？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JumpToSetting();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int menuId = menuItem.getItemId();
                switch (menuId) {
                    case R.id.nav_plaza:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_schedule:
                        ScheduleFragment.isVisible = true;
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_home:
                        viewPager.setCurrentItem(2);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                bottomNavView.getMenu().getItem(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onStop() {
        JSONGenerator.cancelAllRequests();
        super.onStop();
    }

//    @Override
//    protected void onDestroy() {
//        SharedPreferences sp = getSharedPreferences("userData", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.clear();
//        editor.apply();
//        super.onDestroy();
//    }

    private void JumpToSetting() {
        final String SETTINGS_ACTION =
                "android.settings.APPLICATION_DETAILS_SETTINGS";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
            Intent intent = new Intent()
                    .setAction(SETTINGS_ACTION)
                    .setData(Uri.fromParts("package",
                            getApplicationContext().getPackageName(), null));
            startActivity(intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent()
                    .setAction(SETTINGS_ACTION)
                    .setData(Uri.fromParts("package",
                            getApplicationContext().getPackageName(), null));
            startActivity(intent);
        }
    }
}
