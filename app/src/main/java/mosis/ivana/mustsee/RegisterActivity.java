package mosis.ivana.mustsee;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mosis.ivana.mustsee.DataModel.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RESPONSE_IMAGE_CAMERA=0;
    private static final int RESPONSE_IMAGE_UPLOAD=1;

    private Bitmap selectedImageBitmap;
    private boolean isImageSelected=false;
    private FirebaseAuth mAuth;

    private EditText txtFullName;
    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        //get edittext references
        txtFullName= findViewById(R.id.registerInsertName);
        txtEmail= findViewById(R.id.registerInsertEmail);
        txtUsername=findViewById(R.id.registerInsertUsername);
        txtPassword=findViewById(R.id.registerInsertPassword);

        //set this class as onClickListeners for all buttons
        Button btnRegister= findViewById(R.id.btnRegisterScreenRegister);
        btnRegister.setOnClickListener(this);

        Button btnInsertCamera= findViewById(R.id.btnInsertCamera);
        btnInsertCamera.setOnClickListener(this);

        Button btnUploadPhoto= findViewById(R.id.btnInsertUpload);
        btnUploadPhoto.setOnClickListener(this);
    }

    private boolean validateRegisterForm(){
        if(txtFullName.getText().length()==0)
        {
            txtFullName.setError("Enter fullName!");
            txtFullName.requestFocus();
            return false;
        }
        if(txtEmail.getText().length()==0){
            txtEmail.setError("Enter email!");
            txtEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()){
            txtEmail.setError("Enter valid email!");
            txtEmail.requestFocus();
            return false;
        }
        if(txtUsername.getText().length()==0){
            txtUsername.setError("Insert username!");
            txtUsername.requestFocus();
            return false;
        }
        if(txtPassword.getText().length()==0){
            txtPassword.setError("Enter password!");
            txtPassword.requestFocus();
            return false;
        }
        if(txtPassword.getText().length()<6){
            txtPassword.setError("Password must contain at least 6 characters!");
            txtPassword.requestFocus();
            return false;
        }

        if(!isImageSelected)
        {
            Toast.makeText(this,"Upload profile photo!",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnRegisterScreenRegister){
            if(validateRegisterForm()){
                String email= txtEmail.getText().toString().trim();
                String password=txtPassword.getText().toString().trim();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //adding user data to Firebase storage and firebase database
                           FirebaseUser user= mAuth.getCurrentUser();
                           String fullname=txtFullName.getText().toString();
                           final User newUser=new User(user.getUid(),fullname,txtUsername.getText().toString()
                                   ,txtPassword.getText().toString(),txtEmail.getText().toString());

                           //saving image to storage
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            final StorageReference storageRef= FirebaseStorage.getInstance().getReference().child("userProfilePhotos").child(user.getUid());
                            storageRef.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            newUser.setProfilePhotoUrl(uri.toString());
                                            DatabaseReference databaseRef= FirebaseDatabase.getInstance().getReference().child("users").child(newUser.getUserId());
                                            databaseRef.setValue(newUser);

                                            Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
                                            startActivity(i);
                                            finish();

                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,"REGISTRATION FAILED!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            return;
        }
        if(v.getId()==R.id.btnInsertCamera){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, RESPONSE_IMAGE_CAMERA);
            }
            return;
        }
        if(v.getId()==R.id.btnInsertUpload){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESPONSE_IMAGE_UPLOAD);
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imgViewPhoto= findViewById(R.id.profilePhoto);
        if(resultCode == RESULT_OK)
            switch (requestCode) {
                case RESPONSE_IMAGE_UPLOAD:
                {
                    Uri selectedImage = data.getData();
                    try {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        selectedImageBitmap=Bitmap.createScaledBitmap(selectedImageBitmap,200,200,true);
                        imgViewPhoto.setImageBitmap(selectedImageBitmap);
                        isImageSelected=true;
                    }catch(IOException e){
                        Toast.makeText(this,"Loading image failed!",Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case RESPONSE_IMAGE_CAMERA:
                {
                    Bundle extras = data.getExtras();
                    selectedImageBitmap = (Bitmap) extras.get("data");
                    imgViewPhoto.setImageBitmap(selectedImageBitmap);
                    isImageSelected=true;
                    break;
                }
            }


    }
}

