package com.example.uniactive.ui.activity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.uniactive.R;

import java.util.List;

public class ActShowAdapter extends BaseAdapter {

    private Context context;
    private List listString;
    private List listName;
    private List listTime;

    public ActShowAdapter(Context context, List listString, List listName, List listTime) {
        this.context = context;
        this.listString = listString;
        this.listName = listName;
        this.listTime = listTime;
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
        TextView personal_discussion = null;
        TextView discussion_info = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.personal_discussion, null);
        }
        personal_discussion = convertView.findViewById(R.id.person_discussion);
        discussion_info = convertView.findViewById(R.id.discuss_info);
        personal_discussion.setText((String)listString.get(position));
        discussion_info.setText((String)(listName.get(position) + " " + listTime.get(position)));
        return convertView;
    }

}

