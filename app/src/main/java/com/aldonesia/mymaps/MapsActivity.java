package com.aldonesia.mymaps;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.normal : mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            case R.id.terrain : mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            case R.id.sattelite : mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            case R.id.hybrid : mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            case R.id.none : mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button go = (Button) findViewById(R.id.idgo);
        Button search = (Button) findViewById(R.id.idsearch);
        go.setOnClickListener(op);
        search.setOnClickListener(op);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ITS = new LatLng(-7.2819705, 112.795323);
        mMap.addMarker(new MarkerOptions().position(ITS).title("Marker in ITS"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(ITS));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ITS,15));
    }

    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.idgo:hiddenkeyboard(view);
                     gotoLocation(); break;
                case R.id.idsearch:
                    try {
                        goSearch();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void gotoLocation(){
        EditText Lat = (EditText) findViewById(R.id.Lat);
        EditText Lng = (EditText) findViewById(R.id.Lng);
        EditText zoom = (EditText) findViewById(R.id.zoom);

        Double dblLat = Double.parseDouble(Lat.getText().toString());
        Double dblLng = Double.parseDouble(Lng.getText().toString());
        Float dblzoom = Float.parseFloat(zoom.getText().toString());

        Toast.makeText(this,"Move to Lat :" +dblLat +"Long:" +dblLng, Toast.LENGTH_LONG).show();
        gotoMaps(dblLat,dblLng,dblzoom);
    }

    private void hiddenkeyboard(View v) {
        InputMethodManager a = (InputMethodManager)
            getSystemService(INPUT_METHOD_SERVICE);
        a.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void gotoMaps(double Lat, Double Lng, Float zoom){
        LatLng NewLoc = new LatLng(Lat,Lng);
        mMap.addMarker(new MarkerOptions().
                position(NewLoc).
                title("Marker in " +Lat +":" +Lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NewLoc,zoom));
    }

    private void goSearch() throws IOException {
        EditText place = (EditText) findViewById(R.id.place);
        Geocoder g = new Geocoder(getBaseContext());

        try{
            List<Address> list = null;
            list = g.getFromLocationName(place.getText().toString(),1);
            Address address = list.get(0);

            String foundAddress= address.getAddressLine(0);
            Double foundLat = address.getLatitude();
            Double foundLng = address.getLongitude();

            Toast.makeText(getBaseContext(),"Found "+ foundAddress,Toast.LENGTH_LONG).show();
            EditText zoom = (EditText) findViewById(R.id.zoom);
            float flzoom= Float.parseFloat(zoom.getText().toString());
            Toast.makeText(this, "Move to"+ foundAddress +"Lat:"+ foundLat +"Long:"+ foundLng, Toast.LENGTH_LONG).show();
            gotoMaps(foundLat, foundLng, flzoom);

            EditText Lat = (EditText) findViewById(R.id.Lat);
            EditText Lng = (EditText) findViewById(R.id.Lng);

            Lat.setText(foundLat.toString());
            Lng.setText(foundLng.toString());
            Double dblLat = Double.parseDouble(Lat.getText().toString());
            Double dblLng = Double.parseDouble(Lng.getText().toString());
            distance(dblLat, dblLng, foundLat, foundLng);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void distance(double oldLat, double oldLng, double newLat, double newLng){
        Location oldLoc = new Location("old");
        Location newLoc = new Location("new");

        newLoc.setLatitude(newLat);
        newLoc.setLongitude(newLng);
        oldLoc.setLatitude(newLat);
        oldLoc.setLatitude(newLng);

        float distance = (float) oldLoc.distanceTo(newLoc)/1000;
        String thedistance = String.valueOf(distance);
        Toast.makeText(getBaseContext(), "Distance : " + thedistance +" km", Toast.LENGTH_LONG).show();
    }
}
