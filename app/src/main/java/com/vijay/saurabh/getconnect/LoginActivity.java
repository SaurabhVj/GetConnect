package com.vijay.saurabh.getconnect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vijay.saurabh.getconnect.R;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth ;
    EditText et_name , et_password ;
    Button btn_login ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        et_name  = findViewById(R.id.et_name) ;
        et_password = findViewById(R.id.editText2);



    }
    public  void login(View v)
    {
        auth.signInWithEmailAndPassword(et_name.getText().toString() , et_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "User Successfully logged in", Toast.LENGTH_SHORT).show();
                    Intent i  = new Intent(LoginActivity.this , MyNavigationActivity.class);
                    startActivity(i);
                    finish();

                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Wrong Email Address or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
