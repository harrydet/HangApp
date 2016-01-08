package com.harrykristi.hangapp.helpers;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.harrykristi.hangapp.ProfileFragment;
import com.harrykristi.hangapp.events.GeneralInfoEvent;
import com.squareup.otto.Bus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class UploadFileToServer extends AsyncTask<Void, Integer, String> {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private Uri filePathURI;
    private boolean isGallery;

    private String filePath = null;
    private String objectId = null;
    private long totalSize = 0;
    private Context context;

    private Bus mBus;

    public UploadFileToServer(Context context, Uri filePath, String objectId, boolean isGallery) {
        this.filePath = filePath.getPath();
        this.filePathURI = filePath;
        this.objectId = objectId;
        this.context = context;
        this.isGallery = isGallery;

        mBus = BusProvider.getInstance();

    }

    @Override
    protected void onPreExecute() {
        // setting progress bar to zero
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // Making progress bar visible
        //progressBar.setVisibility(View.VISIBLE);

        // updating progress bar value
        //progressBar.setProgress(progress[0]);

        // updating percentage value
        //txtPercentage.setText(String.valueOf(progress[0]) + "%");
    }

    @Override
    protected String doInBackground(Void... params) {
        return uploadFile();
    }

    @SuppressWarnings("deprecation")
    private String uploadFile() {
        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            File sourceFile = new File(filePath);

            FileBody fb = new FileBody(sourceFile);

            if(isGallery){
                try {
                    String pathsegment[] = filePathURI.getLastPathSegment().split(":");
                    String id = pathsegment[1];
                    final String[] imageColumns = { MediaStore.Images.Media.DATA };
                    final String imageOrderBy = null;

                    Uri uri = getUri();
                    Cursor imageCursor = context.getContentResolver().query(uri, imageColumns,
                            MediaStore.Images.Media._ID + "=" + id, null, null);

                    if (imageCursor.moveToFirst()) {

                        sourceFile = new File(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));

                        fb = new FileBody(sourceFile);
                    }

                } catch (Exception e) {
                    Toast.makeText(context, "Failed to get image", Toast.LENGTH_LONG).show();
                }
            }

            // Adding file data to http body
            entity.addPart("image", fb);

            entity.addPart("email", new StringBody("abc@gmail.com"));

            entity.addPart("object_id", new StringBody(objectId));

            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                //Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return responseString;

    }

    @Override
    protected void onPostExecute(String result) {
        Log.e(TAG, "Response from server: " + result);

        // showing the server response in an alert dialog
        //showAlert(result);

        mBus.post(new GeneralInfoEvent(false, "Image uploaded successfully"));

        super.onPostExecute(result);
    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

}