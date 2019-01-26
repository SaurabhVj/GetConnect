package com.vijay.saurabh.getconnect.LoginRegister;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vijay.saurabh.getconnect.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class NameActivity extends AppCompatActivity {

    String email , password ;
    EditText et_name ;
    CircleImageView pp ;
    Button btn_name ;
    Uri resulturi ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        Intent intent = getIntent();
        if(intent != null) {
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
        }
        et_name = findViewById(R.id.et_emailupdate);
        btn_name = findViewById(R.id.btn_updateprofile) ;
        pp = findViewById(R.id.profile_pic);

    }

    public void generatecode(View v)
    {
        Date sysdate = new Date() ;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a" , Locale.getDefault());
        String date = format1.format(sysdate) ;
        Random r = new Random() ;
        int n = 100000 + r.nextInt(900000) ;
        String code = String.valueOf(n) ;
        if(resulturi != null)
        {
            Intent intent = new Intent(NameActivity.this , InviteCodeActivity.class);
            intent.putExtra("name" , et_name.getText().toString());
            intent.putExtra("email" , email);
            intent.putExtra("password",password) ;
            intent.putExtra("date" , date);
            intent.putExtra("code",code);
            intent.putExtra("isSharing" , "false");
            intent.putExtra("imageuri",resulturi) ;
            startActivity(intent);
            finish();


        }
        else
        {
            Toast.makeText(getApplicationContext(), "PLEASE CHOOSE IMAGE", Toast.LENGTH_SHORT).show();

        }


    }

    public void selectImage(View v)
    {
        Intent  i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i , 12);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==12 && resultCode==RESULT_OK && data != null)
        {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1 , 1).start(this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resulturi = result.getUri();
                // if(resulturi != null)
                pp.setImageURI(resulturi);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
