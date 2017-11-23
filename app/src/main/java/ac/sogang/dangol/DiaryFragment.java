package ac.sogang.dangol;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Hyunah on 2017-11-13.
 */

public class DiaryFragment extends Fragment {
    String dbName = "Dangol";
    View view;

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        view = inflater.inflate( R.layout.fragment_diary, container, false );

        mListView = (ListView)view.findViewById(R.id.main_diary_list);
        dataSetting();

        return view;
    }

    private void dataSetting(){
        SQLiteDatabase mDB = getActivity().openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        MyAdapter mMyAdapter = new MyAdapter();

        try {
            Cursor c = mDB.rawQuery("SELECT DiaryID, Title, Text, Time FROM Diary", null);

            if (c != null) {
                if (c.moveToLast()) {
                    do {
                        String title, content, date;
                        title = c.getString(c.getColumnIndexOrThrow("Title"));
                        content = c.getString(c.getColumnIndexOrThrow("Text"));
                        date = c.getString(c.getColumnIndexOrThrow("Time"));
                        int id = c.getInt(c.getColumnIndexOrThrow("DiaryID"));

                        Log.e("dangol_diary", "not throwed yet/" + date);

                        if(title.length() > 30)
                            title = title.substring(0, 30) + "...";
                        if(content.length() > 30)
                            content = content.substring(0, 30) + "...";
                        if(date == null)    date = "0";
                        date = date.substring(0, date.indexOf(" "));

                        mMyAdapter.addItem(title, content, date, id);
                    } while (c.moveToPrevious());
                }
                if(!c.isClosed())   c.close();
            }
        }catch(SQLiteException se) {
            Log.e("dangol_diary", se.toString());
        }catch(NullPointerException ne){
            Log.e("dangol_diary", ne.toString());
        }catch(Exception e){
            Log.e("dangol_diary", e.toString());
        }
        mDB.close();

        mListView.setAdapter(mMyAdapter);
    }
}
