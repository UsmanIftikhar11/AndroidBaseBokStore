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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleCategoryBooks extends AppCompatActivity {

    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    private RecyclerView mUserBookList ;
    private DatabaseReference mDatabase ;
    private DatabaseReference mDatabaseWishlist , mDatabaseCart ;
    private Query mQueryCategory ;
    private static  String product_key ;

    private  String category ;
    static String currentUserId ;

    private boolean mwishlistChecked = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_category_books);

        category = getIntent().getExtras().getString("Category") ;

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mQueryCategory = mDatabase.orderByChild("Category").equalTo(category);
        mDatabaseWishlist = FirebaseDatabase.getInstance().getReference().child("WishList").child(currentUserId);
        mDatabaseCart = FirebaseDatabase.getInstance().getReference().child("Cart").child(currentUserId);

        mDatabase.keepSynced(true);
        mDatabaseWishlist.keepSynced(true);
        mDatabaseCart.keepSynced(true);

        mUserBookList = (RecyclerView)findViewById(R.id.user_singleBookList);
        mUserBookList.setHasFixedSize(true);
        mUserBookList.setLayoutManager(new LinearLayoutManager(this));

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent LoginIntent = new Intent(SingleCategoryBooks.this , MainActivity.class);
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

        FirebaseRecyclerAdapter<Variables , UserSingleBookViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, UserSingleBookViewHolder>(

                Variables.class ,
                R.layout.user_book_row ,
                UserSingleBookViewHolder.class ,
                mQueryCategory
        ) {
            @Override
            protected void populateViewHolder(UserSingleBookViewHolder viewHolder, final Variables model, int position) {

                final String product_key = getRef(position).getKey();

                final DatabaseReference mDatabaseWishlistProduct = mDatabaseWishlist.child(product_key);
                final DatabaseReference mDatabaseBookCart = mDatabaseCart.child(product_key);
                mDatabaseWishlistProduct.keepSynced(true);
                mDatabaseBookCart.keepSynced(true);

                viewHolder.setBookTitle(model.getBookTitle());
                viewHolder.setAuthorName(model.getAuthorName());
                viewHolder.setTotalPages(model.getTotalPages());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setCategory(model.getCategory());
                viewHolder.setImage(getApplicationContext() , model.getImage());
                viewHolder.setwishListBtn(product_key);

                viewHolder.btn_wishlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mwishlistChecked = true ;

                        mDatabaseWishlist.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mwishlistChecked == true){

                                    if(dataSnapshot.hasChild(product_key)){

                                        mDatabaseWishlistProduct.removeValue();
                                        mwishlistChecked = false ;
                                    }
                                    else {

                                        mDatabaseWishlist.child(product_key).child("BookTitle").setValue(model.getBookTitle());
                                        mDatabaseWishlist.child(product_key).child("AuthorName").setValue(model.getAuthorName());
                                        mDatabaseWishlist.child(product_key).child("Price").setValue(model.getPrice());
                                        mDatabaseWishlist.child(product_key).child("Image").setValue(model.getImage());
                                        mDatabaseWishlist.child(product_key).child("TotalPages").setValue(model.getTotalPages());
                                        mDatabaseWishlist.child(product_key).child("Category").setValue(model.getCategory());

                                        mwishlistChecked = false ;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                viewHolder.btn_addCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mwishlistChecked = true ;

                        mDatabaseCart.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mwishlistChecked == true){

                                    if(dataSnapshot.hasChild(product_key)){

                                        mDatabaseBookCart.removeValue();
                                        mwishlistChecked = false ;
                                    }
                                    else {

                                        mDatabaseCart.child(product_key).child("BookTitle").setValue(model.getBookTitle());
                                        mDatabaseCart.child(product_key).child("AuthorName").setValue(model.getAuthorName());
                                        mDatabaseCart.child(product_key).child("Price").setValue(model.getPrice());
                                        mDatabaseCart.child(product_key).child("Image").setValue(model.getImage());
                                        mDatabaseCart.child(product_key).child("TotalPages").setValue(model.getTotalPages());
                                        mDatabaseCart.child(product_key).child("Category").setValue(model.getCategory());

                                        mwishlistChecked = false ;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }) ;
                    }
                });

                viewHolder.btn_comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(SingleCategoryBooks.this , UserBookReview.class);
                        intent.putExtra("BookId" , product_key);
                        startActivity(intent);
                    }
                });

            }
        };
        mUserBookList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UserSingleBookViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton btn_wishlist , btn_addCart , btn_comments ;
        Context context ;

        DatabaseReference mDatabaseWishList , mDatabasebookCart , mWishlistChild , mCartChild ;
        FirebaseAuth mAuth ;

        public UserSingleBookViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            context = itemView.getContext();
            btn_wishlist = (ImageButton)mView.findViewById(R.id.img_btn_wishList);
            btn_addCart = (ImageButton)mView.findViewById(R.id.img_btn_addCart);
            btn_comments = (ImageButton)mView.findViewById(R.id.img_btn_Comments);

            mAuth = FirebaseAuth.getInstance();
            mDatabaseWishList = FirebaseDatabase.getInstance().getReference().child("WishList");
            mDatabasebookCart = FirebaseDatabase.getInstance().getReference().child("Cart");

            mWishlistChild = mDatabaseWishList.child(currentUserId);
            mCartChild = mDatabasebookCart.child(currentUserId);


            mDatabaseWishList.keepSynced(true);
            mDatabasebookCart.keepSynced(true);

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
            post_price.setText("Rs. " + price);
        }
        public void setTotalPages(String totalPages){
            TextView post_totalPages = (TextView)mView.findViewById(R.id.post_TotalPages);
            post_totalPages.setText("Pages: " + totalPages);
        }
        public void setCategory(String category){
            TextView post_category = (TextView)mView.findViewById(R.id.post_BookCategory);
            post_category.setText(category);
        }
        public void setImage(Context ctx , String image){
            ImageView post_img = (ImageView)mView.findViewById(R.id.post_BookImg);
            Picasso.with(ctx).load(image).into(post_img);
        }

        public void setwishListBtn(final String product_key1){

            mWishlistChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(product_key1).hasChild("BookTitle")){

                        btn_wishlist.setImageResource(R.drawable.bookmark_check);
                    }
                    else {
                        btn_wishlist.setImageResource(R.drawable.bookmark);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mCartChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(product_key1).hasChild("BookTitle")){

                        btn_addCart.setImageResource(R.mipmap.ic_remove_shopping_cart_black_24dp);
                    }
                    else {
                        btn_addCart.setImageResource(R.mipmap.ic_add_shopping_cart_black_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}