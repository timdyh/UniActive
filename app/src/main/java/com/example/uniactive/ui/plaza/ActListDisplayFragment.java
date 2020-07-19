package com.example.uniactive.ui.plaza;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.uniactive.R;
import com.example.uniactive.ui.activity.ActivityCard;
import com.example.uniactive.ui.activity.ActivityCardAdapter;
import com.example.uniactive.util.JSONGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ActListDisplayFragment extends Fragment {

    private static final int REQUEST_DETAIL = 103;

    private String label = "";

    private List<ActivityCard> actCardList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private ActivityCardAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout ll_none;

    private Context context;
    private SharedPreferences sp;

    public ActListDisplayFragment() {

    }

    public ActListDisplayFragment(String label) {
        this.label = label;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_act_list_display, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        swipeRefresh = root.findViewById(R.id.swipe_refresh);
        ll_none = root.findViewById(R.id.ll_none);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sp = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        boolean isLogin = sp.getBoolean("isLogin", false);
        String email = isLogin ? sp.getString("email", "") : "";

        layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        if (label.isEmpty()) {
            JSONGenerator.getAll(email, getActivity().getApplicationContext(), getListener(), getErrorListener());
        } else {
            JSONGenerator.searchByLabel(label, getActivity().getApplicationContext(), getListener(), getErrorListener());
        }
        context = getActivity().getApplicationContext();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean isLogin = sp.getBoolean("isLogin", false);
                                String email = isLogin ? sp.getString("email", "") : "";
                                if (label.isEmpty()) {
                                    JSONGenerator.getAll(email, getActivity().getApplicationContext(), getListener(), getErrorListener());
                                } else {
                                    JSONGenerator.searchByLabel(label, getActivity().getApplicationContext(), getListener(), getErrorListener());
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
                            int max_num = jo.getInt("max_num");
                            int count = jo.getInt("count");
                            String intro = jo.getString("introduction");
                            String place = jo.getString("place");
                            String holderImageUrl = jo.getString("img");
                            String actImageUrl = jo.getString("img1");
                            String label1 = jo.getString("label1");
                            String label2 = jo.getString("label2");
                            String label3 = jo.getString("label3");

                            actCardList.add(new ActivityCard(act_id, holder_email, holder_name, holderImageUrl,
                                    start_time, end_time, max_num, count, act_name, intro, place, actImageUrl,
                                    label1, label2, label3));
                        }
                        if (actCardList.size() == 0) {
                            ll_none.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            ll_none.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter = new ActivityCardAdapter(ActListDisplayFragment.this, actCardList);
                            recyclerView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(getActivity(), "系统错误", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_DETAIL:
                    //Toast.makeText(getActivity(), "详情", Toast.LENGTH_SHORT).show();
                    boolean isLogin = sp.getBoolean("isLogin", false);
                    String email = isLogin ? sp.getString("email", "") : "";
                    if (label.isEmpty()) {
                        JSONGenerator.getAll(email, context, getListener(), getErrorListener());
                    } else {
                        JSONGenerator.searchByLabel(label, context, getListener(), getErrorListener());
                    }
                    adapter.notifyDataSetChanged();
                default:
                    break;
            }
        }
    }
}