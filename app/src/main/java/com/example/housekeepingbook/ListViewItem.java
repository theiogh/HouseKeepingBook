package com.example.housekeepingbook;

import android.util.Log;

public class ListViewItem {
    public int type;
    public String strDayOfMonth;
    public String strMonth;
    public String strDayWeek;
    public String strIncomes;
    public String strSpends;
    public String strCategory;
    public String strMemo;
    public String sIncome;
    public String sSpend;



    public void setType(int type){
        this.type = type;
    }

    public void setIncome(String Income){
        sIncome = Income;
    }

    public void setSpend(String Spend){
        sSpend = Spend;
    }

    public void setStrMemo(String strMemo) {
        this.strMemo = strMemo;
    }

    public int getType(){
        return this.type ;
    }
}

