package com.vijay.saurabh.getconnect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;
import com.vijay.saurabh.getconnect.LoginRegister.LoginActivity;
import com.vijay.saurabh.getconnect.LoginRegister.Register;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth ;
    FirebaseUser user ;

    PermissionManager manager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        manager = new PermissionManager() {};
        manager.checkAndRequestPermissions(this);

        user = auth.getCurrentUser();
        if(user==null) {
            setContentView(R.layout.activity_main);
        }
        else
        {
            Intent i = new Intent(MainActivity.this , MyNavigationActivity.class);
            startActivity(i);
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.checkResult(requestCode , permissions , grantResults);
        ArrayList<String> denied_permissions = manager.getStatus().get(0).denied ;

        if(denied_permissions.isEmpty())
        {
            Toast.makeText(this, "Permission Enabled", Toast.LENGTH_SHORT).show();
        }
    }

    public void gotologin(View v)
    {
        Intent i = new Intent(MainActivity.this , LoginActivity.class);
        startActivity(i);
        finish();
    }
    public void gotoregister(View v)
    {
        Intent i = new Intent(MainActivity.this , Register.class);
        startActivity(i);
        finish();
    }

}
