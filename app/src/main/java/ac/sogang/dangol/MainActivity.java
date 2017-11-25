package ac.sogang.dangol;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
            Log.e("dangol_main", "Permission granted");
//            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        else {
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            Log.e("dangol_main", "Permission denied");
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

            Location lastlocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastlocation != null) {
                Double latitude = lastlocation.getLatitude();
                Double longitude = lastlocation.getLongitude();
                Log.e("dangol_main", "Last known location: " + latitude + "\t" + longitude);

            }
        } catch (SecurityException se) {
            Log.e("dangol_main", "catch " + se.getMessage());
        }

        Toast.makeText(this, "Check log", Toast.LENGTH_SHORT).show();

        addMarkerOnView();
        setCameraZoomToMarker();
    }

    void addMarkerOnView() {

        LatLng[] positions = {
                new LatLng(37.552030, 126.9370623),
                new LatLng(37.552030, 126.9360623),
                new LatLng(37.552030, 126.9380623),
                new LatLng(37.556030, 126.9380623),
                new LatLng(37.559030, 126.9370623)
        };

        for(int i=0; i<positions.length; i++) {

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(positions[i]).title("마커 " + i + "번")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_gray)));

            markers.add(marker);
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

            Log.e("dangol_main", "Location Changed: " + latitude + "\t" + longitude);

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
        String dbName = "Dangol";
        String sql = "";
        long sleepTime = 18000;

        public void run() {
//            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            try {
                Log.e("Dangol_main", "start thread");

                while(true){
                    //                  manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
                    //                   manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, gpsListener );
                    Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();
                    String nowTime = new SimpleDateFormat("yyyy.MM.DD HH:mm:ss").format(System.currentTimeMillis());

                    SQLiteDatabase mDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

                    try{
                        //ID 처리 방식 어떻게 할건지.

                        sql = "INSERT INTO readData(readDataID, Latitude, Longitude, Time) VALUES (" + tableID + ", " + latitude + ", "+ longitude +", '"+ nowTime +"')";

                        mDB.execSQL(sql);

                    }catch(SQLiteException se){
                        Log.e("insert_sql", se.toString());
                    }catch(Exception e){
                        Log.e("insert", e.toString());
                    }
                    mDB.close();
                    sleep(sleepTime);
                    Log.e("Dangol_main111", sql);
                }
            }catch(SecurityException se){
                Log.e("dangol_main", se.toString());
            }catch(Exception e){
                Log.e("Dangol_main", e.toString());
            }

            Log.e("dangol_main", "주금");

        }
    }

    public dataFilter(){
        //DB를 열고 readData에서 첫번째 데이터를 불러옴
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
            Log.e("dangol_main", "return to map");
            fragment_num = 0;
            ib_d.setBackground(getResources().getDrawable(R.drawable.menu_diary_gray));
            ib_p.setBackground(getResources().getDrawable(R.drawable.menu_pin_blue));
            super.onBackPressed();
        }
        else if(flag == 1){
            Log.e("dangol_main", "show diary");
            fragment_num = 1;
            ib_d.setBackground(getResources().getDrawable(R.drawable.menu_diary_blue));
            ib_p.setBackground(getResources().getDrawable(R.drawable.menu_pin_gray));
            Fragment fragment = new DiaryFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.map, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        else
            Log.e("dangol_main", "nothing to show");
    }

    @Override
    public void onBackPressed(){
        if(fragment_num == 1){
            fragment_num = 0;
            ImageButton ib_d = (ImageButton)findViewById(R.id.menu_diary);
            ImageButton ib_p = (ImageButton)findViewById(R.id.menu_pin);
            ib_d.setBackground(getResources().getDrawable(R.drawable.menu_diary_gray));
            ib_p.setBackground(getResources().getDrawable(R.drawable.menu_pin_blue));
        }
        super.onBackPressed();
    }
}