package com.example.murasa.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user clicks the start button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, CameraDisplay.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString(); //"No rear camera. Please get a device with one"; //editText.getText().toString();

        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
