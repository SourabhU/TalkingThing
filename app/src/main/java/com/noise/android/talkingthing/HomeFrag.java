package com.noise.android.talkingthing;

import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import static android.R.attr.data;
import static android.R.attr.name;
import static android.R.attr.onClick;
import static com.noise.android.talkingthing.R.id.Register;
import static com.noise.android.talkingthing.R.id.Signin;
import static com.noise.android.talkingthing.R.id.signout;
import static com.noise.android.talkingthing.R.id.start;


public class HomeFrag extends android.support.v4.app.Fragment {

    private Button signout;
    private FirebaseAuth firebaseAuth;
    private TextView username;

    public HomeFrag() {
    }

    public static HomeFrag newInstance() {
        HomeFrag fragment = new HomeFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final Button signout = (Button) rootView.findViewById(R.id.signout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        //Bringing in User information from the database
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                username = rootView.findViewById(R.id.UserName);
                username.setText(name);
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Unable to retrive user data", Toast.LENGTH_SHORT).show();
            }
        });

        signout.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        if (view.getId() == R.id.signout) {
                            firebaseAuth.getInstance().signOut();
                            getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        }

                    }
                }
        );
        return rootView;
    }
}

