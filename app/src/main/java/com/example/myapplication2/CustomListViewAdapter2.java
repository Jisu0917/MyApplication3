package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

public class CustomListViewAdapter2 extends BaseAdapter {
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    private ArrayList<Integer> checkedIndexList = new ArrayList<Integer>() ;

    static boolean allCboxReset = false;

    public CustomListViewAdapter2() {
        allCboxReset = false;
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview2_item, parent, false);
        }

        final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkbox0);
        if (allCboxReset) {
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkedIndexList.add(position);
                } else {
                    if (checkedIndexList.contains(position)) {
                        int index = checkedIndexList.indexOf(position);
                        checkedIndexList.remove(index);
                    }
                }
            }
        });

        return convertView;
    }

    public void addItem() {
        ListViewItem item = new ListViewItem();
        listViewItemList.add(item);
    }

    public ArrayList<Integer> getCheckedIndexList() {
        return checkedIndexList;
    }

    public void resetCheckedIndexList() {
        allCboxReset = true;
        checkedIndexList.clear();
    }

}
