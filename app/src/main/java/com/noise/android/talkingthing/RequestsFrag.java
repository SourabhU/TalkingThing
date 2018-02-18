package com.noise.android.talkingthing;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RequestsFrag extends Fragment {
    public static RequestsFrag newInstance() {
        RequestsFrag fragment = new RequestsFrag();
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

    private FirebaseDatabase firebaseDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child("Users");

        View rootview = inflater.inflate(R.layout.fragment_requests, container, false);

        return inflater.inflate(R.layout.fragment_requests, container, false);
        }
    }
