package mosis.ivana.mustsee;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.graphics.Bitmap;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import mosis.ivana.mustsee.DataModel.Category;
import mosis.ivana.mustsee.DataModel.Place;
import mosis.ivana.mustsee.DataModel.SimpleLocation;

public class AddPlaceActivity extends AppCompatActivity implements View.OnClickListener {
    private Bitmap selectedImageBitmap;
    EditText txtName;
    EditText txtCountry;
    EditText txtCity;
    EditText txtDescription;
    Spinner categorySpinner;
    String category;
    Boolean isImageSelected;

    private static final int RESPONSE_IMAGE_CAMERA = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categorySpinner = findViewById(R.id.newPlaceCategorySpinner);
        categorySpinner.setAdapter(new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, Category.values()));

        //get view Controls
        txtName = findViewById(R.id.newPlaceName);
        txtCountry = findViewById(R.id.newPlaceCountry);
        txtCity = findViewById(R.id.newPlaceCity);
        txtDescription = findViewById(R.id.newPlaceDescription);

        ImageView pictureview = findViewById(R.id.newPlacePhoto);
        pictureview.setOnClickListener(this);

        Button addButton = findViewById(R.id.newPlaceAddButton);
        addButton.setOnClickListener(this);

        isImageSelected = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newPlacePhoto: {
                //take the photo
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, RESPONSE_IMAGE_CAMERA);
                }
                return;
            }
            case R.id.newPlaceAddButton: {
                if (validateInput())
                    writePlaceToFirebase();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView photoView = findViewById(R.id.newPlacePhoto);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESPONSE_IMAGE_CAMERA: {
                    Bundle extras = data.getExtras();
                    selectedImageBitmap = (Bitmap) extras.get("data");
                    photoView.setImageBitmap(selectedImageBitmap);
                    isImageSelected = true;
                    break;
                }
            }
        }
    }

    private Boolean validateInput() {
        if (!isImageSelected) {
            Toast.makeText(this, "You must upload a place photo!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (txtName.getText().length() == 0) {
            txtName.setError("Enter name!");
            txtName.requestFocus();
            return false;
        }
        if (txtCountry.getText().length() == 0) {
            txtCountry.setError("Enter country!");
            txtCountry.requestFocus();
            return false;
        }
        if (txtDescription.getText().length() == 0) {
            txtDescription.setError("Enter description!");
            txtDescription.requestFocus();
            return false;
        }
        return true;
    }

    private void writePlaceToFirebase() {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("places").push();
        String key = dbReference.getKey();

        //create place object
        String name = txtName.getText().toString().trim();
        String country = txtCountry.getText().toString().trim();
        String city = txtCity.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String description = txtDescription.getText().toString().trim();

        final Place newPlace = new Place(key, name, country, city, description, category);

        Location currentLocation=LocationTrackingService.CurrentLocation;
        //get current user location
        newPlace.setCoordinates(new SimpleLocation(currentLocation.getLatitude(), currentLocation.getLongitude()));


        //save picture to storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("placesPhotos").child(key);
        storageRef.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        newPlace.setPhotoURL(uri.toString());
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("places").child(newPlace.getPlaceID());
                        databaseRef.setValue(newPlace);

                        //increase placesAdded count for the user
                        final DatabaseReference userReference=FirebaseDatabase.getInstance()
                                .getReference().child("users").child(HomeActivity.loggedUser.getUserId()).child("placesAddedCount");
                        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int oldCount= dataSnapshot.getValue(Integer.class);
                                userReference.setValue(oldCount+1);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Toast.makeText(AddPlaceActivity.this, "You have successfully added new place!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
}
