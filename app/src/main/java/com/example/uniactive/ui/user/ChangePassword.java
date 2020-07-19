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
import com.example.uniactive.R;
import com.example.uniactive.util.JSONGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChangePassword extends AppCompatActivity {
    private EditText old_psw;
    private EditText new_psw1;
    private EditText new_psw2;
    private Button changepsw;

    private SharedPreferences sp;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("userData", MODE_PRIVATE);
        email = sp.getString("email", "null");

        old_psw = findViewById(R.id.old_psw);
        new_psw1 = findViewById(R.id.new_psw1);
        new_psw2 = findViewById(R.id.new_psw2);
        changepsw = findViewById(R.id.changepsw);

        changepsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpsw = old_psw.getText().toString();
                String newpsw1 = new_psw1.getText().toString().trim();
                String newpsw2 = new_psw2.getText().toString().trim();

                boolean isValid = checkValid(oldpsw, newpsw1, newpsw2);
                if (!isValid) return;

                JSONGenerator.changepsw(email, oldpsw, newpsw1, getApplicationContext(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 1) {
                                        Toast.makeText(ChangePassword.this, "修改成功", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.clear();
                                        editor.apply();
                                        Intent data = new Intent();
                                        setResult(RESULT_OK, data);
                                        finish();
                                        Intent intent = new Intent(ChangePassword.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(ChangePassword.this, "修改失败，请检查输入是否有误", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ChangePassword.this, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private boolean checkValid(String psw, String psw1, String psw2) {
        String pswPattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
        if (TextUtils.isEmpty(psw)) {
            Toast.makeText(ChangePassword.this, "请输入原始密码", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(psw1)) {
            Toast.makeText(ChangePassword.this, "请输入新密码", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(psw2)) {
            Toast.makeText(ChangePassword.this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
        } else if (!Pattern.matches(pswPattern, psw1)) {
            Toast.makeText(ChangePassword.this, "密码必须为8-16位字母和数字的组合，且不能是纯字母或纯数字", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.equals(psw1, psw2)) {
            Toast.makeText(ChangePassword.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
        }  else {
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
}
