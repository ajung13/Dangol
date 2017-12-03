package ac.sogang.dangol;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class Writing2Activity extends AppCompatActivity {
    String dbName = "Dangol";
    Intent intent;
    final double minDiff = 0.000001;
    boolean locationFlag;

    // imageView
    ImageView thumbnailImageView;
    String imagePath = null;
    Integer lastDiaryId = 1;
    String imageName = "";
//    String extension = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing2);
        intent = getIntent();

        thumbnailImageView = (ImageView) findViewById(R.id.thumbnail);

        imagePath = intent.getStringExtra("thumbnail");
        if(imagePath != null){
            setThumbnail();
            getLastDiaryId();
        }
    }

    void setThumbnail() {
        imagePath = intent.getStringExtra("thumbnail");

        if (imagePath != "") {
            Uri imageUri = Uri.parse(imagePath);
            thumbnailImageView.setImageURI(imageUri);
        } else {
            thumbnailImageView.setVisibility(View.GONE);
        }
    }

    void getLastDiaryId() {
        SQLiteDatabase mDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try {
            String sql;
            sql = "SELECT DiaryID FROM Diary ORDER BY DiaryID ASC LIMIT 1";

            Cursor c = mDB.rawQuery(sql, null);

            if (c.moveToFirst()){
                do {
                    // Passing values
                    String id = c.getString(0);

                    if (id != null && id != "") {
                        lastDiaryId = Integer.parseInt(id);
                        lastDiaryId += 1;
                    }

                } while(c.moveToNext());
            }
            c.close();

        }catch(SQLiteException se){
            Log.e("dangol_select_sql", se.toString());
        }catch(Exception e){
            Log.e("dangol_select_sql", e.toString());
        }
        mDB.close();
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

        saveImageToStorage();

        uploadDB(year, month, date, emotion, weather, location.latitude, location.longitude, location_name, title, contents);
        finish();
    }

    /*
       (내부)로컬 저장소에 이미지 저장
       형태 : DiaryImage1.png(번호는 DiaryID)
    */
    void saveImageToStorage() {

        if (imagePath != "" && imagePath != null) {
            Log.e("dangol_write2", "oing");
            Bitmap bitmap = ((BitmapDrawable) thumbnailImageView.getDrawable()).getBitmap();

            String id = Integer.toString(lastDiaryId);
            imageName = "DiaryImage" + id + ".png";

            Log.e("dangol_write2", "imageFile name: " + imageName);

            new ImageSaver(getApplicationContext()).
                    setFileName(imageName).
                    setDirectoryName("images").
                    save(bitmap);
        }
    }

    public void uploadDB(int year, int month, int date, int emotion, int weather, double lat, double lon, String name, String title, String contents){
        SQLiteDatabase mDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try{
            String sql;
            locationFlag = false;
            Log.e("dangol_write2", "upload db");
            int locID = checkLocationID(mDB, name, lat, lon);
            if(!locationFlag){
                sql = "INSERT INTO Location(Name, Latitude, Longitude) VALUES ('" + name + "', " +
                        lat + ", " + lon + ");";
                Log.e("dangol_insert", "sql(1): " + sql);
                mDB.execSQL(sql);
            }

            if (imagePath != "") {
                sql = "INSERT INTO Diary(LocationID, Mood, Weather, Title, Text, Time, Photo) VALUES (" +
                        locID + ", " + emotion + ", " + weather +
                        ", '" + title + "', '" + contents + "', '" + year + "-" + month + "-" + date + " 00:00:00'" + ", '" + imageName + "' " + ");";
            } else {
                sql = "INSERT INTO Diary(LocationID, Mood, Weather, Title, Text, Time) VALUES (" +
                        locID + ", " + emotion + ", " + weather +
                        ", '" + title + "', '" + contents + "', '" + year + "-" + month + "-" + date + " 00:00:00');";
            }

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
        int position = 1;
        try {
            Log.e("dangol_write2", "start");
            Cursor c = db.rawQuery("SELECT * FROM Location", null);
            Log.e("dangol_write2", "start2");
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        position++;
                        Log.e("dangol_write2", "start3");
                        String tmpName = c.getString(c.getColumnIndexOrThrow("Name"));
                        double tmpLat = c.getDouble(c.getColumnIndexOrThrow("Latitude"));
                        double tmpLon = c.getDouble(c.getColumnIndexOrThrow("Longitude"));
                        Log.e("dangol_write2", "start4");
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
            if (c != null && !c.isClosed()) c.close();
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
