package com.example.housekeepingbook;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
// 어플 실행 시 첫 화면을 담당하는 class 입니다.
public class FragmentMain extends Fragment {

    Calendar calendar = Calendar.getInstance(Locale.KOREAN);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월", Locale.KOREAN);
    SimpleDateFormat YearFormat = new SimpleDateFormat("yyyy", Locale.KOREAN);
    SimpleDateFormat MonthFormat = new SimpleDateFormat("M", Locale.KOREAN);
    DecimalFormat myFormatter = new DecimalFormat("###,###");
    String Year,Month;
    View view;
    Context context;
    TextView tv_Incomes,tv_Spends;
    LinearLayout ll_income,ll_spend,ll_kategori,ll_kategori2;
    private int mYear = 0, mMonth = 0,mDay = 0;

    Cursor cursor;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main,container,false);

        tv_Incomes = (TextView)view.findViewById(R.id.tv_Incomes);
        tv_Spends = (TextView)view.findViewById(R.id.tv_Spends);

        LinearLayout ll_btnMove = (LinearLayout)view.findViewById(R.id.ll_btnMove);
        // 통계에 대한 이벤트를 담고있습니다. 아직 진행중입니다.
        TextView tv_income = (TextView)view.findViewById(R.id.tv_income);
        tv_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_btnMove.setGravity(0x03);
            }
        });
        // 통계에 대한 이벤트를 담고있습니다. 아직 진행중입니다.
        TextView tv_spend = (TextView)view.findViewById(R.id.tv_spend);
        tv_spend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_btnMove.setGravity(0x05);
            }
        });
        // 날짜 변경에 대한 이벤트입니다.
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
        return  view;
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

    // 날짜 설정 메소드
    public void SetUpCalendar(){
        // DatePickerDialog 의 기본 날짜 설정값들에 대한 설정 문구.
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        String currentDate = dateFormat.format(calendar.getTime());
        TextView tv_CurrentDate = (TextView)view.findViewById(R.id.tv_CurrentDate);
        tv_CurrentDate.setText(currentDate);
    }
    // 해당 달의 총 수입과 지출에 관해 담고있는 메소드.
    public void Deposit_withdrawal() {
        int TotalIncome = 0;
        int TotalSpend = 0;
        // sql 문 에 사용될 변수들.
        Year = YearFormat.format(calendar.getTime());
        Month = MonthFormat.format(calendar.getTime());

        ll_income = (LinearLayout)view.findViewById(R.id.ll_income);
        ll_kategori = (LinearLayout)view.findViewById(R.id.ll_kategori);
        ll_spend = (LinearLayout)view.findViewById(R.id.ll_spend);
        ll_kategori2 = (LinearLayout)view.findViewById(R.id.ll_kategori2);


        DBHelper dbHelper = DBHelper.getInstance(context);

        String sql_Deposit_withdrawal = "SELECT * FROM " + DBStructure.TABLE_NAME +
                " WHERE " + DBStructure.COLUMN_NAME_DAY +
                " LIKE '%" + Year + "년%"+ Month +"월%';";
        cursor = dbHelper.getReadableDatabase().rawQuery(sql_Deposit_withdrawal,null);

        // 읽어온 결과를 확인하기 위한 문구.
        // 읽어온 값이 없다면 false, 있다면 true.
        if (cursor.moveToNext() == false) {
            //Log.e("cursor", "false");
        } else {
            //Log.e("cursor", "true");
        }
        // 첫번째 cursor에 있다면 실행될 문구.
        // columnIndex
        // 1 - ID , 2 - 수입금액 , 3 - 메모사항(수입) , 4 - 지출금액 , 5 - 메모사항(지출)
        if (cursor.isFirst()) {
            // 인덱스에 해당되는 칼럼의 값을 가져옵니다.
            String income = cursor.getString(2);
            if (income == null) income = "0";
            // 총 수입에 대한 문구입니다.
            TotalIncome += Integer.parseInt(income);
            //Log.e("TotalIncome", String.valueOf(TotalIncome));

            String incomeMemo = cursor.getString(3);
            // 이후 삭제될 문구입니다.
            if (incomeMemo == null) incomeMemo = "미분류";

            String spend = cursor.getString(4);
            if (spend == null) spend = "0";
            // 총 지출에 대한 문구입니다.
            TotalSpend += Integer.parseInt(spend);
            //Log.e("TotalSpend", String.valueOf(TotalSpend));

            String spendMemo = cursor.getString(5);
            if (spendMemo == null) spendMemo = "미분류";

            if (income != "0"){
                incomeText(income);
                // 카테고리 설정 창이 완료되면 변경될 예정.
                categoriText(incomeMemo);
            }
            if (spend != "0"){
                spendText(spend);
                categoriText2(spendMemo);
            }
        }
        // 다음 cursor 의 값이 null 이라면 false, 아니라면 true.
        while(cursor.moveToNext()){
            String income = cursor.getString(2);
            if (income == null) income = "0";
            TotalIncome += Integer.parseInt(income);
            //Log.e("TotalIncome", String.valueOf(TotalIncome));

            String incomeMemo = cursor.getString(3);
            if (incomeMemo == null) incomeMemo = "미분류";

            String spend = cursor.getString(4);
            if (spend == null) spend = "0";
            TotalSpend += Integer.parseInt(spend);
            //Log.e("TotalSpend", String.valueOf(TotalSpend));

            String spendMemo = cursor.getString(5);
            if (spendMemo == null) spendMemo = "미분류";

            if (income != "0") {
                incomeText(income);
                categoriText(incomeMemo);
            }
            if (spend != "0"){
                spendText(spend);
                categoriText2(spendMemo);
            }
        }
        String DecimalIncome = myFormatter.format(TotalIncome) + "원";
        String DecimalSpend = myFormatter.format(TotalSpend) + "원";
        tv_Incomes.setText(DecimalIncome);
        tv_Spends.setText(DecimalSpend);
        dbHelper.close();
        cursor.close();
    }

    // 동적으로 추가를 하기위한 메소드입니다.
    public void incomeText(String string){
        TextView view1 = new TextView(context);
        view1.setText(string);
        view1.setTextColor(Color.parseColor("#bebebe"));
        // 너비, 높이에 대한 설정.
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.END;
        view1.setLayoutParams(lp);
        ll_income.addView(view1);
    }

    public void spendText(String string){
        TextView view1 = new TextView(context);
        view1.setText(string);
        view1.setTextColor(Color.parseColor("#bebebe"));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.END;
        view1.setLayoutParams(lp);
        ll_spend.addView(view1);
    }

    public void categoriText(String string){
        TextView view1 = new TextView(context);
        view1.setText(string);
        view1.setTextColor(Color.parseColor("#bebebe"));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.START;
        view1.setLayoutParams(lp);
        ll_kategori.addView(view1);
    }
    public void categoriText2(String string){
        TextView view1 = new TextView(context);
        view1.setText(string);
        view1.setTextColor(Color.parseColor("#bebebe"));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.START;
        view1.setLayoutParams(lp);
        ll_kategori2.addView(view1);
    }
}
