package com.example.uniactive.ui.plaza;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.uniactive.R;
import com.example.uniactive.ui.activity.ActEditActivity;
import com.example.uniactive.ui.activity.ActListDisplayActivity;
import com.example.uniactive.ui.alarm.SendNotificationActivity;
import com.example.uniactive.ui.user.LoginActivity;
import com.example.uniactive.util.LabelManager;
import com.google.android.material.tabs.TabLayout;

import static com.example.uniactive.ui.activity.ActListDisplayActivity.*;
import static com.example.uniactive.ui.home.HomeFragment.REQUEST_LOGIN;

public class PlazaFragment extends Fragment {

    private SearchView searchView;
    private ImageView iv_add;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_plaza, container, false);
        searchView = root.findViewById(R.id.view_search);
        iv_add = root.findViewById(R.id.iv_add);
        tabLayout = root.findViewById(R.id.tab_layout);
        viewPager = root.findViewById(R.id.view_pager);

        sp = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        boolean isLogin = sp.getBoolean("isLogin", false);

        searchView.onActionViewExpanded();
        searchView.clearFocus();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String keyword = searchView.getQuery().toString();
                Intent intent = new Intent(getActivity(), ActListDisplayActivity.class);
                intent.putExtra("pageId", SEARCH);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLogin = sp.getBoolean("isLogin", false);
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), ActEditActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    //startActivityForResult(intent, REQUEST_LOGIN);
                }
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ActListDisplayFragment());
        List<String> labels = LabelManager.getLabels();
        for (String label : labels) {
            fragments.add(new ActListDisplayFragment(label));
        }

        List<String> titles = new ArrayList<>();
        titles.add("推荐");
        titles.addAll(labels);

        LabelPagerAdapter adapter = new LabelPagerAdapter(getChildFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}