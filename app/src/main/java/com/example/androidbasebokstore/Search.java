package com.example.androidbasebokstore;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

public class Search extends AppCompatActivity {

    private RecyclerView mBookList ;
    private DatabaseReference mDatabase ;

    private Query mQueryUser ;

    private EditText edit_search ;
    private Button btn_search ;

    MaterialSpinner searchSpinner ;
    private String spinnerString ;

    private String searchString ;
    private static String currentUser ;
    private boolean mwishlistChecked = false ;

    private DatabaseReference mDatabaseWishlist ;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser().getUid() ;

        mDatabaseWishlist = FirebaseDatabase.getInstance().getReference().child("WishList").child(currentUser);
        mDatabaseWishlist.keepSynced(true);

        edit_search = (EditText)findViewById(R.id.et_userSearch);
        btn_search = (Button)findViewById(R.id.btn_userSearch);
        mBookList = (RecyclerView)findViewById(R.id.userBookSearch_list);


        searchSpinner = (MaterialSpinner) findViewById(R.id.search_spinner);
        searchSpinner.setItems("Search By" , "Title" , "Author" , "price low to high" , "price high to low" );

        searchSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(item.toString()=="Title")
                {
                    spinnerString = "title" ;
                    mBookList.setHasFixedSize(true);
                    mBookList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
                else if (item.toString()=="Author")
                {
                    spinnerString = "author" ;
                    mBookList.setHasFixedSize(true);
                    mBookList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
                else if (item.toString()=="price low to high")
                {
                    spinnerString = "low" ;
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    mBookList.setHasFixedSize(true);
                    mBookList.setLayoutManager(layoutManager);
                }
                else if (item.toString()=="price high to low")
                {
                    spinnerString = "high" ;
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    mBookList.setHasFixedSize(true);
                    mBookList.setLayoutManager(layoutManager);
                }
                else {
                    spinnerString = null ;
                }
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchString = edit_search.getText().toString().toLowerCase();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");

                if(!TextUtils.isEmpty(searchString) && spinnerString == "title"){
                    mQueryUser = mDatabase.orderByChild("BookTitle").startAt(searchString).endAt(searchString + "~");
                }
                else if(!TextUtils.isEmpty(searchString) && spinnerString == "author"){
                    mQueryUser = mDatabase.orderByChild("AuthorName").startAt(searchString).endAt(searchString + "~");
                }
                else if(spinnerString == "low"){
                    mQueryUser = mDatabase.orderByChild("Price");
                }
                else if(spinnerString == "high"){
                    mQueryUser = mDatabase.orderByChild("Price");
                }
                else {
                    Toast.makeText(getApplicationContext() , "Input search & select search type" , Toast.LENGTH_LONG).show();
                }

                FirebaseRecyclerAdapter<Variables , UserSearchBookViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, UserSearchBookViewHolder>(

                        Variables.class ,
                        R.layout.user_book_row ,
                        UserSearchBookViewHolder.class ,
                        mQueryUser
                ) {
                    @Override
                    protected void populateViewHolder(UserSearchBookViewHolder viewHolder, final Variables model, int position) {

                        final String product_key = getRef(position).getKey();

                        final DatabaseReference mDatabaseWishlistProduct = mDatabaseWishlist.child(product_key);
                        mDatabaseWishlistProduct.keepSynced(true);

                        viewHolder.setBookTitle(model.getBookTitle());
                        viewHolder.setAuthorName(model.getAuthorName());
                        viewHolder.setTotalPages(model.getTotalPages());
                        viewHolder.setPrice(model.getPrice());
                        viewHolder.setCategory(model.getCategory());
                        viewHolder.setImage(getApplicationContext() , model.getImage());
                        viewHolder.setwishListBtn(product_key , model.getBookTitle());

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

                    }
                };
                mBookList.setAdapter(firebaseRecyclerAdapter);
            }
        });
    }

    public static class UserSearchBookViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton btn_wishlist ;
        Context context ;

        DatabaseReference mDatabaseWishList ;
        FirebaseAuth mAuth ;

        public UserSearchBookViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            context = itemView.getContext();
            btn_wishlist = (ImageButton)mView.findViewById(R.id.img_btn_wishList);

            mDatabaseWishList = FirebaseDatabase.getInstance().getReference().child("WishList").child(currentUser);
            mAuth = FirebaseAuth.getInstance();

            mDatabaseWishList.keepSynced(true);

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
            post_totalPages.setText(totalPages);
        }
        public void setCategory(String category){
            TextView post_category = (TextView)mView.findViewById(R.id.post_BookCategory);
            post_category.setText(category);
        }
        public void setImage(Context ctx , String image){
            ImageView post_img = (ImageView)mView.findViewById(R.id.post_BookImg);
            Picasso.with(ctx).load(image).into(post_img);
        }

        public void setwishListBtn(final String product_key , final String title){

            mDatabaseWishList.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(product_key)){

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
        }
    }
}
