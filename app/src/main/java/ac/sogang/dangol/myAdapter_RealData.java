package ac.sogang.dangol;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by serin on 2017-12-01.
 */


public class myAdapter_RealData extends BaseAdapter {
    private ArrayList<MyItem_RealData> mItems = new ArrayList<>();
    Context context;
    private MyItem_RealData myItem;

    public myAdapter_RealData() {
        this.myItem = new MyItem_RealData();
    }

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
        context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.before_writing_fragment, parent, false);
        }

        TextView tv_date = (TextView)convertView.findViewById(R.id.real_date);
        TextView tv_time = (TextView)convertView.findViewById(R.id.real_time);

        myItem = getItem(position);

        tv_date.setText(myItem.getDate());
        tv_time.setText(myItem.getTime());

        Button locationBtn = (Button)convertView.findViewById(R.id.real_show_location);
        Button writeBtn = (Button)convertView.findViewById(R.id.real_write);
        Button deleteBtn = (Button)convertView.findViewById(R.id.real_delete);

/*        locationBtn.setOnClickListener(locationBtnListener);
        writeBtn.setOnClickListener(writeBtnListener);
        deleteBtn.setOnClickListener(deleteBtnListener);*/

        final int pos = position;
        locationBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Log.e("dangol_realData", "location " + pos);
                myItem = getItem(pos);

                Intent intent = new Intent(context, RealDataMapActivity.class);

                LatLng location = new LatLng(intent.getDoubleExtra("lat", myItem.getLatitude()), intent.getDoubleExtra("lon", myItem.getLongitude()));

                intent.putExtra("location", location);
                context.startActivity(intent);
            }
        });
        deleteBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Log.e("dangol_realData", "delete");
                myItem = getItem(pos);
                deleteRow(Integer.toString(myItem.getRealDataId()));
            }
        });
        writeBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                Log.e("dangol_realData", "write");

                Intent intent = new Intent(context, WritingActivity.class);

                Location location = new Location("");
                location.setLatitude(myItem.getLatitude());
                location.setLongitude(myItem.getLongitude());
                intent.putExtra("lat", location.getLatitude());
                intent.putExtra("lon", location.getLongitude());
                intent.putExtra("name", "저장된 위치");
                Log.e("dangol_adapter", "position: " + v.getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    void deleteRow(String id) {

        SQLiteDatabase mDB = context.openOrCreateDatabase("Dangol", MODE_PRIVATE, null);

        try {
            Cursor c = mDB.rawQuery("Delete FROM realData WHERE realData.realDataID =" + id, null);

            if (c != null) {
                if (c.moveToLast()) {
                    do {
                        Log.e("dangol_realDataList", "delete Success");

                    } while (c.moveToPrevious());
                }
                if(!c.isClosed())   c.close();
            }
        }catch(SQLiteException se) {
            Log.e("dangol_realDataList", se.toString());
        }catch(NullPointerException ne){
            Log.e("dangol_realDataList", ne.toString());
        }catch(Exception e){
            Log.e("dangol_realDataList", e.toString());
        }
        mDB.close();
    }

    public void addItem(Integer id, String date, String time, Double lat, Double lng) {
        MyItem_RealData myItem = new MyItem_RealData();
        myItem.setRealDataId(id);
        myItem.setDate(date);
        myItem.setTime(time);
        myItem.setLatitude(lat);
        myItem.setLongitude(lng);

        mItems.add(myItem);
    }
    public void removeAll() {
        mItems.clear();
    }
}
