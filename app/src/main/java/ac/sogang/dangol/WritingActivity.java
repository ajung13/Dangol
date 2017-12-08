package ac.sogang.dangol;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;

import static ac.sogang.dangol.WritingMapActivity.EXTRA_STUFF;

public class WritingActivity extends AppCompatActivity {
    int _year = 0;
    int _month = 0;
    int _date = 0;

    LatLng location;
    String location_name;

    private static final int MAP_ACTIVITY_RESULT_CODE = 0;


    // 이미지용
    Context context;
    private static int RESULT_LOAD_IMAGE = 1;
    ImageView myImageView;
    String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);
        Intent intent = getIntent();

        _year = intent.getIntExtra("year", 0);
        _month = intent.getIntExtra("month", 0);
        _date = intent.getIntExtra("date", 0);
        if(_year == 0) {
            final Date date_now = new Date(System.currentTimeMillis());
            _year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date_now));
            _month = date_now.getMonth() + 1;
            _date = date_now.getDate();
        }
        setDate();

        location = new LatLng(intent.getDoubleExtra("lat", 37.555262), intent.getDoubleExtra("lon", 126.970679));
        location_name = intent.getStringExtra("name");
        if(location_name == null)   location_name = "현재 위치";
        setLocation();

        myImageView = (ImageView) findViewById(R.id.thumbnail);
//        getImageTest();

    }

    // 이미지 저장 테스트
/*    void getImageTest() {
        SQLiteDatabase mDB = openOrCreateDatabase("Dangol", MODE_PRIVATE, null);

        try{
            String sql = "SELECT Photo FROM Diary";
            Cursor c = mDB.rawQuery(sql, null);
            Log.e("dangol_write_test", sql);

            if (c != null) {
                if (c.moveToLast()) {
                        String imageName;
                        imageName = c.getString(c.getColumnIndexOrThrow("Photo"));
                        Log.e("dangol_write_test", "image Path: " + imageName);

                        Bitmap bitmap = new ImageSaver(getApplicationContext()).
                            setFileName(imageName).
                            setDirectoryName("images").
                            load();

                        Log.e("dangol_write", "bitmap: " + bitmap);
                        myImageView.setImageBitmap(bitmap);

                    if(!c.isClosed()) c.close();
                }
            }
        }catch(SQLiteException se){
            Log.e("dangol_write_test", se.toString());
        }catch(Exception e){
            Log.e("dangol_write_test", e.toString());
        }
        mDB.close();
    }*/

    public void onBackPressed(View v) {
        super.onBackPressed();
    }

    public void onCalendarClicked(View v){
        DatePickerDialog dialog = new DatePickerDialog(this, listener, _year, _month - 1, _date);
        dialog.show();
    }

    public void onPositionClicked(View v){
        Intent intent = new Intent(WritingActivity.this, WritingMapActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, "my text");
        intent.putExtra("location", location);
        startActivityForResult(intent, MAP_ACTIVITY_RESULT_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == MAP_ACTIVITY_RESULT_CODE){
            if(resultCode == RESULT_OK){
                location = data.getParcelableExtra(EXTRA_STUFF);
                location_name = data.getStringExtra("name");

                if(location == null)
                    return;
                if(location_name == null || location_name.equals(""))
                    location_name = "선택된 위치";

                Log.e("dangol_write", "new location record: " + location.latitude + ", "
                + location.longitude + " (" + location_name + ")");
                setLocation();
            }
        }

        // 이미지 가져오기
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            myImageView.setImageURI(imageUri);

            imagePath = imageUri.toString();

            Log.e("dangol_write", "imageUri: " + imageUri);
        }
    }

    public void onNextClicked(View v){
        Intent intent = new Intent(WritingActivity.this, Writing2Activity.class);
        intent.putExtra("year", _year);
        intent.putExtra("month", _month);
        intent.putExtra("date", _date);

        int emotion = 0;
        RadioGroup rg_e = (RadioGroup)findViewById(R.id.write_emotion);
        if(!rg_e.isSelected()){
            switch (rg_e.getCheckedRadioButtonId()){
                case R.id.write_icon1:  emotion = 0;    break;
                case R.id.write_icon2:  emotion = 1;    break;
                case R.id.write_icon3:  emotion = 2;    break;
                case R.id.write_icon4:  emotion = 3;    break;
                case R.id.write_icon5:  emotion = 4;    break;
                default:    emotion = 0;    break;
            }
        }

        int weather = 0;
        RadioGroup rg_w = (RadioGroup)findViewById(R.id.write_weather);
        if(!rg_w.isSelected()){
            switch (rg_w.getCheckedRadioButtonId()){
                case R.id.write_icon11:  weather = 0;    break;
                case R.id.write_icon12:  weather = 1;    break;
                case R.id.write_icon13:  weather = 2;    break;
                case R.id.write_icon14:  weather = 3;    break;
                case R.id.write_icon15:  weather = 4;    break;
                default:    weather = 0;    break;
            }
        }

        intent.putExtra("emotion", emotion);
        intent.putExtra("weather", weather);
        intent.putExtra("location", location);
        intent.putExtra("location_name", location_name);

        if(imagePath != null)
            intent.putExtra("thumbnail", imagePath);
        startActivity(intent);
        finish();
    }

    public void onGetImageButtonClicked(View v) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, RESULT_LOAD_IMAGE);
    }

    private void setDate(){
        try {
            Button tv = (Button) findViewById(R.id.write_calendar);
            String tvPrint = _year + "-" + _month + "-" + _date;
            tv.setText(tvPrint);
        }catch(NullPointerException ne){
            Log.e("dangol_write1", ne.toString());
        }catch(Exception e){
            Log.e("dangol_write1", e.toString());
        }
    }
    private void setLocation(){
        TextView tv = (TextView)findViewById(R.id.write_location);
        String tmp = location_name + " (" +String.format("%.3f", location.latitude) + ", " +
                String.format("%.3f", location.longitude) + ")";
        tv.setText(tmp);
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            _year = year;
            _month = month + 1;
            _date = dayOfMonth;
            Log.e("dangol_writing", "new date set: " + year + "-" + month + "-" + dayOfMonth);
            setDate();
        }
    };

}
