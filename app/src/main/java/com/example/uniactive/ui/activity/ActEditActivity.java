package com.example.uniactive.ui.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.donkingliang.labels.LabelsView;
import com.example.uniactive.MainActivity;
import com.example.uniactive.R;
import com.example.uniactive.ui.alarm.SendNotificationActivity;
import com.example.uniactive.ui.home.HomeFragment;
import com.example.uniactive.ui.home.SettingActivity;
import com.example.uniactive.util.JSONGenerator;
import com.example.uniactive.util.LabelManager;
import com.example.uniactive.util.PostPictureResponse;
import com.example.uniactive.util.RetrofitApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

import static com.example.uniactive.ui.activity.ActListDisplayActivity.*;

public class ActEditActivity extends AppCompatActivity{

    private EditText et_act_name;
    private EditText et_act_intro;
    private EditText et_act_place;
    private EditText et_max_num;

    private Button btn_start_date;
    private Button btn_start_time;
    private Button btn_end_date;
    private Button btn_end_time;
    private Button btn_submit;

    private ImageView iv_act_image;
    private ImageView iv_delete;
    private LabelsView labelsView;

    private TextView text_delete;

    private Calendar start = Calendar.getInstance(Locale.CHINA);
    private Calendar end = Calendar.getInstance(Locale.CHINA);

    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private final int dateThemeResId = 2;
    private final int timeThemeResId = 2;

    private SharedPreferences sp;
    private String email;
    private String actImageUrl;
    
    private int pageId = 0;

    private final int REQUEST_PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("userData", MODE_PRIVATE);
        email = sp.getString("email", "null");

        et_act_name = findViewById(R.id.et_act_name);
        et_act_intro = findViewById(R.id.et_act_intro);
        et_act_place = findViewById(R.id.et_act_place);
        et_max_num = findViewById(R.id.et_max_num);

        btn_start_date = findViewById(R.id.btn_start_date);
        btn_start_time = findViewById(R.id.btn_start_time);
        btn_end_date = findViewById(R.id.btn_end_date);
        btn_end_time = findViewById(R.id.btn_end_time);
        btn_submit = findViewById(R.id.btn_submit);

        iv_act_image = findViewById(R.id.iv_act_image);
        iv_delete = findViewById(R.id.iv_delete);
        labelsView = findViewById(R.id.labels);

        text_delete = findViewById(R.id.text_delete);

        labelsView.setLabels(LabelManager.getLabels());

        Intent intent = getIntent();
        pageId = intent.getIntExtra("pageId", 0);
        if (pageId == RELEASE) {     // 编辑活动
            et_act_name.setText(intent.getStringExtra("name"));
            et_act_intro.setText(intent.getStringExtra("intro"));
            et_act_place.setText(intent.getStringExtra("place"));
            et_max_num.setText(String.valueOf(intent.getIntExtra("max_num", 0)));

            start.setTimeInMillis(intent.getLongExtra("start_time", 0));
            end.setTimeInMillis(intent.getLongExtra("end_time", 0));

            actImageUrl = intent.getStringExtra("actImageUrl");
            if (!actImageUrl.isEmpty()) {
                Glide.with(ActEditActivity.this)
                        .load(actImageUrl)
                        .into(iv_act_image);
                iv_delete.setVisibility(View.VISIBLE);
            }

            List<Integer> actLabelIndexes = intent.getIntegerArrayListExtra("actLabelIndexes");
            labelsView.setSelects(actLabelIndexes);

            text_delete.setVisibility(View.VISIBLE);
        } else {        // 发布新活动
            toolbar.setTitle("发布新活动");
            start.add(Calendar.MINUTE, 120);
            end.add(Calendar.MINUTE, 180);
            actImageUrl = "";
        }
        startDate = dateFormat.format(start.getTime());
        startTime = timeFormat.format(start.getTime());
        endDate = dateFormat.format(end.getTime());
        endTime = timeFormat.format(end.getTime());
        btn_start_date.setText(startDate);
        btn_start_time.setText(startTime);
        btn_end_date.setText(endDate);
        btn_end_time.setText(endTime);

        btn_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(btn_start_date, start);
            }
        });

        btn_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(btn_start_time, start);
            }
        });

        btn_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(btn_end_date, end);
            }
        });

        btn_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(btn_end_time, end);
            }
        });

        iv_act_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ActEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActEditActivity.this, new String[] {
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

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actImageUrl = "";
                Glide.with(ActEditActivity.this)
                        .load(R.drawable.upload)
                        .into(iv_act_image);
                iv_delete.setVisibility(View.GONE);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }

                String name = et_act_name.getText().toString().trim();
                String intro = et_act_intro.getText().toString().trim();
                String place = et_act_place.getText().toString().trim();
                String max_num = et_max_num.getText().toString().trim();
                List<String> labels = labelsView.getSelectLabelDatas();

                boolean isValid = checkValid(name, intro, place, max_num, labels);
                if (!isValid) return;

                String label1 = (labels.size() >= 1) ? labels.get(0) : "";
                String label2 = (labels.size() >= 2) ? labels.get(1) : "";
                String label3 = (labels.size() == 3) ? labels.get(2) : "";

                if (pageId == RELEASE) {
                    JSONGenerator.modify(email, intent.getIntExtra("act_id", 0),
                            name, start.getTimeInMillis(), end.getTimeInMillis(),
                            Integer.parseInt(max_num), intro, place, label1, label2, label3,
                            actImageUrl, getApplicationContext(), getListener(), getErrorListener());
                } else {
                    JSONGenerator.hold(email, name, start.getTimeInMillis(), end.getTimeInMillis(),
                            Integer.parseInt(max_num), intro, place, label1, label2, label3,
                            actImageUrl, getApplicationContext(), getListener(), getErrorListener());
                }
            }
        });

        text_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActEditActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定取消发布当前活动吗？（已开始的活动不可取消）");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONGenerator.cancel(intent.getIntExtra("act_id", 0),
                                getApplicationContext(),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            int status = response.getInt("status");
                                            if (status == 1) {
                                                Toast.makeText(ActEditActivity.this, "取消发布成功", Toast.LENGTH_SHORT).show();
                                                Intent data = new Intent();
                                                setResult(RESULT_OK, data);
                                                finish();
                                            } else {
                                                Toast.makeText(ActEditActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                getErrorListener());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
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
                        if (pageId == RELEASE) {
                            Toast.makeText(ActEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            Intent data = new Intent();
                            setResult(RESULT_OK, data);
                        } else {
                            Toast.makeText(ActEditActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    } else {
                        Toast.makeText(ActEditActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ActEditActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void showDatePickerDialog(Button btn, Calendar c) {
        new DatePickerDialog(ActEditActivity.this, dateThemeResId, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                btn.setText(dateFormat.format(c.getTime()));
            }
        },
        c.get(Calendar.YEAR),
        c.get(Calendar.MONTH),
        c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerDialog(Button btn, Calendar c) {
        new TimePickerDialog(ActEditActivity.this, timeThemeResId, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                btn.setText(timeFormat.format(c.getTime()));
            }
        },
        c.get(Calendar.HOUR_OF_DAY),
        c.get(Calendar.MINUTE),
        true).show();
    }

    private boolean checkValid(String name, String intro, String place, String max_num, List<String> labels) {
        Calendar oneHourFromNow = Calendar.getInstance(Locale.CHINA);
        oneHourFromNow.add(Calendar.MINUTE, 60);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(ActEditActivity.this, "请填写活动名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(intro)) {
            Toast.makeText(ActEditActivity.this, "请填写活动内容", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(place)) {
            Toast.makeText(ActEditActivity.this, "请填写活动地点", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(max_num)) {
            Toast.makeText(ActEditActivity.this, "请填写最大容量", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.getTrimmedLength(intro) < 15) {
            Toast.makeText(ActEditActivity.this, "活动内容需至少填写15个字符", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isDigitsOnly(max_num) || max_num.equals("0")) {
            Toast.makeText(ActEditActivity.this, "最大容量必须是正整数", Toast.LENGTH_SHORT).show();
        } else if (start.before(oneHourFromNow)) {
            Toast.makeText(ActEditActivity.this, "活动开始时间需至少为1小时后", Toast.LENGTH_SHORT).show();
        } else if (end.before(start)) {
            Toast.makeText(ActEditActivity.this, "结束时间不得早于开始时间", Toast.LENGTH_SHORT).show();
        } else if (labels.size() == 0) {
            Toast.makeText(ActEditActivity.this, "请至少选择一个类别标签", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
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
                        Toast.makeText(ActEditActivity.this, "文件权限获取失败，请重试", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ActEditActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                    actImageUrl = response.body().getUrl();
                    Glide.with(ActEditActivity.this)
                            .load(actImageUrl)
                            .into(iv_act_image);
                    iv_delete.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<PostPictureResponse> call, Throwable t) {
                    Toast.makeText(ActEditActivity.this, "上传失败", Toast.LENGTH_LONG).show();
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
