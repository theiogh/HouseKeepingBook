package com.example.housekeepingbook;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
// 입력 창에 관한 class 입니다.
public class FragmentInput extends Fragment {

    String IncomeMemo = "";
    String SpendMemo = "";
    SimpleDateFormat TodayFormat = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN);
    Calendar calendar = Calendar.getInstance(Locale.KOREAN);
    int num,Income,Spend;
    View view;
    LinearLayout ll_edit;
    EditText et_insert;
    ArrayList<CategoryList> CategoryData = new ArrayList<CategoryList>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_input,container,false);

        SetupCalendar();

        TextView tv_Previous = (TextView)view.findViewById(R.id.tv_Previous);
        tv_Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        // material 의 Button 을 사용할 시 Background 가 적용되지 않습니다.
        // 대신에 androidx.appcompat.widget.AppCompatButton 을 사용했습니다.
        AppCompatButton btn_income = (AppCompatButton)view.findViewById(R.id.btn_income);
        AppCompatButton btn_spend = (AppCompatButton)view.findViewById(R.id.btn_spend);
        EditText et_Input = (EditText)view.findViewById(R.id.et_Input);
        // 분류 "수입" 에 대한 클릭 리스너 입니다.
        btn_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_ImportAmount = (TextView)view.findViewById(R.id.tv_ImportAmount);

                btn_income.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.on));
                btn_income.setText("수입");
                btn_income.setTextColor(Color.parseColor("#ffffff"));
                btn_spend.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.off));
                btn_spend.setText("지출");
                btn_spend.setTextColor(Color.parseColor("#006bff"));

                tv_ImportAmount.setText("수입 금액");
                et_Input.setHint("수입 금액");
                num = 1;
                Log.e("num", String.valueOf(num));
            }
        });
        // 분류 "지출" 에 대한 클릭 리스너 입니다.
        btn_spend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_ImportAmount = (TextView)view.findViewById(R.id.tv_ImportAmount);
                btn_income.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.off));
                btn_income.setText("수입");
                btn_income.setTextColor(Color.parseColor("#006bff"));
                btn_spend.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.on));
                btn_spend.setText("지출");
                btn_spend.setTextColor(Color.parseColor("#ffffff"));
                tv_ImportAmount.setText("지출 금액");
                et_Input.setHint("지출 금액");
                num = 2;
                Log.e("num", String.valueOf(num));
            }
        });

        EditText et_Category = (EditText)view.findViewById(R.id.et_Category);
        // 키보드의 비활성화를 위한 문구입니다.
        final InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_Category.getWindowToken(),0);
        // 클릭시 카테고리 설정 창 fragment 로 이동합니다.
        et_Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.onFragmentChanged(5);
            }
        });

/*      // 이후 다른 class 로 이동될 문구입니다.
        AppCompatButton category_income = (AppCompatButton)view.findViewById(R.id.category_income);
        AppCompatButton category_spend = (AppCompatButton)view.findViewById(R.id.category_spend);
        category_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridView Category_GridView = (GridView)view.findViewById(R.id.Category_GridView);
                TagAdapter tagAdapter = new TagAdapter(CategoryData);
                Category_GridView.setAdapter(tagAdapter);
            }
        });
        category_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridView Category_GridView = (GridView)view.findViewById(R.id.Category_GridView);
                TagAdapter tagAdapter = new TagAdapter(CategoryData);
                String string = et_insert.getText().toString();
                tagAdapter.addItem(string);
                et_insert.setVisibility(View.INVISIBLE);
                ll_edit.setVisibility(View.INVISIBLE);
                Category_GridView.setAdapter(tagAdapter);

            }
        });

 */
        TextView tv_Next = (TextView)view.findViewById(R.id.tv_Next);
        tv_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String checkInput = et_Input.getText().toString();
                if (num != 1 && num != 2){
                    Toast.makeText(getActivity(), "분류가 정해지지 않았습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    db_write();
                    startActivity(new Intent(getActivity(),MainActivity.class));
                }
            }
        });
        return view;
    }
    // 데이터 쓰기 메소드.
    public void db_write(){
        EditText et_Input = (EditText)view.findViewById(R.id.et_Input);
        EditText et_Memo = (EditText)view.findViewById(R.id.et_Memo);

        // 금액 입력란이 비어있지 않을 경우(정상) 실행
        // 이후 카테고리 설정 유무에 대한 문구가 추가될 예정입니다.
        if (!et_Input.getText().toString().isEmpty()){
            if (num == 1){ // 분류가 수입일 경우 실행
                Income = Integer.parseInt(et_Input.getText().toString()); // 수입 금액
                IncomeMemo = et_Memo.getText().toString(); // 메모사항(수입)
            }else if (num == 2){
                Spend = Integer.parseInt(et_Input.getText().toString()); // 지출 금액
                SpendMemo = et_Memo.getText().toString(); // 메모사항(지출)
            }
            String Today = TodayFormat.format(calendar.getTime()); // 현재 날짜 ( "yyyy년 MM월 dd일" )
            Log.e("Today",Today);

            ContentValues contentValues = new ContentValues(); // 객체 생성
            if (num == 1) {
                contentValues.put(DBStructure.COLUMN_NAME_INCOME,Income); // 분류가 "수입"이라면 수입금액
                Log.e("분류 ", "수입");
            }
            else {
                contentValues.put(DBStructure.COLUMN_NAME_SPEND,Spend); // 분류가 "지출"이라면 지출금액
                Log.e("분류 ", "지출");

            }
            contentValues.put(DBStructure.COLUMN_NAME_DAY,Today);   // 현재 날짜
            if (!et_Memo.getText().toString().isEmpty()){ // 비어있지않을 경우 실행.
                if (num == 1){
                    contentValues.put(DBStructure.COLUMN_NAME_INCOMEMEMO, IncomeMemo); // 메모사항(수입)
                }else {
                    contentValues.put(DBStructure.COLUMN_NAME_SPENDMEMO, SpendMemo); // 메모사항(지출)
                }
            }
            SQLiteDatabase db = DBHelper.getInstance(getActivity()).getWritableDatabase();
            // 오류가 날 경우 Long 타입으로 -1을 반환함.
            long newRowId = db.insert(DBStructure.TABLE_NAME,null,contentValues);
            // 오류가 날 경우 확인하기 위함.
            if (newRowId == -1) {
                Toast.makeText(getActivity(), "저장에 문제가 생겼습니다.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "성공", Toast.LENGTH_SHORT).show();
            }
        }else{ // 금액 입력란이 비었을 경우 실행.
            Toast.makeText(getActivity(), "금액을 적어주세요", Toast.LENGTH_SHORT).show();
        }
    }

    public void SetupCalendar(){
        String currentDate = TodayFormat.format(calendar.getTime());
        TextView tv_CurrentDate = (TextView)view.findViewById(R.id.tv_CurrentDate);
        tv_CurrentDate.setText(currentDate);
    }


}
