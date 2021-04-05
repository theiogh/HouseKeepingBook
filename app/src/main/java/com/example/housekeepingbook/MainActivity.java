package com.example.housekeepingbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentMain fragmentmain = new FragmentMain();
    private FragmentInput fragmentInput = new FragmentInput();
    private FragmentBreakDown fragmentBreakDown = new FragmentBreakDown();
    private FragmentCalendar fragmentCalendar = new FragmentCalendar();
    private FragmentTag fragmentTag = new FragmentTag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 상대바 변경에 대한 문구.
        View status_bar_view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= 21){
            status_bar_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // frameLayout 에 기본으로 fragmentmain을 설정함.
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentmain).commitAllowingStateLoss();


        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new  ItemSelectListener());

    }

    // BottomNavigation 의 아이템 클릭 이벤트.
   class ItemSelectListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()){
                case R.id.mainItem :
                    transaction.replace(R.id.frameLayout,fragmentmain).commitAllowingStateLoss();
                    break;
                case R.id.inputItem :
                    transaction.replace(R.id.frameLayout,fragmentInput).commitAllowingStateLoss();
                    break;
                case R.id.breakdownItem :
                    transaction.replace(R.id.frameLayout,fragmentBreakDown).commitAllowingStateLoss();
                    break;
                case R.id.calendarItem :
                    transaction.replace(R.id.frameLayout,fragmentCalendar).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
    // BottomNavigation 에 의해서가 아닌 Fragment 끼리의 이동을 위함.
    public void onFragmentChanged(int index){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (index){
            case 1 :
                transaction.replace(R.id.frameLayout,fragmentmain).commitAllowingStateLoss();
                break;
            case 2 :
                transaction.replace(R.id.frameLayout,fragmentInput).commitAllowingStateLoss();
                break;
            case 3 :
                transaction.replace(R.id.frameLayout,fragmentBreakDown).commitAllowingStateLoss();
                break;
            case 4 :
                transaction.replace(R.id.frameLayout,fragmentCalendar).commitAllowingStateLoss();
                break;
            case 5 :
                transaction.replace(R.id.frameLayout,fragmentTag).commitAllowingStateLoss();
                break;

        }
    }


}