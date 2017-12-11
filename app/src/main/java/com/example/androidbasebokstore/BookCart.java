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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class BookCart extends AppCompatActivity {

    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    private RecyclerView mCartList ;
    private DatabaseReference mDatabase , mDatabaseCreatedOrders ;
    private static  String product_key ;
    private Button btn_submitOrder ;

    private TextView txt_totalBooks , txt_totalPrice ;

    private int books = 0 , bookPrice = 0;
    private String orderNumber ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cart);

        mAuth = FirebaseAuth.getInstance();


        orderNumber = getSaltString() ;

        String curretnUserId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Cart").child(curretnUserId);
        mDatabaseCreatedOrders = FirebaseDatabase.getInstance().getReference().child("CreatedOrders").child(orderNumber);

        mCartList = (RecyclerView)findViewById(R.id.cart_list);
        mCartList.setHasFixedSize(true);
        mCartList.setLayoutManager(new LinearLayoutManager(this));

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent LoginIntent = new Intent(BookCart.this , MainActivity.class);
                    LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(LoginIntent);
                }

            }
        };

        txt_totalBooks = (TextView)findViewById(R.id.txt_totalBooks);
        txt_totalPrice = (TextView)findViewById(R.id.txt_totalPrice);

        btn_submitOrder = (Button)findViewById(R.id.btn_submitOrder);



            btn_submitOrder.setClickable(true);
            btn_submitOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(books <= 0){
                        Toast.makeText(getApplicationContext() , "Cart is Empty" , Toast.LENGTH_LONG).show();
                    }
                    else {

                        //DatabaseReference mDatabaseBill = mDatabaseCreatedOrders.child(mAuth.getCurrentUser().getUid());
                        mDatabaseCreatedOrders.child("TotalBooks").setValue(books);
                        mDatabaseCreatedOrders.child("TotalPrice").setValue(bookPrice);

                        //String orderNo = getSaltString() ;

                        Intent intent = new Intent(BookCart.this, CheckoutAndUserInformation.class);
                        intent.putExtra("orderNo" , orderNumber);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);

        FirebaseRecyclerAdapter<Variables , CartViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, CartViewHolder>(

                Variables.class ,
                R.layout.cart_row ,
                CartViewHolder.class ,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(CartViewHolder viewHolder, final Variables model, final int position) {

                product_key = getRef(position).getKey();

                final DatabaseReference mDatabaseCart = mDatabase.child(product_key);
                final DatabaseReference mDatabsePushOrder = mDatabaseCreatedOrders.child(product_key);

                viewHolder.setBookTitle(model.getBookTitle());
                viewHolder.setAuthorName(model.getAuthorName());
                viewHolder.setTotalPages(model.getTotalPages());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setCategory(model.getCategory());
                viewHolder.setImage(getApplicationContext() , model.getImage());

                //bookId = product_key;

                //recreate();

                bookPrice = bookPrice + model.getPrice() ;

                books++ ;

                txt_totalBooks.setText(books + " Books");
                txt_totalPrice.setText("Rs. " + bookPrice);


                mDatabaseCreatedOrders.child(product_key).setValue("Ordered");
                //mDatabsePushOrder.child("TotalBooks").setValue(txt_totalBooks);
                //mDatabsePushOrder.child("TotlaPrice").setValue(txt_totalPrice);

                viewHolder.btn_remove1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabaseCart.removeValue();
                        mDatabsePushOrder.removeValue();
                        //finish();
                        //startActivity(getIntent());
                        recreate();
                    }
                });
            }
        };
        mCartList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton btn_remove1 ;
        Context context ;

        public CartViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            context = itemView.getContext();
            btn_remove1 = (ImageButton)mView.findViewById(R.id.img_btn_remove1);
        }

        public void setBookTitle(String bookTitle){
            TextView post_title = (TextView)mView.findViewById(R.id.post_BookTitle1);
            post_title.setText(bookTitle);
        }
        public void setAuthorName(String authorName){
            TextView post_author = (TextView)mView.findViewById(R.id.post_AuthorName1);
            post_author.setText(authorName);
        }
        public void setPrice(int price){
            TextView post_price = (TextView)mView.findViewById(R.id.post_BookPrice1);
            post_price.setText(String.valueOf("Rs. "+price));
        }
        public void setTotalPages(String totalPages){
            TextView post_totalPages = (TextView)mView.findViewById(R.id.post_TotalPages1);
            post_totalPages.setText("pages: "+totalPages);
        }
        public void setCategory(String category){
            TextView post_category = (TextView)mView.findViewById(R.id.post_Category1);
            post_category.setText(category);
        }
        public void setImage(Context ctx , String image){
            ImageView post_img = (ImageView)mView.findViewById(R.id.post_BookImg1);
            Picasso.with(ctx).load(image).into(post_img);
        }
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 05) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
