package com.example.androidbasebokstore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserBookReview extends AppCompatActivity {

    private EditText et_comment ;
    private Button btn_post_comment ;
    private RecyclerView review_list ;
    private String bookKey , userName ;

    private DatabaseReference mDatabse , mDatabaseUser ;
    private FirebaseUser mCurrentUser ;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_review);

        bookKey = getIntent().getExtras().getString("BookId");

        mAuth = FirebaseAuth.getInstance();
        mDatabse = FirebaseDatabase.getInstance().getReference().child("Comments").child(bookKey);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        et_comment = (EditText)findViewById(R.id.et_review);
        btn_post_comment = (Button)findViewById(R.id.btn_review);
        review_list = (RecyclerView)findViewById(R.id.review_list);
        review_list.setHasFixedSize(true);
        review_list.setLayoutManager(new LinearLayoutManager(this));

        btn_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {

        final String comment = et_comment.getText().toString().trim();

        if (!TextUtils.isEmpty(comment)){

            final DatabaseReference newComment = mDatabse.push();

            mDatabse.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newComment.child("Comment").setValue(comment) ;
                    newComment.child("PostedBy").setValue(userName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        else {

            Toast.makeText(getApplicationContext() , "please write something" , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Variables , ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Variables, ProductViewHolder>(

                Variables.class ,
                R.layout.review_row ,
                ProductViewHolder.class ,
                mDatabse
        ) {
            @Override
            protected void populateViewHolder(ProductViewHolder viewHolder, final Variables model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setPostedBy(model.getPostedBy());



            }
        };
        review_list.setAdapter(firebaseRecyclerAdapter);

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView post_name = (TextView)mView.findViewById(R.id.txt_userName);
            post_name.setText(name);
        }
        public void setPostedBy(String postedBy){
            TextView posted_By = (TextView)mView.findViewById(R.id.txt_comment);
            posted_By.setText(postedBy);
        }
    }
}
