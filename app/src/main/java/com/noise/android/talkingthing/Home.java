package com.noise.android.talkingthing;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_forum) {
//                    Toast.makeText(getBaseContext(), "yaaaaaaaaay", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(), Forum.class));
                    finish();
                }
                if (id == R.id.nav_playstore) {
//                    Toast.makeText(getBaseContext(), "Featured App opens", Toast.LENGTH_SHORT).show();
                    //String packagename = getPackageName();
                    Intent playstore = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.appsomniacs.da2"));
                    startActivity(playstore);
                    finish();
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

            BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener
                    (new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            Fragment selectedFragment = null;
                            switch (item.getItemId()) {
                                case R.id.action_HomeFrag:
                                    selectedFragment = HomeFrag.newInstance();
                                    break;
                                case R.id.action_ChatFrag:
                                    selectedFragment = ChatFrag.newInstance();
                                    break;
                                case R.id.action_RequestsFrag:
                                    selectedFragment = RequestsFrag.newInstance();
                                    break;
                            }
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, selectedFragment);
                            transaction.commit();
                            return true;
                        }
                    });

            //Manually displaying the first fragment - one time only
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, ChatFrag.newInstance());
            transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

}