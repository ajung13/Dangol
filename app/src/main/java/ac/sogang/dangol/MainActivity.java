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
import android.widget.ImageButton;
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

        addMarkerOnView();
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
        }
        mMap.setOnMarkerClickListener(this);
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
        long sleepTime = 5000;

        public void run() {
//            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            try {
                Log.e("dangol_main(6)", "start thread");
  /*              if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }*/

                while(true){
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener );
                    Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location == null)    break;
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String nowTime = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").format(System.currentTimeMillis());

                    insertLocationDB(latitude, longitude, nowTime);
                    sleep(sleepTime);
                }
            }catch(SecurityException se){
                Log.e("dangol_main(8)", se.toString());
            }catch(Exception e){
                Log.e("dangol_main(9)", e.toString());
            }

            Log.e("dangol_main(10)", "주금");

        }
    }

    private void insertLocationDB(double lat, double lon, String time){
        SQLiteDatabase mDB = openOrCreateDatabase("Dangol", MODE_PRIVATE, null);
        try{
            String sql = "insert into readData(Latitude, Longitude, Time) values (" + lat + ", " +
                    lon + ", '" + time + "');";
            Log.e("dangol_main_readData", sql);
            mDB.execSQL(sql);
        }catch(SQLiteException se){
            Log.e("dangol_main(14)", se.toString());
        }catch(Exception e){
            Log.e("dangol_main(15)", e.toString());
        }
        mDB.close();
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
            super.onBackPressed();
        }
        else if(flag == 1){
            Log.e("dangol_main(12)", "show diary");
            fragment_num = 1;
            ib_d.setBackgroundResource(R.drawable.menu_diary_blue);
            ib_p.setBackgroundResource(R.drawable.menu_pin_gray);
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
            result = new LatLng[c.getCount()];
            int idx = 0;

            if(c.getCount() != 0){
                if(c.moveToFirst()){
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
                }
                if(!c.isClosed())   c.close();
            }
            mDB.close();
            return result;
        }catch(SQLiteException se){
            Log.e("dangol_main_marker", se.toString());
        }catch(Exception e){
            Log.e("dangol_main_marker", e.toString());
        }

        return null;
    }

    public void onDBUpdateClicked(View v){
        SQLiteDatabase mDB;
        try{
            mDB = openOrCreateDatabase("Dangol", MODE_PRIVATE, null);
            Cursor c = mDB.rawQuery("SELECT * FROM readData", null);
            if(c != null && c.getCount() > 0){
                if(c.moveToFirst()){
                    do{
                        double lat = c.getDouble(c.getColumnIndexOrThrow("Latitude"));
                        double lon = c.getDouble(c.getColumnIndexOrThrow("Longitude"));
                        String time = c.getString(c.getColumnIndexOrThrow("Time"));
                        String date = time.substring(0, time.indexOf(" "));
                        time = time.substring(time.indexOf(" ")+1);
                        //TODO: do your logic here
                        Log.e("dangol_main_update", "lat: " + lat + ", lon: " + lon + ", date: " + date + "time:" + time);
                    }while(c.moveToNext());
                }
            }
            if(c != null && !c.isClosed())    c.close();

            //delete all
            mDB.execSQL("DELETE FROM readData");
            mDB.close();
        }catch(SQLiteException se){
            Log.e("dangol_main(16)", se.toString());
        }catch(Exception e){
            Log.e("dangol_main(17)", e.toString());
        }
    }
}