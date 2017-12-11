package com.example.androidbasebokstore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminOrders extends AppCompatActivity {

    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    private RecyclerView mOrderList ;
    private DatabaseReference mDatabase ;
    private DatabaseReference mDatabaseCurrentUser ;
    private static  String product_key ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        mAuth = FirebaseAuth.getInstance();

        String curretnUserId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("CreatedOrders");

        mOrderList = (RecyclerView)findViewById(R.id.order_list);
        mOrderList.setHasFixedSize(true);
        mOrderList.setLayoutManager(new LinearLayoutManager(this));

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent LoginIntent = new Intent(AdminOrders.this , AdminLogin.class);
                    LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(LoginIntent);
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);

        FirebaseRecyclerAdapter<Variables , OrderViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, OrderViewHolder>(

                Variables.class ,
                R.layout.category_row ,
                OrderViewHolder.class ,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Variables model, int position) {

                product_key = getRef(position).getKey();

                viewHolder.setOrderNumber(model.getOrderNumber());


                /*viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getApplicationContext() , "Whole click" , Toast.LENGTH_LONG).show();
                        Intent singleProduct = new Intent(AdminHomePage.this , AdminSingleCategoryBooks.class);
                        singleProduct.putExtra("Category" , model.getCategory());

                        startActivity(singleProduct);
                    }
                });*/



            }
        };
        mOrderList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setOrderNumber(String orderNumber){
            TextView post_order = (TextView)mView.findViewById(R.id.txt_singleCategory);
            post_order.setText(orderNumber);
        }
    }
}
