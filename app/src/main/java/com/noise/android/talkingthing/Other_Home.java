package com.noise.android.talkingthing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Other_Home extends AppCompatActivity {

    private String username;
    private TextView email;
    private TextView username_changeable;
    private TextView status_changeable;

    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other__home);

        username = getIntent().getExtras().getString("name");
        email = findViewById(R.id.Email);
        username_changeable = findViewById(R.id.Username_changeable);
        status_changeable = findViewById(R.id.status_changeable);

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference();
        ref.child("Users")
                .addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    HashMap<String, String> values = (HashMap<String, String>) child.getValue();
                    if (values.get("name").equals(username)) {
                        email.setText(values.get("email"));
                        username_changeable.setText(username);
                        status_changeable.setText(values.get("status"));
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Other_Home.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        username_changeable.setText(name);
//        status_changeable.setText(status);

    }
}
