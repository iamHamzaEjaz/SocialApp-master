package com.example.hamza.social_app;

import android.content.Intent;
import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.design.widget.Snackbar;
//import android.support.design.widget.TextInputEditText;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hamza.social_app.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class post_activity extends AppCompatActivity {
    ImageView imageView;
    Toolbar toolbar;
    TextInputEditText editText;
    Button button;
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private StorageReference reference;
    private Uri img_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //init views
        toolbar = findViewById(R.id.post_toolbar);
        button = findViewById(R.id.add_post);
        editText = findViewById(R.id.post_desc);
        progressBar = findViewById(R.id.post_progress);
        progressBar.setVisibility(View.INVISIBLE);
        imageView = findViewById(R.id.post_img);
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String desc = editText.getText().toString();
                final StorageReference ref = reference.child("posts").child(getSaltString()+".jpg");
                try
                {
                    UploadTask uploadTask = ref.putFile(img_uri);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                String name = editText.getText().toString();
                                Map<String , Object> user = new HashMap();

                                user.put("time", FieldValue.serverTimestamp());
                                user.put("description",name);
                                user.put("uid",firebaseAuth.getCurrentUser().getUid());
                                user.put("path",downloadUri.toString());
                                firestore.collection("posts").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(post_activity.this,"Post Added Successfully!!",Toast.LENGTH_SHORT).show();
                                        gotomain();
                                    }
                                });
                            }
                        }
                    });
                }
                catch (Exception e){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.post),"Error : "+e.getMessage(),Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(post_activity.this);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                img_uri = resultUri;
                imageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    public void gotomain(){
        Intent intent = new Intent(post_activity.this,MainActivity.class);
        startActivity(intent);
    }

    public void write(View view) {

    }
}
