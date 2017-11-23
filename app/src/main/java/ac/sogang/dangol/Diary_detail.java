package ac.sogang.dangol;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Diary_detail extends AppCompatActivity {
    SQLiteDatabase mDB;
    private Cursor c_diary;
    private Cursor c_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        Intent intent = getIntent();
        int dataID = intent.getIntExtra("id", 0);
        if(dataID == 0){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            Log.e("dangol_diary_detail", "no diary id");
            finish();
        }

        mDB = this.openOrCreateDatabase("Dangol", MODE_PRIVATE, null);

        setDiaryCursor(dataID);
        if(c_diary == null || !c_diary.moveToFirst()){
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            Log.e("dangol_diary_detail", "diary cursor is null");
            if(!c_diary.isClosed()) c_diary.close();
            if(mDB.isOpen())        mDB.close();
            finish();
        }

//        setLocationCursor(c_diary.getInt(c_diary.getColumnIndexOrThrow("LocationID")));
        setLocationCursor(dataID);
        if(c_location == null || !c_location.moveToFirst()){
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            Log.e("dangol_diary_detail", "location cursor is null");
            if(!c_location.isClosed()) c_location.close();
            c_diary.close();    mDB.close();
            finish();
        }

        setDiaryView();

        c_diary.close();
        c_location.close();
        mDB.close();
    }

    private void setDiaryCursor(int id) {
        try {
            String sql = "SELECT * FROM Diary WHERE DiaryID=" + id;
            c_diary = mDB.rawQuery(sql, null);
        } catch (SQLiteException se) {
            Log.e("dangol_diary_detail", se.toString());
        } catch (Exception e) {
            Log.e("dangol_diary_detail", e.toString());
        }
    }
    private void setLocationCursor(int id){
        try {
            String sql = "SELECT * FROM Location WHERE LocationID=" + id;
            c_location = mDB.rawQuery(sql, null);
        } catch (SQLiteException se) {
            Log.e("dangol_diary_detail", se.toString());
        } catch (Exception e) {
            Log.e("dangol_diary_detail", e.toString());
        }
    }

    private void setDiaryView(){
        TextView tv;
        ImageView iv;
        int imageFlag;

        String date = c_diary.getString(c_diary.getColumnIndexOrThrow("Time"));
        if(date != null)
            date = date.substring(0, date.indexOf(" "));
        tv = (TextView)findViewById(R.id.diary_date);
        tv.setText(date);

        tv = (TextView)findViewById(R.id.diary_location);
        tv.setText(c_location.getString(c_location.getColumnIndexOrThrow("Name")));

        iv = (ImageView)findViewById(R.id.diary_emotion);
        imageFlag = c_diary.getInt(c_diary.getColumnIndexOrThrow("Mood"));
        switch(imageFlag){
            case 0: iv.setImageResource(R.drawable.feel_best);  break;
            case 1: iv.setImageResource(R.drawable.feel_good);  break;
            case 2: iv.setImageResource(R.drawable.feel_soso);  break;
            case 3: iv.setImageResource(R.drawable.feel_notgood);   break;
            case 4: iv.setImageResource(R.drawable.feel_bad);   break;
        }

        iv = (ImageView)findViewById(R.id.diary_weather);
        imageFlag = c_diary.getInt(c_diary.getColumnIndexOrThrow("Weather"));
        switch(imageFlag){
            case 0: iv.setImageResource(R.drawable.weather_sunny);  break;
            case 1: iv.setImageResource(R.drawable.weather_sunny_cloud);    break;
            case 2: iv.setImageResource(R.drawable.weather_cloudy); break;
            case 3: iv.setImageResource(R.drawable.weather_rainy);  break;
            case 4: iv.setImageResource(R.drawable.weather_snow);   break;
        }

        tv = (TextView)findViewById(R.id.diary_title);
        tv.setText(c_diary.getString(c_diary.getColumnIndexOrThrow("Title")));

        tv = (TextView)findViewById(R.id.diary_text);
        tv.setText(c_diary.getString(c_diary.getColumnIndexOrThrow("Text")));
    }

    public void onBackPressed(View v){
        super.onBackPressed();
    }
}
