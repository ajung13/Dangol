package ac.sogang.dangol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by serin on 2017-12-01.
 */

public class myAdapter_RealData extends BaseAdapter {
    private ArrayList<MyItem_RealData> mItems = new ArrayList<>();

    @Override
    public int getCount(){
        return mItems.size();
    }

    @Override
    public MyItem_RealData getItem(int position){
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
            convertView = inflater.inflate(R.layout.before_writing_fragment, parent, false);
        }


        TextView tv_date = (TextView)convertView.findViewById(R.id.real_date);
        TextView tv_time = (TextView)convertView.findViewById(R.id.real_time);

        MyItem_RealData myItem = getItem(position);

        tv_date.setText(myItem.getDate());
        tv_time.setText(myItem.getTime());


        return convertView;
    }

    public void addItem(String date, String time){
        MyItem_RealData myItem = new MyItem_RealData();
        myItem.setDate(date);
        myItem.setTime(time);
        mItems.add(myItem);
    }
}
