package com.example.androidbasebokstore;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserConfirmOrder extends AppCompatActivity {

    private String orderNo ;

    private TextView txt_noOfBooks , txt_price ;
    private Button btn_confirmOrder ;

    private DatabaseReference mDatabaseOrders  , mDatabase ;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_confirm_order);

        orderNo =getIntent().getExtras().getString("orderNo");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cart");
        mDatabaseOrders = FirebaseDatabase.getInstance().getReference().child("CreatedOrders").child(orderNo) ;

        txt_noOfBooks = (TextView)findViewById(R.id.txt_noOfBooks);
        txt_price = (TextView)findViewById(R.id.txt_price);

        mDatabaseOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                txt_noOfBooks.setText(dataSnapshot.child("TotalBooks").getValue().toString());
                txt_price.setText(dataSnapshot.child("TotalPrice").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_confirmOrder = (Button)findViewById(R.id.btn_confirmOrder);

        btn_confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseOrders.child("OrderNumber").setValue(orderNo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        mDatabase.child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext() , "Order Placed Successfully" , Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }
        });

    }
}
