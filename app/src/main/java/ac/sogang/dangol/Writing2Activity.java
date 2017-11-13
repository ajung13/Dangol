package ac.sogang.dangol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Writing2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing2);
    }

    public void onBackPressed(View v){
        Intent intent = new Intent(Writing2Activity.this, WritingActivity.class);
        startActivity(intent);
        finish();
    }
    public void onSavePressed(View v){
        Intent intent = getIntent();
        int year = intent.getIntExtra("year", 0);
        int month = intent.getIntExtra("month", 0);
        int date = intent.getIntExtra("date", 0);
        int emotion = intent.getIntExtra("emotion", -1);
        int weather = intent.getIntExtra("weather", -1);

        EditText et = (EditText)findViewById(R.id.write_title);
        String title = et.getText().toString();
        et = (EditText)findViewById(R.id.write_contents);
        String contents = et.getText().toString();

        uploadDB(year, month, date, emotion, weather, title, contents);
        finish();
    }

    public void uploadDB(int year, int month, int date, int emotion, int weather, String title, String contents){
        Log.e("dangol_write2", year + "-" + month + "-" + date + ", emotion: " + emotion + " weather: " + weather);
        Log.e("dangol_write2", "title: " + title + ", contents: " + contents);
        Toast.makeText(getApplicationContext(), "저장되었습니다!", Toast.LENGTH_SHORT).show();
    }
}
