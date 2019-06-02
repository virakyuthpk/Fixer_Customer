package com.example.fixer;

import com.example.fixer.model.CustomerModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ModelApi {

    @Multipart

    @POST("api/customer/changeprofilephoto")
    Call<CustomerModel> updateProfilePicture(
                                    @Part
                                    MultipartBody.Part image,
                                    @Part ("_method")RequestBody method);
}
