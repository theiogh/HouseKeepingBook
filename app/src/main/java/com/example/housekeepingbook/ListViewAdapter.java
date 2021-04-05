package com.example.housekeepingbook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;

// 내역 ListView 에 관한 Adapter 입니다.
// 기존에는 Layout 에 ListView 를 중첩하여 사용할려 하였으나 안쪽 ListView 의 getView 가 호출이 되지않는 이유로 인해 다른 방식을 채택했습니다.
// getCount 문제는 아니였습니다. 아직까지 원인을 알지 못했습니다.
// 대신에 아이템 중 일부를 listView 처럼 사용하는 방식을 활용했습니다.

public class ListViewAdapter extends BaseAdapter {

    private static final int ITEM_VIEW_TYPE_STR = 0;
    private static final int ITEM_VIEW_TYPE_SECOND = 1;
    private static final int ITEM_VIEW_TYPE_MAX = 2;

    private ArrayList<ListViewItem> ItemData = new ArrayList<ListViewItem>();
    DecimalFormat myFormatter = new DecimalFormat("###,###");
    // 속도 향상을 위해 ViewHolder 개념을 이용했습니다.
    private class ViewHolder{
        private TextView tv_CurrentDate;
        private TextView tv_Date;
        private TextView tv_DayOfTheWeak;
        private TextView tv_TotalIncomes;
        private TextView tv_TotalSpends;
        private TextView tv_category;
        private TextView tv_memo;
        private TextView tv_income;
        private TextView tv_spend;
        private ConstraintLayout subItem;
    }

    public ListViewAdapter(ArrayList<ListViewItem> mItemData){
        ItemData = mItemData;
    }

    @Override
    public int getCount() {
        return ItemData.size();
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        return ItemData.get(position).getType();
    }

    @Override
    public Object getItem(int position) {
        return ItemData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Context context = parent.getContext();
        int viewtype = getItemViewType(position);
        switch (viewtype){
            case ITEM_VIEW_TYPE_STR : // 날짜와 총 수입,지출에 관한 문구입니다. 최초 1회만 실행됩니다.
                Log.e("viewType ", "STR");
                if (convertView == null){
                    holder = new ViewHolder();
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    ListViewItem listViewItem = ItemData.get(position);
                    convertView = inflater.inflate(R.layout.fragment_breakdown_listview,parent,false);

                    holder.tv_CurrentDate = (TextView)convertView.findViewById(R.id.tv_CurrentDate);
                    holder.tv_Date = (TextView)convertView.findViewById(R.id.tv_Date);
                    holder.tv_DayOfTheWeak = (TextView)convertView.findViewById(R.id.tv_DayOfTheWeak);
                    holder.tv_TotalIncomes = (TextView)convertView.findViewById(R.id.tv_TotalIncomes);
                    holder.tv_TotalSpends = (TextView)convertView.findViewById(R.id.tv_TotalSpends);


                }else{
                    holder = (ViewHolder)convertView.getTag();
                }

                holder.tv_CurrentDate.setText(ItemData.get(position).strDayOfMonth);
                holder.tv_Date.setText(ItemData.get(position).strMonth);
                holder.tv_DayOfTheWeak.setText(ItemData.get(position).strDayWeek);
                holder.tv_TotalIncomes.setText(ItemData.get(position).strIncomes);
                holder.tv_TotalSpends.setText(ItemData.get(position).strSpends);
                convertView.setTag(holder);
                break;

            case ITEM_VIEW_TYPE_SECOND : // 수입과 지출에 내역들을 출력해줄 문구입니다. 이후 카테고리 설정과 함께 마무리될 예정입니다.
                Log.e("viewType ", "SECOND");
                if (convertView == null){
                    holder = new ViewHolder();
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    ListViewItem listViewItem = ItemData.get(position);
                    convertView = inflater.inflate(R.layout.breakdown_secondlist,parent,false);

                    holder.tv_income = (TextView)convertView.findViewById(R.id.tv_income);
                    holder.tv_spend = (TextView)convertView.findViewById(R.id.tv_spend);
                    holder.tv_category = (TextView)convertView.findViewById(R.id.tv_category);
                    holder.tv_memo = (TextView)convertView.findViewById(R.id.tv_memo);
                    holder.subItem = (ConstraintLayout)convertView.findViewById(R.id.subItem);
                }else{
                    holder = (ViewHolder)convertView.getTag();
                }

                holder.tv_income.setText(ItemData.get(position).sIncome);
                holder.tv_spend.setText(ItemData.get(position).sSpend);
                holder.tv_category.setText(ItemData.get(position).strCategory);
                holder.tv_memo.setText(ItemData.get(position).strMemo);

                convertView.setTag(holder);

                break;
        }
        return convertView;
    }

    public void addIncome(String income){
        ListViewItem item = new ListViewItem();

        item.setType(ITEM_VIEW_TYPE_SECOND);
        String mincome = myFormatter.format(Integer.parseInt(income))+" 원";
        item.setIncome(mincome);
        item.setSpend("");
        ItemData.add(item);
    }

    public void addIncomeMemo(String income, String memo){
        ListViewItem item = new ListViewItem();

        item.setType(ITEM_VIEW_TYPE_SECOND);

        String mincome = myFormatter.format(Integer.parseInt(income))+" 원";
        item.setIncome(mincome);
        item.setSpend("");
        item.setStrMemo(memo);
        ItemData.add(item);
    }
    public void addSpend(String spend){
        ListViewItem item = new ListViewItem();

        item.setType(ITEM_VIEW_TYPE_SECOND);
        String mspend = myFormatter.format(Integer.parseInt(spend))+" 원";

        item.setIncome("");
        item.setSpend(mspend);
        ItemData.add(item);
    }

    public void addSpendMemo(String spend, String memo){
        ListViewItem item = new ListViewItem();

        item.setType(ITEM_VIEW_TYPE_SECOND);
        String mspend = myFormatter.format(Integer.parseInt(spend))+" 원";

        item.setIncome("");
        item.setSpend(mspend);
        item.setStrMemo(memo);
        ItemData.add(item);
    }



}
