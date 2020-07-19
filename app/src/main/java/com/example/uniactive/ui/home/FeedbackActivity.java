package com.example.uniactive.ui.home;

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
import com.example.uniactive.ui.user.ProfileEditActivity;
import com.example.uniactive.util.JSONGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedback_info;
    private Button feedback_submit;

    private SharedPreferences sp;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("userData", MODE_PRIVATE);
        email = sp.getString("email", "***");

        feedback_info = findViewById(R.id.feedback_info);
        feedback_submit = findViewById(R.id.feedback_submit);

        feedback_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = feedback_info.getText().toString().trim();
                if (TextUtils.isEmpty(info)) {
                    Toast.makeText(FeedbackActivity.this, "请输入反馈内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONGenerator.feedback(email, info,
                        getApplicationContext(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 1) {
                                        Toast.makeText(FeedbackActivity.this, "反馈成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(FeedbackActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(FeedbackActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
