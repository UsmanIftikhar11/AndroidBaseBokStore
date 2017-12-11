package com.example.androidbasebokstore;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.squareup.picasso.Picasso;

public class AdminUpdateProduct extends AppCompatActivity {

    private ImageButton btn_select;

    private EditText et_Updatetitle , et_UpdateauthorName , et_Updateprice , et_UpdatenoOfPages , et_Updatecategory  ;
    private Button btn_update ;

    private Uri imageUri = null ;

    private StorageReference mStorage ;
    private DatabaseReference mDatabse ;
    private DatabaseReference mDatabseUsers ;
    private FirebaseAuth mAuth ;
    private FirebaseUser mCurrentUser ;
    private ProgressDialog mProgress ;
    Context ctx ;

    private  String product_Key , title , authorName , price , pages  , image , category;


    private static final int GALLERY_REQUEST = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_product);

        product_Key = getIntent().getExtras().getString("Product_id");
        title = getIntent().getExtras().getString("BookTitle");
        authorName = getIntent().getExtras().getString("AuthorName");
        price = getIntent().getExtras().getString("BookPrice");
        pages = getIntent().getExtras().getString("TotalPages");
        image = getIntent().getExtras().getString("Image");
        category = getIntent().getExtras().getString("Category");


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabse = FirebaseDatabase.getInstance().getReference().child("Books");

        mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mProgress = new ProgressDialog(this);

        btn_select = (ImageButton)findViewById(R.id.UpdateImageSelect);
        et_Updatetitle = (EditText)findViewById(R.id.et_updateTitle);
        et_UpdateauthorName = (EditText)findViewById(R.id.et_updateAutherName);
        et_Updateprice = (EditText)findViewById(R.id.et_UpdatePrice);
        et_UpdatenoOfPages = (EditText)findViewById(R.id.et_updateNoOfPages);
        et_Updatecategory = (EditText)findViewById(R.id.et_updateCategory);
        btn_update = (Button)findViewById(R.id.submit_book);


        et_Updatetitle.setText(title);
        et_UpdateauthorName.setText(authorName);
        et_Updateprice.setText(price);
        et_UpdatenoOfPages.setText(pages);
        et_Updatecategory.setText(category);
        Picasso.with(ctx).load(image).into(btn_select);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent , GALLERY_REQUEST);

            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUploadingBook();
            }
        });
    }

    private void startUploadingBook() {

        mProgress.setMessage("Adding Product...");


        final String bookTitle_string = et_Updatetitle.getText().toString().toLowerCase().trim();
        final String authorName_string = et_UpdateauthorName.getText().toString().trim();
        final String price_string = et_Updateprice.getText().toString().trim();
        final String pages_string = et_UpdatenoOfPages.getText().toString().trim();
        final String category_string = et_Updatecategory.getText().toString().trim();

        if(!TextUtils.isEmpty(bookTitle_string) && !TextUtils.isEmpty(authorName_string) && !TextUtils.isEmpty(price_string) && !TextUtils.isEmpty(pages_string) && !TextUtils.isEmpty(category_string) && imageUri != null ){

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
                                        Intent intent = new Intent(AdminUpdateProduct.this  , AdminHomePage.class);
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
