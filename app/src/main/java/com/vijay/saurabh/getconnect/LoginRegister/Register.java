package com.vijay.saurabh.getconnect.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.vijay.saurabh.getconnect.R;

public class Register extends AppCompatActivity {

    EditText et_email ;
    Button Signup ;
    FirebaseAuth auth ;
    ProgressDialog pd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_email = findViewById(R.id.et_email);
        Signup = findViewById(R.id.btn_signup) ;
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this) ;
    }

    public void gotopasswordActivity(View v)
    {
        pd.setMessage("Check For Email");
        pd.show();
        auth.fetchProvidersForEmail(et_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if (task.isSuccessful())
                {
                    pd.dismiss();
                    boolean check = !task.getResult().getProviders().isEmpty();
                    if(!check)
                    {
                        Intent intent = new Intent(Register.this , PasswordActivity.class);
                        intent.putExtra("Email" , et_email.getText().toString());
                        startActivity(intent);
                        finish();

                        //email doesnot exist
                    }
                    else
                    {
                        Toast.makeText(Register.this, "This email is already exsit", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
