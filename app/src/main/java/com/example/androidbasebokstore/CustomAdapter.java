package com.example.androidbasebokstore;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mAni on 17/12/2017.
 */

public class CustomAdapter extends BaseAdapter {

    Context c ;
    ArrayList<Variables> categorya ;

    public CustomAdapter(Context c, ArrayList<Variables> categorya) {
        this.c = c;
        this.categorya = categorya;
    }

    @Override
    public int getCount() {
        return categorya.size();
    }

    @Override
    public Object getItem(int i) {
        return categorya.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(c).inflate(R.layout.category_row , parent , false);
        }

        TextView post_category = (TextView) convertView.findViewById(R.id.txt_singleCategory);

        final Variables v = (Variables) this.getItem(position);

        post_category.setText(v.getCategory());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c , SingleCategoryBooks.class);
                intent.putExtra("Category" , v.getCategory());
                c.startActivity(intent);
            }
        });

        return convertView;
    }
}
