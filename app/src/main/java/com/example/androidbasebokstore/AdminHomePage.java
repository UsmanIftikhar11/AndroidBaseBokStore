package com.example.androidbasebokstore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminHomePage extends AppCompatActivity {


    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    private GridLayout mBookList ;
    private DatabaseReference mDatabase ;
    private DatabaseReference mDatabaseCurrentUser ;
    private static  String product_key ;

    private ImageButton btn_orders ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);

        mAuth = FirebaseAuth.getInstance();

        String curretnUserId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Category");

        mBookList = (GridLayout)findViewById(R.id.adminGrid);
        gridLayoutClick(mBookList) ;

        btn_orders = (ImageButton)findViewById(R.id.btn_orders);

        btn_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomePage.this , AdminOrders.class);
                startActivity(intent);
            }
        });

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent LoginIntent = new Intent(AdminHomePage.this , AdminLogin.class);
                    LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(LoginIntent);
                }

            }
        };

    }

    private void gridLayoutClick(GridLayout mBookList) {

        for(int i = 0 ; i < mBookList.getChildCount() ; i++){

            CardView cardView = (CardView) mBookList.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AdminHomePage.this , AdminSingleCategoryBooks.class);
                    if (finalI == 0){
                        intent.putExtra("Category" , "Art");
                    }
                    else if(finalI == 1){
                        intent.putExtra("Category" , "Action and Adventure");
                    }
                    else if(finalI == 2){
                        intent.putExtra("Category" , "Children's");
                    }
                    else if(finalI == 3){
                        intent.putExtra("Category" , "Drama");
                    }
                    else if(finalI == 4){
                        intent.putExtra("Category" , "Guide");
                    }
                    else if(finalI == 5){
                        intent.putExtra("Category" , "Health");
                    }
                    else if(finalI == 6){
                        intent.putExtra("Category" , "History");
                    }
                    else if(finalI == 7){
                        intent.putExtra("Category" , "Horror");
                    }
                    else if(finalI == 8){
                        intent.putExtra("Category" , "Math");
                    }
                    else if(finalI == 9){
                        intent.putExtra("Category" , "Mystery");
                    }
                    else if(finalI == 10){
                        intent.putExtra("Category" , "Poetry");
                    }
                    else if(finalI == 11){
                        intent.putExtra("Category" , "Romance");
                    }
                    else if(finalI == 12){
                        intent.putExtra("Category" , "Science");
                    }
                    else if(finalI == 13){
                        intent.putExtra("Category" , "Science Fiction");
                    }
                    else if(finalI == 14){
                        intent.putExtra("Category" , "Sports");
                    }
                    else if(finalI == 15){
                        intent.putExtra("Category" , "Travel");
                    }
                    else {
                        intent.putExtra("Category" , "Others");
                    }
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);

        /*FirebaseRecyclerAdapter<Variables , CategoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, CategoryViewHolder>(

                Variables.class ,
                R.layout.category_row ,
                CategoryViewHolder.class ,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, final Variables model, int position) {

                product_key = getRef(position).getKey();

                viewHolder.setCategory(model.getCategory());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getApplicationContext() , "Whole click" , Toast.LENGTH_LONG).show();
                        Intent singleProduct = new Intent(AdminHomePage.this , AdminSingleCategoryBooks.class);
                        singleProduct.putExtra("Category" , model.getCategory());

                        startActivity(singleProduct);
                    }
                });



            }
        };
        mBookList.setAdapter(firebaseRecyclerAdapter);*/
    }

    /*public static class CategoryViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCategory(String category){
            TextView post_category = (TextView)mView.findViewById(R.id.txt_singleCategory);
            post_category.setText(category);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_logout){

            //logout(); mAuth.signOut();
            mAuth.getInstance().signOut();
        }

        if(item.getItemId() == R.id.action_add){

            startActivity(new Intent(AdminHomePage.this , AdminAddBook.class));
        }

        if(item.getItemId() == R.id.action_search){

            startActivity(new Intent(AdminHomePage.this , AdminSearch.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
