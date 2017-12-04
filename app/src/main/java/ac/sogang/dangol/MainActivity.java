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
import android.os.AsyncTask;
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

import static java.lang.Thread.sleep;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    static private GoogleMap mMap;
    static private LocationManager manager = null;
    static private GPSListener gpsListener = null;
    static private Location lastlocation;

    ArrayList<Marker> markers = new ArrayList<>();
    private int fragment_num;

    private boolean startFlag = false;

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

    @Override
    protected void onResume(){
        super.onResume();
        if(!startFlag){
            startFlag = true;
            return;
        }
        Log.e("dangol_main", "refresh");

        if(fragment_num == 0)
            addMarkerOnView();
        else{
            changeFragment(findViewById(R.id.menu_pin));
            changeFragment(findViewById(R.id.menu_diary));
        }

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

        if(dangolApp.th == null) {
            dangolApp.th = new TimeThread(getApplicationContext());
            dangolApp.th.execute();
        }
        else if(dangolApp.th.isCancelled())
            dangolApp.th.execute();

        addMarkerOnView();
        if(!markers.isEmpty())
            setCameraZoomToMarker();
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
        mMap.clear();
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

    static private class GPSListener implements LocationListener {
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

    static public class TimeThread extends AsyncTask<Void, Void, Void>{
        private Context context;
        public TimeThread(Context con){
            this.context = con;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Log.e("dangol_task", "task pre-execute");
        }
        @Override
        protected Void doInBackground(Void... params){
            String dbName = "Dangol";
            long sleepTime = 180000;
//            long sleepTime = 10000;
            long minTime = 5000;
            float minDistance = 10;

            SQLiteDatabase mDB;
            String nowDateTime = "";

            try {
                String sql = "";
                //제일 처음 위치를 받아옴 (초기화)
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener );
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);

                sleep(2000);
                Location location1 = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location1 == null)   location1 = lastlocation;
                Double latitude = location1.getLatitude();
                Double longitude = location1.getLongitude();
                nowDateTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(System.currentTimeMillis());

                int count = 0;
                while(true) {
                    //잠을 재운다
                    sleep(sleepTime);

                    //3분 후 값을 읽어온다
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, gpsListener);
                    Location location2 = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location2 == null) location2 = lastlocation;
                    if (location2 == null) break;
                    String nowDateTime2 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(System.currentTimeMillis());

                    if (location1.distanceTo(location2) <= 10) {
                        // Location Class에 존재하는 distanceTo 함수, 두 지점 사이의 거리를 Meter 단위로 반환, 만약 두 지점 사이가 10m 이하이면 count++
                        count++;
                        String str = location2.getLatitude() + ", "+ location2.getLongitude()+", ";
                        Log.e("dangol_task_check_data", str + Integer.toString(count));
                    } else {
                        if (count >= 7) {
                            // 유효한 데이터일 경우 데이터 저장
                            mDB = context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                            sql = "INSERT INTO realData(Latitude, Longitude, Time) VALUES (" + latitude + ", " + longitude + ", '" + nowDateTime + "');";
                            mDB.execSQL(sql);
                            Log.e("dangol_task", sql);
                            mDB.close();
                        }

                        // 데이터 리셋, 위치 재설정
                        count = 0;
                        location1 = location2;
                        latitude = location2.getLatitude();
                        longitude = location2.getLongitude();
                        nowDateTime = nowDateTime2;
                    }
                }
            }catch(SecurityException se){
                Log.e("dangol_task", "se: " + se.toString());
            }catch(InterruptedException ie){
                Log.e("dangol_task", "ie: " + ie.toString());
            }catch(SQLiteException sqe){
                Log.e("dangol_task", "sqe: " + sqe.toString());
            }catch(Exception e){
                Log.e("dangol_task", e.toString());
            }
            Log.e("dangol_main", "thread dead at " + nowDateTime);
            return null;
        }

        @Override
        protected void onPostExecute(Void param){
            super.onPostExecute(param);
            Toast.makeText(context, "Dangol thread dead", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
            Log.e("dangol_task", "cancelled");
        }
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
            ib_p.setBackgroundResource(R.drawable.menu_pin_brown);
            setLayout();
            addMarkerOnView();
            super.onBackPressed();
        }
        else if(flag == 1){
            Log.e("dangol_main(12)", "show diary");
            fragment_num = 1;
            ib_d.setBackgroundResource(R.drawable.menu_diary_brown);
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
            ib_p.setBackgroundResource(R.drawable.menu_pin_brown);
            setLayout();
            addMarkerOnView();
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
