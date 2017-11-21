package ac.sogang.dangol;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        final Date date_now = new Date(System.currentTimeMillis());
        _year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date_now));
        _month = date_now.getMonth() + 1;
        _date = date_now.getDate();
        setDate();

        location = new LatLng(37.552030, 126.9370623);
        setLocation();
    }

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
                Log.e("dangol_write", "new location record: " + location.latitude + ", "
                + location.longitude + " (" + location_name + ")");
                setLocation();
            }
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

        startActivity(intent);
        finish();
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
        String tmp = String.format("%.3f", location.latitude) + ", " +
                String.format("%.3f", location.longitude);
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
