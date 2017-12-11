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

public class MainActivity extends AppCompatActivity {

    TextView txt_forgot_pass ;
    EditText et_email , et_pass ;
    Button btn_login , btn_createAccount , btn_adminLogin ;

    private FirebaseAuth mAuth ;
    private DatabaseReference mDatabase ;
    private ProgressDialog mProgress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mProgress = new ProgressDialog(this);

        txt_forgot_pass = (TextView)findViewById(R.id.txt_forgotPass);
        et_email = (EditText)findViewById(R.id.edit_email);
        et_pass = (EditText)findViewById(R.id.edit_pass);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_createAccount = (Button)findViewById(R.id.btn_createAccount);
        btn_adminLogin = (Button)findViewById(R.id.btn_adminLogin);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLogin();
            }
        });

        btn_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this , SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        btn_adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this , AdminLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkLogin() {
        String email = et_email.getText().toString().trim();
        String password = et_pass.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Checking Login...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        mProgress.dismiss();
                        checkUserExist();
                    }
                    else {
                        mProgress.dismiss();
                        Toast.makeText(MainActivity.this , "Error Login " , Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        else {
            Toast.makeText(MainActivity.this , "Fill all fields " , Toast.LENGTH_LONG).show();
        }

    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){

                    Intent mainIntent = new Intent(MainActivity.this , UserHome.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }
                else {
                    // later on give the link of edit page saying you have incomplete data fill details again
                   /* Intent setupIntent = new Intent(CompanyLogin.this , CompanyRegistration.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);*/
                    Toast.makeText(MainActivity.this , "Setup your account " , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
