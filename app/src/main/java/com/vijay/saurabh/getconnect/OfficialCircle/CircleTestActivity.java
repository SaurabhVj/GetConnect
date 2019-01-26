package com.vijay.saurabh.getconnect.OfficialCircle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vijay.saurabh.getconnect.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CircleTestActivity extends AppCompatActivity {
    DatabaseReference mref;
    RecyclerView rv;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_test);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("CircleMembers");
        rv = findViewById(R.id.recycle_view);
        rv.setHasFixedSize(true);
        //to click on item with item
        rv.setItemAnimator(new DefaultItemAnimator());
        //to take the recycler view in linear form
        /*recyclerView.setLayoutManager(new LinearLayoutManager(this));*/
        //to take the recycler view in grid form
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setSmoothScrollbarEnabled(true);
        rv.setLayoutManager(llm);

        FirebaseRecyclerAdapter<CircleJoin,MyHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CircleJoin, MyHolder>
                (CircleJoin.class,R.layout.card_layout,MyHolder.class,mref) {
            @Override
            protected void populateViewHolder(final MyHolder viewHolder, CircleJoin model, int position) {
                String circlememberid = model.getCirclenumberid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(circlememberid);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = (String) dataSnapshot.child("name").getValue();
                        String imageurl = (String) dataSnapshot.child("imageurl").getValue();
                        if(imageurl!=null && name!=null)
                        {
                            viewHolder.username.setText(name);
                            Picasso.get().load(imageurl).placeholder(R.drawable.tom).into(viewHolder.profpic);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        rv.setAdapter(firebaseRecyclerAdapter);

    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView username;
        CircleImageView profpic;
        ImageView status;


        public MyHolder(@NonNull View itemView) {
            //to typecast the widgets
            super(itemView);
            mView = itemView;
            username = mView.findViewById(R.id.item_title);
            profpic = mView.findViewById(R.id.item_img);
            status = mView.findViewById(R.id.item_state);
        }
    }

}
