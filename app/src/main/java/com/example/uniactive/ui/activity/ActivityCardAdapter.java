package com.example.uniactive.ui.activity;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.uniactive.R;

import com.bumptech.glide.Glide;
import com.example.uniactive.ui.plaza.ActListDisplayFragment;
import com.example.uniactive.ui.user.CommentActivity;
import com.example.uniactive.ui.user.ShowCommentActivity;

import static com.example.uniactive.ui.activity.ActListDisplayActivity.*;

public class ActivityCardAdapter extends RecyclerView.Adapter<ActivityCardAdapter.ViewHolder>{
    
    private Context context;

    private List<ActivityCard> actCardList;

    private int pageId = 0;

    private static final int REQUEST_COMMENT = 101;
    private static final int REQUEST_EDIT = 102;
    private static final int REQUEST_DETAIL = 103;
    private static final int REQUEST_COLLECT = 201;
    private static final int REQUEST_SEARCH = 202;

    private ActListDisplayFragment fragment;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tv_holder_name;
        ImageView iv_holder_image;
        TextView tv_time;
        TextView tv_act_name;
        TextView tv_act_intro;
        ImageView iv_act_image;
        LinearLayout ll_action, ll_action_extra;
        TextView tv_action;
        ImageView iv_action;
        TextView tv_act_status;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            tv_holder_name = view.findViewById(R.id.tv_holder_name);
            iv_holder_image = view.findViewById(R.id.iv_holder_image);
            tv_time = view.findViewById(R.id.tv_time);
            tv_act_name = view.findViewById(R.id.tv_act_name);
            tv_act_intro = view.findViewById(R.id.tv_act_intro);
            iv_act_image = view.findViewById(R.id.iv_act_image);
            ll_action = view.findViewById(R.id.ll_action);
            ll_action_extra = view.findViewById(R.id.ll_action_extra);
            tv_action = view.findViewById(R.id.tv_action);
            iv_action = view.findViewById(R.id.iv_action);
            tv_act_status = view.findViewById(R.id.tv_act_status);
        }
    }

    public ActivityCardAdapter(ActListDisplayFragment fr, List<ActivityCard> ActivityList) {
        fragment = fr;
        actCardList = ActivityList;
    }

    public ActivityCardAdapter(List<ActivityCard> ActivityList, int pageId) {
        actCardList = ActivityList;
        this.pageId = pageId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ActivityCard act = actCardList.get(position);
                Intent intent = new Intent(context, ActDetailActivity.class);
                putExtraData(intent, act);
                switch (pageId) {
                    case 0:
                        fragment.startActivityForResult(intent, REQUEST_DETAIL);
                        break;
                    case PARTICIPATE:
                        ((ActListDisplayActivity)context).startActivityForResult(intent, REQUEST_COMMENT);
                        break;
                    case RELEASE:
                        ((ActListDisplayActivity)context).startActivityForResult(intent, REQUEST_EDIT);
                        break;
                    case COLLECT:
                        ((ActListDisplayActivity)context).startActivityForResult(intent, REQUEST_COLLECT);
                        break;
                    case SEARCH:
                        ((ActListDisplayActivity)context).startActivityForResult(intent, REQUEST_SEARCH);
                        break;
                    default:
                        break;
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ActivityCard act = actCardList.get(position);
        long start = act.getStartTime();
        long end = act.getEndTime();
        long now = new Date().getTime();

        holder.tv_holder_name.setText(act.getHolderName());
        holder.tv_act_name.setText(act.getActName());
        holder.tv_time.setText(getTimeStr(start, end));
        holder.tv_act_intro.setText(act.getActIntro());

        String holderImageUrl = act.getHolderImageUrl();
        if (!holderImageUrl.isEmpty()) {
            Glide.with(context)
                    .load(holderImageUrl)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(holder.iv_holder_image);
        }

        String actImageUrl = act.getActImageUrl();
        if (!actImageUrl.isEmpty()) {
            Glide.with(context)
                    .load(actImageUrl)
                    .into(holder.iv_act_image);
        }

        switch (pageId) {
            case PARTICIPATE:
                holder.ll_action.setVisibility(View.VISIBLE);
                if (act.getScore() < 0) {
                    if (now < start) {
                        holder.tv_action.setText("未开始");
                        holder.tv_action.setTextColor(context.getColor(R.color.gray));
                        holder.iv_action.setBackgroundResource(R.drawable.time);
                    } else if (now < end) {
                        holder.tv_action.setText("进行中");
                        holder.tv_action.setTextColor(context.getColor(R.color.blue));
                        holder.iv_action.setBackgroundResource(R.drawable.start);
                    } else {
                        holder.tv_action.setText("去评价");
                        holder.tv_action.setTextColor(context.getColor(R.color.orange));
                        holder.iv_action.setBackgroundResource(R.drawable.edit);
                        holder.ll_action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, CommentActivity.class);
                                putExtraData(intent, act);
                                ((ActListDisplayActivity)context).startActivityForResult(intent, REQUEST_COMMENT);
                                //context.startActivity(intent);
                            }
                        });
                    }
                } else {
                    holder.tv_action.setText("已评价");
                    holder.tv_action.setTextColor(context.getColor(R.color.green));
                    holder.iv_action.setBackgroundResource(R.drawable.finish);
                    holder.ll_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ShowCommentActivity.class);
                            putExtraData(intent, act);
                            context.startActivity(intent);
                        }
                    });
                }
                break;
            case RELEASE:
                holder.ll_action.setVisibility(View.VISIBLE);
                holder.tv_action.setText("编辑");
                holder.ll_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        ActivityCard act = actCardList.get(position);
                        if (now < start) {
                            Intent intent = new Intent(context, ActEditActivity.class);
                            intent = putExtraData(intent, act);
                            ((ActListDisplayActivity)context).startActivityForResult(intent, REQUEST_EDIT);
                            //context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "活动开始后不可编辑", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.tv_act_status.setVisibility(View.VISIBLE);
                int act_status = act.getActStatus();
                if (act_status == 0) {
                    holder.tv_act_status.setText("审核中");
                } else if (act_status == 1) {
                    holder.tv_act_status.setText("已通过");
                    holder.tv_act_status.setTextColor(context.getColor(R.color.green));
                } else {
                    holder.tv_act_status.setText("未通过");
                    holder.tv_act_status.setTextColor(context.getColor(R.color.red));
                }

                holder.ll_action_extra.setVisibility(View.VISIBLE);
                holder.ll_action_extra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ComListDisplay.class);
                        intent.putExtra("act_id", act.getActId());
                        context.startActivity(intent);
                    }
                });
                break;
            default:
                break;
        }
    }

    private Intent putExtraData(Intent intent, ActivityCard act) {
        intent.putExtra("act_id", act.getActId());
        intent.putExtra("name", act.getActName());
        intent.putExtra("intro", act.getActIntro());
        intent.putExtra("place", act.getActPlace());
        intent.putExtra("start_time", act.getStartTime());
        intent.putExtra("end_time", act.getEndTime());
        intent.putExtra("reject", act.getReject());
        intent.putExtra("max_num", act.getMaxNum());
        intent.putExtra("count", act.getCount());
        intent.putExtra("score", act.getScore());
        intent.putExtra("comment", act.getComment());
        intent.putExtra("act_status", act.getActStatus());
        intent.putExtra("holderEmail", act.getHolderEmail());
        intent.putExtra("holderName", act.getHolderName());
        intent.putExtra("holderImageUrl", act.getHolderImageUrl());
        intent.putExtra("actImageUrl", act.getActImageUrl());
        intent.putIntegerArrayListExtra("actLabelIndexes", act.getActLabelIndexes());
        intent.putExtra("pageId", pageId);
        return intent;
    }

    private String getTimeStr(long start, long end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String start_time = dateFormat.format(new Date(start));
        // String end_time = dateFormat.format(new Date(end));
        return start_time;
    }

    @Override
    public int getItemCount() {
        return actCardList.size();
    }
}