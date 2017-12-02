package com.example.rohit.moodie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    Vision vision;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent i=getIntent();
        bitmap = (Bitmap) i.getParcelableExtra("bitmap");
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyBgSS7OTsuE4yr9MjRlP8tMvX9QC2Vcc1c"));

        vision = visionBuilder.build();


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Convert photo to byte array

                // More code here
                try {
                    detectFaces(getImageEncodeImage(bitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }
    public void detectFaces(Image img) throws Exception, IOException {

        Feature feature = new Feature();
        feature.setType("FACE_DETECTION");

        AnnotateImageRequest request = new AnnotateImageRequest();
        request.setImage(img);
        request.setFeatures(Arrays.asList(feature));

        BatchAnnotateImagesRequest batchRequest =
                new BatchAnnotateImagesRequest();

        batchRequest.setRequests(Arrays.asList(request));
        BatchAnnotateImagesResponse batchResponse =vision.images().annotate(batchRequest).execute();

        List<FaceAnnotation> faces = batchResponse.getResponses()
                .get(0).getFaceAnnotations();



        int numberOfFaces = faces.size();

        for(int i=0; i<numberOfFaces; i++) {

            Log.i("Joy",faces.get(i).getJoyLikelihood());
            Log.i("Anger",faces.get(i).getAngerLikelihood());
            Log.i("Surprise",faces.get(i).getSurpriseLikelihood());
            Log.i("Sorrow",faces.get(i).getSorrowLikelihood());

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "message", Toast.LENGTH_LONG).show();
            }
        });


    }
}
