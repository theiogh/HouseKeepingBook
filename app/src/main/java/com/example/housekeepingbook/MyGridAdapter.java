package com.example.housekeepingbook;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Calendar 의 사용되는 Adapter 입니다.
// getView 에서 직접 데이터를 읽어와 뿌려줍니다.
// 이후 ViewHolder 개념을 채택할 예정입니다.
public class MyGridAdapter extends BaseAdapter {

    List<Date> dates;
    Calendar calendar;
    LayoutInflater inflater;
    Context context;
    TextView tv_day,tv_Incomes,tv_Spends;
    String Date;
    View view;

    public MyGridAdapter(Context con, List<Date> dates, Calendar calendar) {

        context = con;
        this.dates = dates;
        this.calendar = calendar;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return dates.size();
    }
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int Income = 0;
        int Spend = 0;
        Date monthDate = dates.get(position);
        Calendar dateCalender = Calendar.getInstance();
        dateCalender.setTime(monthDate);
        // 현재 월 기준 몇 일
        int Day = dateCalender.get(Calendar.DAY_OF_MONTH);
        // 표준시간대의 월
        int displayMonth = dateCalender.get(Calendar.MONTH)+1;
        // 현재 월 ( 1월 : 0 )
        int currentMonth = calendar.get(Calendar.MONTH)+1;
        // 표준시간대의 년도
        int displayYear = dateCalender.get(Calendar.YEAR);
        // 현재 년도
        int currentYear = calendar.get(Calendar.YEAR);

        // convertview : 이전에 사용되었던 뷰
        view = convertView;
        if(view == null ){
            view = inflater.inflate(R.layout.activity_calendar_day,null);
            tv_day = view.findViewById(R.id.tv_day);
            tv_Incomes = view.findViewById(R.id.tv_Incomes);
            tv_Spends = view.findViewById(R.id.tv_Spends);
        }
        Date = String.valueOf(currentYear)+"년 "+ String.valueOf(currentMonth)+"월 "+ String.valueOf(Day)+"일";
        DBHelper dbHelper = DBHelper.getInstance(context);
        String sql_Day_Deposit_withdrawal = "SELECT * FROM "+DBStructure.TABLE_NAME+" WHERE "+DBStructure.COLUMN_NAME_DAY+" = ?;";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql_Day_Deposit_withdrawal,new String[]{Date});
/*
        if (cursor.moveToNext() == false) {
            Log.e("cursor", "false");
        } else {
            Log.e("cursor", "true");
        }
 */
        if (cursor.isFirst()) {
            String income = cursor.getString(2);
            if (income == null) income = "0";
            Income += Integer.parseInt(income);
            //Log.e("income", String.valueOf(Income));
            if (Income != 0) tv_Incomes.setText("+ "+String.valueOf(Income));

            String spend = cursor.getString(4);
            if (spend == null) spend = "0";
            Spend += Integer.parseInt(spend);
            //Log.e("spend",String.valueOf(Spend));
            if (Spend != 0)  tv_Spends.setText("- "+String.valueOf(Spend));
        }

        while(cursor.moveToNext()) {
            String income = cursor.getString(2);
            if (income == null) income = "0";
            Income += Integer.parseInt(income);
            //Log.e("income", String.valueOf(Income));
            if (Income != 0) tv_Incomes.setText("+ "+String.valueOf(Income));

            String spend = cursor.getString(4);
            if (spend == null) spend = "0";
            Spend += Integer.parseInt(spend);
            //Log.e("spend",String.valueOf(Spend));
            if (Spend != 0)  tv_Spends.setText("- "+String.valueOf(Spend));
        }
        cursor.close();
        dbHelper.close();

        if(displayMonth == currentMonth && displayYear == currentYear){
        }else{
            tv_day.setTextColor(Color.parseColor("#4D000000"));
            tv_Incomes.setText(null);
            tv_Spends.setText(null);
        }
        tv_day.setText(String.valueOf(Day));
        return view;
    }
}

