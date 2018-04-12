package com.noise.android.talkingthing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class Newpost extends AppCompatActivity {

    private EditText Topic;
    private EditText Description;
    private Button post_button;

    private String topic;
    private String description;
    private String date;

    private DatabaseReference Reference;
    private DatabaseReference Forum;

    private HashMap<String, String> Post_info = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);

        Topic = findViewById(R.id.topic_input);
        Description = findViewById(R.id.description);
        post_button = findViewById(R.id.post_button);

        Reference = FirebaseDatabase.getInstance().getReference();

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_button.setEnabled(false);

                topic = Topic.getText().toString();
                if (topic.isEmpty()){
                    Toast.makeText(Newpost.this, "Please mention topic", Toast.LENGTH_SHORT).show();
                    post_button.setEnabled(true);
                    return;
                }
                description = Description.getText().toString();
                date = DateFormat.getDateInstance().format(new Date());

                Forum = Reference.child("Forum").child(date);

                Forum.child(date).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : children) {
                                if (child.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    Toast.makeText(Newpost.this, "Cannot post more for the day", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Post_info.put("topic", topic);
                                    Post_info.put("description", description);
                                    Forum.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Post_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(Newpost.this, "Posted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                        else {
                            Post_info.put("topic", topic);
                            Post_info.put("description", description);
                            Forum.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Post_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Newpost.this, "Posted", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
//                        Toast.makeText(Newpost.this, "it happened", Toast.LENGTH_SHORT).show();
                            }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}