package com.example.trackingroad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Vehicle extends AppCompatActivity {

    TextView ownerNameText,vehicleTypeText,modelNameText,registrationText,registrationDateText,chassisNoText,engineText,fuelTypeText;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);
        this.setTitle("Vehicle Information");

        //addingBackButton
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        ownerNameText=(TextView)findViewById(R.id.ownerNameText);
        vehicleTypeText=(TextView)findViewById(R.id.vehicleTypeText);
        modelNameText=(TextView)findViewById(R.id.modelNameText);
        registrationText=(TextView)findViewById(R.id.registrationText);
        registrationDateText=(TextView)findViewById(R.id.registrationDateText);
        chassisNoText=(TextView)findViewById(R.id.chassisNoText);
        engineText=(TextView)findViewById(R.id.engineText);
        fuelTypeText=(TextView)findViewById(R.id.fuelTypeText);

        mAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        DocumentReference documentReference=fStore.collection("VehicleInformation").document(userId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                ownerNameText.setText(documentSnapshot.getString("Owner"));
                vehicleTypeText.setText(documentSnapshot.getString("VehicleType"));
                modelNameText.setText(documentSnapshot.getString("Model"));
                registrationText.setText(documentSnapshot.getString("RegistrationNo"));
                registrationDateText.setText(documentSnapshot.getString("RegistrationDate"));
                chassisNoText.setText(documentSnapshot.getString("ChassisNo"));
                engineText.setText(documentSnapshot.getString("EngineNo"));
                fuelTypeText.setText(documentSnapshot.getString("FuelType"));
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