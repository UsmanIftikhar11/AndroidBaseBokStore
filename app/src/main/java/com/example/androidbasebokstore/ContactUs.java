package com.example.androidbasebokstore;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactUs extends AppCompatActivity {

    Button btn_suggest ;
    EditText edittext_suggest , edittext_name ;
    String suggest , names ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        btn_suggest = (Button)findViewById(R.id.btn_suggest);
        edittext_suggest = (EditText)findViewById(R.id.edittext_suggest);
        edittext_name = (EditText)findViewById(R.id.edittext_name);

        btn_suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                suggest = edittext_suggest.getText().toString();
                names = edittext_name.getText().toString();
                Intent intent = null, chooser = null;
                intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("milto:"));
                String[] to ={"bilal.ciit1@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Complaint from "+names);
                intent.putExtra(Intent.EXTRA_TEXT, suggest);
                intent.setType("message/rfc822");
                chooser = intent.createChooser(intent, "Send Email Using");
                startActivity(chooser);
            }
        });
    }
}
