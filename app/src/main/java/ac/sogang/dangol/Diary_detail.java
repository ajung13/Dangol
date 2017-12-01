package ac.sogang.dangol;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Diary_detail extends AppCompatActivity {
    private int dataID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        Intent intent = getIntent();
        dataID = intent.getIntExtra("id", 0);
        if(dataID == 0){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            Log.e("dangol_diary_detail", "no diary id");
            finish();
        }
        DiaryDetailFragment frag = new DiaryDetailFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.diary_detail_frag, frag);
        ft.commit();
    }

    public int getDataID(){
        return dataID;
    }
    public void onBackPressed(View v){
        super.onBackPressed();
    }
}
