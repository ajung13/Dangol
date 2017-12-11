package ac.sogang.dangol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class GuideActivity extends AppCompatActivity {
    ImageView iv;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        Log.e("dangol_guide", "first run");

        iv = (ImageView)findViewById(R.id.guideImage);
        flag = 0;
        iv.setImageResource(R.drawable.guide0);

//        Intent intent = new Intent(this, TimeThread.class);
//        startService(intent);
    }

    public void guideImageChange(View v){
        flag++;
        switch(flag){
            case 1: iv.setImageResource(R.drawable.guide1); break;
            case 2: iv.setImageResource(R.drawable.guide2); break;
            case 3: iv.setImageResource(R.drawable.guide3); break;
            case 4: iv.setImageResource(R.drawable.guide5); break;
            case 5:
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
        }
    }
}
