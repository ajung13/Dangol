package ac.sogang.dangol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class IntroActivity extends AppCompatActivity {
    String dbName = "Dangol";
    String[][] tableName = {
            {"readData", "readDataID integer", "Latitude double", "Longitude double", "Time datetime"},
            {"realData", "realDataID integer", "Latitude double", "Longitude double", "Time datetime"},
            {"Location", "LocationID integer", "Name text", "Latitude double", "Longitude double"},
            {"Diary", "DiaryID integer", "LocationID integer", "Mood integer", "Weather integer", "Title text", "Text text", "Time datetime", "Photo text"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        makeTables();

        if(checkFirst()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(IntroActivity.this, GuideActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
    }

    private boolean checkFirst(){
        SharedPreferences prefs = getSharedPreferences("ac.sogang.dangol", MODE_PRIVATE);
        if(prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).commit();
            return true;
        }
        return false;
    }

    private void makeTables(){
        SQLiteDatabase mDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        try{
            for(int i = 0; i < tableName.length; i++){
                String[] tableNameData = tableName[i];
                String sql = "create table if not exists " + tableNameData[0]+" (";
                for(int j = 1; j < tableNameData.length - 1; j++) {
                    if(j==1)    sql += tableNameData[j] + " primary key autoincrement, ";
                    else        sql += tableNameData[j] + ", ";
                }
                sql += tableNameData[tableNameData.length - 1] + ");";
//                Log.e("dangol_intro", sql);
                mDB.execSQL(sql);
            }
        }catch(SQLiteException se){
            Log.e("dangol_intro", "SQLite exception - " + se.toString());
        }catch(Exception e) {
            Log.e("dangol_intro", "Exception - " + e.toString());
        }

        mDB.close();
    }
    private void insertDB(){
        SQLiteDatabase mDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try{

            mDB.execSQL("INSERT INTO readData(readDataID, Latitude, Longitude, Time) VALUES ('R-1', 0.000, 0.000, '2017-11-13 15:05:11')");

        }catch(SQLiteException se){
            Log.e("insert_sql", se.toString());
        }catch(Exception e){
            Log.e("insert", e.toString());
        }
        mDB.close();
    }

    private void checkDB(){
        SQLiteDatabase mDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try {
            Cursor c = mDB.rawQuery("SELECT Time FROM readData", null);
            String data = "";
            Log.e("lala","오긴하는거니?");
            if (c != null) {
                if (c.moveToFirst()) {
                    int i = 0;
                    data += getPackageName() + ": ";
                    do {
                        data += c.getString(i++) + "\t";
                    } while (c.moveToNext());
                    Log.e("checkData", data);
                    Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
                }
            }
        }catch(SQLiteException se){
            Log.e("check_sql", se.toString());
        }catch(Exception e){
            Log.e("check", e.toString());
        }
        mDB.close();
    }
}
