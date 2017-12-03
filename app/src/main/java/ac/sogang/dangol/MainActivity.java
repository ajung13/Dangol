package ac.sogang.dangol;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationManager manager = null;
    private GPSListener gpsListener = null;
    private Location lastlocation;

    ArrayList<Marker> markers = new ArrayList<>();
    private int fragment_num;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fragment_num = 0;

        checkDangerousPermissions();
        setLayout();

    }

    private void setLayout(){
        int realData = realDataCnt();
        if(realData <= 0)   return;

        FrameLayout fl = (FrameLayout)findViewById(R.id.main_frame_layout);
        RelativeLayout rl = new RelativeLayout(this);
        rl.setId(R.id.realDataLayout);
        rl.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 150));
        rl.setBackgroundColor(getResources().getColor(R.color.white));
        rl.setAlpha((float)0.9);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RealDataListActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(this);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        tv.setLayoutParams(params1);
        tv.setText("현재 " + realData + "개 장소에 대한 기록을 남길 수 있습니다.");
        tv.setTextColor(getResources().getColor(R.color.contents));
        tv.setTextSize(15);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.diary_next);
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params2.setMargins(20, 20, 20, 20);
        iv.setLayoutParams(params2);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        rl.addView(tv);
        rl.addView(iv);
        fl.addView(rl);
    }

    private int realDataCnt(){
        String dbName = "Dangol";
        SQLiteDatabase mDB;
        int cnt = 0;
        String sql = "SELECT * from realData;";

        mDB = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        Cursor c = mDB.rawQuery(sql,null);
        cnt = c.getCount();

        c.close();
        mDB.close();
        return cnt;
    }

    public void onWriteClicked(View v) {
        Intent intent = new Intent(MainActivity.this, WritingActivity.class);
        if(lastlocation != null) {
            intent.putExtra("lat", lastlocation.getLatitude());
            intent.putExtra("lon", lastlocation.getLongitude());
        }
        startActivity(intent);
    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED)
                break;
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            Log.e("dangol_main(1)", "Permission granted");
//            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        else {
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            Log.e("dangol_main(2)", "Permission denied");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]))
                Toast.makeText(this, "Explain for permission", Toast.LENGTH_SHORT).show();
            else
                ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        mMap.setMyLocationEnabled(true);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;

        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);

            lastlocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastlocation != null) {
                Double latitude = lastlocation.getLatitude();
                Double longitude = lastlocation.getLongitude();
                Log.e("dangol_main(3)", "Last known location: " + latitude + "\t" + longitude);
            }
        } catch (SecurityException se) {
            Log.e("dangol_main(4)", "catch " + se.getMessage());
        }

        Toast.makeText(this, "Check log", Toast.LENGTH_SHORT).show();

        TimeThread timethread = new TimeThread();
        timethread.start();
        addMarkerOnView();
        if(!markers.isEmpty())
            setCameraZoomToMarker();


        if(dangolApp.th == null) {
            dangolApp.th = new TimeThread();
            dangolApp.th.start();
        }
        else if(!dangolApp.th.isAlive()){
            dangolApp.th.start();
        }
    }

    void addMarkerOnView() {
 /*       LatLng[] positions = {
                new LatLng(37.552030, 126.9370623),
                new LatLng(37.552030, 126.9360623),
                new LatLng(37.552030, 126.9380623),
                new LatLng(37.556030, 126.9380623),
                new LatLng(37.559030, 126.9370623)
        };*/
        LatLng[] positions = selectLocations();
        if(positions != null) {
            for (int i = 0; i < positions.length; i++) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(positions[i]).title("마커 " + i + "번")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_gray)));
                markers.add(marker);
            }

            mMap.setOnMarkerClickListener(this);
        }
    }

    void setCameraZoomToMarker() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.moveCamera(cu);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        MapDialog mDialog = new MapDialog();
        mDialog.dialogInit(marker.getPosition());

        mDialog.show(getSupportFragmentManager(), "0");
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, permissions[i] + " permission granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, permissions[i] + " permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            Log.e("dangol_main(5)", "Location Changed: " + latitude + "\t" + longitude);

            LatLng myLocation = new LatLng(latitude, longitude);
            lastlocation = location;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 19));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public class TimeThread extends Thread {
        String dbName = "Dangol";
        long sleepTime = 180000;

        SQLiteDatabase mDB;

        public void run() {
//            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            String sql = "";
            String nowDateTime = "";

            try {

                Log.e("dangol_main", "start thread");

                //제일 처음 위치를 받아옴 (초기화)
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, gpsListener );

                sleep(2000);
                Location location1 = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location1 == null)   location1 = lastlocation;
                Double latitude = location1.getLatitude();
                Double longitude = location1.getLongitude();
                nowDateTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(System.currentTimeMillis());

                int count = 0;
                while(true){

                    //                  manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
                    //                   manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
                    try{
                        //잠을 재운다
                        sleep(sleepTime);

                        //3분 후 값을 읽어온다
                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, gpsListener );
                        Location location2 = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if(location2 == null)   location2 = lastlocation;
                        if(location2 == null)   break;
                        String nowDateTime2 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(System.currentTimeMillis());

                        Log.e("insert_sql0", "location(prev): " + location1.getLatitude() + ", " + location1.getLongitude());
                        Log.e("insert_sql0", "location(now): " + location2.getLatitude() + ", " + location2.getLongitude());
                        //                  SQLiteDatabase mDB = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                        if (location1.distanceTo(location2) <= 10) {
                             /* Location Class에 존재하는 distanceTo 함수, 두 지점 사이의 거리를 Meter 단위로 반환, 만약 두 지점 사이가 10m 이하이면 count++ */
                            count++;
                            String str = location2.getLatitude() + ", "+ location2.getLongitude()+", ";
                            Log.e("check_data", str + Integer.toString(count));
                        }
                        else {
                            if(count >= 7) {
                                // 유효한 데이터일 경우 데이터 저장
                                mDB = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                                sql = "INSERT INTO realData(Latitude, Longitude, Time) VALUES (" + latitude + ", "+ longitude +", '"+ nowDateTime + "');";

                                mDB.execSQL(sql);
                                Log.e("check_data", sql);
                                mDB.close();

                            }

                            // 데이터 리셋, 위치 재설정
                            count = 0;
                            location1 = location2;
                            latitude = location2.getLatitude();
                            longitude = location2.getLongitude();
                            nowDateTime = nowDateTime2;
                        }
                    }catch(SQLiteException se){
                        Log.e("insert_sql", se.toString());
                    }catch(Exception e){
                        Log.e("insert", e.toString());
                    }
                }
            }catch(SecurityException se){
                Log.e("dangol_main(8)", se.toString());
            }catch(Exception e){
                Log.e("dangol_main(9)", e.toString());
            }
            Log.e("dangol_main", "thread dead at " + nowDateTime);
        }
    }

    public void  dataFilter(SQLiteDatabase mDB){
//        SQLiteDatabase mDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        try {
//            String sql = "SELECT * FROM readData where readDate = date('now', '-1 days')";
//            String sql = "SELECT * FROM readData where readDate = date('now')";
            String sql = "SELECT * FROM readData;";
            Cursor c = mDB.rawQuery(sql, null);
            String data = "";
            if (c != null) {
                if (c.moveToFirst()) {
                    int i = 0;
                    data += getPackageName() + ": ";
                    do {
                        data += c.getString(i++) + "\t";
                        Log.e("dangol_checkdata", c.getString(i));
                    } while (c.moveToNext());
                    Log.e("checkData", data);
                    Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
                }
                if(!c.isClosed())   c.close();
            }
        }catch(SQLiteException se){
            Log.e("check_sql", se.toString());
        }catch(Exception e){
            Log.e("check", e.toString());
        }
//        mDB.close();
    }

    public void  dataFilter(){

        SQLiteDatabase mDB = this.openOrCreateDatabase("Dangol", MODE_PRIVATE, null);

        try {
//            String sql = "SELECT * FROM readData where readDate = date('now', '-1 days')";
//            String sql = "SELECT * FROM readData where readDate = date('now')";
            String sql = "SELECT * FROM readData;";
            Cursor c = mDB.rawQuery(sql, null);
            String data = "";
            if (c != null) {
                if (c.moveToFirst()) {
                    int i = 0;
                    data += getPackageName() + ": ";
                    do {
                        data += c.getString(i++) + "\t";
                        Log.e("dangol_checkdata", c.getString(i));
                    } while (c.moveToNext());
                    Log.e("checkData", data);
                    Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
                }
                if(!c.isClosed())   c.close();
            }
        }catch(SQLiteException se){
            Log.e("check_sql", se.toString());
        }catch(Exception e){
            Log.e("check", e.toString());
        }
//        mDB.close();
    }

    public void changeFragment(View v){
        int flag = 0;
        switch(v.getId()){
            case R.id.menu_diary:
                if(fragment_num != 1)
                    flag = 1;
                break;
            case R.id.menu_pin:
                if(fragment_num != 0)
                    flag = 2;
                break;
            default:
                break;
        }

        ImageButton ib_d = (ImageButton)findViewById(R.id.menu_diary);
        ImageButton ib_p = (ImageButton)findViewById(R.id.menu_pin);

        if(flag == 2){
            Log.e("dangol_main(11)", "return to map");
            fragment_num = 0;
            ib_d.setBackgroundResource(R.drawable.menu_diary_gray);
            ib_p.setBackgroundResource(R.drawable.menu_pin_blue);
            setLayout();
            super.onBackPressed();
        }
        else if(flag == 1){
            Log.e("dangol_main(12)", "show diary");
            fragment_num = 1;
            ib_d.setBackgroundResource(R.drawable.menu_diary_blue);
            ib_p.setBackgroundResource(R.drawable.menu_pin_gray);
            FrameLayout fl = (FrameLayout)findViewById(R.id.main_frame_layout);
            fl.removeView(findViewById(R.id.realDataLayout));
            Fragment fragment = new DiaryFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.map, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        else
            Log.e("dangol_main(13)", "nothing to show");
    }

    @Override
    public void onBackPressed(){
        if(fragment_num == 1){
            fragment_num = 0;
            ImageButton ib_d = (ImageButton)findViewById(R.id.menu_diary);
            ImageButton ib_p = (ImageButton)findViewById(R.id.menu_pin);
            ib_d.setBackgroundResource(R.drawable.menu_diary_gray);
            ib_p.setBackgroundResource(R.drawable.menu_pin_blue);
            setLayout();
        }
        super.onBackPressed();
    }

    private LatLng[] selectLocations(){
        LatLng[] result;
        SQLiteDatabase mDB;
        Cursor c;

        try{
            mDB = this.openOrCreateDatabase("Dangol", MODE_PRIVATE, null);
            c = mDB.rawQuery("SELECT * FROM Location", null);


            if(c.getCount() != 0 && c.moveToFirst()){
                result = new LatLng[c.getCount()];
                int idx = 0;
                do{
                    double lat = c.getDouble(c.getColumnIndexOrThrow("Latitude"));
                    double lon = c.getDouble(c.getColumnIndexOrThrow("Longitude"));
                    result[idx] = new LatLng(lat, lon);
//                        Log.e("dangol_main_marker", idx + ") " + result[idx].latitude + ", " + result[idx].longitude);
                    idx++;
                }while(c.moveToNext());
            }
            else{
                Toast.makeText(this, "아직 기록이 없어요!", Toast.LENGTH_SHORT).show();
                Log.e("dangol_main_marker", "not write yet");
                return null;
            }
            if(!c.isClosed())   c.close();
            mDB.close();
            return result;
        }catch(SQLiteException se){
            Log.e("dangol_main_marker", se.toString());
        }catch(Exception e){
            Log.e("dangol_main_marker", e.toString());
        }

        return null;
    }
}
