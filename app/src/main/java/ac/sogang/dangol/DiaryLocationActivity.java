package ac.sogang.dangol;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

public class DiaryLocationActivity extends AppCompatActivity {
    String locName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_location);

        LatLng location = getIntent().getParcelableExtra("location");
        if(location == null){
            Log.e("dangol_diary_loc", "Location is null");
        }
        else{
            Log.e("dangol_diary_loc", "find " + location.latitude + ", " + location.longitude);
            addList(findLocID(location));
        }
    }

    private int findLocID(LatLng loc){
        int locID = 0;
        SQLiteDatabase mDB = openOrCreateDatabase("Dangol", MODE_PRIVATE, null);
        String sql = "SELECT * FROM Location WHERE Latitude=" + loc.latitude + " AND Longitude=" + loc.longitude;
        Cursor c = mDB.rawQuery(sql, null);
        if(c != null && c.getCount() > 0){
            c.moveToFirst();
            locID = c.getInt(c.getColumnIndex("LocationID"));
            locName = c.getString(c.getColumnIndex("Name"));
            Log.e("dangol_diary_loc", "loc: " + locName);
        }
        else{
            Log.e("dangol_diary_loc", "no data");
        }
        if(c != null && !c.isClosed())  c.close();
        mDB.close();
        return locID;
    }

    private void addList(int locID){
        Log.e("dangol_diary_loc", "addList start " + locID);
        MyAdapter myAdapter = new MyAdapter();
        myAdapter.changeFlag(true);
        myAdapter.setThisActivity(getApplicationContext());
        SQLiteDatabase mDB = openOrCreateDatabase("Dangol", MODE_PRIVATE, null);
        Cursor c = mDB.rawQuery("SELECT * FROM Diary WHERE LocationID=" + locID, null);
        if(c != null && c.getCount() > 0){
            if(c.moveToFirst()){
                do{
                    String title = c.getString(c.getColumnIndex("Title"));
                    String text = c.getString(c.getColumnIndex("Text"));
                    String time = c.getString(c.getColumnIndex("Time"));
                    int emotion = c.getInt(c.getColumnIndex("Mood"));
                    int weather = c.getInt(c.getColumnIndex("Weather"));
                    String imagePath = c.getString(c.getColumnIndexOrThrow("Photo"));

//                    Log.e("dangol_diary_loc", title + "   " + time);
                    myAdapter.addItem(title, text, time, locName, emotion, weather, imagePath);
                }while(c.moveToNext());
            }
        }
        else{
            Log.e("dangol_diary_loc", "error occurs");
        }
        if(c != null && !c.isClosed())  c.close();
        mDB.close();

        ListView lv = (ListView)findViewById(R.id.diary_location_list);
        lv.setAdapter(myAdapter);
    }
}
