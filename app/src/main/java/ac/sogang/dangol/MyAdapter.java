package ac.sogang.dangol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hyunah on 2017-11-23.
 */

public class MyAdapter extends BaseAdapter {
    private ArrayList<MyItem> mItems = new ArrayList<>();

    @Override
    public int getCount(){
        return mItems.size();
    }

    @Override
    public MyItem getItem(int position){
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.diary_listview, parent, false);
        }

        TextView tv_title = (TextView)convertView.findViewById(R.id.diary_list_title);
        TextView tv_date = (TextView)convertView.findViewById(R.id.diary_list_date);
        TextView tv_content = (TextView)convertView.findViewById(R.id.diary_list_content);

        MyItem myItem = getItem(position);

        tv_title.setText(myItem.getTitle());
        tv_date.setText(myItem.getDate());
        tv_content.setText(myItem.getContent());

        //event listener

        return convertView;
    }

    public void addItem(String title, String contents, String date, int id){
        MyItem myItem = new MyItem();
        myItem.setTitle(title);
        myItem.setContent(contents);
        myItem.setDate(date);
        myItem.setID(id);
        mItems.add(myItem);
    }
}
