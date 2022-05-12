package com.example.myapplication2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomChoiceListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    private ArrayList<ListViewItem> listViewItemList2 = new ArrayList<ListViewItem>() ;

    // ListViewAdapter의 생성자
    public CustomChoiceListViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.container);
        ImageView boxImageView = (ImageView) convertView.findViewById(R.id.imageView_CBox);
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView_star) ;
        TextView textTextView = (TextView) convertView.findViewById(R.id.textView_content) ;
        TextView textTextView2 = (TextView) convertView.findViewById(R.id.textView_dateTime) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);
        ListViewItem listViewItem2 = listViewItemList2.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        container.setBackgroundColor(listViewItem.getBgColor());
        boxImageView.setImageDrawable(listViewItem.getBox());
        iconImageView.setImageDrawable(listViewItem.getIcon());
        textTextView.setText(listViewItem.getText());
        textTextView2.setText(listViewItem2.getText());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem(int bgColor, Drawable box, Drawable icon, String text) {
        ListViewItem item = new ListViewItem();
        item.setBgColor(bgColor);
        item.setBox(box);
        item.setIcon(icon);
        item.setText(text);

        listViewItemList.add(item);
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem2(String text) {
        ListViewItem item = new ListViewItem();

        item.setText(text);

        listViewItemList2.add(item);
    }

}
