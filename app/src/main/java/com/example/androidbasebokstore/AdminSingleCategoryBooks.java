package com.example.androidbasebokstore;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class AdminSingleCategoryBooks extends AppCompatActivity {

    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    private RecyclerView mBookList ;
    private DatabaseReference mDatabase ;
    private Query mQueryCategory ;
    private DatabaseReference mDatabaseCurrentUser ;
    private static  String product_key ;
    private String category ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_single_category_books);

        mAuth = FirebaseAuth.getInstance();

        String curretnUserId = mAuth.getCurrentUser().getUid();
        category = getIntent().getExtras().getString("Category");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mQueryCategory = mDatabase.orderByChild("Category").equalTo(category);

        mBookList = (RecyclerView)findViewById(R.id.category_list);
        mBookList.setHasFixedSize(true);
        mBookList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //mAuth.addAuthStateListener(mAuthListner);

        FirebaseRecyclerAdapter<Variables , BookViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, BookViewHolder>(

                Variables.class ,
                R.layout.bok_row ,
                BookViewHolder.class ,
                mQueryCategory
        ) {
            @Override
            protected void populateViewHolder(BookViewHolder viewHolder, final Variables model, int position) {

                product_key = getRef(position).getKey();

                viewHolder.setBookTitle(model.getBookTitle());
                viewHolder.setAuthorName(model.getAuthorName());
                viewHolder.setTotalPages(model.getTotalPages());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setCategory(model.getCategory());
                viewHolder.setImage(getApplicationContext() , model.getImage());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getApplicationContext() , "Whole click" , Toast.LENGTH_LONG).show();
                        Intent singleProduct = new Intent(AdminSingleCategoryBooks.this , AdminUpdateProduct.class);
                        singleProduct.putExtra("Product_id" , product_key);
                        singleProduct.putExtra("BookTitle" , model.getBookTitle());
                        singleProduct.putExtra("AuthorName" , model.getAuthorName());
                        singleProduct.putExtra("BookPrice" , model.getPrice());
                        singleProduct.putExtra("TotalPages" , model.getTotalPages());
                        singleProduct.putExtra("Category" , model.getCategory());
                        singleProduct.putExtra("Image" , model.getImage());

                        startActivity(singleProduct);
                    }
                });

                viewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabase.child(product_key).removeValue();
                    }
                });



            }
        };
        mBookList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton btn_remove ;
        Context context ;

        public BookViewHolder(View itemView) {
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
            post_price.setText("Rs. " + price);
        }
        public void setTotalPages(String totalPages){
            TextView post_totalPages = (TextView)mView.findViewById(R.id.post_TotalPages);
            post_totalPages.setText("pages: "+totalPages);
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
