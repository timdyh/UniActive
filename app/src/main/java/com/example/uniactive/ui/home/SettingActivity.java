package com.example.uniactive.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.uniactive.MainActivity;
import com.example.uniactive.R;
import com.example.uniactive.ui.alarm.SendNotificationActivity;
import com.example.uniactive.ui.user.ChangePassword;
import com.example.uniactive.ui.user.LoginActivity;
import com.example.uniactive.ui.user.LogoffActivity;
import com.example.uniactive.ui.user.RegisterActivity;
import com.example.uniactive.util.JSONGenerator;

public class SettingActivity extends AppCompatActivity {

    private HomeItemView iv_change_psw;
    private HomeItemView iv_logoff;
    private HomeItemView iv_logout;

    private SharedPreferences sp;

    public static final int REQUEST_CHANGE_PSW = 1;
    public static final int REQUEST_LOGOFF = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("userData", MODE_PRIVATE);

        iv_change_psw = findViewById(R.id.iv_change_psw);
        iv_logoff = findViewById(R.id.iv_logoff);
        iv_logout = findViewById(R.id.iv_logout);

        iv_change_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ChangePassword.class);
                startActivityForResult(intent, REQUEST_CHANGE_PSW);
            }
        });

        iv_logoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, LogoffActivity.class);
                startActivityForResult(intent, REQUEST_LOGOFF);
            }
        });

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定退出当前账号？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent ser = new Intent(HomeFragment.instance.getActivity(), SendNotificationActivity.class);
                        HomeFragment.instance.getActivity().stopService(ser);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();
                        Intent data = new Intent();
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CHANGE_PSW:
                case REQUEST_LOGOFF:
                    Intent data1 = new Intent();
                    setResult(RESULT_OK, data1);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        JSONGenerator.cancelAllRequests();
        super.onStop();
    }
}
