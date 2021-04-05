package com.example.housekeepingbook;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// 내역 창에 관한 class 입니다.
public class FragmentBreakDown extends Fragment {

    Calendar calendar = Calendar.getInstance(Locale.KOREAN);
    SimpleDateFormat FullFormat = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월", Locale.KOREAN);
    SimpleDateFormat YearFormat = new SimpleDateFormat("yyyy년", Locale.KOREAN);
    SimpleDateFormat MonthFormat = new SimpleDateFormat("M월", Locale.KOREAN);
    SimpleDateFormat DayFormat = new SimpleDateFormat("d일", Locale.KOREAN);
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy.MM",Locale.KOREAN);
    DecimalFormat myFormatter = new DecimalFormat("###,###");
    String currentDate,Year, Month, Day, strDayWeek,ByDate = "Daily";
    Context context;
    Cursor cursor;
    DBHelper dbHelper;
    View view;
    Date sDate;
    TextView tv_Incomes,tv_Spends,tv_Balance;
    String sql_Y_M;
    int mYear = 0, mMonth = 0,mDay = 0,lastDay,dayWeek, i;
    ListView listview;
    ListViewAdapter adapter;
    ArrayList<ListViewItem> mItemData = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = getActivity();

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_breakdown,container,false);
        listview = (ListView)view.findViewById(R.id.listView);

        LinearLayout ll_btnMove = (LinearLayout)view.findViewById(R.id.ll_btnMove);
        TextView tv_Daily = (TextView)view.findViewById(R.id.tv_Daily);
        TextView tv_MonthToMonth = (TextView)view.findViewById(R.id.tv_MonthToMonth);
        TextView tv_ByYear = (TextView)view.findViewById(R.id.tv_ByYear);
        // "일별" 에 관한 문구. 아직 진행중입니다.
        tv_Daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_btnMove.setGravity(0x03);
                ByDate = "Daily";
                SetUpCalendar();
                Deposit_withdrawal();
            }
        });
        // "일별" 에 관한 문구. 아직 진행중입니다.
        tv_MonthToMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_btnMove.setGravity(0x11);
                ByDate = "MonthToMonth";
                SetUpCalendar();
                Deposit_withdrawal();
            }
        });
        // "일별" 에 관한 문구. 아직 진행중입니다.
        tv_ByYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_btnMove.setGravity(0x05);
                ByDate = "ByYear";
                SetUpCalendar();
                Deposit_withdrawal();
            }
        });
        ImageButton btn_Search = (ImageButton)view.findViewById(R.id.btn_Search);
        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DatePickerDialog 를 이용하여 원하는 날짜를 선택할 수 있게했습니다.
                // 5개의 인자중 뒤에서 3개는 날짜에 대한 기본 설정값입니다.
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),listener,mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        SetUpCalendar();
        Deposit_withdrawal();

        return  view;
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view1, int year, int month, int dayOfMonth) {
            // 선택한 날짜를 calendar 에 설정합니다.
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            // 해당 달의 마지막 날짜를 받아옵니다.
            // 2월일경우, 28이 해당됩니다.
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            SetUpCalendar();
            Deposit_withdrawal();
        }
    };
    // 날짜의 설정과 내역 ListView 에 대한 메소드.
    public void SetUpCalendar(){
        // ListView 의 갱신이 될 때마다 같은 아이템들의 반복적인 생성을 막기위한 문구.
        mItemData.clear();
        // DatePickerDialog 의 기본 날짜 설정값들에 대한 설정 문구.
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 현재 날짜를 가져옵니다.
        currentDate = dateFormat.format(calendar.getTime());
        TextView tv_CurrentDate = (TextView)view.findViewById(R.id.tv_CurrentDate);
        tv_CurrentDate.setText(currentDate);

        // 내역 ListView 에 관한 문구입니다.
        // for문을 사용하여 1일부터 마지막날까지의 내역을 스캔합니다.
        for (i = 1; i<lastDay+1; i++){
            Log.e("i", String.valueOf(i));
            listView();
            // 내역 ListView에 관한 Adapter.
            adapter = new ListViewAdapter(mItemData);
            listview.setAdapter(adapter);
        }
    }
    // 해당 달의 총 수입과 지출에 관해 담고있는 메소드.
    public void Deposit_withdrawal() {
        int TotalIncome = 0;
        int TotalSpend = 0;
        // sql 문 에 사용될 변수들.
        Year = YearFormat.format(calendar.getTime());
        Month = MonthFormat.format(calendar.getTime());
        Day = DayFormat.format(calendar.getTime());

        tv_Incomes = (TextView)view.findViewById(R.id.tv_Incomes);
        tv_Spends = (TextView)view.findViewById(R.id.tv_Spends);
        tv_Balance = (TextView)view.findViewById(R.id.tv_Balance);

        DBHelper dbHelper = DBHelper.getInstance(context);


        // "yyyy년 MM월"
        // 일별로 Search 하기위한 sql 문.
        sql_Y_M = "SELECT * FROM " + DBStructure.TABLE_NAME +
                " WHERE " + DBStructure.COLUMN_NAME_DAY +
                " LIKE '%" + Year + "%"+ Month +"%';";
        cursor = dbHelper.getReadableDatabase().rawQuery(sql_Y_M,null);

        // 읽어온 결과를 확인하기 위한 문구.
        // 읽어온 값이 없다면 false, 있다면 true.
        if (!cursor.moveToNext()) {
            //Log.e("cursor", "false");
        } else {
            //Log.e("cursor", "true");
        }
        // 첫번째 cursor에 있다면 실행될 문구.
        // columnIndex 
        // 1 - ID , 2 - 수입금액 , 3 - 메모사항(수입) , 4 - 지출금액 , 5 - 메모사항(지출)
        if (cursor.isFirst()) {
            // 인덱스에 해당되는 칼럼들의 값을 가져옵니다.
            String income = cursor.getString(2);
            // 가져온 값이 null 일 경우 "0"을 입력합니다.
            if (income == null) income = "0";
            // 총 수입에 대한 문구입니다.
            TotalIncome += Integer.parseInt(income);
            //Log.e("TotalIncome", String.valueOf(TotalIncome));
            String spend = cursor.getString(4);
            if (spend == null) spend = "0";
            // 총 지출에 대한 문구입니다.
            TotalSpend += Integer.parseInt(spend);
        }
        // 다음 cursor 의 값이 null 이라면 false, 아니라면 true.
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
        // 총 수입에서 지출에 뺀 합계의 대한 문구입니다.
        int Balance = TotalIncome - TotalSpend;
        // 숫자의 3자리 마다 , 을 표시하기 위한 문구입니다.
        String DecimalTotalIncome = myFormatter.format(TotalIncome);
        String DecimalTotalSpend = myFormatter.format(TotalSpend);
        String DecimalBalance = myFormatter.format(Balance);

        tv_Incomes.setText(DecimalTotalIncome);
        tv_Spends.setText(DecimalTotalSpend);
        tv_Balance.setText(DecimalBalance);

        cursor.close();
        dbHelper.close();
    }
    // 내역 ListView 에 관한 메소드.
    public void listView(){
        // 객체 생성.
        // 해당 메소드 밖에서 생성할경우 item 은 마지막 값만을 출력합니다.
        ListViewItem item = new ListViewItem();
        int mTotalIncome = 0;
        int mTotalSpend = 0;
        // 일수를 담습니다.
        item.strDayOfMonth = String.valueOf(i);
        String sql_Daily = String.valueOf(mYear)+"년 "+ String.valueOf(mMonth+1)+"월 "+ String.valueOf(i)+"일";

        dbHelper = DBHelper.getInstance(context);
        String sql_Day_Deposit_withdrawal = "SELECT * FROM "+DBStructure.TABLE_NAME+" WHERE "+DBStructure.COLUMN_NAME_DAY+" = ?;";

        try {// 요일에 관한 try catch 문입니다. String to Date 를 하기 위해선 try catch 로 감싸야합니다.
            Date mDate = FullFormat.parse(sql_Daily);
            sDate = dateFormat.parse(sql_Daily);
            Calendar cal = Calendar.getInstance();
            cal.setTime(mDate);
            dayWeek = cal.get(Calendar.DAY_OF_WEEK);
            switch (dayWeek){
                case 1 :
                    strDayWeek = "Sun";
                    break;
                case 2 :
                    strDayWeek = "Mon";
                    break;
                case 3 :
                    strDayWeek = "Tus";
                    break;
                case 4 :
                    strDayWeek = "Wed";
                    break;
                case 5 :
                    strDayWeek = "Thu";
                    break;
                case 6 :
                    strDayWeek = "Fri";
                    break;
                case 7 :
                    strDayWeek = "Sat";
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String strsMonth = sDateFormat.format(sDate);
        // 해당 달의 값을 담습니다.
        item.strMonth = strsMonth;
        // 요일 값을 담습니다.
        item.strDayWeek = strDayWeek;
        cursor = dbHelper.getReadableDatabase().rawQuery(sql_Day_Deposit_withdrawal,new String[]{sql_Daily});

        if (!cursor.moveToNext()) {
            //Log.e("cursor ", "false");
        } else {
            Log.e("cursor ", "true");
            mItemData.add(item);
        }
        if (cursor.isFirst()) {
            if (!cursor.isNull(2)){
                String income = cursor.getString(2);
                //Log.e("income1 ",income);
                if (!cursor.isNull(3)){
                    String incomeMemo = cursor.getString(3);
                    //Log.e("incomeMemo ",incomeMemo);
                    // 메모사항이 있다면 수입금액과 함께 담습니다.
                    adapter.addIncomeMemo(income,incomeMemo);
                }else{
                    // 메모사항이 없다면 금액만 담습니다.
                    adapter.addIncome(income);
                }
                mTotalIncome += Integer.parseInt(income);
            }
            if (!cursor.isNull(4)){
                ListViewAdapter adapter = new ListViewAdapter(mItemData);
                String spend = cursor.getString(4);
                //Log.e("spend1 ",spend);
                if (!cursor.isNull(5)){
                    String spendMemo = cursor.getString(5);
                    //Log.e("spendMemo ",spendMemo);
                    adapter.addSpendMemo(spend,spendMemo);
                }else{
                    adapter.addSpend(spend);
                }
                mTotalSpend += Integer.parseInt(spend);
            }
        }
        while(cursor.moveToNext()){
            if (!cursor.isNull(2)){
                String income = cursor.getString(2);
                //Log.e("income2 ",income);
                if (!cursor.isNull(3)){
                    String incomeMemo = cursor.getString(3);
                    //Log.e("incomeMemo2 ",incomeMemo);
                    adapter.addIncomeMemo(income,incomeMemo);
                }else{
                    adapter.addIncome(income);
                }
                mTotalIncome += Integer.parseInt(income);
            }
            if (!cursor.isNull(4)){
                String spend = cursor.getString(4);
                //Log.e("spend2 ",spend);
                if (!cursor.isNull(5)) {
                    String spendMemo = cursor.getString(5);
                    //Log.e("spendMemo2 ",spendMemo);
                    adapter.addSpendMemo(spend,spendMemo);
                }else{
                    adapter.addSpend(spend);
                }
                mTotalSpend += Integer.parseInt(spend);
            }
        }
        if(mTotalIncome != 0){
            String strIncomes = myFormatter.format(mTotalIncome)+" 원";
            item.strIncomes = strIncomes;
        }
        if (mTotalSpend != 0){
            String strSpends = myFormatter.format(mTotalSpend)+" 원";
            item.strSpends = strSpends;
        }
        cursor.close();
        dbHelper.close();

    }
}
