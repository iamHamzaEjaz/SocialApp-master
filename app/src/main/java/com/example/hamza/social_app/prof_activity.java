package com.example.hamza.social_app;

import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.design.widget.TextInputEditText;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hamza.social_app.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;
import id.zelory.compressor.Compressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class prof_activity extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar progressBar;
    Button button;
    TextInputEditText editText,editText2;
    CircleImageView circleImageView;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference reference;
    private String uid;
    private Uri mainImageURI = null;
    private Boolean isChanged = false;
    private Bitmap compressedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.profile_progressBar);
        button = findViewById(R.id.edit_profile);
        editText = findViewById(R.id.user_profile_name);
        editText2 = findViewById(R.id.user_status);

        circleImageView = findViewById(R.id.user_profile_pic);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        uid = firebaseAuth.getCurrentUser().getUid();

        progressBar.setVisibility(View.INVISIBLE);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BringImagePicker();

            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = editText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(user_name) && mainImageURI!= null){
                    if (isChanged){
                        File file = new File(mainImageURI.getPath());
                        try{
                            compressedImage = new Compressor(prof_activity.this)
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(file);
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] thumbdata = byteArrayOutputStream.toByteArray();
                        final StorageReference ref = reference.child("profile_images").child(uid+".jpg");
                        try{
                            UploadTask uploadTask = ref.putBytes(thumbdata);
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
                                        String status = editText2.getText().toString();
                                        Map<String , Object> user = new HashMap();
                                        user.put("name",name);
                                        user.put("status",status);
                                        user.put("image",downloadUri.toString());
                                        firestore.collection("profile_info").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(prof_activity.this,"Profile Updated Successfully!!",Toast.LENGTH_SHORT);
                                                gotomain();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        catch (Exception e){
                            Toast.makeText(prof_activity.this,"Error : "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(prof_activity.this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                circleImageView.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
    public void gotomain(){
        Intent intent = new Intent(prof_activity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
