package com.example.uniactive.ui.schedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.uniactive.R;
import com.example.uniactive.ui.activity.ActDetailActivity;
import com.example.uniactive.util.JSONGenerator;
import com.google.android.material.datepicker.MaterialCalendar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ScheduleFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private ScheduleFragment context;
    private CalListView listView;
    private SharedPreferences sp;
    private View root;

    private ArrayList<ActInfo> ActCard = new ArrayList<>();
    public static final int REQUEST_ACTION = 9;
    public static boolean isVisible = false;
    private static ArrayList<String> ToPaint;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_schedule, container, false);
        Calendar c = Calendar.getInstance();
        sp = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        calendarView = root.findViewById(R.id.calendar);
        initSchedule(root, c);
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        //获取焦点，界面可见时候执行刷新
        if (isVisible){
            Calendar calendar = calendarView.getSelectedDate().getCalendar();
            setNowActivity(root, calendar, 1);
        }
    }

    private void initSchedule(View root, Calendar c) {
        context = this;
        calendarView.setDateSelected(c, true);
        setNowActivity(root, c, 1);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                setNowActivity(root, date.getCalendar(), 0);
            }
        });

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                setNowActivity(root, date.getCalendar(), 1);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActDetailActivity.class);
                intent.putExtra("act_id", ActCard.get(position).getActId());
                intent.putExtra("place", ActCard.get(position).getPlace());
                intent.putExtra("name", ActCard.get(position).getActName());
                intent.putExtra("intro", ActCard.get(position).getActIntro());
                intent.putExtra("start_time", ActCard.get(position).getStartTime());
                intent.putExtra("end_time", ActCard.get(position).getEndTime());
                intent.putExtra("max_num", ActCard.get(position).getMaxNum());
                intent.putExtra("count", ActCard.get(position).getCount());
                intent.putExtra("holderEmail", ActCard.get(position).getHolderEmail());
                intent.putExtra("holderName", ActCard.get(position).getHolderName());
                intent.putExtra("actImageUrl", ActCard.get(position).getActImageUrl());
                intent.putExtra("holderImageUrl", ActCard.get(position).getHolderImageUrl());
                intent.putIntegerArrayListExtra("actLabelIndexes", ActCard.get(position).getActLabelIndexes());
                startActivityForResult(intent, REQUEST_ACTION);
            }
        });
    }

    private void setNowActivity(View root, Calendar c, int label) {
        listView = root.findViewById(R.id.activity_date);
        GetMes(c, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<ActInfo> resultCard, ArrayList<String> Paint) {
                ActCard = new ArrayList<>();
                ToPaint = new ArrayList<>();
                ActCard.addAll(resultCard);
                CalShowAdapter myCalAdapter2Day = new CalShowAdapter(getContext(), ActCard);
                listView.setAdapter(myCalAdapter2Day);
                ToPaint.addAll(Paint);
                if (label == 1) {
                    calendarView.addDecorator(new CurrentDecorator());
                    calendarView.addDecorator(new PrimeDayDisableDecorator());
                }
            }
        });
    }

    private void GetMes(Calendar c, final VolleyCallback callback) { //获得一天中所有活动的名称
        String email = sp.getString("email", null);
        if (email == null)
            return;
        JSONGenerator.queryJoinedActs(email, getContext(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<ActInfo> resultCard = new ArrayList<>();
                        Set<String> Paint = new HashSet<>();
                        ArrayList<String> calendars = new ArrayList<>();
                        try {
                            int status = response.getInt("status");
                            if (status == 1) {
                                JSONArray jsonObject = response.getJSONArray("result");
                                for (int i = 0; i < jsonObject.length(); i++) {
                                    long start_time = (long)jsonObject.getJSONObject(i).get("start_time");
                                    if (CheckIfToday(c, start_time)) {
                                        JSONObject jo = jsonObject.getJSONObject(i);
                                        int id = jo.getInt("act_id");
                                        String act_name = jo.getString("name");
                                        String holder = jo.getString("holder");
                                        String holder_email = jo.getString("holder_email");
                                        long end_time = jo.getLong("end_time");
                                        int num = jo.getInt("max_num");
                                        int count = jo.getInt("count");
                                        String intro = jo.getString("introduction");
                                        String holderImageUrl = jo.getString("img");
                                        String actImageUrl = jo.getString("img1");
                                        String label1 = jo.getString("label1");
                                        String label2 = jo.getString("label2");
                                        String label3 = jo.getString("label3");
                                        String place = jo.getString("place");
                                        resultCard.add(new ActInfo(id, place, holder_email, holder, holderImageUrl,
                                                start_time, end_time, num, count, act_name, intro, actImageUrl, label1, label2, label3));
                                    }
                                    Calendar c = Calendar.getInstance();
                                    c.setTimeInMillis(start_time);
                                    Paint.add(c.get(Calendar.YEAR) + "/" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH));
                                }
                                Iterator<String> it = Paint.iterator();
                                while (it.hasNext()) {
                                    calendars.add(it.next());
                                }
                                callback.onSuccess(resultCard, calendars);
                            } else if (status == 0) {
                                Toast.makeText(getContext(), "查找活动错误", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "系统错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean CheckIfToday(Calendar c, long tmp) {
        Date date = new Date(tmp);
        Date today = c.getTime();
        return date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate();
    }

    public interface VolleyCallback {
        void onSuccess(ArrayList<ActInfo> resultCard, ArrayList<String> Paint);
    }

    public static ArrayList<String> GetToPaint() {
        return ToPaint;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACTION && resultCode == RESULT_OK) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.getLongExtra("now_time", 0));
            setNowActivity(root, calendar, 1);
        }
    }
}