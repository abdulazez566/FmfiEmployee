package com.fmfi.employee.rest;


import com.fmfi.employee.model.IResponse;
import com.fmfi.employee.model.UploadImageRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterfaceDnn {

    @POST("API/MobileImageApi/PostUserImage")
    Call<IResponse> UploadImage(@Body UploadImageRequest model);
}
