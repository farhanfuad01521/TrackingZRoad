package com.example.trackingroad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VehicleInformation extends AppCompatActivity {

    EditText ownerName,vehicleType,modelT,registrationNo,registrationDate,chassisNo,engineNo,fuelType;
    Button clear,save;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_information);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        clear=(Button)findViewById(R.id.clearId);
        save=(Button)findViewById(R.id.saveId);

        ownerName=(EditText)findViewById(R.id.ownerNameId);
        vehicleType=(EditText)findViewById(R.id.vehicleTypeId);
        modelT=(EditText)findViewById(R.id.modelNameId);
        registrationNo=(EditText)findViewById(R.id.registrationId);
        registrationDate=(EditText)findViewById(R.id.registrationDateId);
        chassisNo=(EditText)findViewById(R.id.chassisNoId);
        engineNo=(EditText)findViewById(R.id.engineId);
        fuelType=(EditText)findViewById(R.id.fuelTypeId);

        mAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ownerName.setText("");
                vehicleType.setText("");
                modelT.setText("");
                registrationNo.setText("");
                registrationDate.setText("");
                chassisNo.setText("");
                engineNo.setText("");
                fuelType.setText("");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveVehicleInformation();
            }
        });
    }

    private void saveVehicleInformation() {

        final String owner=ownerName.getText().toString().trim();
        final String vehicle=vehicleType.getText().toString().trim();
        final String model=modelT.getText().toString().trim();
        final String registration=registrationNo.getText().toString().trim();
        final String registrationD=registrationDate.getText().toString().trim();
        final String chassis=chassisNo.getText().toString().trim();
        final String engine=engineNo.getText().toString().trim();
        final String fuel=fuelType.getText().toString().trim();


        if(owner.isEmpty())
        {
            ownerName.setError("Find Your Location First");
            ownerName.requestFocus();
            return;
        }

        if(vehicle.isEmpty())
        {
            vehicleType.setError("Find Your Location First");
            vehicleType.requestFocus();
            return;
        }

        if(model.isEmpty())
        {
            modelT.setError("Find Your Location First");
            modelT.requestFocus();
            return;
        }

        if(registration.isEmpty())
        {
            registrationNo.setError("Find Your Location First");
            registrationNo.requestFocus();
            return;
        }

        if(registrationD.isEmpty())
        {
            registrationDate.setError("Find Your Location First");
            registrationDate.requestFocus();
            return;
        }

        if(chassis.isEmpty())
        {
            chassisNo.setError("Find Your Location First");
            chassisNo.requestFocus();
            return;
        }

        if(engine.isEmpty())
        {
            engineNo.setError("Find Your Location First");
            engineNo.requestFocus();
            return;
        }

        if(fuel.isEmpty())
        {
            fuelType.setError("Find Your Location First");
            fuelType.requestFocus();
            return;
        }

        userID=mAuth.getCurrentUser().getUid();
        DocumentReference documentReference=fStore.collection("VehicleInformation").document(userID);

        Map<String,Object> vehicleSet=new HashMap<>();
        vehicleSet.put("Owner",owner);
        vehicleSet.put("VehicleType",vehicle);
        vehicleSet.put("Model",model);
        vehicleSet.put("RegistrationNo",registration);
        vehicleSet.put("RegistrationDate",registrationD);
        vehicleSet.put("ChassisNo",chassis);
        vehicleSet.put("EngineNo",engine);
        vehicleSet.put("FuelType",fuel);

        documentReference.set(vehicleSet).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}