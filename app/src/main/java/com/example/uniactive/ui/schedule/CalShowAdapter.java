package com.example.uniactive.ui.schedule;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.uniactive.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalShowAdapter extends BaseAdapter {

    private Context context;
    private List listCard;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public CalShowAdapter(Context context, List listCard) {
        this.context = context;
        this.listCard = listCard;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listCard.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        TextView activity_name = null;
        TextView activity_place = null;
        TextView activity_time = null;
        ImageView activity_picture = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_day, null);
        }
        activity_name = convertView.findViewById(R.id.activity_name);
        activity_place = convertView.findViewById(R.id.activity_place);
        activity_time = convertView.findViewById(R.id.activity_time);
        TextView bottom_line = convertView.findViewById(R.id.bottom_line);
        activity_picture = convertView.findViewById(R.id.activity_picture);

        Calendar start = Calendar.getInstance(Locale.CHINA);
        Calendar end = Calendar.getInstance(Locale.CHINA);
        start.setTimeInMillis(((ActInfo)listCard.get(position)).getStartTime());
        end.setTimeInMillis(((ActInfo)listCard.get(position)).getEndTime());
        String startDate = dateFormat.format(start.getTime());
        String startTime = timeFormat.format(start.getTime());
        String endDate = dateFormat.format(end.getTime());
        String endTime = timeFormat.format(end.getTime());
        String res = startDate + " " + startTime + "--" + endDate + " " + endTime;
        activity_name.setText(((ActInfo)listCard.get(position)).getActName());
        activity_place.setText("活动地点：" + ((ActInfo)listCard.get(position)).getPlace());
        activity_time.setText(res);
        bottom_line.setVisibility(View.VISIBLE);

        String actImageUrl = ((ActInfo)listCard.get(position)).getActImageUrl();
        if (!actImageUrl.isEmpty()) {
            Glide.with(context)
                    .load(actImageUrl)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(activity_picture);
        }

        return convertView;
    }

}
