package com.example.housekeepingbook;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
// 달력에 관한 class 입니다.
public class FragmentCalendar extends Fragment {

    Calendar calendar = Calendar.getInstance(Locale.KOREAN);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월", Locale.KOREAN);
    SimpleDateFormat YearFormat = new SimpleDateFormat("yyyy년", Locale.KOREAN);
    SimpleDateFormat MonthFormat = new SimpleDateFormat("M월", Locale.KOREAN);
    DecimalFormat myFormatter = new DecimalFormat("###,###");
    TextView tv_Incomes,tv_Spends,tv_Balance,tv_Previous,tv_Next;
    String Year, Month, sql_Y_M;
    View view;
    Context context;
    Cursor cursor;
    List<Date> dates = new ArrayList<>();
    MyGridAdapter myGridAdapter;
    GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;
    private int mYear = 0, mMonth = 0,mDay = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendarview,container,false);

        tv_Previous = (TextView)view.findViewById(R.id.tv_Previous);
        // 클릭 시 날짜의 변경에 대한 이벤트를 담고있습니다.
        tv_Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetUpCalendar();
                Deposit_withdrawal();
            }
        });
        tv_Next = (TextView)view.findViewById(R.id.tv_Next);
        // 클릭 시 날짜의 변경에 대한 이벤트를 담고있습니다.
        tv_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetUpCalendar();
                Deposit_withdrawal();
            }
        });

        ImageButton btn_Search = (ImageButton)view.findViewById(R.id.btn_Search);
        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),listener,mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });

        SetUpCalendar();
        Deposit_withdrawal();
        return view;
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view1, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            SetUpCalendar();
            Deposit_withdrawal();
        }
    };

    public void SetUpCalendar() {

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        TextView tv_CurrentDate = (TextView)view.findViewById(R.id.tv_CurrentDate);
        String currentDate = dateFormat.format(calendar.getTime());
        tv_CurrentDate.setText(currentDate);
        dates.clear();

        // 복사본을 만들고 반환.
        // GridAdapter 의 getCount 에 사용하기 위함.
        Calendar monthCalendar = (Calendar) calendar.clone();

        // 현재 날짜를 달의 1일로 설정
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // 첫 요일을 일요일(=1)로 설정
        int FirstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);

        // dates의 크기가 42를 넘지 않을동안 실행.
        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        gridView = (GridView)view.findViewById(R.id.gridView);
        myGridAdapter = new MyGridAdapter(context, dates, calendar);
        gridView.setAdapter(myGridAdapter);
    }

    public void Deposit_withdrawal() {
        int TotalIncome = 0;
        int TotalSpend = 0;
        Year = YearFormat.format(calendar.getTime());
        Month = MonthFormat.format(calendar.getTime());

        tv_Incomes = (TextView)view.findViewById(R.id.tv_Incomes);
        tv_Spends = (TextView)view.findViewById(R.id.tv_Spends);
        tv_Balance = (TextView)view.findViewById(R.id.tv_Balance);

        DBHelper dbHelper = DBHelper.getInstance(context);

        // "yyyy년 MM월"
        sql_Y_M = "SELECT * FROM " + DBStructure.TABLE_NAME +
                " WHERE " + DBStructure.COLUMN_NAME_DAY +
                " LIKE '%" + Year + "%"+ Month +"%';";

        cursor = dbHelper.getReadableDatabase().rawQuery(sql_Y_M,null);

        if (cursor.moveToNext() == false) {
            //Log.e("cursor", "false");
        } else {
            //Log.e("cursor", "true");
        }
        if (cursor.isFirst()) {
            String income = cursor.getString(2);
            if (income == null) income = "0";
            TotalIncome += Integer.parseInt(income);
            //Log.e("TotalIncome", String.valueOf(TotalIncome));
            String spend = cursor.getString(4);
            if (spend == null) spend = "0";
            TotalSpend += Integer.parseInt(spend);
            //Log.e("TotalSpend", String.valueOf(TotalSpend));
        }
        while(cursor.moveToNext()){
            String income = cursor.getString(2);
            if (income == null) income = "0";
            TotalIncome += Integer.parseInt(income);
            //Log.e("TotalIncome", String.valueOf(TotalIncome));
            String spend = cursor.getString(4);
            if (spend == null) spend = "0";
            TotalSpend += Integer.parseInt(spend);
            //Log.e("TotalSpend", String.valueOf(TotalSpend));
        }
        int Balance = TotalIncome - TotalSpend;
        String DecimalTotalIncome = myFormatter.format(TotalIncome);
        String DecimalTotalSpend = myFormatter.format(TotalSpend);
        String DecimalBalance = myFormatter.format(Balance);

        tv_Incomes.setText(DecimalTotalIncome);
        tv_Spends.setText(DecimalTotalSpend);
        tv_Balance.setText(DecimalBalance);

        cursor.close();
        dbHelper.close();
    }
}
