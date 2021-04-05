package com.example.housekeepingbook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// 카테고리 설정 창에 사용될 Adapter 입니다.
// 진행중입니다.
public class TagAdapter extends BaseAdapter {
    ArrayList<CategoryList> CategoryData = new ArrayList<CategoryList>();
    public TagAdapter(ArrayList<CategoryList> mCategoryData){
        CategoryData = mCategoryData;
        Log.e("size() ", String.valueOf(CategoryData.size()));
    }

    @Override
    public int getCount() {
        return CategoryData.size();
    }

    private class ViewHolder{
        private TextView category;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Context context = parent.getContext();
        if (convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category,parent,false);

            holder.category = (TextView)convertView.findViewById(R.id.category);

            convertView.setTag(holder);
        }else holder = (ViewHolder)convertView.getTag();

        holder.category.setText(CategoryData.get(position).Category);

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addItem(String string){
        CategoryList CL = new CategoryList();

        CL.setCategory(string);
        CategoryData.add(CL);
    }
}
