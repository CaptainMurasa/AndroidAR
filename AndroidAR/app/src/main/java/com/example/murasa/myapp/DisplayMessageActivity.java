package com.example.murasa.myapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

//import static android.content.Context.CAMERA_SERVICE;

public class DisplayMessageActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) // target anything with Lolipop
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Context context = this;

        // Initialize Program
        PackageManager packM = context.getPackageManager();
        CameraCharacteristics chars;
        Boolean rear = false;

        // Camera Check
        /*@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) // for Android 4.0 // is this possible?
        if(packM.hasSystemFeature(packM.FEATURE_CAMERA) == false)
        {
            Toast.makeText(this, "This device doesn't have a rear camera.",
                    Toast.LENGTH_SHORT).show();
            return; // end program
        } */ //depreciated, keep here in case if we want to expand to pre-5.0

        CameraManager camMan = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            for (String cameraId : camMan.getCameraIdList()) {
                Integer mainCam;
                chars = camMan.getCameraCharacteristics(cameraId);
                mainCam = chars.get(CameraCharacteristics.LENS_FACING); // see where the camera is facing

                if (mainCam != null && mainCam == CameraCharacteristics.LENS_FACING_BACK) {
                    Toast.makeText(context, "This is a rear facing camera!", Toast.LENGTH_SHORT).show();
                    rear = true; // we seriously just need one!
                } else
                    Toast.makeText(context, "This is not a rear facing camera!", Toast.LENGTH_SHORT).show();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (!rear) {
            Toast.makeText(context, "No rear facing camera. :( Need a camera.... Quitting.", Toast.LENGTH_SHORT).show();
            System.exit(1);
        }
        else
            Toast.makeText(context, "I have a rear facing camera! Carry on!", Toast.LENGTH_SHORT).show();

        //bluetooth check
        if(packM.hasSystemFeature(packM.FEATURE_BLUETOOTH) == false)
        {
            Toast.makeText(this, "This app requires bluetooth to run. D: Gonna quit real shortly. D:",
                    Toast.LENGTH_SHORT).show();
            System.exit(1); // end program
        }
        else
            Toast.makeText(context, "I have Bluetooth! Next!", Toast.LENGTH_SHORT).show(); // do we want to test for Bluetooth LE?

        // start live camera
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);

    }
}