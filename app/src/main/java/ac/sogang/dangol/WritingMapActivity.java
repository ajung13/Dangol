package ac.sogang.dangol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
    private Marker mMarker;
    private LatLng position;

    static final String EXTRA_STUFF = "ac.sogang.dangol.EXTRA_STUFF";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_write);
        mapFragment.getMapAsync(this);

        intent = new Intent();
        position = getIntent().getParcelableExtra("location");

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build();
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("KR").build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.e("dangol_write_map", "Place: " + place.getName());
                position = place.getLatLng();
                updateMap();
            }

            @Override
            public void onError(Status status) {
                Log.e("dangol_write_map", "Error: " + status.getStatusMessage());
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMap();
    }

    @Override
    public void onMarkerDragStart(Marker marker){}
    @Override
    public void onMarkerDragEnd(Marker marker){}
    @Override
    public void onMarkerDrag(Marker marker){}

    private void updateMap(){
        // Add a marker in Sydney and move the camera

        mMap.clear();
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(position).title("저장된 위치")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_brown)).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.setOnMarkerDragListener(this);
        setCameraZoomToMarker();
    }

    private void setCameraZoomToMarker() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mMarker.getPosition());
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 15);
        mMap.moveCamera(cu);
    }

    public void onMapSelected(View v){
        //dialog for setting the name of that location
        AlertDialog.Builder ad = new AlertDialog.Builder(WritingMapActivity.this);
        ad.setTitle("장소 이름을 정해주세요");
        ad.setMessage("ex. 스타벅스 신촌점");
        final EditText et = new EditText(WritingMapActivity.this);
        ad.setView(et);

        ad.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = et.getText().toString();
                if(name.equals("")){
                    Toast.makeText(getApplicationContext(), "장소 이름이 없으면 정확도가 떨어질 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                //put the location into basket
                final LatLng position2 = new LatLng(mMarker.getPosition().latitude, mMarker.getPosition().longitude);
                Bundle basket = new Bundle();
                basket.putParcelable(EXTRA_STUFF, position2);
                basket.putString("name", name);
                dialog.dismiss();
                intent.putExtras(basket);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }
}
