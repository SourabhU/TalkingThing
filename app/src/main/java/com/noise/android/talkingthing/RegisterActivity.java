package com.noise.android.talkingthing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring the objects
    private Button Signup;
    private EditText Email_Reg;
    private EditText Password_1_Reg;
    private EditText Password_2_Reg;
    private EditText Username;

    private ProgressDialog progressdialog_Reg;

    private FirebaseAuth firebaseAuth_Reg;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initializing objects
        Signup = (Button) findViewById(R.id.Signup);
        Signup.setOnClickListener(this);

        Username = (EditText) findViewById(R.id.UserName);
        Email_Reg = (EditText) findViewById(R.id.Email_Reg);
        Password_1_Reg = (EditText) findViewById(R.id.Password_1_Reg);
        Password_2_Reg = (EditText) findViewById(R.id.Password_2_Reg);

        progressdialog_Reg = new ProgressDialog(this);

        firebaseAuth_Reg = FirebaseAuth.getInstance();
    }

    private void SignupUser(){
        final String username = Username.getText().toString().trim();
        String email = Email_Reg.getText().toString().trim();
        String password1 = Password_1_Reg.getText().toString().trim();
        String password2 = Password_2_Reg.getText().toString().trim();

        //Check emptiness of inputs
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)){
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //Check if password1 and password2 are the same
        if (!TextUtils.equals(password1,password2)){
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        //Inputs are valid apparently
        progressdialog_Reg.setMessage("Signing up, please wait.");
        progressdialog_Reg.setCancelable(true);
        progressdialog_Reg.show();

        /*FirebaseUser user = firebaseAuth_Reg.getCurrentUser();

        if(!user.isEmailVerified()){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(RegisterActivity.this, "Verfication Email Sent", Toast.LENGTH_SHORT).show();
                }
            });
        }*/

        firebaseAuth_Reg.createUserWithEmailAndPassword(email,password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                            String UID = User.getUid();

                            firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

                            HashMap<String,String> UsersData = new HashMap<>();
                            UsersData.put("name",username);
                            UsersData.put("image","default");
                            UsersData.put("status","Active");
                            UsersData.put("thumnail","default");

                            firebaseDatabase.setValue(UsersData);

                            progressdialog_Reg.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();
                              finish();
                                  startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        }
                        else{
                            progressdialog_Reg.dismiss();
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, "Email address taken", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    //Method to read register button press
    @Override
    public void onClick(View view){
        if(view == Signup)
            SignupUser();
    }
}
