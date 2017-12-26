package com.example.androidbasebokstore;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserConfirmOrder extends AppCompatActivity {

    private String orderNo , totalBooks , totalPrice , Name , Address , City , Phone ;

    private TextView txt_noOfBooks , txt_price ;
    private Button btn_confirmOrder ;

    private DatabaseReference mDatabaseOrders  , mDatabaseCart , mDataBaseOrderDetails , mCart ;
    private FirebaseAuth mAuth ;

    private static  String product_key ;

    private RecyclerView mBillList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_confirm_order);

        orderNo =getIntent().getExtras().getString("orderNo");
        totalBooks =getIntent().getExtras().getString("totalBooks");
        totalPrice =getIntent().getExtras().getString("totalPrice");
        Name =getIntent().getExtras().getString("Name");
        Address =getIntent().getExtras().getString("Adress");
        City =getIntent().getExtras().getString("City");
        Phone =getIntent().getExtras().getString("Phone");


        mAuth = FirebaseAuth.getInstance();
        mDatabaseCart = FirebaseDatabase.getInstance().getReference().child("Cart").child(mAuth.getCurrentUser().getUid());
        mCart = FirebaseDatabase.getInstance().getReference().child("Cart");
        mDatabaseOrders = FirebaseDatabase.getInstance().getReference().child("CreatedOrders").child(orderNo);
        mDataBaseOrderDetails = FirebaseDatabase.getInstance().getReference().child("OrderDetails").child(orderNo);

        mBillList = (RecyclerView)findViewById(R.id.bill_list);
        mBillList.setHasFixedSize(true);
        mBillList.setLayoutManager(new LinearLayoutManager(this));

        txt_noOfBooks = (TextView)findViewById(R.id.txt_noOfBooks);
        txt_price = (TextView)findViewById(R.id.txt_price);


        txt_noOfBooks.setText(totalBooks);
        txt_price.setText(totalPrice);


        btn_confirmOrder = (Button)findViewById(R.id.btn_confirmOrder);

        btn_confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabaseOrders.child("OrderNo").setValue(orderNo);
                mDatabaseOrders.child("TotalBooks").setValue(totalBooks);
                mDatabaseOrders.child("TotalPrice").setValue(totalPrice);
                mDatabaseOrders.child("Name").setValue(Name);
                mDatabaseOrders.child("Address").setValue(Address);
                mDatabaseOrders.child("City").setValue(City);
                mDatabaseOrders.child("OrderBy").setValue(mAuth.getCurrentUser().getUid());
                mDatabaseOrders.child("Status").setValue("Pending");
                mDatabaseOrders.child("Phone").setValue(Phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        FirebaseRecyclerAdapter<Variables , BillViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, BillViewHolder>(

                                Variables.class ,
                                R.layout.bill_row ,
                                BillViewHolder.class ,
                                mDatabaseCart
                        ) {
                            @Override
                            protected void populateViewHolder(final BillViewHolder viewHolder, final Variables model, int position) {

                                product_key = getRef(position).getKey();
                                final DatabaseReference orderDetails = mDataBaseOrderDetails.child(product_key);

                                orderDetails.child("BookTitle").setValue(model.getBookTitle());
                                orderDetails.child("AuthorName").setValue(model.getAuthorName());
                                orderDetails.child("Price").setValue(model.getPrice());

                                mDatabaseCart.child(product_key).removeValue();

                            }
                        };
                        mBillList.setAdapter(firebaseRecyclerAdapter);
                    }
                });

                Toast.makeText(getApplicationContext() , "Order Placed Successfully" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UserConfirmOrder.this , UserHome.class);
                startActivity(intent);
                /*mDatabaseOrders.child("OrderNumber").setValue(orderNo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        mDatabaseCart.child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext() , "Order Placed Successfully" , Toast.LENGTH_LONG).show();
                            }
                        });

                        mCart.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mDataBaseOrderDetails.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        if(databaseError == null){

                                            mCart.child(mAuth.getCurrentUser().getUid()).child("AuthorName").removeValue();
                                            mCart.child(mAuth.getCurrentUser().getUid()).child("BookTitle").removeValue();
                                            mCart.child(mAuth.getCurrentUser().getUid()).child("Category").removeValue();
                                            mCart.child(mAuth.getCurrentUser().getUid()).child("Image").removeValue();
                                            mCart.child(mAuth.getCurrentUser().getUid()).child("Price").removeValue();
                                            mCart.child(mAuth.getCurrentUser().getUid()).child("TotalPages").removeValue();


                                            Toast.makeText(getApplicationContext() , "Order Placed Successfully" , Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(UserConfirmOrder.this , UserHome.class);
                                            startActivity(intent);
                                            mCart.child(mAuth.getCurrentUser().getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getApplicationContext() , "Order Placed Successfully" , Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(UserConfirmOrder.this , UserHome.class);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });*/


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Variables , BillViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, BillViewHolder>(

                Variables.class ,
                R.layout.bill_row ,
                BillViewHolder.class ,
                mDatabaseCart
        ) {
            @Override
            protected void populateViewHolder(final BillViewHolder viewHolder, final Variables model, int position) {

                product_key = getRef(position).getKey();

                viewHolder.setBookTitle(model.getBookTitle());
                viewHolder.setAuthorName(model.getAuthorName());
                viewHolder.setPrice(model.getPrice());

            }
        };
        mBillList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BillViewHolder extends RecyclerView.ViewHolder{

        View mView;
        Context context ;

        public BillViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            context = itemView.getContext();
        }

        public void setBookTitle(String bookTitle){
            TextView post_title = (TextView)mView.findViewById(R.id.txt_billBookName);
            post_title.setText(bookTitle);
        }
        public void setAuthorName(String authorName){
            TextView post_author = (TextView)mView.findViewById(R.id.txt_billAuthorName);
            post_author.setText("By: " + authorName);
        }
        public void setPrice(int price){
            TextView post_price = (TextView)mView.findViewById(R.id.txt_billBookPrice);
            post_price.setText(String.valueOf("Rs." + price));
        }
    }
}
