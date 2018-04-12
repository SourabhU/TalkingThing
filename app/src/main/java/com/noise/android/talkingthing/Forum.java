package com.noise.android.talkingthing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Forum extends AppCompatActivity {

    private ListView forum_list;
    private Button make_post;

    private String date;
    private String user_id;

    private DatabaseReference Database;
    private DatabaseReference Forum;

//    private HashMap<String,String> information = new HashMap<>();
    final ArrayList<String> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        forum_list = findViewById(R.id.forum_list);
        make_post = findViewById(R.id.make_post);

        date = DateFormat.getDateInstance().format(new Date());

        getSupportActionBar().setTitle("Forum");

        Database = FirebaseDatabase.getInstance().getReference();

        Forum = Database.child("Forum").child(date);

        Forum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child: children) {
                    HashMap<String,String> info = (HashMap<String, String>) child.getValue();
                    user_id = child.getKey();
                    posts.add(info.get("topic"));
                }
                ArrayAdapter<String> post_topics = new ArrayAdapter<String>(getApplicationContext(),R.layout.forum_template, posts);
                forum_list.setAdapter(post_topics);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        forum_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Open Activity for specific post
                Intent post_full = new Intent(getApplicationContext(),Post.class);
                String post = posts.get(position);
                post_full.putExtra("pt",post);
                post_full.putExtra("user_id",user_id);
                startActivity(post_full);
            }
        });

        make_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Newpost.class));
            }
        });
    }
}
