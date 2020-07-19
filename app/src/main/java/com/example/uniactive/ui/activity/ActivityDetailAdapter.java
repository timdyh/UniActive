package com.example.uniactive.ui.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.uniactive.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class ActivityDetailAdapter extends BaseAdapter {
    private Context context;
    private List listName;
    private List listComment;
    private List listTime;
    private List Ifself;
    private List Url;

    public ActivityDetailAdapter(Context context, List listComment, List listName, List listTime, List IfSelf, List Url) {
        this.context = context;
        this.listName = listName;
        this.listComment = listComment;
        this.listTime = listTime;
        this.Ifself = IfSelf;
        this.Url = Url;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listName.size();
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
        TextView person_name_display = null;
        TextView person_comment_display = null;
        TextView person_comment_time = null;
        TextView reedit = null;
        ImageView person_picture_display = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.personal_comment, null);
        }
        person_name_display = convertView.findViewById(R.id.person_name_display);
        person_comment_display = convertView.findViewById(R.id.person_comment_display);
        person_comment_time = convertView.findViewById(R.id.person_comment_time);
        reedit = convertView.findViewById(R.id.comments_reedit);
        person_picture_display = convertView.findViewById(R.id.person_picture_display);
        TextView bottom_line = convertView.findViewById(R.id.bottom_line_comment);

        person_comment_display.setText((String)listComment.get(position));
        person_name_display.setText((String)listName.get(position));
        person_comment_time.setText((String)listTime.get(position));
        if ((int)Ifself.get(position) == 0) {
            reedit.setVisibility(View.GONE);
        }
        else {
            reedit.setVisibility(View.VISIBLE);
        }
        String avatarUrl = (String)Url.get(position);
        if (!avatarUrl.isEmpty()) {
            Glide.with(convertView)
                    .load(avatarUrl)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(person_picture_display);
        }
        else {
            Glide.with(convertView)
                    .load(R.drawable.avatar)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(person_picture_display);
        }
        bottom_line.setVisibility(View.VISIBLE);
        return convertView;
    }
}
