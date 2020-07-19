package com.example.uniactive.ui.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.uniactive.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CommentCardAdapter extends RecyclerView.Adapter<CommentCardAdapter.ViewHolder>{

    private Context context;
    private List<CommentCard> actCardList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView comment_name;
        RatingBar comment_star;
        TextView comment_com;
        TextView comment_time;
        ImageView comment_picture;


        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            comment_name = view.findViewById(R.id.commenter_name);
            comment_star = view.findViewById(R.id.commenter_stars);
            comment_com = view.findViewById(R.id.commenter_com);
            comment_time = view.findViewById(R.id.commenter_time);
            comment_picture = view.findViewById(R.id.commenter_image);
        }
    }

    public CommentCardAdapter(List<CommentCard> CommentList) {
        actCardList = CommentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_comments, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommentCard act = actCardList.get(position);
        String time = act.getCom_Time();
        holder.comment_time.setText(time);
        holder.comment_com.setText(act.getComment());
        holder.comment_star.setRating(act.getRate());
        holder.comment_name.setText(act.getCom_Name());
        if (!act.getUrl().isEmpty()) {
            Glide.with(context)
                    .load(act.getUrl())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(holder.comment_picture);
        }
    }

    @Override
    public int getItemCount() {
        return actCardList.size();
    }
}