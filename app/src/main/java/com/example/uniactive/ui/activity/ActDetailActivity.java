package com.example.uniactive.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.uniactive.R;
import com.example.uniactive.ui.alarm.SendNotificationActivity;
import com.example.uniactive.ui.home.HomeFragment;
import com.example.uniactive.ui.user.CommentActivity;
import com.example.uniactive.ui.schedule.CalListView;
import com.example.uniactive.ui.user.LoginActivity;
import com.example.uniactive.util.JSONGenerator;
import com.example.uniactive.util.LabelManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActDetailActivity extends AppCompatActivity {

    private TextView name, starttime, endtime, capacity, label, introduction, tv_place, tv_no_comment, sign, tv_holder_name;
    private LinearLayout rl_reject;
    private TextView reject_view;
    private String reject;
    private int act_status;
    private int act_id, max_num, count;
    private String ActivityName, intro, place, holderEmail, holderName;
    private ArrayList<Integer> actLabel;
    private long start_time, end_time;
    private String actImageUrl, holderImageUrl;
    private CalListView listView;
    private ActivityDetailAdapter MyActShowAdapter;
    private Button BeforePage, AfterPage, PointPage, AddComments, ExtendComment;
    private EditText PointToPage;
    private SharedPreferences sp;
    private int defaultNum = 0;
    private boolean IfExtend = false;
    private int MAXN = 10;
    private String email;
    private ImageView picture_activity, iv_collect, iv_holder_image;
    private int hasCollected = 0;

    public static final int REQUEST_COMMENT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimen);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        act_id = intent.getIntExtra("act_id", 0);
        ActivityName = intent.getStringExtra("name");
        intro = intent.getStringExtra("intro");
        place = intent.getStringExtra("place");
        holderEmail = intent.getStringExtra("holderEmail");
        actImageUrl = intent.getStringExtra("actImageUrl");
        max_num = intent.getIntExtra("max_num", 0);
        reject = intent.getStringExtra("reject");
        act_status = intent.getIntExtra("act_status", 0);
        start_time = intent.getLongExtra("start_time", 0);
        end_time = intent.getLongExtra("end_time", 0);
        count = intent.getIntExtra("count", -1);
        actLabel = intent.getIntegerArrayListExtra("actLabelIndexes");
        holderName = intent.getStringExtra("holderName");
        holderImageUrl = intent.getStringExtra("holderImageUrl");
        sp = ActDetailActivity.this.getSharedPreferences("userData", MODE_PRIVATE);

        name = findViewById(R.id.name_activity);
        starttime = findViewById(R.id.start_time_activity);
        endtime = findViewById(R.id.end_time_activity);
        capacity = findViewById(R.id.capacity);
        label = findViewById(R.id.label);
        introduction = findViewById(R.id.introduction);
        tv_place = findViewById(R.id.place_activity);
        BeforePage = findViewById(R.id.PageBefore);
        AfterPage = findViewById(R.id.PageAfter);
        PointPage = findViewById(R.id.PagePoint);
        PointToPage = findViewById(R.id.PageEdit);
        AddComments = findViewById(R.id.AddComments);
        ExtendComment = findViewById(R.id.extend_comment);
        listView = findViewById(R.id.Comments);
        sign = findViewById(R.id.ActivitySign);
        picture_activity = findViewById(R.id.picture_activity);
        tv_no_comment = findViewById(R.id.tv_no_comment);
        iv_collect = findViewById(R.id.iv_collect);
        iv_holder_image = findViewById(R.id.iv_holder_image);
        tv_holder_name = findViewById(R.id.tv_holder_name);
        rl_reject = findViewById(R.id.rl_reject);
        reject_view = findViewById(R.id.reject);

        email = sp.getString("email", "");

        FindStatus(); //查看用户和活动的关系。
        init(); //添加活动信息，判断是否可以报名。
        DealWithDiscussion(); //展开收起讨论

        if (!holderEmail.equals(email) || act_status == 0 || act_status == 1) {
            rl_reject.setVisibility(View.GONE);
            reject_view.setVisibility(View.GONE);
        } else {
            rl_reject.setVisibility(View.VISIBLE);
            reject_view.setVisibility(View.VISIBLE);
            reject_view.setText(reject);
        }

        iv_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                boolean isLogin = sp.getBoolean("isLogin", false);
                if (!isLogin) {
                    Toast.makeText(ActDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                email = sp.getString("email", "");
                if (hasCollected == 1) {
                    JSONGenerator.cancelFav(email, act_id, getApplicationContext(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 1) {
                                        Toast.makeText(ActDetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                                        iv_collect.setBackgroundResource(R.drawable.star_gray);
                                        hasCollected = 0;
                                        Intent data = new Intent();
                                        setResult(RESULT_OK, data);
                                    } else {
                                        Toast.makeText(ActDetailActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ActDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    JSONGenerator.addFav(email, act_id, getApplicationContext(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 1) {
                                        Toast.makeText(ActDetailActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
                                        iv_collect.setBackgroundResource(R.drawable.star_yellow);
                                        hasCollected = 1;
                                        Intent data = new Intent();
                                        setResult(RESULT_OK, data);
                                    } else {
                                        Toast.makeText(ActDetailActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ActDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });

        AddComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActDetailActivity.this, CommentActivity.class);
                intent.putExtra("act_id", act_id);
                intent.putExtra("type", 1);
                intent.putExtra("IfDetail", true);
                startActivityForResult(intent, REQUEST_COMMENT);
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLogin = sp.getBoolean("isLogin", false);
                if (!isLogin) {
                    Toast.makeText(ActDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                String email = sp.getString("email", "");
                if (sign.getText().equals("退出")) {
                    if (!CheckIsClickable(start_time)) {
                        Toast.makeText(ActDetailActivity.this, "活动已经开始，无法退出", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONGenerator.quit(email, act_id, getApplicationContext(),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int status = response.getInt("status");
                                        if (status == 1) {
                                            Toast.makeText(ActDetailActivity.this, "退出成功！", Toast.LENGTH_SHORT).show();
                                            sign.setText("报名");
                                            capacity.setText(String.valueOf(--count) + " / " + String.valueOf(max_num));
                                            DeleteActivity();
                                            Intent data = new Intent();
                                            data.putExtra("now_time", start_time);
                                            setResult(RESULT_OK, data);
                                            // finish();
                                        } else if (status == 0) {
                                            Toast.makeText(ActDetailActivity.this, "退出失败，请重试或者联系管理员！", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ActDetailActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(ActDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else if (sign.getText().equals("报名")) {
                    if (count == max_num) {
                        Toast.makeText(ActDetailActivity.this, "人数已满，无法报名。", Toast.LENGTH_SHORT).show();
                    }
                    else if (!CheckIsClickable(start_time)) {
                        Toast.makeText(ActDetailActivity.this, "活动已经开始，无法报名。", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        JSONGenerator.join(email, act_id, getApplicationContext(),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            int status = response.getInt("status");
                                            if (status == 1) {
                                                Toast.makeText(ActDetailActivity.this, "报名成功！", Toast.LENGTH_SHORT).show();
                                                sign.setText("退出");
                                                capacity.setText(String.valueOf(++count) + " / " + String.valueOf(max_num));
                                                AddActivity();
                                                Intent data = new Intent();
                                                data.putExtra("now_time", start_time);
                                                setResult(RESULT_OK, data);
                                                // finish();
                                            } else if (status == 0) {
                                                Toast.makeText(ActDetailActivity.this, "时间冲突，无法报名", Toast.LENGTH_SHORT).show();
                                            } else if (status == 2) {
                                                Toast.makeText(ActDetailActivity.this, "人数已满，无法报名", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(ActDetailActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(ActDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
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

    private void FindStatus() {
        JSONGenerator.if_user_join(email, act_id, getApplicationContext(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 1) {
                                sign.setText("报名");
                            }
                            else if (status == 0) {
                                sign.setText("退出");
                            }
                            else {
                                Toast.makeText(ActDetailActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                            }
                            hasCollected = response.getInt("if_fav");
                            if (hasCollected == 1) {
                                iv_collect.setBackgroundResource(R.drawable.star_yellow);
                            } else if (hasCollected == 0){
                                iv_collect.setBackgroundResource(R.drawable.star_gray);
                            } else {
                                Toast.makeText(ActDetailActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ActDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        name.setText(ActivityName);
        starttime.setText(GetT(start_time));
        endtime.setText(GetT(end_time));
        tv_place.setText(place);
        String s = "";
        for (int i = 0; i < actLabel.size(); i++) {
            s += LabelManager.getLabels().get(actLabel.get(i));
            if (i < actLabel.size() - 1) {
                s += " ";
            }
        }
        label.setText(s);
        capacity.setText(String.valueOf(count) + " / " + String.valueOf(max_num));
        introduction.setText(intro);
        if (!actImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(actImageUrl)
                    .into(picture_activity);
        }

        tv_holder_name.setText(holderName);
        if (!holderImageUrl.isEmpty()) {
            Glide.with(ActDetailActivity.this)
                    .load(holderImageUrl)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(iv_holder_image);
        }
    }

    private void DealWithDiscussion() {
        ExtendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExtendComment.getText().equals("展开讨论区")) {
                    DisplayComment();
                }
                else if (ExtendComment.getText().equals("收起讨论区")) {
                    IfExtend = false;
                    listView.setVisibility(View.INVISIBLE);
                    tv_no_comment.setVisibility(View.GONE);
                    BeforePage.setVisibility(View.INVISIBLE);
                    AfterPage.setVisibility(View.INVISIBLE);
                    PointPage.setVisibility(View.INVISIBLE);
                    PointToPage.setVisibility(View.INVISIBLE);
                    AddComments.setVisibility(View.INVISIBLE);
                    ExtendComment.setText("展开讨论区");
                }
            }
        });
    }

    private void DisplayComment() {
        IfExtend = true;
        defaultNum = 0;
        GetComments(new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<String> resultComments, ArrayList<String> resultName, ArrayList<String> resultTime, ArrayList<Integer> resultIfSelf, ArrayList<Integer> resultDiscId, ArrayList<String> resultUrl) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (resultIfSelf.get(position + defaultNum * MAXN) == 1) {
                            Intent intent = new Intent(ActDetailActivity.this, CommentActivity.class);
                            intent.putExtra("EditComment", true);
                            intent.putExtra("Comment", resultComments.get(position + defaultNum * MAXN));
                            intent.putExtra("DiscId", resultDiscId.get(position + defaultNum * MAXN));
                            intent.putExtra("IfDetail", true);
                            startActivityForResult(intent, REQUEST_COMMENT);
                        }
                    }
                });

                BeforePage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defaultNum--;
                        DealwithDiscussionButton(resultComments, resultName, resultTime, resultIfSelf, resultUrl);
                    }
                });

                PointPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PointToPage.getText().toString().equals("")) {
                            Toast.makeText(ActDetailActivity.this, "您正处于第" + String.valueOf(defaultNum + 1) + "页。", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (Integer.parseInt(PointToPage.getText().toString()) <= 0 || (Integer.parseInt(PointToPage.getText().toString()) - 1) * MAXN >= resultComments.size()) {
                            Toast.makeText(ActDetailActivity.this, "无该选择页面。", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (Integer.parseInt(PointToPage.getText().toString()) == defaultNum + 1) {
                            Toast.makeText(ActDetailActivity.this, "您正处于第" + String.valueOf(defaultNum + 1) + "页。", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        defaultNum = Integer.parseInt(PointToPage.getText().toString()) - 1;
                        DealwithDiscussionButton(resultComments, resultName, resultTime, resultIfSelf, resultUrl);
                    }
                });

                AfterPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defaultNum++;
                        DealwithDiscussionButton(resultComments, resultName, resultTime, resultIfSelf, resultUrl);
                    }
                });
            }
        });
        ExtendComment.setText("收起讨论区");
    }

    private void GetComments(final VolleyCallback callback) {
        JSONGenerator.queryDiscuss(act_id, getApplicationContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("result");
                    ArrayList<String> Comments = new ArrayList<>();
                    ArrayList<String> Names = new ArrayList<>();
                    ArrayList<String> Times = new ArrayList<>();
                    ArrayList<Integer> IfSelf = new ArrayList<>();
                    ArrayList<Integer> DiscId = new ArrayList<>();
                    ArrayList<String> Url = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String publisher = (String)jsonArray.getJSONObject(i).get("publisher");
                        Comments.add((String)jsonArray.getJSONObject(i).get("content"));
                        Names.add(publisher);
                        String s = (String)jsonArray.getJSONObject(i).get("post_time");
                        Times.add(s);
                        DiscId.add((int)jsonArray.getJSONObject(i).get("disc_id"));
                        Url.add((String)jsonArray.getJSONObject(i).get("img"));
                        if (publisher.equals(email)) {
                            IfSelf.add(1);
                        }
                        else {
                            IfSelf.add(0);
                        }
                    }
                    DealwithDiscussionButton(Comments, Names, Times, IfSelf, Url);
                    callback.onSuccess(Comments, Names, Times, IfSelf, DiscId, Url);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DealwithDiscussionButton(ArrayList<String> Comments, ArrayList<String> Names, ArrayList<String> Times, ArrayList<Integer> IfSelf, ArrayList<String> Url) {
        ArrayList<String> Comments1 = GetSubComments(Comments);
        ArrayList<String> Names1 = GetSubName(Names);
        ArrayList<String> Times1 = GetSubTime(Times);
        ArrayList<Integer> IfSelf1 = GetSubSelf(IfSelf);
        ArrayList<String> Url1 = GetSubUrl(Url);
        if (Comments.size() == 0) {
            tv_no_comment.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            BeforePage.setVisibility(View.INVISIBLE);
            AfterPage.setVisibility(View.INVISIBLE);
            PointPage.setVisibility(View.INVISIBLE);
            PointToPage.setVisibility(View.INVISIBLE);
            AddComments.setVisibility(View.INVISIBLE);
        }
        else {
            tv_no_comment.setVisibility(View.GONE);
            if (IfExtend) {
                MyActShowAdapter = new ActivityDetailAdapter(ActDetailActivity.this, Comments1, Names1, Times1, IfSelf1, Url1);
                listView.setAdapter(MyActShowAdapter);
                listView.setVisibility(View.VISIBLE);
                BeforePage.setVisibility(View.VISIBLE);
                AfterPage.setVisibility(View.VISIBLE);
                PointPage.setVisibility(View.VISIBLE);
                PointToPage.setVisibility(View.VISIBLE);
                PointToPage.setText("");
                PointToPage.setHint(String.valueOf(defaultNum + 1));
            }
        }

        if (IfExtend) {
            AddComments.setVisibility(View.VISIBLE);
        }
        if (defaultNum == 0) {
            BeforePage.setEnabled(false);
        }
        else {
            BeforePage.setEnabled(true);
        }

        if ((defaultNum + 1) * MAXN >= Comments.size()) {
            AfterPage.setEnabled(false);
        }
        else {
            AfterPage.setEnabled(true);
        }
    }

    private ArrayList<String> GetSubComments(ArrayList<String> Comments) {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = MAXN * defaultNum; i < Math.min(Comments.size(), MAXN * (defaultNum + 1)); i++) {
            arr.add(Comments.get(i));
        }
        return arr;
    }

    private ArrayList<String> GetSubName(ArrayList<String> Names) {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = MAXN * defaultNum; i < Math.min(Names.size(), MAXN * (defaultNum + 1)); i++) {
            arr.add(Names.get(i));
        }
        return arr;
    }

    private ArrayList<String> GetSubTime(ArrayList<String> Times) {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = MAXN * defaultNum; i < Math.min(Times.size(), MAXN * (defaultNum + 1)); i++) {
            arr.add(Times.get(i));
        }
        return arr;
    }

    private ArrayList<Integer> GetSubSelf(ArrayList<Integer> IfSelf) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = MAXN * defaultNum; i < Math.min(IfSelf.size(), MAXN * (defaultNum + 1)); i++) {
            arr.add(IfSelf.get(i));
        }
        return arr;
    }

    private ArrayList<String> GetSubUrl(ArrayList<String> Url) {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = MAXN * defaultNum; i < Math.min(Url.size(), MAXN * (defaultNum + 1)); i++) {
            arr.add(Url.get(i));
        }
        return arr;
    }

    private String GetT(long start) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar s = Calendar.getInstance();
        s.setTimeInMillis(start);
        String startDate = dateFormat.format(s.getTime());
        String startTime = timeFormat.format(s.getTime());
        return startDate + " " + startTime;
    }

    private boolean CheckIsClickable(long s) {
        Calendar c = Calendar.getInstance();
        Date cur = c.getTime();
        return s > cur.getTime();
    }

    private void AddActivity() {
        int length = sp.getInt("num_act_to_start", -1);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("act_" + length, String.valueOf(start_time) + " " + ActivityName);
        editor.putInt("id_act_" + length, act_id);
        editor.putInt("num_act_to_start", length + 1);
        editor.putInt("status_act_" + length, 0);
        editor.apply();
        if (HomeFragment.instance != null) {
            Intent ser = new Intent(HomeFragment.instance.getActivity(), SendNotificationActivity.class);
            HomeFragment.instance.getActivity().startService(ser);
        }
    }

    private void DeleteActivity() {
        int length = sp.getInt("num_act_to_start", -1);
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < length; i++) {
            int id = sp.getInt("id_act_" + i, -1);
            if (id == act_id) {
                editor.putInt("status_act_" + i, -2);
                break;
            }
        }
        editor.apply();
        if (HomeFragment.instance != null) {
            Intent ser = new Intent(HomeFragment.instance.getActivity(), SendNotificationActivity.class);
            HomeFragment.instance.getActivity().startService(ser);
        }
    }

    public interface VolleyCallback {
        void onSuccess(ArrayList<String> resultComments, ArrayList<String> resultName, ArrayList<String> resultTime, ArrayList<Integer> IfSelf, ArrayList<Integer> DiscId, ArrayList<String> Url);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COMMENT && resultCode == RESULT_OK) {
            DisplayComment();
        }
    }
}
