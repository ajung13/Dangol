package ac.sogang.dangol;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by changnam on 2017. 11. 11..
 */

public class MapDialog extends DialogFragment {
    View v;
    LatLng location;

    public void dialogInit(LatLng input){
        this.location = input;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setView(R.layout.sample_dialog_map);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.sample_dialog_map, null);
        builder.setView(v);

        setTextViews();

        Button diaryBtn = (Button)v.findViewById(R.id.dialog_diary_button);
        if(diaryBtn == null)
            Log.e("dangol_dialog", "diary btn null");
        else
            diaryBtn.setOnClickListener(diaryOpenListener);
        ImageButton closeBtn = (ImageButton)v.findViewById(R.id.dialog_close_button);
        if(closeBtn == null)
            Log.e("dangol_dialog", "close btn null");
        else
            closeBtn.setOnClickListener(closeListener);

        AlertDialog dialog = builder.create();
        dialog.show();

//                .setPositiveButton("보기", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
        // Create the AlertDialog object and return it
        return dialog;
    }

    private void setTextViews(){
        TextView locName = (TextView)v.findViewById(R.id.mainTitle);
        TextView firstRec = (TextView)v.findViewById(R.id.firstItem);
        TextView secondRec = (TextView)v.findViewById(R.id.secondItem);
        int locID = -1;

        if(location == null){
            Log.e("dangol_dialog", "location null");
            return;
        }

        SQLiteDatabase mDB = getActivity().openOrCreateDatabase("Dangol", Context.MODE_PRIVATE, null);
        String tmp = "";
        Cursor c;
        try{
            String sql = "SELECT LocationID, Name FROM Location WHERE Latitude=" + location.latitude +
                    " AND Longitude=" + location.longitude;
            c = mDB.rawQuery(sql, null);
            if(c != null && c.getCount() > 0){
                c.moveToFirst();
                do{
                    tmp += c.getString(c.getColumnIndexOrThrow("Name"));
                    if(c.isFirst()) locID = c.getInt(c.getColumnIndexOrThrow("LocationID"));
                    if(!c.isLast()) tmp += ", ";
                }while(c.moveToNext());
            }
            if(c != null && !c.isClosed())  c.close();

            if(!tmp.equals(""))   locName.setText(tmp);
        }catch(SQLiteException se){
            Log.e("dangol_dialog(1)", se.toString());
        }catch(Exception e){
            Log.e("dangol_dialog(2)", e.toString());
        }

        tmp = "";
        try{
            String sql = "SELECT Time FROM Diary WHERE LocationID=" + locID;
            c = mDB.rawQuery(sql, null);
            if(c != null && c.getCount() > 0){
                c.moveToFirst();
                tmp = c.getString(c.getColumnIndexOrThrow("Time"));
                secondRec.setText(String.valueOf(c.getCount()));
            }
            else{
                secondRec.setText("0");
                Log.e("dangol_dialog", "no data");
            }
            if(c != null && !c.isClosed())  c.close();

            if(!tmp.equals("")){
                tmp = tmp.substring(0, tmp.indexOf(" "));
                firstRec.setText(tmp);
            }
        }catch(SQLiteException se){
            Log.e("dangol_dialog(1)", se.toString());
        }catch(Exception e){
            Log.e("dangol_dialog(2)", e.toString());
        }

        if(mDB != null && mDB.isOpen()) mDB.close();
    }

    Button.OnClickListener diaryOpenListener = new View.OnClickListener(){
        public void onClick(View v){

        }
    };

    Button.OnClickListener closeListener = new View.OnClickListener(){
        public void onClick(View v){
            Log.e("dangol_dialog", "close");
//            MapDialog.super.onDismiss(getDialog());
            getDialog().dismiss();
        }
    };
}
