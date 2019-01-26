package com.vijay.saurabh.getconnect.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.vijay.saurabh.getconnect.R;

public class Register extends AppCompatActivity {

    EditText et_email ;
    EditText et_pass ;
    EditText et_confirm ;
    Button Signup ;
    TextView tv_hvac ;
    FirebaseAuth auth ;
    ProgressDialog pd ;
    String email ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_email = findViewById(R.id.et_emailreg);
        et_pass = findViewById(R.id.et_passwdreg);
        et_confirm = findViewById(R.id.cnf_passwd);
        Signup = findViewById(R.id.btn_signupreg);
        tv_hvac = findViewById(R.id.hv_ac) ;


        Signup = findViewById(R.id.btn_signupreg) ;
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this) ;
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                                if(et_pass.getText().toString().length() >= 6)
                                {
                                    if(et_pass.getText().toString().equals(et_confirm.getText().toString())) {
                                        email = et_email.getText().toString();
                                        Intent intent = new Intent(Register.this, NameActivity.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("password", et_pass.getText().toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(Register.this, "Password dint match", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Password Length should be of more character", Toast.LENGTH_SHORT).show();
                                }

                        /*Intent intent = new Intent(Register.this , PasswordActivity.class);
                        intent.putExtra("Email" , et_email.getText().toString());
                        startActivity(intent);
                        finish();*/

                                //email doesnot exist
                            }
                            else

                            {
                                pd.dismiss();
                                Toast.makeText(Register.this, "This email is already exsit", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

            }
        });
        tv_hvac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this , LoginActivity.class) ;
                startActivity(i) ;
                finish();
            }
        });
    }


}
