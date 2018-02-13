package com.noise.android.talkingthing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatFrag extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;

    private RecyclerView Userlist;

    public static ChatFrag newInstance() {
        ChatFrag fragment = new ChatFrag();
        return fragment;
    }

    public class Users {

        public String  name;
        public String image;
        public String status;

        public Users(){

        }

        public void setName(String name) {
            this.name = name;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public String getStatus() {
            return status;
        }

        public Users(String name, String image, String status) {
            this.name = name;
            this.image = image;
            this.status = status;
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        Userlist = rootView.findViewById(R.id.Userlist);
        Userlist.setHasFixedSize(true);
        Userlist.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_chat_template,
                UsersViewHolder.class,
                firebaseDatabase

        ){
            @Override
            protected void populateViewHolder(UsersViewHolder view, Users model, int position) {

                view.setName(model.getName());
            }
        };

        Userlist.setAdapter(firebaseRecyclerAdapter);
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View view;

        public UsersViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setName(String name){

            TextView usernameview = view.findViewById(R.id.Username);
            usernameview.setText(name);
        }
    }
}