package com.example.uniactive.ui.schedule;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class PrimeDayDisableDecorator implements DayViewDecorator {

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Calendar calendar = day.getCalendar();
        String s = calendar.get(Calendar.YEAR) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        return ScheduleFragment.GetToPaint().contains(s);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new SpecialSpan());
    }


}
