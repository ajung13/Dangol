package ac.sogang.dangol;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.text.SimpleDateFormat;


public class RealDataListActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_data_list);
        mListView = (ListView)findViewById(R.id.realDataList);


//        for(int i=0; i<10; i++)
//            addTestRealData(i);


        addList();

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

    private void addList(){
        myAdapter_RealData adapter = new myAdapter_RealData();

        SQLiteDatabase mDB = openOrCreateDatabase("Dangol", MODE_PRIVATE, null);

        try {
            Cursor c = mDB.rawQuery("SELECT realDataID, Latitude, Longitude, Time FROM realData", null);

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

                        adapter.addItem(finalDate, finalTime);

                    } while (c.moveToPrevious());
                }
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

    }
}


//
//public class DiaryFragment extends Fragment {
//    String dbName = "Dangol";
//    View view;
//
//    private ListView mListView;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
//        view = inflater.inflate( R.layout.fragment_diary, container, false );
//
//        mListView = (ListView)view.findViewById(R.id.main_diary_list);
//        dataSetting();
//
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//                MyItem listItem = (MyItem) parent.getAdapter().getItem(position);
//                Intent intent = new Intent(getActivity(), Diary_detail.class);
//                intent.putExtra("id", listItem.getID());
//                startActivity(intent);
//            }
//        });
//
//        return view;
//    }
//
//    private void dataSetting(){
//        SQLiteDatabase mDB = getActivity().openOrCreateDatabase(dbName, MODE_PRIVATE, null);
//        MyAdapter mMyAdapter = new MyAdapter();
//
//        try {
//            Cursor c = mDB.rawQuery("SELECT DiaryID, Title, Text, Time FROM Diary", null);
//
//            if (c != null) {
//                if (c.moveToLast()) {
//                    do {
//                        String title, content, date, imageName;
//                        title = c.getString(c.getColumnIndexOrThrow("Title"));
//                        content = c.getString(c.getColumnIndexOrThrow("Text"));
//                        date = c.getString(c.getColumnIndexOrThrow("Time"));
//
//                        int id = c.getInt(c.getColumnIndexOrThrow("DiaryID"));
//
//                        if(title.length() > 30)
//                            title = title.substring(0, 30) + "...";
//                        if(content.length() > 30)
//                            content = content.substring(0, 30) + "...";
//                        if(date != null)
//                            date = date.substring(0, date.indexOf(" "));
//
//                        mMyAdapter.addItem(title, content, date, id);
//                    } while (c.moveToPrevious());
//                }
//                if(!c.isClosed())   c.close();
//            }
//        }catch(SQLiteException se) {
//            Log.e("dangol_diary(se)", se.toString());
//        }catch(NullPointerException ne){
//            Log.e("dangol_diary(ne)", ne.toString());
//        }catch(Exception e){
//            Log.e("dangol_diary(e)", e.toString());
//        }
//        mDB.close();
//
//        mListView.setAdapter(mMyAdapter);
//    }
//}