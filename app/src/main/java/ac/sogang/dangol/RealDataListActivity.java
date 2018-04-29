package ac.sogang.dangol;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.text.SimpleDateFormat;


public class RealDataListActivity extends AppCompatActivity {

    private ListView mListView;
    myAdapter_RealData adapter = new myAdapter_RealData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_data_list);
        mListView = (ListView)findViewById(R.id.realDataList);

        int cnt = addList();
        TextView tv = (TextView)findViewById(R.id.real_data_txt);
        tv.setText(cnt + "개의 기록이 있어요");
    }

    void addTestRealData(int i){
        SQLiteDatabase mDB = openOrCreateDatabase("Dangol", MODE_PRIVATE, null);

        String nowDateTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(System.currentTimeMillis());

        double lat = 37.555847 + i*0.2;
        double lng = 126.983370 + i*0.2;

        String sql = "INSERT INTO realData(Latitude, Longitude, Time) VALUES (" + lat + ", "+ lng +", '"+ nowDateTime + "');";

        mDB.execSQL(sql);
        Log.e("check_data", sql);
        mDB.close();
    }


    String[] dateParse(String date) {

        String dateArr[] = date.split("\\s+");

        String d = dateArr[0];
        String t = dateArr[1];

        String dArr[] = d.split("\\.");
        String tArr[] = t.split(":");

        String[] result = new String[6];

        result[0] = dArr[0];
        result[1] = dArr[1];
        result[2] = dArr[2];

        result[3] = tArr[0];
        result[4] = tArr[1];
        result[5] = tArr[2];
        return result;
    }

    private int addList(){
        int cnt = 0;
        SQLiteDatabase mDB = openOrCreateDatabase("Dangol", MODE_PRIVATE,
                null);

        try {
            Cursor c = mDB.rawQuery("SELECT realDataID, Latitude, Longitude, Time FROM realData", null);

            Log.e("dangol_real", c.getCount() + "개");

            if (c != null) {
                if (c.moveToLast()) {
                    do {
                        Integer realDataId;
                        Double lat, lng;
                        String date;

                        realDataId = c.getInt(c.getColumnIndexOrThrow("realDataID"));
                        lat = c.getDouble(c.getColumnIndexOrThrow("Latitude"));
                        lng = c.getDouble(c.getColumnIndexOrThrow("Longitude"));
                        date = c.getString(c.getColumnIndexOrThrow("Time"));

                        String sortedDate[] = dateParse(date);
                        Log.e("dangol_realDataList", sortedDate[0] + " " + sortedDate[3]);

                        String finalDate = sortedDate[0] + "년 " + sortedDate[1] + "월 " + sortedDate[2] + "일";
                        String finalTime = sortedDate[3] + "시 " + sortedDate[4] + "분";

                        Log.e("dangol_realDataList", "lat: " + lat + " lng: " + lng);
                        adapter.addItem(realDataId, finalDate, finalTime, lat, lng);
                    } while (c.moveToPrevious());
                }
                cnt = c.getCount();
                if(!c.isClosed())   c.close();
            } else {
                Log.e("dangol_realDataList", "Cursor is null");
            }
        }catch(SQLiteException se) {
            Log.e("dangol_realDataList", se.toString());
        }catch(NullPointerException ne){
            Log.e("dangol_realDataList", ne.toString());
        }catch(Exception e){
            Log.e("dangol_realDataList", e.toString());
        }
        mDB.close();

        mListView.setAdapter(adapter);
        return cnt;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}