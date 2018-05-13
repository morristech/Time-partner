package me.shouheng.timepartner.fragments.calendar;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.shouheng.timepartner.adapters.DailyDetailsAdapter;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.database.dao.loader.DayDetailsLoader;
import me.shouheng.timepartner.database.dao.loader.MonthDetailsLoader;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.custom.TpCalendar;

public class CldFragMonth extends Fragment implements TpCalendar.OnDaySelectedListener{
    // 日历的日期：年 月（1表示1月） 日
    private int mYear, mMonth, mDay;
    private DailyDetailsAdapter adapter;
    private DayDetailsLoader detailsContainer;

    public CldFragMonth(int mYear, int mMonth, int mDay){
        this.mYear = mYear;
        this.mMonth = mMonth;
        this.mDay = mDay;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();

        MonthDetailsLoader loader = MonthDetailsLoader.getInstance(context);
        loader.loadGivenMonth(mYear, mMonth);

        View view = inflater.inflate(R.layout.calendar_frag_month, container, false);
        LinearLayout listCalendar = (LinearLayout) view.findViewById(R.id.list_calendar);

        TpCalendar tpCalendar = new TpCalendar(context);
        tpCalendar.setDate(mYear, mMonth, mDay);
        tpCalendar.setRateMap(loader.getRateMap());
        tpCalendar.setMap(loader.getMap());
        tpCalendar.draw();
        tpCalendar.setOnDaySelectedListener(this);
        listCalendar.addView(tpCalendar);

        long millisOfDay = TpTime.getMillisFromDate(mYear, mMonth - 1, mDay);
        detailsContainer = DayDetailsLoader.getInstance(context);
        adapter = new DailyDetailsAdapter(context);
        adapter.addDetails(detailsContainer.loadGivenDate(millisOfDay));
        RecyclerView dailyDetail = (RecyclerView) view.findViewById(R.id.daily_detail);
        dailyDetail.setLayoutManager(new LinearLayoutManager(context));
        dailyDetail.setAdapter(adapter);

        return view;
    }

    @Override
    public void onSelected(long millis) {
        adapter.addDetails(detailsContainer.loadGivenDate(millis));
        adapter.notifyDataSetChanged();
    }
}
