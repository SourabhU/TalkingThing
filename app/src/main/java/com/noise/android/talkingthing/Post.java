package com.noise.android.talkingthing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Post extends AppCompatActivity {

    private String topic;
    private String date;

    private TextView topic_display;
    private TextView date_display;
    private Button reply_button;
    private ListView list_item;

    private String user_id;

    private DatabaseReference Reference;
    private DatabaseReference Forum;

    private ArrayList<String> response = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        topic = getIntent().getStringExtra("pt");
        user_id = getIntent().getStringExtra("user_id");

        topic_display = findViewById(R.id.topic_display);
        date_display = findViewById(R.id.date_display);
        reply_button = findViewById(R.id.reply_button);

        list_item = findViewById(R.id.list_item);

        date = DateFormat.getDateInstance().format(new Date());

        topic_display.setText(topic);
        date_display.setText(date);

        Reference = FirebaseDatabase.getInstance().getReference();

        Forum = Reference.child("Forum").child(date).child(user_id);

        Forum.child("responses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    final String[] responder_name = new String[1];
                    Reference.child("Users").child(child.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,String> name = (HashMap<String, String>) dataSnapshot.getValue();
                            responder_name[0] = name.get("name");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                                response.add(child.getValue() + "--by " + responder_name[0]);
                    ArrayAdapter<String> response_list = new ArrayAdapter<>(getApplicationContext(),R.layout.post_template, response);
                    list_item.setAdapter(response_list);
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Respond.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            }
        });
    }
}
