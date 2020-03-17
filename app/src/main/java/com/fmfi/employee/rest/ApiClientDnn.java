package com.fmfi.employee.rest;


import com.fmfi.employee.model.InternetConnectionListener;
import com.fmfi.employee.setting.Global;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientDnn
{
    private static InternetConnectionListener mInternetConnectionListener;

    public static void setmInternetConnectionListener(InternetConnectionListener mInternetConnectionListener) {
        mInternetConnectionListener = mInternetConnectionListener;
    }

    private static Retrofit retrofit = null;
    public static Retrofit getClientDnn(InternetConnectionListener listener)
    {
        if(listener != null)
            mInternetConnectionListener = listener;
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()

                //.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request.Builder builder = originalRequest.newBuilder().header("Content-Type",
                                "application/json");
                        //.addHeader("Content-Language", Global.UserUILanguage)
                        //        .addHeader("License", "TevoiMobileApp");

                        Request newRequest = builder.build();
                        okhttp3.Response response = chain.proceed(newRequest);

                        // todo deal with the issues the way you need to
                        if (response.code() == 500)
                        {
                            /*startActivity(
                                    new Intent(
                                            ErrorHandlingActivity.this,
                                            ServerIsBrokenActivity.class
                                    )
                            );*/

                            return response;
                        }

                        return response;

                        //return chain.proceed(newRequest);
                    }
                })
                /*.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        okhttp3.Response response = chain.proceed(request);

                        // todo deal with the issues the way you need to
                        if (response.code() == 500) {
                            startActivity(
                                    new Intent(
                                            ErrorHandlingActivity.this,
                                            ServerIsBrokenActivity.class
                                    )
                            );

                            return response;
                        }

                        return response;
                    }
                })*/
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        /*OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();*/
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Global.ServerURL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

