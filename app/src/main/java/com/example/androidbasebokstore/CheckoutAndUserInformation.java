package com.example.androidbasebokstore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckoutAndUserInformation extends AppCompatActivity {

    private EditText et_Name , et_Adress , et_City , et_Phone ;
    private Button btn_submitAdress ;

    private String orderNo , totalBooks , totalPrice ;
    private String Name , Address , City , Phone ;

    private DatabaseReference mDatabaseAdress ;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_and_user_information);

        et_Name = (EditText)findViewById(R.id.et_Name);
        et_Adress = (EditText)findViewById(R.id.et_Adress);
        et_City = (EditText)findViewById(R.id.et_City);
        et_Phone = (EditText)findViewById(R.id.et_Phone);
        btn_submitAdress = (Button)findViewById(R.id.btn_submitAdress);

        orderNo = getIntent().getExtras().getString("orderNo");
        totalBooks = getIntent().getExtras().getString("totalBooks");
        totalPrice = getIntent().getExtras().getString("totalPrice");

        mAuth = FirebaseAuth.getInstance() ;
        mDatabaseAdress = FirebaseDatabase.getInstance().getReference().child("CreatedOrders").child(orderNo);



        btn_submitAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name = et_Name.getText().toString().trim();
                Address = et_Adress.getText().toString().trim();
                City = et_City.getText().toString().trim();
                Phone = et_Phone.getText().toString().trim();

                if(!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Address) && !TextUtils.isEmpty(City) && !TextUtils.isEmpty(Phone)){

                    Intent intent = new Intent(CheckoutAndUserInformation.this , UserConfirmOrder.class);
                    intent.putExtra("orderNo" , orderNo);
                    intent.putExtra("totalBooks" , totalBooks);
                    intent.putExtra("totalPrice" , totalPrice);
                    intent.putExtra("Name" , Name);
                    intent.putExtra("Adress" , Address);
                    intent.putExtra("City" , City);
                    intent.putExtra("Phone" , Phone);

                    startActivity(intent);
                }
            }
        });

    }

    /*@Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent = new Intent(CheckoutAndUserInformation.this , BookCart.class) ;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }*/
}
