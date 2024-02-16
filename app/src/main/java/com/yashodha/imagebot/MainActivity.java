package com.yashodha.imagebot;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.ORB;
import org.opencv.features2d.SIFT;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    private ImageView storedImageView, userImageView;

    private Button submitButton, selectImageButton;
    private TextView similarityTextView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        int imageResource = intent.getIntExtra("foodimage", 1);
        storedImageView = findViewById(R.id.storedImageView);
        userImageView = findViewById(R.id.userImageView);
        submitButton = findViewById(R.id.submitButton);
        similarityTextView = findViewById(R.id.similarityTextView);
        selectImageButton = findViewById(R.id.selectImageButton);

        // Load stored image from drawable
        Bitmap storedBitmap = BitmapFactory.decodeResource(getResources(), imageResource);
        storedImageView.setImageBitmap(storedBitmap);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show progress dialog
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Please wait...");
                progressDialog.setMessage("We are checking your food...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Load user-inserted image from userImageView
                Bitmap userBitmap = ((BitmapDrawable) userImageView.getDrawable()).getBitmap();

                // Perform heavy computation in a background thread
                new ComputeSimilarityTask(progressDialog).execute(storedBitmap, userBitmap);
            }
        });


        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check runtime permission for camera
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    // Permission already granted, proceed to select image from camera or gallery
                    selectImage();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            userImageView.setImageBitmap(imageBitmap);
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                userImageView.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private double computeSimilarity(Bitmap storedBitmap, Bitmap userBitmap) {
        try {
            // Check if OpenCV is initialized
            if (!OpenCVLoader.initDebug()) {
                Log.e("OpenCV", "OpenCV initialization failed.");
                return -1; // Return an error code
            }

            // Check if bitmaps are null
            if (storedBitmap == null || userBitmap == null) {
                Log.e("Bitmap", "Bitmaps are null.");
                return -1; // Return an error code
            }

            // Convert bitmaps to Mats
            Mat storedMat = new Mat();
            Utils.bitmapToMat(storedBitmap, storedMat);
            Mat userMat = new Mat();
            Utils.bitmapToMat(userBitmap, userMat);

            // Convert images to grayscale
            Mat storedGray = new Mat();
            Imgproc.cvtColor(storedMat, storedGray, Imgproc.COLOR_BGR2GRAY);
            Mat userGray = new Mat();
            Imgproc.cvtColor(userMat, userGray, Imgproc.COLOR_BGR2GRAY);

            // Use SIFT for feature detection and description
            SIFT sift = SIFT.create();

            MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
            MatOfKeyPoint keypoints2 = new MatOfKeyPoint();

            sift.detect(storedGray, keypoints1);
            sift.detect(userGray, keypoints2);

            // Compute descriptors
            Mat descriptors1 = new Mat();
            Mat descriptors2 = new Mat();

            sift.compute(storedGray, keypoints1, descriptors1);
            sift.compute(userGray, keypoints2, descriptors2);

            // FLANN based matcher
            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
            MatOfDMatch matches = new MatOfDMatch();
            matcher.match(descriptors1, descriptors2, matches);

            // Filter good matches using Lowe's ratio test
            List<DMatch> goodMatches = new ArrayList<>();
            List<DMatch> matchesList = matches.toList();
            for (DMatch match : matchesList) {
                if (match.distance < 0.75 * matchesList.get(1).distance) {
                    goodMatches.add(match);
                }
            }

            // Perform geometric verification using RANSAC
            Mat mask = new Mat();
            List<Point> points1List = new ArrayList<>();
            List<Point> points2List = new ArrayList<>();
            for (DMatch match : goodMatches) {
                points1List.add(keypoints1.toList().get(match.queryIdx).pt);
                points2List.add(keypoints2.toList().get(match.trainIdx).pt);
            }

            MatOfPoint2f points1 = new MatOfPoint2f();
            points1.fromList(points1List);
            MatOfPoint2f points2 = new MatOfPoint2f();
            points2.fromList(points2List);

            Mat homography = Calib3d.findHomography(points1, points2, Calib3d.RANSAC, 5, mask);

            // Count inliers
            int inliers = Core.countNonZero(mask);
            int totalMatches = goodMatches.size();

            // Compute similarity based on inliers ratio
            double similarity = (double) inliers / totalMatches * 100;

            return similarity;
        } catch (Exception e) {
            Log.e("Exception", "Exception occurred in computeSimilarity: " + e.getMessage());
            e.printStackTrace();
            return -1; // Return an error code
        }
    }

    private void selectImage() {
        // Show dialog to select image source: camera or gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Camera
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                        break;
                    case 1: // Gallery
                        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickImage, PICK_IMAGE_REQUEST);
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to select image from camera or gallery
                selectImage();
            } else {
                Toast.makeText(this, "Camera permission is required to capture images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ComputeSimilarityTask extends AsyncTask<Bitmap, Void, Double> {
        private ProgressDialog progressDialog;

        public ComputeSimilarityTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected Double doInBackground(Bitmap... bitmaps) {
            // Perform heavy computation
            return computeSimilarity(bitmaps[0], bitmaps[1]);
        }

        @Override
        protected void onPostExecute(Double similarity) {
            super.onPostExecute(similarity);

            // Hide progress dialog
            progressDialog.dismiss();
            // Display similarity to user
            int similarityInt = (int) Math.round(similarity);

            boolean s = similarity >= 50;
            if (s) {
                FancyAlertDialog.Builder
                        .with(MainActivity.this)
                        .setTitle("Report")
                        .setBackgroundColor(Color.parseColor("#303F9F"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                        .setMessage("Your food is perfectly designed as advertised. Your food and the image of the food in the ad match "+ similarityInt +" %.")
                        .setPositiveBtnBackground(Color.parseColor("#FF4081"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                        .setPositiveBtnText("OK")
                        .setNegativeBtnText("Cancel")
                        .setAnimation(Animation.POP)
                        .isCancellable(true)
                        .setIcon(R.drawable.smile_icon, View.VISIBLE)
                        .build()
                        .show();

            } else {
                FancyAlertDialog.Builder
                        .with(MainActivity.this)
                        .setTitle("Report")
                        .setBackgroundColor(Color.parseColor("#303F9F"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                        .setMessage("Your food was not prepared as successfully as advertised. The food you received is only "+ similarityInt +" % similar to the food in the ad. '")
                        .setPositiveBtnText("OK")
                        .setNegativeBtnText("Cancel")
                        .setAnimation(Animation.POP)
                        .isCancellable(true)
                        .setIcon(R.drawable.sad_icon, View.VISIBLE)
                        .build()
                        .show();
            }


        }
    }
}
