package com.example.hamza.social_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.design.widget.BottomNavigationView;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.hamza.social_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private HomeFragment homeFragment;
    private MenuFragment menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        homeFragment = new HomeFragment();
        menuFragment = new MenuFragment();
        initializeFragment();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_posts:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.bottom_add:
                        Intent intent = new Intent(MainActivity.this,post_activity.class);
                        startActivity(intent);
                        return true;
                    case R.id.bottom_menu:
                        replaceFragment(menuFragment);
                        return true;
                    case R.id.bottom_logout:
                        logout();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){
            fragmentTransaction.hide(menuFragment);
        }
        if(fragment == menuFragment){
            fragmentTransaction.hide(homeFragment);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    private void initializeFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.maincontainer, homeFragment);
        fragmentTransaction.add(R.id.maincontainer, menuFragment);
        fragmentTransaction.hide(menuFragment);
        fragmentTransaction.commit();
    }

    public void logout(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("LogOut").setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               firebaseAuth.signOut();
                Intent main = new Intent(MainActivity.this,home_activity.class);
                startActivity(main);
                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setCancelable(true);
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }
}