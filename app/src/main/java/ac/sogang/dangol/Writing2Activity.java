package ac.sogang.dangol;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class Writing2Activity extends AppCompatActivity {
    String dbName = "Dangol";
    Intent intent;
    final double minDiff = 0.000001;
    boolean locationFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing2);
        intent = getIntent();
    }

    public void onBackPressed(View v){
        Intent back_intent = new Intent(Writing2Activity.this, WritingActivity.class);
        back_intent.putExtras(intent.getExtras());
        startActivity(back_intent);
        finish();
    }
    public void onSavePressed(View v){
        int year = intent.getIntExtra("year", 0);
        int month = intent.getIntExtra("month", 0);
        int date = intent.getIntExtra("date", 0);
        int emotion = intent.getIntExtra("emotion", -1);
        int weather = intent.getIntExtra("weather", -1);
        LatLng location = intent.getParcelableExtra("location");
        String location_name = intent.getStringExtra("location_name");

        EditText et = (EditText)findViewById(R.id.write_title);
        String title = et.getText().toString();
        et = (EditText)findViewById(R.id.write_contents);
        String contents = et.getText().toString();
        title = checkString(title, 0);
        contents = checkString(contents, 1);

        Log.e("dangol_write2", "location: " + location.latitude + ", " + location.longitude);
        Log.e("dangol_write2", "location name: " + location_name);

        uploadDB(year, month, date, emotion, weather, location.latitude, location.longitude, location_name, title, contents);
        finish();
    }

    public void uploadDB(int year, int month, int date, int emotion, int weather, double lat, double lon, String name, String title, String contents){
        SQLiteDatabase mDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try{
            String sql;
            locationFlag = false;
            int locID = checkLocationID(mDB, name, lat, lon);
            if(!locationFlag){
                sql = "INSERT INTO Location(Name, Latitude, Longitude) VALUES ('" + name + "', " +
                        lat + ", " + lon + ");";
                Log.e("dangol_insert", "sql(1): " + sql);
                mDB.execSQL(sql);
            }

            sql = "INSERT INTO Diary(LocationID, Mood, Weather, Title, Text, Time) VALUES (" +
                    locID + ", " + emotion + ", " + weather +
                    ", '" + title + "', '" + contents + "', '" + year + "-" + month + "-" + date + " 00:00:00');";
            Log.e("dangol_insert", "sql(2): " + sql);
            mDB.execSQL(sql);
        }catch(SQLiteException se){
            Log.e("dangol_insert_sql", se.toString());
        }catch(Exception e){
            Log.e("dangol_insert", e.toString());
        }
        mDB.close();
        Toast.makeText(getApplicationContext(), "저장되었습니다!", Toast.LENGTH_SHORT).show();
    }

    private int checkLocationID(SQLiteDatabase db, String name, double lat, double lon){
        int position = 0;
        try {
            Cursor c = db.rawQuery("SELECT * FROM Location", null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        position++;
                        String tmpName = c.getString(c.getColumnIndexOrThrow("Name"));
                        double tmpLat = c.getDouble(c.getColumnIndexOrThrow("Latitude"));
                        double tmpLon = c.getDouble(c.getColumnIndexOrThrow("Longitude"));
                        Log.e("dangol_write2_check!", "(" + c.getInt(c.getColumnIndexOrThrow("LocationID"))
                                + ") " + tmpName);

                        if(name.equals(tmpName) && (tmpLat - lat < minDiff) && (tmpLon - lon < minDiff)){
                            position = c.getInt(c.getColumnIndexOrThrow("LocationID"));
                            Log.e("dangol_write2_check", "found");
                            locationFlag = true;
                            break;
                        }
                    } while (c.moveToNext());
                }
            }
            if (!c.isClosed()) c.close();
        }catch(SQLiteException se){
            Log.e("dangol_write2_save", se.toString());
        }catch(NullPointerException ne){
            Log.e("dangol_write2_save", ne.toString());
        }catch(Exception e){
            Log.e("dangol_write2_save", e.toString());
        }
        return position;
    }

    private String checkString(String str, int flag){
        if(str.length() == 0){
            if(flag == 0)   str = "제목 없음";
            else            str = "내용 없음";
        }
        if(str.contains("'"))
            str = str.replace("'", " ");
        return str;
    }
}
