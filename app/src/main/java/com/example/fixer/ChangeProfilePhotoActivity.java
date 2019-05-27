package com.example.fixer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeProfilePhotoActivity extends AppCompatActivity {

    private static final int RC_PIC_CODE = 10;
    Intent intent;
    Button btnOK , btnCancel;
    ImageView imgCamera;
    ImageView imgGallery;
    CircleImageView imgInfo;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameraprofile_activity);

        intent =getIntent();

        btnOK= (Button) findViewById(R.id.btn_ok);
        btnCancel= (Button) findViewById(R.id.btn_cancel);
        imgCamera = (ImageView) findViewById(R.id.img_camera);
        imgGallery = (ImageView) findViewById(R.id.img_gallery);
        imgInfo = (CircleImageView) findViewById(R.id.img_info);



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toHome();
            }
        });
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toHome();
            }
        });


    }



    private void toHome(){
        imgInfo.buildDrawingCache();
        Bitmap bitmap = imgInfo.getDrawingCache(); // your bitmap
        ByteArrayOutputStream _bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, _bs);
        intent.putExtra("byteArray", _bs.toByteArray());

        setResult(2,intent);
        finish();

    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,100);
    }

    private void capture() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePhotoIntent,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == RESULT_OK)
            switch (requestCode){
                case 100:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String

                    imgInfo.setImageURI(selectedImage);
                    Log.e("img to string::", String.valueOf(imgInfo));
                    break;

                case 200:


                    Bitmap bp = (Bitmap) data.getExtras().get("data");
                    imgInfo.setImageBitmap(bp);

                    break;

            }


    }







}
