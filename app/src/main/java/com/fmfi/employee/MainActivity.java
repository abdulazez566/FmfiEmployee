package com.fmfi.employee;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.EditText;
import android.net.Uri;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.net.URL;
import android.provider.MediaStore;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import android.util.Base64;

import com.fmfi.employee.model.IResponse;
import com.fmfi.employee.model.UploadImageRequest;
import com.fmfi.employee.setting.Global;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button CaptureImageFromCamera,UploadImageToServer;

    
    ImageView ImageViewHolder;

    EditText imageName;

    ProgressDialog progressDialog ;

    Intent intent ;

    public  static final int RequestPermissionCode  = 1 ;

    Bitmap bitmap;

    boolean check = true;

    String GetImageNameFromEditText;

    String ImageNameFieldOnServer = "image_name" ;

    String ImagePathFieldOnServer = "image_path" ;

    //String ImageUploadPathOnSever ="https://androidjsonblog.000webhostapp.com/capture_img_upload_to_server.php" ;
    String ImageUploadPathOnSever = "http://192.168.1.4/Tevoi/DesktopModules/TevoiAPIModuleFolder/API/ServicesTest/PostUserImage";
    //String ImageUploadPathOnSever = "http://h2817272.stratoserver.net/FmfiPs/DesktopModules/FmfiPsModuleFolder/API/MobileImageApi/PostUserImage";

    Button scanQr;
    public String ReferenceId;
    private Uri file;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CaptureImageFromCamera = (Button)findViewById(R.id.button);
//        ImageViewHolder = (ImageView)findViewById(R.id.imageView);
        UploadImageToServer = (Button) findViewById(R.id.button2);
        imageName = (EditText)findViewById(R.id.editText);
        scanQr = (Button)findViewById(R.id.scanQr);
        imageName.setEnabled(false);


        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        try
        {
            String referenceId = bundle.getString("ReferenceId");
            imageName.setText(referenceId);
        }
        catch (Exception c)
        {

        }


        EnableRuntimePermissionToAccessCamera();

        CaptureImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);*/

                String referenceNumber = imageName.getText().toString();
                if(referenceNumber.equals(""))
                {
                    Toast.makeText(MainActivity.this, "You need to scan qr code first", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    /*file = FileProvider.getUriForFile(
                            MainActivity.this,
                            "com.fmfi.employee.provider", //(use your app signature + ".provider" )
                            getOutputMediaFile());

                   // file = Uri.fromFile(getOutputMediaFile());
                   intent.putExtra(MediaStore.EXTRA_OUTPUT, file);*/

                    startActivityForResult(intent, 7);
                }


            }
        });

        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScannedBarcodeActivity.class));
            }
        });

        UploadImageToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = imageName.getText().toString();
                //String FinalData = ImageHelper.uploadFile(bitmap, ImageUploadPathOnSever);
                //Toast.makeText(MainActivity.this, FinalData, Toast.LENGTH_SHORT).show();
                /*try
                {
                    URL url = new URL(ImageUploadPathOnSever);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setDoInput(true);
                    c.setRequestMethod("POST");
                    c.setDoOutput(true);
                    //c.connect();
                    OutputStream output = c.getOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
                    output.close();
                    Scanner result = new Scanner(c.getInputStream());
                    String response = result.nextLine();
                    Log.e("ImageUploader", "Error uploading image: " + response);
                    result.close();
                } catch (IOException e) {
                    Log.e("ImageUploader", "Error uploading image", e);
                }*/
                ImageUploadToServerFunction();

            }
        });
    }

// Star activity for result method to Set captured image on image view after click.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Teeest" , "Marwaaaaaaa");
        super.onActivityResult(requestCode, resultCode, data);

        /*try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
            // adding captured image in imageview.
            ImageViewHolder.setImageBitmap(bitmap);
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }*/
        String referenceNumber = imageName.getText().toString();
        if(referenceNumber.equals(""))
        {
            Toast.makeText(this, "You need to scan qr code first", Toast.LENGTH_LONG).show();
        }
        else {


            if (requestCode == 7 && resultCode == RESULT_OK && data != null) {
                if (data.getData() == null) {
                    Bundle d = data.getExtras();
                    bitmap = (Bitmap) d.get("data");
                    //abd
//                    ImageViewHolder.setImageBitmap(bitmap);
                    Toast.makeText(this, "fff", Toast.LENGTH_SHORT).show();


                    ProgressDialog mProgressDialog;
                    mProgressDialog = new ProgressDialog(this);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setMessage("Loading");
                    mProgressDialog.show();


                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    UploadImageRequest request = new UploadImageRequest();
                    request.Id = referenceNumber;
                    request.ImgString = encoded;

                    Call<IResponse> call = Global.clientDnn.UploadImage(request);
                    call.enqueue(new Callback<IResponse>() {
                        @Override
                        public void onResponse(Call<IResponse> call, Response<IResponse> response) {
                            IResponse login = response.body();
                            if (login.getNumber() == 0) {
                                Toast.makeText(MainActivity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            } else {
                                Toast.makeText(getBaseContext(), login.Message, Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<IResponse> call, Throwable t) {

                            mProgressDialog.dismiss();
                        }
                    });

                } else {


                    try {
                        Uri uri = data.getData();

                        // Adding captured image in bitmap.
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                        // adding captured image in imageview.
                        //abd
//                        ImageViewHolder.setImageBitmap(bitmap);

                        //String referenceNumber = imageName.getText().toString();
                        ProgressDialog mProgressDialog;
                        mProgressDialog = new ProgressDialog(this);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setMessage("Loading");
                        mProgressDialog.show();


                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();

                        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        UploadImageRequest request = new UploadImageRequest();
                        request.Id = referenceNumber;
                        request.ImgString = encoded;

                        Call<IResponse> call = Global.clientDnn.UploadImage(request);
                        call.enqueue(new Callback<IResponse>() {
                            @Override
                            public void onResponse(Call<IResponse> call, Response<IResponse> response) {
                                IResponse login = response.body();
                                if (login.getNumber() == 0) {
                                    Toast.makeText(MainActivity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                } else {
                                    Toast.makeText(getBaseContext(), login.Message, Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<IResponse> call, Throwable t) {

                                mProgressDialog.dismiss();
                            }
                        });

                        String h = "";
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

            }
        }

    }

    // Requesting runtime permission to access camera.
    public void EnableRuntimePermissionToAccessCamera(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            CaptureImageFromCamera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        /*if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(MainActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }*/
    }

    // Upload captured image online on server function.
    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        // Converting bitmap image to jpeg format, so by default image will upload in jpeg format.
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                // Showing progress dialog at image upload time.
                progressDialog = ProgressDialog.show(MainActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(MainActivity.this,string1,Toast.LENGTH_LONG).show();

                // Setting image as transparent after done uploading.
                //abd
//                ImageViewHolder.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params)
            {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageNameFieldOnServer, GetImageNameFromEditText);

                HashMapParams.put(ImagePathFieldOnServer, ConvertImage);

                //String FinalData = ImageHelper.uploadFile(bitmap, ImageUploadPathOnSever);

                String FinalData = imageProcessClass.ImageHttpRequest(ImageUploadPathOnSever, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "***";
                String attachmentName = "file";
                String attachmentFileName = "bitmap.bmp";
                String crlf = "\r\n";

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");
                /*httpURLConnectionObject.setRequestProperty(
                        "Content-Type", "application/x-www-form-urlencoded");*/
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);

                // region
                DataOutputStream request = new DataOutputStream(
                        httpURLConnectionObject.getOutputStream());
                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" +
                        attachmentFileName + "\"" + lineEnd);
                request.writeBytes("Content-Type: image/png" + lineEnd);
                request.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                request.writeBytes(lineEnd);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] pixels = bos.toByteArray();
                request.write(pixels);
                request.writeBytes(lineEnd);
                request.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                InputStream responseStream = new
                        BufferedInputStream(httpURLConnectionObject.getInputStream());
                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));
                String line = "";
                StringBuilder stringBuilder2 = new StringBuilder();
                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();
                String response = stringBuilder2.toString();
                if(!response.contains("File")) {
                    String imageURL = response.substring(0, response.length() - 1);
                    return imageURL;
                }
                request.flush();
                request.close();
                responseStream.close();
                httpURLConnectionObject.disconnect();

                // endregion




                /*OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();*/

                /*RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }*/

            } catch (Exception e) {

                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }

    @Override
    //public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                CaptureImageFromCamera.setEnabled(true);
            }
        }

        /*switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }*/
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

}