package com.example.androidbasebokstore;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AdminAddBook extends AppCompatActivity {


    private ImageButton btn_select;

    private EditText et_title , et_authorName , et_price , et_noOfPages , et_category  ;
    private Button btn_submit ;

    private Uri imageUri = null ;

    private StorageReference mStorage ;
    private DatabaseReference mDatabse ;
    private DatabaseReference mDatabseUsers ;
    private DatabaseReference mDatabaseCategory , newCat ;
    private FirebaseAuth mAuth ;
    private FirebaseUser mCurrentUser ;
    private ProgressDialog mProgress ;

    MaterialSpinner spinner ;

    private static final int GALLERY_REQUEST = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_book);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabse = FirebaseDatabase.getInstance().getReference().child("Books");
        mDatabaseCategory = FirebaseDatabase.getInstance().getReference().child("Category");



        mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mProgress = new ProgressDialog(this);

        btn_select = (ImageButton)findViewById(R.id.imageSelect);
        et_title = (EditText)findViewById(R.id.et_titles);
        et_authorName = (EditText)findViewById(R.id.et_autherName);
        et_price = (EditText)findViewById(R.id.et_price);
        et_noOfPages = (EditText)findViewById(R.id.et_noOfPages);
        et_category = (EditText)findViewById(R.id.et_category);
        btn_submit = (Button)findViewById(R.id.submit_book);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent , GALLERY_REQUEST);

            }
        });

        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("Choose category" , "Art" , "Action and Adventure" , "Children's" , "Drama" , "Guide" , "Health" , "History" , "Horror" , "Math" , "Mystery" , "Poetry" , "Romance" , "Science" ,"Science fiction", "Travel" , "Sports" , "Others");

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(item.toString()=="Art")
                {
                    et_category.setText("Art");
                    et_category.setClickable(false);
                }
                else if (item.toString()=="Action and Adventure")
                {
                    et_category.setText("Action and Adventure");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Children's")
                {
                    et_category.setText("Children's");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Drama")
                {
                    et_category.setText("Drama");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Guide")
                {
                    et_category.setText("Guide");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Health")
                {
                    et_category.setText("Health");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="History")
                {
                    et_category.setText("History");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Horror")
                {
                    et_category.setText("Horror");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Math")
                {
                    et_category.setText("Math");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Mystery")
                {
                    et_category.setText("Mystery");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Poetry")
                {
                    et_category.setText("Poetry");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Romance")
                {
                    et_category.setText("Romance");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Science")
                {
                    et_category.setText("Science");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Science fiction")
                {
                    et_category.setText("Science fiction");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Travel")
                {
                    et_category.setText("Travel");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Sports")
                {
                    et_category.setText("Sports");
                    et_category.setClickable(false);
                }
                else if(item.toString()=="Others")
                {
                    et_category.setText("Others");
                    et_category.setClickable(false);
                }
                else {
                    et_category.setText("Choose category");
                    et_category.setClickable(false);
                }
            }
        });

        /*newCat = mDatabaseCategory.push();

        mDatabaseCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                newCat.child("Category").child(spinner.getItems().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUploadingBook();
            }
        });
    }

    private void startUploadingBook() {

        mProgress.setMessage("Adding Product...");


        final String bookTitle_string = et_title.getText().toString().toLowerCase().trim();
        final String authorName_string = et_authorName.getText().toString().trim();
        final int price_string = Integer.parseInt(et_price.getText().toString().trim());
        final String pages_string = et_noOfPages.getText().toString().trim();
        final String category_string = et_category.getText().toString().trim();

        if(!TextUtils.isEmpty(bookTitle_string) && !TextUtils.isEmpty(authorName_string) && !TextUtils.isEmpty(String.valueOf(price_string)) && !TextUtils.isEmpty(pages_string) && !TextUtils.isEmpty(category_string) && imageUri != null ){

            mProgress.show();

            StorageReference filePath = mStorage.child("Book").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests") final Uri downlaodUrl = taskSnapshot.getDownloadUrl();
                    //editText_title.setText(null);

                    final DatabaseReference newProduct = mDatabse.push();

                    mDatabseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // String s = String.valueOf(dataSnapshot.child("name").getValue());
                            // final DatabaseReference newProduct = mDatabse.push();



                            newProduct.child("BookTitle").setValue(bookTitle_string);
                            newProduct.child("AuthorName").setValue(authorName_string);
                            newProduct.child("Price").setValue(price_string);
                            newProduct.child("Image").setValue(downlaodUrl.toString());
                            newProduct.child("Category").setValue(category_string);
                            newProduct.child("TotalPages").setValue(pages_string).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        //startActivity(new Intent(CompanyAddProduct.this  , CompanyHome.class));
                                        Intent intent = new Intent(AdminAddBook.this  , AdminHomePage.class);
                                        startActivity(intent);

                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //  newProduct.child("CompanyName").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {

                    mProgress.dismiss();
                }

            });
        }

        else {
            Toast.makeText(getApplicationContext() , "fill all fields" , Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            btn_select.setImageURI(imageUri);
        }
    }
}
