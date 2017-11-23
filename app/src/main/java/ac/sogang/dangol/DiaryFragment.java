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
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Hyunah on 2017-11-13.
 */

public class DiaryFragment extends Fragment {
    String dbName = "Dangol";
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        view = inflater.inflate( R.layout.fragment_diary, container, false );
        TextView tv = (TextView)view.findViewById(R.id.diary);
        String tmp = selectDB();
        tv.setText(tmp);
        return view;
    }

    private String selectDB(){
        SQLiteDatabase mDB = getActivity().openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        String data = "";

        try {
            Cursor c = mDB.rawQuery("SELECT * FROM Diary", null);

 //           Log.e("lala","오긴하는거니?");
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String tmp;
                        tmp = "ID: " + c.getInt(c.getColumnIndex("DiaryID")) +
                                "\nMood: " + c.getInt(c.getColumnIndex("Mood")) +
                                "\nWeather: " + c.getInt(c.getColumnIndex("Weather")) +
                                "\nTitle: " + c.getString(c.getColumnIndex("Title")) +
                                "\nText: " + c.getString(c.getColumnIndex("Text")) + "\n\n";
                        data += tmp;
                    } while (c.moveToNext());
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
        return data;
    }
}
