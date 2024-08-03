package com.example.greenswuniex

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.Calendar

class TodayDecorator (context: Context):DayViewDecorator {



    private val drawable: Drawable = ContextCompat.getDrawable(context, R.drawable.today_background)!!
    private val today: CalendarDay


    init {
        val calendar = Calendar.getInstance()
        today = CalendarDay.from(calendar)
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day != null && day == today
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable)
    }
}


