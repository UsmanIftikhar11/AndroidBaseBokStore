package com.example.androidbasebokstore;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    private GridLayout mBookList ;
    CustomAdapter adapter ;
    private DatabaseReference mDatabase ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        mAuth = FirebaseAuth.getInstance();

        String curretnUserId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Category");

        mBookList = (GridLayout)findViewById(R.id.grid);

        gridLayoutClick(mBookList) ;
        //mBookList.setHasFixedSize(true);
        //mBookList.setLayoutManager(new LinearLayoutManager(this));

        //adapter = new CustomAdapter(this , retreive());
        //mBookList.setAdapter(adapter);

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent LoginIntent = new Intent(UserHome.this , MainActivity.class);
                    LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(LoginIntent);
                }

            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void gridLayoutClick(GridLayout mBookList) {

        for(int i = 0 ; i < mBookList.getChildCount() ; i++){

            CardView cardView = (CardView) mBookList.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserHome.this , SingleCategoryBooks.class);
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

    /*private void fetchData (DataSnapshot dataSnapshot){

        category.clear();

        for(DataSnapshot ds : dataSnapshot.getChildren()){

             Variables variables = ds.getValue(Variables.class);
            category.add(variables);
        }
    }

    public ArrayList<Variables> retreive(){

        //DatabaseReference db = null;
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return category ;
    }*/



    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);

        /*FirebaseRecyclerAdapter<Variables , UserBookViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, UserBookViewHolder>(

                Variables.class ,
                R.layout.category_row ,
                UserBookViewHolder.class ,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(UserBookViewHolder viewHolder, final Variables model, int position) {

                String product_key = getRef(position).getKey();

                viewHolder.setCategory(model.getCategory());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getApplicationContext() , "Whole click" , Toast.LENGTH_LONG).show();
                        Intent singleProduct = new Intent(UserHome.this , SingleCategoryBooks.class);
                        singleProduct.putExtra("Category" , model.getCategory());

                        startActivity(singleProduct);
                    }
                });



            }
        };
        mBookList.setAdapter((ListAdapter) firebaseRecyclerAdapter);*/
    }

    /*public static class UserBookViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UserBookViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCategory(String category){
            TextView post_category = (TextView)mView.findViewById(R.id.txt_singleCategory);
            post_category.setText(category);
        }
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to logout ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mAuth.getInstance().signOut();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });

            final AlertDialog ad = builder.create();
            ad.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        displayselectedscreen(id);

        return true;
    }

    private void displayselectedscreen (int id){

        switch (id)
        {
            case R.id.nav_wishList:
                Intent intent = new Intent(UserHome.this, WishList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_search:
                Intent intent1 = new Intent(UserHome.this, Search.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
            case R.id.nav_cart:
                Intent intent2 = new Intent(UserHome.this, BookCart.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                break;
            case R.id.nav_myOrders:
                Intent intent3 = new Intent(UserHome.this, MyOrders.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                break;
            case R.id.nav_exit:
                mAuth.getInstance().signOut();
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
