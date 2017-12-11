package com.example.androidbasebokstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.androidbasebokstore.R.id.btn_login;

public class AdminLogin extends AppCompatActivity {

    EditText et_email, et_pass;
    Button btn_loginAdmin;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Admin");
        mDatabase.keepSynced(true);
        mProgress = new ProgressDialog(this);

        et_email = (EditText) findViewById(R.id.edit_email_admin);
        et_pass = (EditText) findViewById(R.id.edit_pass_admin);
        btn_loginAdmin = (Button) findViewById(R.id.btn_login_admin);

        btn_loginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLogin();
            }
        });
    }

    private void checkLogin() {
        String email = et_email.getText().toString().trim();
        String password = et_pass.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgress.setMessage("Checking Login...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        mProgress.dismiss();
                        checkUserExist();
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(AdminLogin.this, "Error Login ", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(AdminLogin.this, "Fill all fields ", Toast.LENGTH_LONG).show();
        }

    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user_id)) {

                    Intent mainIntent = new Intent(AdminLogin.this, AdminHomePage.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                } else {
                    // later on give the link of edit page saying you have incomplete data fill details again
                   /* Intent setupIntent = new Intent(CompanyLogin.this , CompanyRegistration.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);*/
                    Toast.makeText(AdminLogin.this, "Invalid Login ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
