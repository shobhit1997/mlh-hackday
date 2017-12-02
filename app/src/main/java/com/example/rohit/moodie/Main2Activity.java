package com.example.rohit.moodie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    MediaPlayer mediaPlayer;
    Button play,pause;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent i=getIntent();
        bitmap = (Bitmap) i.getParcelableExtra("bitmap");
        imageView=(ImageView)findViewById(R.id.imageView1);
        imageView.setImageBitmap(bitmap);
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyBgSS7OTsuE4yr9MjRlP8tMvX9QC2Vcc1c"));

        vision = visionBuilder.build();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mediaPlayer.isPlaying())
                {
                    mediaPlayer.start();
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }


            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        mediaPlayer.release();
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

//        for(int i=0; i<numberOfFaces; i++) {
            if(numberOfFaces>0) {
                Log.i("Joy", faces.get(0).getJoyLikelihood());
                Log.i("Anger", faces.get(0).getAngerLikelihood());
                Log.i("Surprise", faces.get(0).getSurpriseLikelihood());
                Log.i("Sorrow", faces.get(0).getSorrowLikelihood());
                String sorrow=faces.get(0).getSorrowLikelihood();
                String joy=faces.get(0).getJoyLikelihood();
                String anger=faces.get(0).getAngerLikelihood();
                String surprise=faces.get(0).getSurpriseLikelihood();
                if(sorrow.equals("VERY_LIKELY"))
                {
                    playSong(0);
                }
                else if(joy.equals("VERY_LIKELY"))
                {
                    playSong(1);
                }
                else if(anger.equals("VERY_LIKELY"))
                {
                    playSong(2);
                }
                else if(surprise.equals("VERY_LIKELY"))
                {
                    playSong(3);
                }
                else if(sorrow.equals("POSSIBLE"))
                {
                    playSong(0);
                }
                else if(joy.equals("POSSIBLE"))
                {
                    playSong(1);
                }
                else if(anger.equals("POSSIBLE"))
                {
                    playSong(2);
                }
                else if(surprise.equals("POSSIBLE"))
                {
                    playSong(3);
                }
                else
                {
                    playSong(0);
                }


            }



//        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "message", Toast.LENGTH_LONG).show();
            }
        });


    }

    public void playSong(int no)
    {
        int songs[]=new int[4];
        songs[0]=R.raw.sad;
        songs[1]=R.raw.happy;
        songs[2]=R.raw.anger;
        songs[3]=R.raw.surprise;
        ArrayList<String> quotes=new ArrayList<>();
        quotes.add("Any fool can be happy. It takes a man with real heart to make beauty out of the stuff that makes us weep.");
        quotes.add("Think of all the beauty still left around you and be happy.");
        quotes.add("Holding on to anger is like grasping a hot coal with the intent of throwing it at someone else; you are the one who gets burned.");
        quotes.add("The secret to humor is surprise.");

        TextView tx=(TextView)findViewById(R.id.editText);
        tx.setText(quotes.get(no));

        mediaPlayer=MediaPlayer.create(this,songs[no]);
        mediaPlayer.start();
    }
}
