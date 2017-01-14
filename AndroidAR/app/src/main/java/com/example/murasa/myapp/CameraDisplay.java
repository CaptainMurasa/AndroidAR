package com.example.murasa.myapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

//import static android.content.Context.CAMERA_SERVICE;

public class CameraDisplay extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final String TAG = "ARCam";

    // Initialize Program
    private PackageManager packM;
    private Context context;
    private CameraCharacteristics camChars;
    private Boolean rear;
    private CameraManager camMan;
    private TextureView previewView;
    private CameraDevice cam;
    private CameraCaptureSession session;
    private String cameraId;
    private Surface previewSurface;
    private Size previewSize;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) // target anything with Lolipop
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rear = false;
        context = this;
        packM = context.getPackageManager();
        camMan = (CameraManager) getSystemService(CAMERA_SERVICE);
        previewView = new TextureView(this);
        cameraId = null;
        Integer mainCam;

        // Camera Check
        /*@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) // for Android 4.0 // is this possible?
        if(packM.hasSystemFeature(packM.FEATURE_CAMERA) == false)
        {
            Toast.makeText(this, "This device doesn't have a rear camera.",
                    Toast.LENGTH_SHORT).show();
            return; // end program
        } */ //depreciated, keep here in case if we want to expand to pre-5.0

        try {
            for (String Id : camMan.getCameraIdList())
            {
                camChars = camMan.getCameraCharacteristics(Id);
                mainCam = camChars.get(CameraCharacteristics.LENS_FACING); // see where the camera is facing

                if (mainCam != null && mainCam == CameraCharacteristics.LENS_FACING_BACK) {
                    Toast.makeText(context, "This is a rear facing camera!", Toast.LENGTH_SHORT).show();
                    cameraId = Id;
                    rear = true; // we seriously just need one!
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (!rear) {
            Toast.makeText(context, "No rear facing camera. :( Need a camera.... Quitting.", Toast.LENGTH_SHORT).show();
            System.exit(1);
        } else
            Toast.makeText(context, "I have a rear facing camera! Carry on!", Toast.LENGTH_SHORT).show();

        //bluetooth check
        if (packM.hasSystemFeature(packM.FEATURE_BLUETOOTH) == false) {
            Toast.makeText(this, "This app requires bluetooth to run. D: Gonna quit real shortly. D:",
                    Toast.LENGTH_SHORT).show();
            System.exit(1); // end program
        } else
            Toast.makeText(context, "I have Bluetooth! Next!", Toast.LENGTH_SHORT).show(); // do we want to test for Bluetooth LE?

        // make surface
        setContentView(previewView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        previewView.setSurfaceTextureListener(this); // wait to create surface

        /*Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);*/

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            initCamera(surfaceTexture);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to open camera :(", e);
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1)
    {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture)
    {
        if(cam != null)
        {
            cam.close();
            cam = null;
        }
        session = null;
        return true;
    }

    // initialize the camera
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initCamera(SurfaceTexture surface) throws CameraAccessException
    {
        if(!rear) {
            throw new CameraAccessException(CameraAccessException.CAMERA_ERROR, "Couldn't find suitable camera");
        }

        StreamConfigurationMap streamConfigs = camChars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        //find preview size that matches the aspect ratio of the camera sensor
        Size[] previewSizes = streamConfigs.getOutputSizes(SurfaceTexture.class);
        Size rawSize = streamConfigs.getOutputSizes(ImageFormat.RAW_SENSOR)[0];
        previewSize = findOptimalPreviewSize(previewSizes, rawSize);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CameraDisplay Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /**
     * Given a target size for raw output, search available preview sizes for one with a similar
     * aspect ratio that does not exceed screen size.
     */
    private Size findOptimalPreviewSize(Size[] sizes, Size targetSize) {
        float targetRatio = targetSize.getWidth() * 1.0f / targetSize.getHeight();
        float tolerance = 0.1f;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int maxPixels = screenWidth * Math.round(screenWidth * targetRatio);
        int width, height;
        float ratio;
        for (Size size : sizes) {
            width = size.getWidth();
            height = size.getHeight();
            if (width * height <= maxPixels) {
                ratio = ((float) width) / height;
                if (Math.abs(ratio - targetRatio) < tolerance) {
                    return size;
                }
            }
        }
        return null;
    }

}