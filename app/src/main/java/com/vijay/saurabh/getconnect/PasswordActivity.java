package com.vijay.saurabh.getconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vijay.saurabh.getconnect.R;

public class PasswordActivity extends AppCompatActivity {
    String email ;
    EditText et_pass ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        et_pass = findViewById(R.id.et_password2);
        Intent intent = getIntent();
        if(intent != null) {
            email = intent.getStringExtra("Email");

        }


    }
    public void gotonamepickActivity(View v)
    {
        if(et_pass.getText().toString().length() >= 6)
        {
            Intent intent = new Intent(PasswordActivity.this , NameActivity.class) ;
            intent.putExtra("email" , email);
            intent.putExtra("password" , et_pass.getText().toString());
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, "Password Length should be of more character", Toast.LENGTH_SHORT).show();
        }


    }



}
