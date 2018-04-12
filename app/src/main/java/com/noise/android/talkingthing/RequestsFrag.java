package com.noise.android.talkingthing;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestsFrag extends Fragment {
    public static RequestsFrag newInstance() {
        RequestsFrag fragment = new RequestsFrag();
        return fragment;
    }

    private EditText search_bar;
    private String username;
    private Button search;
    private ListView list_item;
    final private ArrayList<String> names = new ArrayList<>();
    private String user_key;

    NotificationCompat.Builder mBuilder;
    private static final int channelID = 48920;

//    public class Users {
//
//        public String  name;
//        public String email;
//        public String image;
//        public String status;
//
//        public Users(){
//
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public void setEmail(String email) {
//            this.email = email;
//        }
//
//        public void setImage(String image) {
//            this.image = image;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public String getEmail() {
//            return email;
//        }
//
//        public String getImage() {
//            return image;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public Users(String name, String image, String status) {
//            this.name = name;
//            this.image = image;
//            this.status = status;
//        }
//
//
//    }

    private FirebaseDatabase firebaseDatabase;

    private FirebaseUser User;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference reference = firebaseDatabase.getReference();

        final DatabaseReference friend_data = reference.child("friend_requests");

        final List<String> values = new ArrayList<String>();

        final View rootview = inflater.inflate(R.layout.fragment_requests, container, false);

        search_bar = rootview.findViewById(R.id.search_bar);

        list_item = rootview.findViewById(R.id.list_item);

        search = rootview.findViewById(R.id.search_btn);

        search.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        username = search_bar.getText().toString();
                        if(TextUtils.isEmpty(username)) {
                            Toast.makeText(getActivity(), "Please fill in a username", Toast.LENGTH_SHORT).show();
                            return;
                        }

               reference.child("Users")
                       .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        final Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child: children) {
                            HashMap<String,String> values = (HashMap<String, String>) child.getValue();
                            if(values.get("name").equals(username)){
                                Intent lol = new Intent(getContext(), Other_Home.class);
                                lol.putExtra("user_id",child.getKey().toString());
                                getActivity().startActivity(lol);
                            }
//                            Toast.makeText(getActivity(), values.get("email"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        // ...
                        Toast.makeText(getContext(), "No such user exists", Toast.LENGTH_SHORT).show();
                    }
                });
                    }
                }
        );

        // TO SHOW ALL REQUESTS IN REQUESTS SECTION
        friend_data.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (final DataSnapshot child: children) {
                            HashMap<String, String> state = (HashMap<String, String>) child.getValue();
                            if (state.get("request_type").equals("received")) {
                                reference.child("Users").child(child.getKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        HashMap<String, String> childref = (HashMap<String, String>) dataSnapshot.getValue();
                                        names.add(childref.get("name"));

                                       mBuilder = new NotificationCompat.Builder(getContext(),"48920")
                                                .setContentTitle(childref.get("name"))
                                                .setContentText("Sent you a friend request")
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                .setAutoCancel(true);

                                       notif_action(rootview,reference,list_item.getPositionForView(rootview));

                                        ArrayAdapter<String> Requests = new ArrayAdapter(getContext(), R.layout.requests_template, names);
                                        list_item.setAdapter(Requests);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        return;
                                    }
                                });
                            }
//                            Toast.makeText(getContext(), child.getKey(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "No requests", Toast.LENGTH_SHORT).show();
                    }
                });

        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                list_item.setEnabled(false);
                reference.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            HashMap<String,String> information = (HashMap<String,String>) child.getValue();
                            if (information.get("name").equals(names.get(position))){
                                user_key = child.getKey();
                                Intent home = new Intent(getContext(),Other_Home.class);
                                home.putExtra("user_id",user_key);
                                getActivity().startActivity(home);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "No requests", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return rootview;
    }

    public void notif_action(View view, DatabaseReference reference, final int position){
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    HashMap<String,String> information = (HashMap<String,String>) child.getValue();
                    if (information.get("name").equals(names.get(position))){
                        user_key = child.getKey();
                        Intent home = new Intent(getContext(),Other_Home.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),0,home,PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(pendingIntent);

                        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                        manager.notify(48920,mBuilder);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}