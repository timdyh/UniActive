package com.example.uniactive.ui.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uniactive.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ShareActivity extends AppCompatActivity {

    private TextView share_info;
    private Button share_quit;
    private Button share_do;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        share_info = findViewById(R.id.share_info);
        share_quit = findViewById(R.id.share_quit);
        share_do = findViewById(R.id.share_do);

        share_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("UniActive", share_info.getText());
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(ShareActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
//                    Uri uri = Uri.parse("weixin://");
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
                    Intent intent = new Intent();
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(ShareActivity.this, "检查到您手机没有安装微信，请安装后使用该功能", Toast.LENGTH_LONG).show();
                }
            }
        });

        share_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
