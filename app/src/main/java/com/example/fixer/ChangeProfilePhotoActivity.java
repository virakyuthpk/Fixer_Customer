package com.example.fixer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fixer.model.CustomerModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangeProfilePhotoActivity extends AppCompatActivity {

    private static final int RC_PIC_CODE = 10;

    Button btnOK , btnCancel;
    ImageView imgCamera;
    ImageView imgGallery;
    CircleImageView imgInfo;

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String UPLOAD_URL = LoginActivity.getRoot()+"/api/customer/changeprofilephoto";
    Bitmap bitmaprofile;

    File f;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameraprofile_activity);



        btnOK= findViewById(R.id.btn_ok);
        btnCancel= findViewById(R.id.btn_cancel);
        imgCamera = findViewById(R.id.img_camera);
        imgGallery = findViewById(R.id.img_gallery);
        imgInfo = findViewById(R.id.img_info);



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmaprofile=null;
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

                toHomeWithPhoto();

            }
        });


    }


    private void toHome(){


        finish();

    }




    private void toHomeWithPhoto(){

        Intent intent = getIntent();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmaprofile.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();

        intent.putExtra("image",bitmaprofile);

        setResult(5,intent);
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
                    Uri imageUri = data.getData();


                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
//
//
                    try {
                        bitmaprofile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        imgInfo.setImageBitmap(bitmaprofile);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;

                case 200:


                    bitmaprofile = (Bitmap) data.getExtras().get("data");
                    imgInfo.setImageBitmap(bitmaprofile);


                    break;

            }


    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

//    private void uploadImage() {
//        //Showing the progress dialog
//        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST,UPLOAD_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        //Disimissing the progress dialog
//                        loading.dismiss();
//                        //Showing toast message of the response
//                        Toast.makeText(ChangeProfilePhotoActivity.this, KEY_IMAGE , Toast.LENGTH_LONG).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Dismissing the progress dialog
//                        loading.dismiss();
//
//                        //Showing toast
//                        Toast.makeText(ChangeProfilePhotoActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
//                    }
//                }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                //Converting Bitmap to String
//                String image = getStringImage(bitmaprofile);
//
//
//                //Getting Image Name
//                String name = "chanthu";
//
//                //Creating parameters
//                Map<String,String> params = new Hashtable<String, String>();
//
//                //Adding parameters
//                params.put(KEY_IMAGE, image);
//                params.put(KEY_NAME, name);
//
//                //returning parameters
//                return params;
//            }
//        };
//
//        //Creating a Request Queue
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        //Adding request to the queue
//        requestQueue.add(stringRequest);
//
////        stringRequest.setRetryPolicy(new RetryPolicy() {
////            @Override
////            public int getCurrentTimeout() {
////                return 50000;
////            }
////
////            @Override
////            public int getCurrentRetryCount() {
////                return 50000;
////            }
////
////            @Override
////            public void retry(VolleyError error) throws VolleyError {
////
////            }
////        });
//
//
//        }
//
//
//    //    Convert from bitmap picture
//    public void convertBitToBite(Bitmap bp) throws IOException {
//        f = new File(getCacheDir(),"imageToUpload");
//        f.createNewFile();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bp.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
//        byte[] bitmapData=byteArrayOutputStream.toByteArray();
//
//        FileOutputStream fileOutputStream = new FileOutputStream(f);
//        fileOutputStream.write(bitmapData);
//        fileOutputStream.flush();
//        fileOutputStream.close();
//
//    }
//
//    public void changeProfile() throws IOException {
//        convertBitToBite(bitmaprofile);
//
//        MultipartBody.Part body=null;
//
//        RequestBody methodPart = RequestBody.create(MultipartBody.FORM, "PATCH");
//
//        if (f != null){
//            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
//            body = MultipartBody.Part.createFormData("image","fileAndroid", reqFile);
//        }
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(UPLOAD_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//
//        ModelApi modelApi = retrofit.create(ModelApi.class);
//
//        Call<CustomerModel> call = modelApi.updateProfilePicture(body,methodPart);
//
//        call.enqueue(new Callback<CustomerModel>() {
//
//            @Override
//            public void onResponse(Call<CustomerModel> call, retrofit2.Response<CustomerModel> response) {
//                if (!response.isSuccessful()){
//                    Log.w("Upload Profile:: ",response.code()+""+response.message());
//                    return;
//                }
//                CustomerModel userResponce = response.body();
//
//                Log.w("Upload Profile:: ","Successfully "+response);
//                Toast.makeText(getApplicationContext(),"Update Profile successfully",Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onFailure(Call<CustomerModel> call, Throwable t) {
//                Log.w("Upload Profile fail::","Fail "+t.getMessage());
//            }
//        });
//    }





}
