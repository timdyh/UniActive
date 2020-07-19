package com.example.uniactive.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.uniactive.R;
import com.example.uniactive.ui.alarm.SendNotificationActivity;
import com.example.uniactive.ui.user.LoginActivity;
import com.example.uniactive.ui.activity.ActListDisplayActivity;
import com.example.uniactive.ui.user.ProfileEditActivity;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.uniactive.ui.activity.ActListDisplayActivity.*;

public class HomeFragment extends Fragment {

    private TextView tv_nickname;
    private TextView tv_edit_profile;
    private ImageView iv_avatar;
    private RelativeLayout rl_portrait;
    private HomeItemView iv_participate;
    private HomeItemView iv_release;
    private HomeItemView iv_collect;
    private HomeItemView iv_feedback;
    private HomeItemView iv_about;
    private HomeItemView iv_setting;

    private SharedPreferences sp;

    public static final int REQUEST_LOGIN = 1;
    public static final int REQUEST_EDIT_PROFILE = 2;
    public static final int REQUEST_SETTING = 3;
    public static HomeFragment instance = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        tv_nickname = root.findViewById(R.id.tv_nickname);
        tv_edit_profile = root.findViewById(R.id.tv_edit_profile);
        iv_avatar = root.findViewById(R.id.iv_avatar);
        instance = this;

        sp = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        boolean isLogin = sp.getBoolean("isLogin", false);
        if (isLogin) {
            String nickname = sp.getString("nickname", "null");
            tv_nickname.setText(nickname);
            tv_edit_profile.setText("编辑资料");

            String avatarUrl = sp.getString("avatarUrl", "");
            if (!avatarUrl.isEmpty()) {
                Glide.with(getActivity())
                        .load(avatarUrl)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv_avatar);
            }
        }
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rl_portrait = getActivity().findViewById(R.id.rl_portrait);
        iv_participate = getActivity().findViewById(R.id.iv_participate);
        iv_release = getActivity().findViewById(R.id.iv_release);
        iv_collect = getActivity().findViewById(R.id.iv_collect);
        iv_feedback = getActivity().findViewById(R.id.iv_feedback);
        iv_about = getActivity().findViewById(R.id.iv_about);
        iv_setting = getActivity().findViewById(R.id.iv_setting);

        rl_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLogin = sp.getBoolean("isLogin", false);
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                    startActivityForResult(intent, REQUEST_EDIT_PROFILE);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                }
            }
        });

        iv_participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLogin = sp.getBoolean("isLogin", false);
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), ActListDisplayActivity.class);
                    intent.putExtra("pageId", PARTICIPATE);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                }
            }
        });

        iv_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLogin = sp.getBoolean("isLogin", false);
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), ActListDisplayActivity.class);
                    intent.putExtra("pageId", RELEASE);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                }
            }
        });

        iv_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLogin = sp.getBoolean("isLogin", false);
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), ActListDisplayActivity.class);
                    intent.putExtra("pageId", COLLECT);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                }
            }
        });

        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLogin = sp.getBoolean("isLogin", false);
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), SettingActivity.class);
                    startActivityForResult(intent, REQUEST_SETTING);
                } else {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                }
            }
        });

        iv_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);
            }
        });

        iv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            tv_nickname = getActivity().findViewById(R.id.tv_nickname);
            tv_edit_profile = getActivity().findViewById(R.id.tv_edit_profile);

            switch (requestCode) {
                case REQUEST_LOGIN:
                case REQUEST_EDIT_PROFILE:
                    String nickname = data.getStringExtra("nickname");
                    tv_nickname.setText(nickname);
                    tv_edit_profile.setText("编辑资料");
                    String avatarUrl = data.getStringExtra("avatarUrl");
                    if (!avatarUrl.isEmpty()) {
                        Glide.with(getActivity())
                                .load(avatarUrl)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(iv_avatar);
                    }

                    Intent ser = new Intent(getContext(), SendNotificationActivity.class);
                    getActivity().startService(ser);
                    break;
                case REQUEST_SETTING:
                    tv_nickname.setText("您尚未登录");
                    tv_edit_profile.setText("立即登录");
                    Glide.with(getActivity())
                            .load(R.drawable.avatar)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(iv_avatar);
                    break;
                default:
                    break;
            }
        }
    }
}