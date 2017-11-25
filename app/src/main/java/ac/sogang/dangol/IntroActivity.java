package ac.sogang.dangol;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class IntroActivity extends AppCompatActivity {
    String dbName = "Dangol";
    String[][] tableName = {
            {"readData", "readDataID integer", "Latitude double", "Longitude double", "Time datetime"},
            {"realData", "realDataID integer", "Latitude double", "Longitude double", "Time datetime"},
            {"Location", "LocationID integer", "Name text", "Latitude double", "Longitude double"},
            {"Diary", "DiaryID integer", "LocationID integer", "Mood integer", "Weather integer", "Title text", "Text text", "Time datetime", "Foto text"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        makeTables();

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
}
