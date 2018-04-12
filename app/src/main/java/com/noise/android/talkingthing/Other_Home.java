package com.noise.android.talkingthing;

import android.content.Intent;
import android.icu.util.Currency;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Other_Home extends AppCompatActivity {

    private TextView email;
    private TextView username_changeable;
    private TextView status_changeable;
    private Button button_add;
    private Button button_accept;
//    private Button button_sendmessage;

    int current_state = 0;

    FirebaseDatabase firebaseDatabase;

    private DatabaseReference friend_req;
    private DatabaseReference friend_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other__home);

        final String User_id = getIntent().getStringExtra("user_id");

        email = findViewById(R.id.Email);
        username_changeable = findViewById(R.id.Username_changeable);
        status_changeable = findViewById(R.id.status_changeable);
        button_add = findViewById(R.id.button_add);
        button_accept = findViewById(R.id.button_accept);
        button_accept.setEnabled(false);
//        button_sendmessage = findViewById((R.id.button_sendmessage));

        firebaseDatabase = FirebaseDatabase.getInstance();

        final String Current_User = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        final DatabaseReference ref = firebaseDatabase.getReference();
        ref.child("Users").child(User_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,String> values = (HashMap<String,String>) dataSnapshot.getValue();
                username_changeable.setText(values.get("name"));
                status_changeable.setText(values.get("status"));
                return;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });

        friend_req = firebaseDatabase.getReference().child("friend_requests");
        friend_list = firebaseDatabase.getReference().child("friends");

        friend_req.child(User_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            if (child.getKey().equals(Current_User)) {
                                current_state = 1;
                                button_accept.setEnabled(true);
                                button_add.setEnabled(false);
                            }
                            else {
                                current_state = 0;
                                button_accept.setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        friend_list.child(User_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            if (child.getKey().equals(User_id)) {
                                current_state = 2;
                                button_accept.setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        return;
                    }
                });

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_add.setEnabled(false);

                //  WHEN NOT FRIENDS
                if(current_state == 0){
                    friend_req.child(Current_User).child(User_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        current_state = 1;
                                        button_add.setText("Cancel request");
                                        button_add.setEnabled(true);
                                        friend_req.child(User_id).child(Current_User).child("request_type").setValue("received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Other_Home.this, "Friend request sent", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                    else {
                                        Toast.makeText(Other_Home.this, "Adding friend failed", Toast.LENGTH_SHORT).show();
                                        button_add.setEnabled(true);
                                    }
                                }
                            });
                }

                //  WHEN FRIEND REQUEST IS TO BE CANCELLED
                else if(current_state == 1){
                    friend_req.child(Current_User).child(User_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friend_req.child(User_id).child(Current_User).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Other_Home.this, "Request cancelled", Toast.LENGTH_SHORT).show();
                                    current_state = 0;
                                    button_add.setText("Add friend");
                                    button_add.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                else if(current_state == 2){
                    friend_list.child(User_id).child(Current_User).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friend_list.child(Current_User).child(User_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Other_Home.this, "Unfriended", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!friend_req.child(Current_User).toString().isEmpty()) {
                    friend_req.child(Current_User).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            button_accept.setEnabled(false);
                            if (dataSnapshot.hasChild(User_id)) {
                                String type = dataSnapshot.child(User_id).child("request_type").getValue().toString();
                                if (type.equals("received")) {
                                    final String date = DateFormat.getDateInstance().format(new Date());
                                    current_state = 2;
                                  friend_list.child(User_id).child(Current_User).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          friend_list.child(Current_User).child(User_id).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void aVoid) {
//                                                  friend_req.child(Current_User).child(User_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                      @Override
//                                                      public void onSuccess(Void aVoid) {
//                                                          friend_req.child(User_id).child(Current_User).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                              @Override
//                                                              public void onSuccess(Void aVoid) {
//                                                                  Toast.makeText(Other_Home.this, "Frieng added", Toast.LENGTH_SHORT).show();
//                                                                  button_add.setEnabled(true);
//                                                                  button_add.setText("unfriend");
//                                                                  return;
//                                                              }
//                                                          });
//                                                      }
//                                                  });
                                              }
                                          });
                                      }
                                  });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            button_accept.setEnabled(true);
                            return;
                        }
                    });
                }
            }
        });

//        button_sendmessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent chat_jump = new Intent(getBaseContext(),Home.class).putExtra("targetUser",User_id);
//                startActivity(chat_jump);
//                finish();
//            }
//        });

//        username_changeable.setText(name);
//        status_changeable.setText(status);
    }
}