package ac.sogang.dangol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class RealDataListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_data_list);

        addList();
    }

    private void addList(){
        myAdapter_RealData adapter = new myAdapter_RealData();

        adapter.addItem("17.11.29", "22:11:05");
        adapter.addItem("17.11.30", "10:38:26");
        adapter.addItem("17.12.01", "18:00:00");

        ListView lv = (ListView)findViewById(R.id.realDataList);
        lv.setAdapter(adapter);
    }
}
