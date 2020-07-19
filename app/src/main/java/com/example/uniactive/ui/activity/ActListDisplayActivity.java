package com.example.uniactive.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.uniactive.R;
import com.example.uniactive.util.JSONGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ActListDisplayActivity extends AppCompatActivity {

    private List<ActivityCard> actCardList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private ActivityCardAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout ll_none;

    private SharedPreferences sp;
    private String email;
    private String keyword;

    private int pageId = 0;

    public static final int SEARCH = 4;
    public static final int PARTICIPATE = 1;
    public static final int RELEASE = 2;
    public static final int COLLECT = 3;

    private static final int REQUEST_COMMENT = 101;
    private static final int REQUEST_EDIT = 102;
    private static final int REQUEST_COLLECT = 201;
    private static final int REQUEST_SEARCH = 202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_list_display);

        sp = getSharedPreferences("userData", MODE_PRIVATE);
        email = sp.getString("email", "null");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rv_myrelease);
        swipeRefresh = findViewById(R.id.rv_refresh);
        ll_none = findViewById(R.id.ll_none);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        pageId = intent.getIntExtra("pageId", 0);
        keyword = intent.getStringExtra("keyword");
        switch (pageId) {
            case SEARCH:
                getSupportActionBar().setTitle("搜索结果");
                //String keyword = intent.getStringExtra("keyword");
                JSONGenerator.searchByKeyword(keyword, getApplicationContext(), getListener(), getErrorListener());
                break;
            case PARTICIPATE:
                getSupportActionBar().setTitle("我参与的活动");
                JSONGenerator.queryJoinedActs(email, getApplicationContext(), getListener(), getErrorListener());
                break;
            case RELEASE:
                getSupportActionBar().setTitle("我发布的活动");
                JSONGenerator.queryHeld(email, getApplicationContext(), getListener(), getErrorListener());
                break;
            case COLLECT:
                getSupportActionBar().setTitle("我收藏的活动");
                JSONGenerator.queryFav(email, getApplicationContext(), getListener(), getErrorListener());
                break;
            default:
                break;
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pageId = intent.getIntExtra("pageId", 0);
                                switch (pageId) {
                                    case SEARCH:
                                        getSupportActionBar().setTitle("搜索结果");
                                        String keyword = intent.getStringExtra("keyword");
                                        JSONGenerator.searchByKeyword(keyword, getApplicationContext(), getListener(), getErrorListener());
                                        break;
                                    case PARTICIPATE:
                                        getSupportActionBar().setTitle("我参与的活动");
                                        JSONGenerator.queryJoinedActs(email, getApplicationContext(), getListener(), getErrorListener());
                                        break;
                                    case RELEASE:
                                        getSupportActionBar().setTitle("我发布的活动");
                                        JSONGenerator.queryHeld(email, getApplicationContext(), getListener(), getErrorListener());
                                        break;
                                    case COLLECT:
                                        getSupportActionBar().setTitle("我收藏的活动");
                                        JSONGenerator.queryFav(email, getApplicationContext(), getListener(), getErrorListener());
                                        break;
                                    default:
                                        break;
                                }
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private Response.Listener<JSONObject> getListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        actCardList.clear();
                        JSONArray ja = response.getJSONArray("result");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);
                            int act_id = jo.getInt("act_id");
                            String act_name = jo.getString("name");
                            String holder_email = jo.getString("holder_email");
                            String holder_name = jo.getString("holder");
                            long start_time = jo.getLong("start_time");
                            long end_time = jo.getLong("end_time");
                            String reject = jo.getString("reject");
                            int max_num = jo.getInt("max_num");
                            int count = jo.getInt("count");
                            String intro = jo.getString("introduction");
                            String place = jo.getString("place");
                            String holderImageUrl = jo.getString("img");
                            String actImageUrl = jo.getString("img1");
                            String label1 = jo.getString("label1");
                            String label2 = jo.getString("label2");
                            String label3 = jo.getString("label3");

                            if (pageId == PARTICIPATE) {
                                int score = jo.getInt("score");
                                String comment = jo.getString("comment");
                                actCardList.add(new ActivityCard(act_id, holder_email, holder_name, holderImageUrl,
                                        start_time, end_time, max_num, count, act_name, intro, place, actImageUrl,
                                        label1, label2, label3, score, comment));
                            } else if (pageId == RELEASE) {
                                int act_status = jo.getInt("act_status");
                                actCardList.add(new ActivityCard(act_id, holder_email, holder_name, holderImageUrl,
                                        start_time, end_time, reject, max_num, count, act_name, intro, place, actImageUrl,
                                        label1, label2, label3, act_status));
                            } else {
                                actCardList.add(new ActivityCard(act_id, holder_email, holder_name, holderImageUrl,
                                        start_time, end_time, max_num, count, act_name, intro, place, actImageUrl,
                                        label1, label2, label3));
                            }
                        }
                        if (actCardList.size() == 0) {
                            ll_none.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            ll_none.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter = new ActivityCardAdapter(actCardList, pageId);
                            recyclerView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(ActListDisplayActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActListDisplayActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        };
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_COMMENT:
                    getSupportActionBar().setTitle("我参与的活动");
                    JSONGenerator.queryJoinedActs(email, getApplicationContext(), getListener(), getErrorListener());
                    break;
                case REQUEST_EDIT:
                    getSupportActionBar().setTitle("我发布的活动");
                    JSONGenerator.queryHeld(email, getApplicationContext(), getListener(), getErrorListener());
                    break;
                case REQUEST_COLLECT:
                    getSupportActionBar().setTitle("我收藏的活动");
                    JSONGenerator.queryFav(email, getApplicationContext(), getListener(), getErrorListener());
                    break;
                case REQUEST_SEARCH:
                    getSupportActionBar().setTitle("搜索结果");
                    JSONGenerator.searchByKeyword(keyword, getApplicationContext(), getListener(), getErrorListener());
                    break;
                default:
                    break;
            }
        }
    }
}