package com.example.hamza.social_app;

import android.content.Intent;
//import android.support.design.button.MaterialButton;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hamza.social_app.R;
import com.google.firebase.auth.FirebaseAuth;


public class home_activity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ImageView imageView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imageView = findViewById(R.id.starter_icon);
        firebaseAuth = FirebaseAuth.getInstance();

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.rotate);
        imageView.setAnimation(animation);

        button = findViewById(R.id.get_started);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_activity.this,auth_activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_bottom_down,R.anim.slide_bottom);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(home_activity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
