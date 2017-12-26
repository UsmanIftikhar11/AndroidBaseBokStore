package com.example.androidbasebokstore;

import android.content.Intent;
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

public class MyOrders extends AppCompatActivity {

    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    private RecyclerView mMyOrderList ;
    private DatabaseReference mDatabase ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        mAuth = FirebaseAuth.getInstance();

        String curretnUserId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("CreatedOrders");

        mMyOrderList = (RecyclerView)findViewById(R.id.myOrders_list);
        mMyOrderList.setHasFixedSize(true);
        mMyOrderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //mAuth.addAuthStateListener(mAuthListner);

        FirebaseRecyclerAdapter<Variables , MyOrderViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, MyOrderViewHolder>(

                Variables.class ,
                R.layout.category_row ,
                MyOrderViewHolder.class ,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(MyOrderViewHolder viewHolder, final Variables model, int position) {

                //product_key = getRef(position).getKey();

                viewHolder.setOrderNo(model.getOrderNo());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getApplicationContext() , "Whole click" , Toast.LENGTH_LONG).show();
                        Intent singleProduct = new Intent(MyOrders.this , SingleOrderDetails.class);
                        singleProduct.putExtra("orderNo" , model.getOrderNo());
                        singleProduct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(singleProduct);
                    }
                });



            }
        };
        mMyOrderList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyOrderViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyOrderViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setOrderNo(String orderNo){
            TextView post_order = (TextView)mView.findViewById(R.id.txt_singleCategory);
            post_order.setText(orderNo);
        }
    }
}
