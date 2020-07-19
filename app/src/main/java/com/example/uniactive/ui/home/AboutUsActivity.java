package com.example.uniactive.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Trace;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uniactive.R;
import com.example.uniactive.util.CheckNewResponse;
import com.example.uniactive.util.RetrofitApi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;

public class AboutUsActivity extends AppCompatActivity {
    private HomeItemView iv_check_new;
    private HomeItemView iv_share;
    private TextView iv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv_check_new = findViewById(R.id.iv_check_new);
        iv_share = findViewById(R.id.iv_share);
        iv_version = findViewById(R.id.iv_version);

        String version = iv_version.getText().toString().split(" ")[1];

        iv_check_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitApi.checkNew(version, new Callback<CheckNewResponse>() {
                    @Override
                    public void onResponse(Call<CheckNewResponse> call, retrofit2.Response<CheckNewResponse> response) {
                        //Toast.makeText(AboutUsActivity.this, version, Toast.LENGTH_SHORT).show();
                        try {
                            String url = response.body().getUrl();
                            String message = response.body().getMessage();
                            Boolean isSuccess = response.body().isSuccess();
                            AlertDialog.Builder builder = new AlertDialog.Builder(AboutUsActivity.this);
                            builder.setTitle("提示");
                            builder.setMessage(message);
                            if (isSuccess) {
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //浏览器打开
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        Uri content_url = Uri.parse(url);
                                        intent.setData(content_url);
                                        startActivity(Intent.createChooser(intent, "请选择浏览器"));
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                            else {
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<CheckNewResponse> call, Throwable t) {
                        Toast.makeText(AboutUsActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUsActivity.this, ShareActivity.class);
                startActivity(intent);
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