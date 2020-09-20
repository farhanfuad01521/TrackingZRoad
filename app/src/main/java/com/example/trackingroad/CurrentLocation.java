package com.example.trackingroad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CurrentLocation extends AppCompatActivity {

    Button btLocation,saveLocation;
    TextView textView1, textView2, textView3, textView4, textView5;

    FusedLocationProviderClient fusedLocationProviderClient;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    ProgressBar progressBar;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        this.setTitle("Current Location");

        //addingBackButton
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        saveLocation=(Button)findViewById(R.id.saveLocation);
        btLocation = (Button) findViewById(R.id.bt_location);
        textView1 = (TextView) findViewById(R.id.text_view1);
        textView2 = (TextView) findViewById(R.id.text_view2);
        textView3 = (TextView) findViewById(R.id.text_view3);
        textView4 = (TextView) findViewById(R.id.text_view4);
        textView5 = (TextView) findViewById(R.id.text_view5);

        mAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        progressBar=(ProgressBar)findViewById(R.id.progressBarCurrentId);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(CurrentLocation.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(CurrentLocation.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });


        saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveCurrentLocation();
            }
        });

    }

    private void saveCurrentLocation() {
        final String latitude=textView1.getText().toString().trim();
        final String longitude=textView2.getText().toString().trim();
        final String country=textView3.getText().toString().trim();
        final String locality=textView4.getText().toString().trim();
        final String address=textView5.getText().toString().trim();

        if(latitude.isEmpty())
        {
            textView1.setError("Find Your Location First");
            textView1.requestFocus();
            return;
        }

        if(longitude.isEmpty())
        {
            textView2.setError("Find Your Location First");
            textView2.requestFocus();
            return;
        }

        if(country.isEmpty())
        {
            textView3.setError("Find Your Location First");
            textView3.requestFocus();
            return;
        }

        if(locality.isEmpty())
        {
            textView4.setError("Find Your Location First");
            textView4.requestFocus();
            return;
        }

        if(address.isEmpty())
        {
            textView5.setError("Find Your Location First");
            textView5.requestFocus();
            return;
        }

        userID=mAuth.getCurrentUser().getUid();
        DocumentReference documentReference=fStore.collection("Save Location").document(userID);

        Map<String,Object> save=new HashMap<>();
        save.put("Latitude",latitude);
        save.put("Longitude",longitude);
        save.put("Country",country);
        save.put("Locality",locality);
        save.put("Address",address);

        documentReference.set(save).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Successfully Saved",Toast.LENGTH_SHORT).show();
                Log.d("Success","onSuccess: Location Save"+userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                Log.d("Failure","onFailure: "+e.toString());
            }
        });
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if (location != null) {
                    try {

                        Geocoder geocoder = new Geocoder(CurrentLocation.this, Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        //set Latitude
                        textView1.setText(Html.fromHtml("<font color='#6200EE'><b>Latitude :</b><br></font>" + addresses.get(0).getLatitude()));

                        //set Longitude
                        textView2.setText(Html.fromHtml("<font color='#6200EE'><b>Longitude :</b><br></font>" + addresses.get(0).getLongitude()));

                        //set Country Name
                        textView3.setText(Html.fromHtml("<font color='#6200EE'><b>Country Name :</b><br></font>" + addresses.get(0).getCountryName()));

                        //set Locality
                        textView4.setText(Html.fromHtml("<font color='#6200EE'><b>Locality :</b><br></font>" + addresses.get(0).getLocality()));

                        //get Address
                        textView5.setText(Html.fromHtml("<font color='#6200EE'><b>Address :</b><br></font>" + addresses.get(0).getAddressLine(0)));
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}