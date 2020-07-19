package com.example.uniactive.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.donkingliang.labels.LabelsView;
import com.example.uniactive.R;
import com.example.uniactive.util.JSONGenerator;
import com.example.uniactive.util.LabelManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_email, et_psw1, et_psw2, et_nickname, et_verify_code;
    private RadioButton rb_male;
    private Button btn_register, btn_send;

    private LabelsView labelsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_email = findViewById(R.id.et_email);
        et_psw1 = findViewById(R.id.et_psw1);
        et_psw2 = findViewById(R.id.et_psw2);
        et_nickname = findViewById(R.id.et_nickname);
        et_verify_code = findViewById(R.id.et_verify_code);
        rb_male = findViewById(R.id.rb_male);
        btn_register = findViewById(R.id.btn_register);
        btn_send = findViewById(R.id.send_button);
        labelsView = findViewById(R.id.labels);

        labelsView.setLabels(LabelManager.getLabels());

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }

                String email = et_email.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONGenerator.sendVerifyCode(email, getApplicationContext(),
                        new Response.Listener<JSONObject>() {
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
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }

                String email = et_email.getText().toString().trim();
                String psw1 = et_psw1.getText().toString().trim();
                String psw2 = et_psw2.getText().toString().trim();
                String nickname = et_nickname.getText().toString().trim();
                int gender = rb_male.isChecked() ? 1 : 0;
                List<String> labels = labelsView.getSelectLabelDatas();
                String verifyCode = et_verify_code.getText().toString().trim();

                boolean isValid = checkValid(email, psw1, psw2, nickname, labels);
                if (!isValid) return;
                if (verifyCode.length() == 0) {
                    Toast.makeText(getApplicationContext(), "请输入邮箱验证码", Toast.LENGTH_SHORT).show();
                    return;
                }

                String label1 = (labels.size() >= 1) ? labels.get(0) : "";
                String label2 = (labels.size() >= 2) ? labels.get(1) : "";
                String label3 = (labels.size() >= 3) ? labels.get(2) : "";
                String label4 = (labels.size() >= 4) ? labels.get(3) : "";
                String label5 = (labels.size() == 5) ? labels.get(4) : "";

                JSONGenerator.register(email, psw1, gender, nickname,
                        label1, label2, label3, label4, label5,
                        verifyCode, getApplicationContext(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 1) {
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        Intent data = new Intent();
                                        data.putExtra("email", email);
                                        setResult(RESULT_OK, data);
                                        finish();
                                    } else if (status == 2) {
                                        Toast.makeText(getApplicationContext(), "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
                                    } else if (status == 3) {
                                        Toast.makeText(getApplicationContext(), "该邮箱未发送过验证码，或验证码已过期，请重新发送", Toast.LENGTH_SHORT).show();
                                    } else if (status == 0) {
                                        Toast.makeText(RegisterActivity.this, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    private boolean checkValid(String email, String psw1, String psw2, String nickname, List<String> labels) {
        String emailPattern = "^[0-9a-zA-Z_]{2,16}$";
        String pswPattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(psw1)) {
            Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(psw2)) {
            Toast.makeText(RegisterActivity.this, "请再次输入密码", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(RegisterActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
        } else if (!Pattern.matches(emailPattern, email)) {
            Toast.makeText(RegisterActivity.this, "邮箱前缀必须为2-16位字母、数字或下划线的组合", Toast.LENGTH_SHORT).show();
        } else if (!Pattern.matches(pswPattern, psw1)) {
            Toast.makeText(RegisterActivity.this, "密码必须为8-16位字母和数字的组合，且不能是纯字母或纯数字", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.equals(psw1, psw2)) {
            Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
        } else if (labels.size() == 0) {
            Toast.makeText(RegisterActivity.this, "请至少选择一个兴趣标签", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
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
