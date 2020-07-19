package com.example.uniactive.ui.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.uniactive.MainActivity;
import com.example.uniactive.R;
import com.example.uniactive.ui.alarm.SendNotificationActivity;
import com.example.uniactive.ui.home.HomeFragment;
import com.example.uniactive.util.JSONGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LogoffActivity extends AppCompatActivity {
    private EditText logoff_psw;
    private Button logoff;

    private SharedPreferences sp;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logoff);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("userData", MODE_PRIVATE);
        email = sp.getString("email", "null");

        logoff_psw = findViewById(R.id.logoff_psw);
        logoff = findViewById(R.id.logoff);

        logoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psw = logoff_psw.getText().toString();
                if (TextUtils.isEmpty(psw)) {
                    Toast.makeText(LogoffActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONGenerator.logoff(email, psw, getApplicationContext(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 1) {
                                        Toast.makeText(LogoffActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
                                        Intent ser = new Intent(HomeFragment.instance.getActivity(), SendNotificationActivity.class);
                                        HomeFragment.instance.getActivity().stopService(ser);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.clear();
                                        editor.apply();
                                        Intent data = new Intent();
                                        setResult(RESULT_OK, data);
                                        finish();
                                    } else {
                                        Toast.makeText(LogoffActivity.this, "注销失败，请检查注销条件", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LogoffActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
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
