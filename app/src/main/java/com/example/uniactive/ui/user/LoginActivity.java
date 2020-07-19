package com.example.uniactive.ui.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.uniactive.R;
import com.example.uniactive.util.JSONGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText et_email, et_psw;
    private Button btn_login;
    private TextView tv_register, tv_forgot_psw;

    private SharedPreferences sp;

    public final int REQUEST_REGISTER = 1;
    public final int REQUEST_RESET_PASSWORD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("userData", MODE_PRIVATE);

        tv_register = findViewById(R.id.tv_register);
        tv_forgot_psw = findViewById(R.id.tv_forgot_psw);
        et_email = findViewById(R.id.et_email);
        et_psw = findViewById(R.id.et_psw);
        btn_login = findViewById(R.id.btn_login);

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REGISTER);
            }
        });

        tv_forgot_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", et_email.getText().toString().trim());
                startActivityForResult(intent, REQUEST_RESET_PASSWORD);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 收起键盘
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }

                String email = et_email.getText().toString().trim();
                String psw = et_psw.getText().toString().trim();

                boolean isValid = checkValid(email, psw);
                if (!isValid) return;

                JSONGenerator.login(email, psw,
                        getApplicationContext(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 1) {
                                        int userStatus = response.getInt("user_status");
                                        if (userStatus == 0) {
                                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                            String nickname = response.getString("nickname");
                                            int gender = response.getInt("gender");
                                            String avatarUrl = response.getString("img");
                                            JSONArray jsonArray = response.getJSONArray("act_to_start");
                                            String[] info_to_act = new String[jsonArray.length()];
                                            int[] id_to_act = new int[jsonArray.length()];
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                info_to_act[i] = String.valueOf((long)jsonArray.getJSONObject(i).get("start_time")) + " " + (String)jsonArray.getJSONObject(i).get("name");
                                                id_to_act[i] = (int)jsonArray.getJSONObject(i).get("act_id");
                                            }
                                            saveUserData(email, nickname, gender, avatarUrl, info_to_act, id_to_act);
                                            Intent data = new Intent();
                                            data.putExtra("nickname", nickname);
                                            data.putExtra("avatarUrl", avatarUrl);
                                            setResult(RESULT_OK, data);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "该账号已被封禁", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (status == 0) {
                                        Toast.makeText(LoginActivity.this, "邮箱或密码错误", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private boolean checkValid(String email, String psw) {
        String emailPattern = "^[0-9a-zA-Z_]{2,16}$";
        String pswPattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(psw)) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
        } else if (!(Pattern.matches(emailPattern, email) && Pattern.matches(pswPattern, psw))) {
            Toast.makeText(LoginActivity.this, "邮箱或密码错误", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void saveUserData(String email, String nickname, int gender, String avatarUrl, String[] info_to_act, int[] id_to_act) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLogin", true);
        editor.putString("email", email);
        editor.putString("nickname", nickname);
        editor.putInt("gender", gender);
        editor.putString("avatarUrl", avatarUrl);
        editor.putInt("num_act_to_start", id_to_act.length);
        for (int i = 0; i < id_to_act.length; i++) {
            editor.putString("act_" + i, info_to_act[i]);
            editor.putInt("id_act_" + i, id_to_act[i]);
            editor.putInt("status_act_" + i, 0); //是否设置了闹钟，0为未设置（需要设置），1为已设置，2为已经完成，-1为已取消，-2为需要取消。
        }
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_REGISTER:
                    String email = data.getStringExtra("email");
                    et_email.setText(email);
                    et_email.setSelection(email.length());
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