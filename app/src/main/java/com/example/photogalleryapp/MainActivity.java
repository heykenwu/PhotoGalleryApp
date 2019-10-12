package com.example.photogalleryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.photogalleryapp.Manager.CameraManager;
import com.example.photogalleryapp.Manager.PhotoDisplayManager;
import com.example.photogalleryapp.Utils.Photo;


public class MainActivity extends AppCompatActivity {
    private PhotoDisplayManager photoDisplayManager;
    private CameraManager cameraManager;

    private ImageView image_photoDisplay;
    private TextView text_timeStamp;
    private EditText text_caption;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide the title and title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        //show the activity in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // camera manager
        cameraManager = new CameraManager(this);

        // photo display manager
        photoDisplayManager = PhotoDisplayManager.getInstance();
        photoDisplayManager.readFromFolder("/Android/data/com.example.photogalleryapp/files/Pictures");

        // Main image display
        image_photoDisplay = findViewById(R.id.image_photoDisplay);
        text_timeStamp = findViewById(R.id.text_timeStamp);
        text_caption = findViewById(R.id.text_caption);
        Photo temp;
        if ((temp = photoDisplayManager.getNextPhoto()) != null) {
            updateDisplay(temp);
        }

        // Search button
        ImageButton button_search = findViewById(R.id.button_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        // Snap button
        ImageButton button_snap = findViewById(R.id.button_snap);
        button_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.dispatchTakePictureIntent();
            }
        });

        // Left button
        ImageButton button_left = findViewById(R.id.button_left);
        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo temp;
                if ((temp = photoDisplayManager.getPrevPhoto()) != null) {
                    updateDisplay(temp);
                }

            }
        });

        // Right button
        ImageButton button_right = findViewById(R.id.button_right);
        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo temp;
                if ((temp = photoDisplayManager.getNextPhoto()) != null) {
                    updateDisplay(temp);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Display photo upon picture taken
        if (requestCode == CameraManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Photo temp;
            if ((temp = cameraManager.getLastTakenPicture()) != null) {
                photoDisplayManager.addToList(temp.getName(), temp.getDate());
                updateDisplay(temp);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Photo temp;
        // need to update upon return by clicking items in gallery activity
        if ((temp = photoDisplayManager.getCurrentPhoto()) != null) {
            updateDisplay(temp);
        }
    }

    // update display
    private void updateDisplay(Photo photo) {
        image_photoDisplay.setImageBitmap(photo.getThumbnail());
        text_caption.setText(photo.getName());
        text_timeStamp.setText(android.text.format.DateFormat
                .format("yyyy-MM-dd", photo.getDate()));
    }
}
