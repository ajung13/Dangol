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
    String[][] tableName = {{"read_data", "location", "diary", "photos"},
            {"data_id int, latitude long, longitude long, time datatime", "location_id int, name varchar(30), latitude long" }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

//        makeTables();

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
            for(String tName : tableName[0]){
                //----MUST FILL THIS FIELD----
                String sql = "create table if not exists " + tName + "";
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
