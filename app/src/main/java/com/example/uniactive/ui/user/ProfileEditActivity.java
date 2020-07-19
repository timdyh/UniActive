package com.example.uniactive.ui.user;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.uniactive.R;
import com.example.uniactive.util.JSONGenerator;
import com.example.uniactive.util.PostPictureResponse;
import com.example.uniactive.util.RetrofitApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ProfileEditActivity extends AppCompatActivity {

    private LinearLayout ll_portrait;
    private ProfileItemView iv_email;
    private ProfileItemView iv_nickname;
    private ProfileItemView iv_gender;
    private OptionsPickerView opv_gender;
    private TextView tv_save;
    private ImageView iv_avatar;

    private SharedPreferences sp;

    private String email;
    private String nickname;
    private int gender;
    private String avatarUrl;

    private final int REQUEST_PICK_IMAGE = 1;
    private final List<String> genderOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("userData", MODE_PRIVATE);

        ll_portrait = findViewById(R.id.ll_portrait);
        iv_email = findViewById(R.id.iv_email);
        iv_nickname = findViewById(R.id.iv_nickname);
        iv_gender = findViewById(R.id.iv_gender);
        tv_save = findViewById(R.id.tv_save);
        iv_avatar = findViewById(R.id.iv_avatar);

        email = sp.getString("email", "null");
        nickname = sp.getString("nickname", "null");
        gender = sp.getInt("gender", 1);
        avatarUrl = sp.getString("avatarUrl", "");

        iv_email.setText(email);
        iv_nickname.setText(nickname);
        iv_gender.setText((gender == 1) ? "男" : "女");
        genderOptions.add("男");
        genderOptions.add("女");

        if (!avatarUrl.isEmpty()) {
            Glide.with(ProfileEditActivity.this)
                    .load(avatarUrl)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(iv_avatar);
        }

        ll_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ProfileEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfileEditActivity.this, new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                            }, REQUEST_PICK_IMAGE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_PICK_IMAGE);
                }
            }
        });

        iv_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }

                opv_gender = new OptionsPickerBuilder(ProfileEditActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        String option = genderOptions.get(options1);
                        iv_gender.setText(option);
                    }
                }).setCancelColor(Color.GRAY).build();
                opv_gender.setPicker(genderOptions);
                opv_gender.show();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = iv_nickname.getText();
                gender = (iv_gender.getText().equals("男")) ? 1 : 0;
                if (TextUtils.isEmpty(nickname)) {
                    Toast.makeText(ProfileEditActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONGenerator.modifyInfo(email, nickname, gender, avatarUrl,
                        getApplicationContext(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    if (status == 1) {
                                        Toast.makeText(ProfileEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                        saveUserData(nickname, gender, avatarUrl);
                                        Intent data = new Intent();
                                        data.putExtra("nickname", nickname);
                                        data.putExtra("avatarUrl", avatarUrl);
                                        setResult(RESULT_OK, data);
                                        finish();
                                    } else {
                                        Toast.makeText(ProfileEditActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ProfileEditActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void saveUserData(String nickname, int gender, String avatarUrl) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("nickname", nickname);
        editor.putInt("gender", gender);
        editor.putString("avatarUrl", avatarUrl);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    Uri image = data.getData();
                    upload(image);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                boolean gotPermissionUpload = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        gotPermissionUpload = false;
                        Toast.makeText(ProfileEditActivity.this, "文件权限获取失败，请重试", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if (gotPermissionUpload) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_PICK_IMAGE);
                }
                break;
            default:
                break;
        }
    }

    private void upload(Uri image) {
        RetrofitApi.postPicture(email, image, getApplicationContext(),
            new Callback<PostPictureResponse>() {
                @Override
                public void onResponse(Call<PostPictureResponse> call, retrofit2.Response<PostPictureResponse> response) {
                    Toast.makeText(ProfileEditActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                    avatarUrl = response.body().getUrl();
                    Glide.with(ProfileEditActivity.this)
                            .load(avatarUrl)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(iv_avatar);
                }

                @Override
                public void onFailure(Call<PostPictureResponse> call, Throwable t) {
                    Toast.makeText(ProfileEditActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                }
            });
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
}
