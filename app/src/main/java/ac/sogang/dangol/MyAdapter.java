package ac.sogang.dangol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hyunah on 2017-11-23.
 */

public class MyAdapter extends BaseAdapter {
    private ArrayList<MyItem> mItems = new ArrayList<>();
    private boolean adapterFlag = false;

    public void changeFlag(boolean flag){
        //flag: false - Simple List(diary_frag)
        //      true - Full List(diary_detail)
        adapterFlag = flag;
    }

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

        if(adapterFlag){
            TextView tv_date = (TextView)convertView.findViewById(R.id.diary_date);
            TextView tv_location = (TextView)convertView.findViewById(R.id.diary_location);
            TextView tv_title = (TextView)convertView.findViewById(R.id.diary_title);
            TextView tv_contents = (TextView)convertView.findViewById(R.id.diary_text);
            ImageView iv_emotion = (ImageView)convertView.findViewById(R.id.diary_emotion);
            ImageView iv_weather = (ImageView)convertView.findViewById(R.id.diary_weather);

            MyItem myItem = getItem(position);

            tv_date.setText(myItem.getDate());
            tv_location.setText(myItem.getLocation());
            tv_title.setText(myItem.getTitle());
            tv_contents.setText(myItem.getContent());
            switch(myItem.getEmotion()){
                case 0: iv_emotion.setImageResource(R.drawable.feel_best);  break;
                case 1: iv_emotion.setImageResource(R.drawable.feel_good);  break;
                case 2: iv_emotion.setImageResource(R.drawable.feel_soso);  break;
                case 3: iv_emotion.setImageResource(R.drawable.feel_notgood);   break;
                case 4: iv_emotion.setImageResource(R.drawable.feel_bad);   break;
            }
            switch(myItem.getWeather()){
                case 0: iv_weather.setImageResource(R.drawable.weather_sunny);  break;
                case 1: iv_weather.setImageResource(R.drawable.weather_sunny_cloud);    break;
                case 2: iv_weather.setImageResource(R.drawable.weather_cloudy); break;
                case 3: iv_weather.setImageResource(R.drawable.weather_rainy);  break;
                case 4: iv_weather.setImageResource(R.drawable.weather_snow);   break;
            }
        }
        else{
            TextView tv_title = (TextView)convertView.findViewById(R.id.diary_list_title);
            TextView tv_date = (TextView)convertView.findViewById(R.id.diary_list_date);
            TextView tv_content = (TextView)convertView.findViewById(R.id.diary_list_content);

            MyItem myItem = getItem(position);

            tv_title.setText(myItem.getTitle());
            tv_date.setText(myItem.getDate());
            tv_content.setText(myItem.getContent());
        }
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
    public void addItem(String title, String contents, String date, String location, int emo, int wea){
        MyItem myItem = new MyItem();
        myItem.setTitle(title);
        myItem.setContent(contents);
        myItem.setDate(date);
        myItem.setLocation(location);
        myItem.setEmotion(emo);
        myItem.setWeather(wea);
        mItems.add(myItem);
    }
}
