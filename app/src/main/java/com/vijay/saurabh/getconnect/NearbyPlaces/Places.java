package com.vijay.saurabh.getconnect.NearbyPlaces;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.vijay.saurabh.getconnect.MyNavigationActivity;
import com.vijay.saurabh.getconnect.R;

public class Places extends AppCompatActivity {
    DatabaseReference mref;
    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        Firebase.setAndroidContext(this);
        mref = FirebaseDatabase.getInstance().getReference().child("Places");
        rv = findViewById(R.id.recycle);
        rv.setHasFixedSize(true);
        //to click on item with item
        rv.setItemAnimator(new DefaultItemAnimator());
        //to take the recycler view in linear form
        /*recyclerView.setLayoutManager(new LinearLayoutManager(this));*/
        //to take the recycler view in grid form
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setSmoothScrollbarEnabled(true);
        rv.setLayoutManager(llm);
        Query qref = mref.orderByChild("name");

        FirebaseRecyclerAdapter<PlaceData,MyHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PlaceData, MyHolder>
                (PlaceData.class,R.layout.place_item,MyHolder.class,qref) {
            @Override
            protected void populateViewHolder(MyHolder viewHolder, PlaceData model, final int position) {
                final String name = model.getName();
                final double lat = Double.parseDouble(model.getLat());
                final double lang = Double.parseDouble(model.getLang());
                viewHolder.lbname.setText(name);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Places.this,MyNavigationActivity.class);
                        i.putExtra("lat",lat);
                        i.putExtra("lang",lang);
                        i.putExtra("place",name);
                        startActivity(i);
                        finish();
                    }
                });
            }
        };

        rv.setAdapter(firebaseRecyclerAdapter);

    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView lbname;


        public MyHolder(@NonNull View itemView) {
            //to typecast the widgets
            super(itemView);
            mView = itemView;
            lbname = mView.findViewById(R.id.lb_name);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Places.this,MyNavigationActivity.class));
        finish();
    }
}
