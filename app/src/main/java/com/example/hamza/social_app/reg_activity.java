package com.example.hamza.social_app;

import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.design.widget.Snackbar;
//import android.support.design.widget.TextInputEditText;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hamza.social_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class reg_activity extends AppCompatActivity {
    TextInputEditText email,pwd1,pwd2;
    Button login,signup;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        //init views
        login =findViewById(R.id.reg_login);
        email = findViewById(R.id.reg_email);
        pwd1 = findViewById(R.id.reg_pwd1);
        pwd2 = findViewById(R.id.reg_pwd2);
        signup = findViewById(R.id.reg_signup);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String psd1 = pwd1.getText().toString();
                String psd2 = pwd2.getText().toString();

                if (mail.isEmpty() || psd1.isEmpty() || psd2.isEmpty()){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.reg_layout),"Check Details",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else if (!psd1.equals(psd2)){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.reg_layout),"Passwords does not match",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(mail,psd1).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.reg_layout),"User Created !!",Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            gotoProfile();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.reg_layout),"Error : "+e.getMessage().toString(),Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    });
                }
            }
        });
    }
    public void gotoLogin(){
        Intent intent = new Intent(reg_activity.this,auth_activity.class);
        startActivity(intent);
        finish();
    }
    public void gotoProfile(){
        Intent intent = new Intent(reg_activity.this,prof_activity.class);
        startActivity(intent);
        finish();
    }
}
