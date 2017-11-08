package ac.sogang.dangol;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WritingActivity extends AppCompatActivity {
    int _year = 0;
    int _month = 0;
    int _date = 0;
    int emotion = 0;
    int weather = 0;
    String title = "";
    String contents = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        Date date_now = new Date(System.currentTimeMillis());
        _year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date_now));
        _month = date_now.getMonth() + 1;
        _date = date_now.getDate();
        setDate();
    }

    public void onBackPressed(View v){
        super.onBackPressed();
    }

    public void onSaveClicked(View v){
        RadioGroup rg_e = (RadioGroup)findViewById(R.id.write_emotion);
//        if(!rg_e.isSelected())    emotion = 0;        //default
//        else{
            emotion = rg_e.getCheckedRadioButtonId() - R.id.write_icon1;
            if(emotion!=0)
                emotion -= 5;
//        }

        RadioGroup rg_w = (RadioGroup)findViewById(R.id.write_weather);
        if(!rg_w.isSelected())    weather = 0;        //default
        weather = rg_w.getCheckedRadioButtonId() - R.id.write_icon11;

        EditText et = (EditText)findViewById(R.id.write_title);
        title = et.getText().toString();

        et = (EditText)findViewById(R.id.write_contents);
        contents = et.getText().toString();

        uploadDB();

        finish();
    }

    private void uploadDB(){
        Toast.makeText(getApplicationContext(), "기분: " + emotion + ", 날씨: " + weather,
                Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "제목: " + title + ", 내용: " + contents, Toast.LENGTH_SHORT).show();
    }

    private void setDate(){
        Button tv = (Button)findViewById(R.id.write_calendar);
        String tvPrint = _year + "-" + _month + "-" + _date;
        tv.setText(tvPrint);
    }

    public void onCalendarClicked(View v){
        DatePickerDialog dialog = new DatePickerDialog(this, listener, _year, _month - 1, _date);
        dialog.show();
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
