package com.example.uniactive.ui.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.uniactive.MainActivity;
import com.example.uniactive.R;
import com.example.uniactive.ui.home.SettingActivity;
import com.example.uniactive.util.JSONGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CommentActivity extends AppCompatActivity {

    private Button confirm;
    private TextView delete;
    private EditText comments;
    private RatingBar rate;
    private int act_id, type;
    private boolean edit, IfDetail;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_add);
        comments = findViewById(R.id.comments);
        confirm = findViewById(R.id.comments_confirm);
        delete = findViewById(R.id.comments_delete);
        rate = findViewById(R.id.ratingBar);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("加入讨论");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        act_id = intent.getIntExtra("act_id", 0);
        type = intent.getIntExtra("pageId", -1);
        edit = intent.getBooleanExtra("EditComment", false);
        IfDetail = intent.getBooleanExtra("IfDetail", false);
        System.out.println(intent.getBooleanExtra("EditComment", false));
        if (type == 1) {
            toolbar.setTitle("输入评价");
            comments.setHint("请输入你对活动的评价");
            int score = intent.getIntExtra("score", -2);
            if (score != -1) {
                rate.setRating(score);
                rate.setEnabled(false);
            }
        }
        else if (edit) {
            rate.setVisibility(View.GONE);
            toolbar.setTitle("修改评论");
            comments.setText(intent.getStringExtra("Comment"));
            delete.setVisibility(View.VISIBLE);
        }
        else {
            comments.setHint("请输入你的评论");
            rate.setVisibility(View.GONE);
        }

        sp = CommentActivity.this.getSharedPreferences("userData", MODE_PRIVATE);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    if (comments.getText().toString().equals("") && rate.getRating() == 0.0) {
                        Toast.makeText(CommentActivity.this, "请输入你对活动的评价并进行评分。", Toast.LENGTH_SHORT).show();
                    } else if (comments.getText().toString().equals("")) {
                        Toast.makeText(CommentActivity.this, "请输入你对活动的评价。", Toast.LENGTH_SHORT).show();
                    } else if (rate.getRating() == 0) {
                        Toast.makeText(CommentActivity.this, "请进行评分。", Toast.LENGTH_SHORT).show();
                    } else if (comments.getText().toString().length() < 10) {
                        Toast.makeText(CommentActivity.this, "输入评价长度过短（必须不少于10个字符）", Toast.LENGTH_SHORT).show();
                    }
                    else if (comments.getText().toString().length() > 90) {
                        Toast.makeText(CommentActivity.this, "输入评价过长（必须不多于90个字符）", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AddComments();
                    }
                }
                else if (edit) {
                    if (comments.getText().toString().equals("")) {
                        Toast.makeText(CommentActivity.this, "请输入您的评论。", Toast.LENGTH_SHORT).show();
                    }
                    else if (comments.getText().toString().length() < 10) {
                        Toast.makeText(CommentActivity.this, "输入评论长度过短（必须不少于10个字符）", Toast.LENGTH_SHORT).show();
                    }
                    else if (comments.getText().toString().length() > 90) {
                        Toast.makeText(CommentActivity.this, "输入评论过长（必须不多于90个字符）", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        EditDiscussion(intent.getIntExtra("DiscId", -1), intent.getStringExtra("Comment"));
                    }
                }
                else {
                    if (comments.getText().toString().equals("")) {
                        Toast.makeText(CommentActivity.this, "请输入您的评论。", Toast.LENGTH_SHORT).show();
                    }
                    else if (comments.getText().toString().length() < 10) {
                        Toast.makeText(CommentActivity.this, "输入评论长度过短（必须不少于10个字符）", Toast.LENGTH_SHORT).show();
                    }
                    else if (comments.getText().toString().length() > 90) {
                        Toast.makeText(CommentActivity.this, "输入评论过长（必须不多于90个字符）", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AddDiscussion();
                    }
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                builder.setTitle("提示");
                builder.setMessage("删除这条评论？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONGenerator.delDiscuss(intent.getIntExtra("DiscId", -1), getApplicationContext(), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 1) {
                                        Toast.makeText(CommentActivity.this, "删除评论成功！", Toast.LENGTH_SHORT).show();
                                        Intent data = new Intent();
                                        setResult(RESULT_OK, data);
                                        finish();
                                    } else if (status == 0) {
                                        Toast.makeText(CommentActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CommentActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void AddComments() {
        String email = sp.getString("email", "");
        int score = (int)rate.getRating();
        String com = comments.getText().toString().trim();
        JSONGenerator.comment(email, act_id, score, com, getApplicationContext(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 1) {
                                Toast.makeText(CommentActivity.this, "评价成功！", Toast.LENGTH_SHORT).show();
                                Intent data = new Intent();
                                data.putExtra("score", score);
                                data.putExtra("com", com);
                                setResult(RESULT_OK, data);
                                finish();
                            } else if (status == 0) {
                                Toast.makeText(CommentActivity.this, "评价失败，请重试", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CommentActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CommentActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AddDiscussion() {
        String email = sp.getString("email", "");
        String com = comments.getText().toString().trim();
        JSONGenerator.addDiscuss(email, act_id, com, getApplicationContext(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 1) {
                                Toast.makeText(CommentActivity.this, "添加评论成功！", Toast.LENGTH_SHORT).show();
                                Intent data = new Intent();
                                setResult(RESULT_OK, data);
                                finish();
                            } else if (status == 0) {
                                Toast.makeText(CommentActivity.this, "评论失败，请重试", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CommentActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CommentActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void EditDiscussion(int disc_id, String comment) {
        String com = comments.getText().toString().trim();
        if (!com.equals(comment)) {
            JSONGenerator.modifyDiscuss(disc_id, com, getApplicationContext(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                            Toast.makeText(CommentActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            Intent data = new Intent();
                            setResult(RESULT_OK, data);
                            finish();
                        } else {
                            Toast.makeText(CommentActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CommentActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(CommentActivity.this, "您并未做出任何修改", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        JSONGenerator.cancelAllRequests();
        super.onStop();
    }
}
