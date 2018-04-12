package com.noise.android.talkingthing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class Respond extends AppCompatActivity {

    private String user_id;
    private String current_user_id;
    private String response;
    private EditText response_entry;
    private Button respond_button;
    private String date;

    private DatabaseReference Forum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond);

        user_id = getIntent().getStringExtra("user_id");

        current_user_id  = FirebaseAuth.getInstance().getCurrentUser().getUid();

        response_entry = findViewById(R.id.reponse_entry);
        respond_button = findViewById(R.id.respond_button);

        Forum = FirebaseDatabase.getInstance().getReference().child("Forum");
        date = DateFormat.getDateInstance().format(new Date());

        respond_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                respond_button.setEnabled(false);

                response = response_entry.getText().toString();
                if (response.isEmpty()){
                    Toast.makeText(Respond.this, "Please fill in a response", Toast.LENGTH_SHORT).show();
                    respond_button.setEnabled(true);
                    return;
                }

                Forum.child(date).child(user_id).child("responses").child(current_user_id).setValue(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Respond.this, "Response added", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
}
