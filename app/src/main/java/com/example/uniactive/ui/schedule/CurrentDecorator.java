package com.example.uniactive.ui.schedule;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class CurrentDecorator implements DayViewDecorator {

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Calendar c = day.getCalendar();
        Calendar calendar = Calendar.getInstance();
        return c.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && c.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && c.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CurrentSpan());
    }


}

