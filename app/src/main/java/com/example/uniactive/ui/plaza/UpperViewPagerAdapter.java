package com.example.uniactive.ui.plaza;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class UpperViewPagerAdapter extends PagerAdapter {
    private List<View> datas;

    public UpperViewPagerAdapter(List<View> list) {
        datas=list;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view=datas.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(datas.get(position));
    }
}
