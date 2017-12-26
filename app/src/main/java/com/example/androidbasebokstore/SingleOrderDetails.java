package com.example.androidbasebokstore;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SingleOrderDetails extends AppCompatActivity {

    private String orderNo ;

    private TextView txt_orderStatus , txt_name , txt_address , txt_phone , txt_totalBooks , txt_totalPrice , txt_orderNo ;

    private RecyclerView booksDetail_list ;

    private DatabaseReference mDatabaseCreatedOrders , mDatabaseOrderDetails ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order_details);

        orderNo = getIntent().getExtras().getString("orderNo");

        mDatabaseCreatedOrders = FirebaseDatabase.getInstance().getReference().child("CreatedOrders").child(orderNo);
        mDatabaseOrderDetails = FirebaseDatabase.getInstance().getReference().child("OrderDetails").child(orderNo);

        txt_orderStatus = (TextView)findViewById(R.id.txt_orderStatus);
        txt_name = (TextView)findViewById(R.id.txt_orderBy);
        txt_address = (TextView)findViewById(R.id.txt_Address);
        txt_phone = (TextView)findViewById(R.id.txt_orderPhone);
        txt_totalBooks = (TextView)findViewById(R.id.txt_books);
        txt_totalPrice = (TextView)findViewById(R.id.txt_bill);
        txt_orderNo = (TextView)findViewById(R.id.txt_orderNo);

        booksDetail_list = (RecyclerView)findViewById(R.id.booksDetail_list);
        booksDetail_list.setHasFixedSize(true);
        booksDetail_list.setLayoutManager(new LinearLayoutManager(this));

        mDatabaseCreatedOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txt_orderStatus.setText(dataSnapshot.child("Status").getValue().toString());
                txt_name.setText(dataSnapshot.child("Name").getValue().toString());
                txt_address.setText(dataSnapshot.child("Address").getValue().toString());
                txt_phone.setText(dataSnapshot.child("Phone").getValue().toString());
                txt_totalBooks.setText(dataSnapshot.child("TotalBooks").getValue().toString());
                txt_totalPrice.setText(dataSnapshot.child("TotalPrice").getValue().toString());
                txt_orderNo.setText(dataSnapshot.child("OrderNo").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Variables , BooksDetailViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, BooksDetailViewHolder>(

                Variables.class ,
                R.layout.bill_row ,
                BooksDetailViewHolder.class ,
                mDatabaseOrderDetails
        ) {
            @Override
            protected void populateViewHolder(final BooksDetailViewHolder viewHolder, final Variables model, int position) {

                //product_key = getRef(position).getKey();

                viewHolder.setBookTitle(model.getBookTitle());
                viewHolder.setAuthorName(model.getAuthorName());
                viewHolder.setPrice(model.getPrice());

            }
        };
        booksDetail_list.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BooksDetailViewHolder extends RecyclerView.ViewHolder{

        View mView;
        Context context ;

        public BooksDetailViewHolder(View itemView) {
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
