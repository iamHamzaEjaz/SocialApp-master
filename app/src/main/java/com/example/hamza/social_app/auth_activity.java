package com.example.hamza.social_app;

import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.design.widget.Snackbar;
//import android.support.design.widget.TextInputEditText;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hamza.social_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class auth_activity extends AppCompatActivity {
    TextInputEditText email,password;
    Button button , forgot , signup;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        //init views
        button = findViewById(R.id.auth_login);
        email = findViewById(R.id.login_email);
        password =findViewById(R.id.login_password);
        forgot = findViewById(R.id.forgot);
        signup = findViewById(R.id.auth_signup);
        //init auth
        firebaseAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = email.getText().toString();
                String b = password.getText().toString();
                if (a.isEmpty() && b.isEmpty()){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.auth),"No Details Filled",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else if (a.isEmpty()){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.auth),"Check the Email and Try Again",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else if (b.isEmpty()){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.auth),"Check the Password and Try Again",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else {
                    firebaseAuth.signInWithEmailAndPassword(a,b).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.auth),"Logging in",Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            gotomain();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.auth),"Error : "+e.getMessage().toString(),Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
                }
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(auth_activity.this);
                final  View view = inflater.inflate(R.layout.dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(auth_activity.this);
                builder.setView(view);
                Button reset = view.findViewById(R.id.reset);
                final EditText editText = view.findViewById(R.id.reset_email);
                final AlertDialog  dialog = builder.create();
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();

                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String reset_email = editText.getText().toString();
                        if (reset_email.isEmpty()){
                            Toast.makeText(auth_activity.this,"Enter a valid email",Toast.LENGTH_LONG).show();
                        }
                        else {
                            firebaseAuth.sendPasswordResetEmail(reset_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        dialog.cancel();
                                        Toast.makeText(auth_activity.this,"Email sent!!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.cancel();
                                    Snackbar snackbar = Snackbar.make(findViewById(R.id.auth),"Error : "+e.getMessage().toString(),Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            });
                        }
                    }
                });
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotosignup();
            }
        });
    }
    public void gotomain(){
        Intent intent = new Intent(auth_activity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void gotosignup(){
        Intent intent = new Intent(auth_activity.this,reg_activity.class);
        startActivity(intent);
        finish();
    }
}
