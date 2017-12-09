package ac.sogang.dangol;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by class-03 on 11/28/17.
 */

public class DiaryDetailFragment extends Fragment {
    SQLiteDatabase mDB;
    private Cursor c_diary;
    private Cursor c_location;
    private int dataID;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        view = inflater.inflate( R.layout.diary_detail_fragment, container, false );

        dataID = getActivity().getIntent().getIntExtra("id", 0);
        Log.e("dangol_detail_frag", "id: " + dataID);

        setDB();

        Button deleteBtn = (Button)view.findViewById(R.id.diary_delete);

        deleteBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                deleteDiary(dataID);
                getActivity().finish();
            }
        });

        return view;
    }

    private void setDB(){
        mDB = getActivity().openOrCreateDatabase("Dangol", MODE_PRIVATE, null);

        int locID = setDiaryCursor(dataID);
        if(c_diary == null){
            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            Log.e("dangol_diary_detail", "diary cursor is null");
            if(!c_diary.isClosed()) c_diary.close();
            if(mDB.isOpen())        mDB.close();
            getActivity().finish();
        }

        setLocationCursor(locID);
        if(c_location == null || !c_location.moveToFirst()){
            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            Log.e("dangol_diary_detail", "location cursor is null");
            if(!c_location.isClosed()) c_location.close();
            c_diary.close();    mDB.close();
            getActivity().finish();
        }

        setDiaryView();

        c_diary.close();
        c_location.close();
        mDB.close();
    }

    void deleteDiary(int id){
        mDB = getActivity().openOrCreateDatabase("Dangol", MODE_PRIVATE, null);

        try {
            Cursor c_loc = mDB.rawQuery("Select LocationID FROM Diary WHERE Diary.DiaryID =" + id, null);
            Cursor c_del = mDB.rawQuery("Delete FROM Diary WHERE Diary.DiaryID =" + id, null);

            String locationID = c_loc.getString(c_loc.getColumnIndexOrThrow("LocationID"));

            if(!c_loc.isClosed())   c_loc.close();

            if (c_del != null) {
                if (c_del.moveToLast()) {
                    do {
                        Log.e("dangol_realDataList", "delete Success");
                    } while (c_del.moveToPrevious());
                }
                if(!c_del.isClosed())   c_del.close();
            }

            Cursor c_cnt = mDB.rawQuery("SELECT * FROM Diary WHERE Diary.LocationID =" + locationID, null);
            if(c_cnt.getCount() == 0){
                Cursor c_del_loc = mDB.rawQuery("Delete FROM Location WHERE Location.LocationID =" + locationID, null);
                if (c_del_loc != null) {
                    if (c_del_loc.moveToLast()) {
                        do {
                            Log.e("dangol_realDataList", "delete Success");
                        } while (c_del_loc.moveToPrevious());
                    }
                    if(!c_del_loc.isClosed())   c_del_loc.close();
                }
            }

            if(!c_cnt.isClosed())   c_cnt.close();

        }catch(SQLiteException se) {
            Log.e("dangol_realDataList", se.toString());
        }catch(NullPointerException ne){
            Log.e("dangol_realDataList", ne.toString());
        }catch(Exception e){
            Log.e("dangol_realDataList", e.toString());
        }

        mDB.close();

    }

    private int setDiaryCursor(int id) {
        int locId = -1;
        try {
            String sql = "SELECT * FROM Diary WHERE DiaryID=" + id;
            c_diary = mDB.rawQuery(sql, null);
            if(c_diary != null && c_diary.moveToFirst())
                locId = c_diary.getInt(c_diary.getColumnIndexOrThrow("LocationID"));
        } catch (SQLiteException se) {
            Log.e("dangol_diary_detail", se.toString());
        } catch (Exception e) {
            Log.e("dangol_diary_detail", e.toString());
        }
        return locId;
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
        Log.e("dangol_diary_detail", "setDiaryView Start");
        TextView tv;
        ImageView iv;
        int imageFlag;
        String []dateTime;


        String date = c_diary.getString(c_diary.getColumnIndexOrThrow("Time"));
        if(date != null)
            date = date.substring(0, date.indexOf(" "));
        tv = (TextView)view.findViewById(R.id.diary_date);

        dateTime = date.split(" ");

        if (dateTime.length > 1) {
            if(dateTime[1] == "00:00:00"){
                tv.setText(dateTime[0]);
            }
            else{
                tv.setText(date);
            }
        } else {
            tv.setText(date);
        }


        tv = (TextView)view.findViewById(R.id.diary_location);
        tv.setText(c_location.getString(c_location.getColumnIndexOrThrow("Name")));


        iv = (ImageView)view.findViewById(R.id.diary_emotion);
        imageFlag = c_diary.getInt(c_diary.getColumnIndexOrThrow("Mood"));
        switch(imageFlag){
            case 0: iv.setImageResource(R.drawable.feel_best);  break;
            case 1: iv.setImageResource(R.drawable.feel_good);  break;
            case 2: iv.setImageResource(R.drawable.feel_soso);  break;
            case 3: iv.setImageResource(R.drawable.feel_notgood);   break;
            case 4: iv.setImageResource(R.drawable.feel_bad);   break;
        }

        iv = (ImageView)view.findViewById(R.id.diary_weather);
        imageFlag = c_diary.getInt(c_diary.getColumnIndexOrThrow("Weather"));
        switch(imageFlag){
            case 0: iv.setImageResource(R.drawable.weather_sunny);  break;
            case 1: iv.setImageResource(R.drawable.weather_sunny_cloud);    break;
            case 2: iv.setImageResource(R.drawable.weather_cloudy); break;
            case 3: iv.setImageResource(R.drawable.weather_rainy);  break;
            case 4: iv.setImageResource(R.drawable.weather_snow);   break;
        }

        Log.e("dangol_diary_detail", "setDiaryView 2");

        iv = (ImageView)view.findViewById(R.id.diary_image);
        String imagePath = c_diary.getString(c_diary.getColumnIndexOrThrow("Photo"));
        if (imagePath != null) {
            Bitmap bitmap = new ImageSaver(getActivity()).
                    setFileName(imagePath).
                    setDirectoryName("images").
                    load();

            Log.e("dangol_detail_frag", "bitmap: " + bitmap);
            iv.setImageBitmap(bitmap);
        } else {
            Log.e("dangol_detail_frag", "no image");
            iv.setVisibility(View.GONE);
        }

        Log.e("dangol_diary_detail", "setDiaryView 3");

        tv = (TextView)view.findViewById(R.id.diary_title);
        tv.setText(c_diary.getString(c_diary.getColumnIndexOrThrow("Title")));

        tv = (TextView)view.findViewById(R.id.diary_text);
        tv.setText(c_diary.getString(c_diary.getColumnIndexOrThrow("Text")));
    }
}
