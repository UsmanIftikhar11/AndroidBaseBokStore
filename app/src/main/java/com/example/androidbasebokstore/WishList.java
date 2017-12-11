package com.example.androidbasebokstore;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class WishList extends AppCompatActivity {

    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    private RecyclerView mWishList ;
    private DatabaseReference mDatabase ;
    private static  String product_key ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        mAuth = FirebaseAuth.getInstance();

        String curretnUserId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("WishList").child(curretnUserId);

        mWishList = (RecyclerView)findViewById(R.id.wish_list);
        mWishList.setHasFixedSize(true);
        mWishList.setLayoutManager(new LinearLayoutManager(this));

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent LoginIntent = new Intent(WishList.this , MainActivity.class);
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

        FirebaseRecyclerAdapter<Variables , WishListViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, WishListViewHolder>(

                Variables.class ,
                R.layout.bok_row ,
                WishListViewHolder.class ,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(WishListViewHolder viewHolder, final Variables model, int position) {

                product_key = getRef(position).getKey();

                final DatabaseReference mDatabaseWish = mDatabase.child(product_key);

                viewHolder.setBookTitle(model.getBookTitle());
                viewHolder.setAuthorName(model.getAuthorName());
                viewHolder.setTotalPages(model.getTotalPages());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setCategory(model.getCategory());
                viewHolder.setImage(getApplicationContext() , model.getImage());

                viewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabaseWish.removeValue();
                    }
                });
            }
        };
        mWishList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class WishListViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton btn_remove ;
        Context context ;

        public WishListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            context = itemView.getContext();
            btn_remove = (ImageButton)mView.findViewById(R.id.img_btn_remove);
        }

        public void setBookTitle(String bookTitle){
            TextView post_title = (TextView)mView.findViewById(R.id.post_BookTitle);
            post_title.setText(bookTitle);
        }
        public void setAuthorName(String authorName){
            TextView post_author = (TextView)mView.findViewById(R.id.post_AuthorName);
            post_author.setText(authorName);
        }
        public void setPrice(int price){
            TextView post_price = (TextView)mView.findViewById(R.id.post_BookPrice);
            post_price.setText(String.valueOf(price));
        }
        public void setTotalPages(String totalPages){
            TextView post_totalPages = (TextView)mView.findViewById(R.id.post_TotalPages);
            post_totalPages.setText(totalPages);
        }
        public void setCategory(String category){
            TextView post_category = (TextView)mView.findViewById(R.id.post_Category);
            post_category.setText(category);
        }
        public void setImage(Context ctx , String image){
            ImageView post_img = (ImageView)mView.findViewById(R.id.post_BookImg);
            Picasso.with(ctx).load(image).into(post_img);
        }
    }
}
