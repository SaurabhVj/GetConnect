package com.vijay.saurabh.getconnect.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vijay.saurabh.getconnect.OfficialCircle.CreateUsers;
import com.vijay.saurabh.getconnect.MyNavigationActivity;
import com.vijay.saurabh.getconnect.R;

public class InviteCodeActivity extends AppCompatActivity {
    TextView tv ;
    String name , password , email , code , isSharing , date  , userId;
    Uri imageuri ;
    Button btn_next ;
    FirebaseAuth auth ;
    FirebaseUser user ;

    DatabaseReference databaseReference ;
    StorageReference storageReference ;
    ProgressDialog pd ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);
        tv = findViewById(R.id.tv_code);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("User_images");
        pd = new ProgressDialog(this) ;
        Intent i = getIntent();
        if(i != null)
        {
            name = i.getStringExtra("name");
            email = i.getStringExtra("email");
            password = i.getStringExtra("password");
            code = i.getStringExtra("code");
            isSharing = i.getStringExtra("isSharing");
            date = i.getStringExtra("date");
            imageuri = i.getParcelableExtra("imageuri");
        }
        tv.setText(code);


    }

    public void registeruser(View v)
    {
        pd.setMessage("Please Wait while We are Creating your account");
        pd.show();

        auth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    user = auth.getCurrentUser() ;
                    CreateUsers createUsers = new CreateUsers(name , password , code , "false" , "na" , "na" , "na" , user.getUid());
                    user = auth.getCurrentUser();
                    userId = user.getUid();

                    databaseReference.child(userId).setValue(createUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                final StorageReference sr = storageReference.child(user.getUid()+".jpg");
                                sr.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        sr.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if(task.isSuccessful())
                                                {
                                                    String image_download_path =task.getResult().toString();


                                                    databaseReference.child(user.getUid())
                                                            .child("imageurl").setValue(image_download_path).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                pd.dismiss();
                                                                Toast.makeText(InviteCodeActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                                                                Intent i = new Intent(InviteCodeActivity.this , MyNavigationActivity.class);
                                                                startActivity(i);
                                                                finish();


                                                            }
                                                            else
                                                            {
                                                                pd.dismiss();
                                                                Toast.makeText(InviteCodeActivity.this, "an error occured", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                                }
                                                            });

                                                }

                                            }
                                        });



                                    }


                                });
                            }
                        }



                    });
                }
            }

        });
    }
}



