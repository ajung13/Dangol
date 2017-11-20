package ac.sogang.dangol;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

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

public class WritingMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private boolean moveCameraToMarker;
    Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_write);
        mapFragment.getMapAsync(this);

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

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(37.552030, 126.9370623);
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(position).title("저장된 위치")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.setOnMarkerDragListener(this);
        setCameraZoomToMarker();
    }

    @Override
    public void onMarkerDragStart(Marker marker){}

    @Override
    public void onMarkerDragEnd(Marker marker){}

    @Override
    public void onMarkerDrag(Marker marker){}

    public void onMapSelected(View v){
        Log.e("dangol_writing_map", "hi?");
        String tmp = "Drag complete\t";
        tmp += "lat: " + mMarker.getPosition().latitude + " / lon: " + mMarker.getPosition().longitude;
        Log.e("dangol_writing_map", tmp);
    }

    void setCameraZoomToMarker() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mMarker.getPosition());
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.05); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.moveCamera(cu);
    }
}
