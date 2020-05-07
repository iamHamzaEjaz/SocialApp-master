package com.example.hamza.social_app;

import android.content.Intent;
//import android.support.design.button.MaterialButton;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hamza.social_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class EditProfile extends AppCompatActivity {


    Button button;
    private EditText user_name,user_status;

    ProgressBar progressBar;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference reference;
    private String uid;
    private DocumentReference objectDocumentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);



        button = findViewById(R.id.get_started);
        progressBar = findViewById(R.id.profile_progressBar);
        button = findViewById(R.id.edit_profile);
        user_name = findViewById(R.id.user_name);
        user_status = findViewById(R.id.user_status);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        uid = firebaseAuth.getCurrentUser().getUid();


    }

    public void updateDocumentFieldValue(View view)
    {
        try
        {
            if(user_name.getText().toString().isEmpty() && user_status.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
            else {

               objectDocumentReference= firestore.collection("profile_info")
                        .document(uid);

                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("name", user_name.getText().toString());
                objectMap.put("status", user_status.getText().toString());
                objectDocumentReference.update(objectMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(EditProfile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //objectDialog.dismiss();
                                Toast.makeText(EditProfile.this, "Fails to update data:"
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }
        catch (Exception e)
        {
            //objectDialog.dismiss();
            Toast.makeText(this, "updateDocumentFieldValue:"+
                    e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (firebaseAuth.getCurrentUser() != null){
//            Intent intent = new Intent(home_activity.this,MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
}
