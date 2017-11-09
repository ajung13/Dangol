package ac.sogang.dangol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WritingActivity extends AppCompatActivity {
    String date = "";
    int emotion = 0;
    int weather = 0;
    String title = "";
    String contents = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

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
        Toast.makeText(getApplicationContext(), "날짜: " + date + ", 기분: " + emotion + ", 날씨: " + weather,
                Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "제목: " + title + ", 내용: " + contents, Toast.LENGTH_SHORT).show();
    }

    private void setDate(){
        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(date_now);

        TextView tv = (TextView)findViewById(R.id.write_calendar);
        tv.setText(date);
    }
}
