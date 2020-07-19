package com.example.uniactive.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.uniactive.R;
import com.example.uniactive.util.JSONGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText reset_email, reset_password, reset_qualify, reset_verify_code;
    private Button reset_send, reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        reset_email = findViewById(R.id.reset_email);
        reset_email.setText(intent.getStringExtra("email"));
        reset_password = findViewById(R.id.reset_password);
        reset_qualify = findViewById(R.id.reset_qualify);
        reset_verify_code = findViewById(R.id.reset_verify_code);
        reset_send = findViewById(R.id.reset_send_button);
        reset = findViewById(R.id.btn_reset);

        reset_send.setOnClickListener(v -> {
            String email = reset_email.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ResetPasswordActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                return;
            } else if (!Pattern.matches("^[0-9a-zA-Z_]{2,16}$", email)) {
                Toast.makeText(getApplicationContext(), "邮箱前缀必须为2-16位字母、数字或下划线的组合", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONGenerator.sendVerifyCode(email,
                    getApplicationContext(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int status = response.getInt("status");
                        if (status == 0) {
                            Toast.makeText(getApplicationContext(), "发送过于频繁，请10分钟后再试", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "验证码已发送至邮箱，有效期10分钟", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                        }
            });
        });

        reset.setOnClickListener(v -> {
            String email = reset_email.getText().toString().trim();
            String psw1 = reset_password.getText().toString().trim();
            String psw2 = reset_qualify.getText().toString().trim();
            String verifyCode = reset_verify_code.getText().toString().trim();
            if (!checkValid(email, psw1, psw2, verifyCode)) {
                return;
            }
            JSONGenerator.resetPassword(email, verifyCode, psw1,
                    getApplicationContext(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                            Toast.makeText(getApplicationContext(), "重置密码成功，请返回登录", Toast.LENGTH_SHORT).show();
                            Intent data = new Intent();
                            data.putExtra("email", email);
                            setResult(RESULT_OK, data);
                            finish();
                        } else if (status == 3) {
                            Toast.makeText(getApplicationContext(), "您未发送验证码，或验证码已过期，请重新发送", Toast.LENGTH_SHORT).show();
                        } else if (status == 2) {
                            Toast.makeText(getApplicationContext(), "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
                        } else if (status == 4) {
                            Toast.makeText(getApplicationContext(), "该邮箱未注册过账号", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "系统错误", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private boolean checkValid(String email, String psw1, String psw2, String verifyCode) {
        String emailPattern = "^[0-9a-zA-Z_]{2,16}$";
        String pswPattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "请输入邮箱", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(psw1)) {
            Toast.makeText(getApplicationContext(), "请输入新密码", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(psw2)) {
            Toast.makeText(getApplicationContext(), "请确认新密码", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(verifyCode)) {
            Toast.makeText(getApplicationContext(), "请输入邮箱验证码", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.equals(psw1, psw2)) {
            Toast.makeText(getApplicationContext(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
        } else if (!Pattern.matches(emailPattern, email)) {
            Toast.makeText(getApplicationContext(), "邮箱前缀必须为2-16位字母、数字或下划线的组合", Toast.LENGTH_SHORT).show();
        } else if (!Pattern.matches(pswPattern, psw1)) {
            Toast.makeText(getApplicationContext(), "密码必须为8-16位字母和数字的组合，且不能是纯字母或纯数字", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        JSONGenerator.cancelAllRequests();
        super.onStop();
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
