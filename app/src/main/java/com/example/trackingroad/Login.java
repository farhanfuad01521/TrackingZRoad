package com.example.trackingroad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    TextView setProfileNameText;

    static TextView distance,time;
    Button staticButton,mapButton,vehicleButton,locationButton,gasStationButton,trackButton,
            internetConnection,distanceTwoPlace,btnStart,btnStop,btnPause;

    static boolean status;
    LocationManager locationManager;
    static long startTime,endTime;
    static ProgressDialog progressDialog;
    static int p=0;

    LocationService myService;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    String userId;
    String Id;

    private ServiceConnection sc=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            LocationService.LocalBinder binder=(LocationService.LocalBinder)iBinder;
            myService=binder.getServices();
            status=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            status=false;
        }
    };


    @Override
    protected void onDestroy() {
        if(status==true)
        {
            unbindService();
        }
        super.onDestroy();
    }

    private void unbindService() {

        if(status==false)
        {
            return;
        }
        Intent i=new Intent(getApplicationContext(),LocationService.class);
        unbindService(sc);
        status=false;
    }

    @Override
    public void onBackPressed() {
        if(status==false)
        {
            super.onBackPressed();
        }
        else
        {
            moveTaskToBack(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1000:
            {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this,"GRANTED",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this,"DENIED",Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
    }

    //ON CREATE//
    ///////////////
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("Home");

        //Request Permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },1000);
        }

        distance=(TextView)findViewById(R.id.distance);
        time=(TextView)findViewById(R.id.time);
        btnStart=(Button)findViewById(R.id.btnStart);
        btnPause=(Button)findViewById(R.id.btnPause);
        btnStop=(Button)findViewById(R.id.btnStop);


        setProfileNameText=(TextView)findViewById(R.id.profileEmailTextId);

        staticButton=(Button)findViewById(R.id.staticId);
        mapButton=(Button)findViewById(R.id.mapId);
        vehicleButton=(Button)findViewById(R.id.vehicleId);
        locationButton=(Button)findViewById(R.id.locationId);
        gasStationButton=(Button)findViewById(R.id.gasStationId);
        trackButton=(Button)findViewById(R.id.trackId);
        distanceTwoPlace=(Button)findViewById(R.id.distanceTwoPlaceId);
        internetConnection=(Button)findViewById(R.id.checkInternetId);

        mAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        userId=mAuth.getCurrentUser().getUid();

        DocumentReference documentReference=fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                setProfileNameText.setText(documentSnapshot.getString("fName"));
            }
        });


        staticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent statics=new Intent(Login.this,Static.class);
                startActivity(statics);

            }
        });


        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent currentLocation=new Intent(getApplicationContext(),CurrentLocation.class);
                startActivity(currentLocation);
            }
        });




        vehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent vehicle=new Intent(Login.this,Vehicle.class);
                startActivity(vehicle);
            }
        });




        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });




        //
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent track=new Intent(getApplicationContext(),TrackOn.class);
                startActivity(track);
            }
        });



        internetConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkConnection();

            }
        });


        distanceTwoPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent dist=new Intent(getApplicationContext(),DistanceTwoPlace.class);
                startActivity(dist);

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGPS();
                locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    return;

                if(status==false)
                {
                    bindService();
                }
                progressDialog=new ProgressDialog(Login.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Getting Location....");
                progressDialog.show();


                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnPause.setText("Pause");
                btnStop.setVisibility(View.VISIBLE);

            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnPause.getText().toString().equalsIgnoreCase("Pause"))
                {
                    btnPause.setText("Resume");
                    p=1;

                }

                else if(btnPause.getText().toString().equalsIgnoreCase("Resume"))
                {
                    checkGPS();
                    locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

                    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        return;

                    btnPause.setText("Pause");
                    p=0;
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status==true)
                {
                    unbindService();
                    setDistanceAndTime();
                }
                btnStart.setVisibility(View.VISIBLE);
                btnPause.setText("Pause");
                btnPause.setVisibility(View.GONE);
                btnStop.setVisibility(View.GONE);


            }
        });


        gasStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nearBy=new Intent(getApplicationContext(),NearBY.class);
                startActivity(nearBy);
            }
        });


        setProfileNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profile=new Intent(getApplicationContext(),Profile.class);
                startActivity(profile);
            }
        });
    }


    //
    private void setDistanceAndTime() {

        final String distanceT=distance.getText().toString().trim();
        final String timeT=time.getText().toString().trim();

        if(distanceT.isEmpty())
        {
            distance.setError("Find Your Location First");
            distance.requestFocus();
            return;
        }

        if(timeT.isEmpty())
        {
            time.setError("Find Your Location First");
            time.requestFocus();
            return;
        }

        Id=mAuth.getCurrentUser().getUid();
        DocumentReference documentReference=fStore.collection("Speed & Time").document(Id);

        Map<String,Object> distanceTime=new HashMap<>();
        distanceTime.put("Speed",distanceT);
        distanceTime.put("Time",timeT);

        documentReference.set(distanceTime).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Successfully Saved",Toast.LENGTH_SHORT).show();
                Log.d("Success","onSuccess: Location Save"+Id);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                Log.d("Failure","onFailure: "+e.toString());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.home_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.vehicleInfoId)
        {
            Intent vehicleInfo=new Intent(Login.this,VehicleInformation.class);
            startActivity(vehicleInfo);
        }

        if(item.getItemId()==R.id.settingId)
        {
            Intent showSetting=new Intent(Login.this,profileSetting.class);
            startActivity(showSetting);
        }

        if(item.getItemId()==R.id.logoutId)
        {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent logout=new Intent(Login.this,MainActivity.class);
            startActivity(logout);
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkGPS() {

        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showGPSDisabledAlert();
    }

    private void showGPSDisabledAlert() {

        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);

                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();
            }
        });
        AlertDialog alert=alertDialogBuilder.create();
        alert.show();
    }


    private void bindService() {

        if(status==true)
            return;

        Intent i=new Intent(getApplicationContext(),LocationService.class);
        bindService(i,sc,BIND_AUTO_CREATE);
        status=true;
        startTime=System.currentTimeMillis();

    }



    public void checkConnection()
    {
        ConnectivityManager manager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=manager.getActiveNetworkInfo();

        if(null!=activeNetwork)
        {
            if(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI)
            {
                Toast.makeText(getApplicationContext(),"Wifi Enable",Toast.LENGTH_SHORT).show();
            }
            else if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE)
            {
                Toast.makeText(getApplicationContext(),"Network Enable",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }
}