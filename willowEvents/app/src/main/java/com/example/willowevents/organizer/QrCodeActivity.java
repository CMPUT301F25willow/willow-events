package com.example.willowevents.organizer;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Activity that:
 *  - Receives event info via Intent extras
 *  - Generates a QR code that links directly to the poster image URL
 *  - Displays the QR in qr_code.xml's ImageView
 *  - Lets the user download the QR to the gallery
 */
public class QrCodeActivity extends AppCompatActivity {

    // Keys used by EventOrganizerInfoView when starting this Activity
    public static final String EXTRA_EVENT_NAME = "event_name";
    public static final String EXTRA_EVENT_DESCRIPTION = "event_description";
    public static final String EXTRA_EVENT_POSTER_URL = "event_poster_url";

    private Bitmap qrBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code);

        // Layout views from qr_code.xml
        ImageView qrImage = findViewById(R.id.imageView);
        Button downloadButton = findViewById(R.id.poster_heading3); // Download button
        Button closeButton = findViewById(R.id.poster_heading2);    // Close Button

        // get and sanitize intents

        // Raw values from Intent
        String rawName = getIntent().getStringExtra(EXTRA_EVENT_NAME);
        String rawDesc = getIntent().getStringExtra(EXTRA_EVENT_DESCRIPTION);
        String rawPoster = getIntent().getStringExtra(EXTRA_EVENT_POSTER_URL);

        // Final values (must not change so lambdas can capture them)
        final String name = (rawName == null || rawName.trim().isEmpty()) ? "Event" : rawName;
        final String desc = (rawDesc == null) ? "" : rawDesc;
        final String posterUrl = (rawPoster == null) ? "" : rawPoster;

        if (posterUrl.isEmpty()) {
            Toast.makeText(this, "No poster URL found for this event", Toast.LENGTH_LONG).show();
            //No poster = No QR code needed
            finish();
            return;
        }

        // Link QR to poster url
        // e.g. https://firebasestorage.googleapis.com/...
        final String content = posterUrl;

        // Generate QR bitmap
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            // 800x800 is a clear size; ImageView will scale it down
            qrBitmap = encoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 800, 800);
            qrImage.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // ==== Download button ====
        downloadButton.setOnClickListener(v -> {
            if (qrBitmap == null) {
                Toast.makeText(this, "No QR code to save", Toast.LENGTH_SHORT).show();
                return;
            }
            saveQrToGallery(qrBitmap, name);
        });

        // ==== Close button just finishes this screen ====
        closeButton.setOnClickListener(v -> finish());
    }

    /**
     * Saves the QR bitmap in the device's Pictures/WillowEvents folder using MediaStore.
     */
    private void saveQrToGallery(Bitmap bitmap, String baseName) {
        if (bitmap == null) {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();
            return;
        }

        if (baseName == null || baseName.trim().isEmpty()) {
            baseName = "event";
        }

        String fileName = baseName.replaceAll("\\s+", "_") + "_qr.png";

        // describe the new image file we want to create
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WillowEvents");

        //ask MediaStore for a place to write the file
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            return;
        }

        //write the bitmap data into that file
        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            if (out == null) {
                Toast.makeText(this, "Failed to open output stream", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            if (success) {
                Toast.makeText(this, "QR code saved to gallery", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error while saving QR code", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "IO error while saving QR code", Toast.LENGTH_SHORT).show();
        }
    }
}


