package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ImageViewActivity extends AppCompatActivity {
    ImageView imageView,btnDownload;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.imageView);
        btnDownload = findViewById(R.id.btnDownload);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_FULLSCREEN);


        final String url = getIntent().getStringExtra("url");
        final String name = getIntent().getStringExtra("imageusername");
        Picasso.get().load(url).into(imageView);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage(url,name);
            }
        });
    }

    private void DownloadImage(String url, String name) {
        Toast.makeText(this, "Download Started", Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse(url);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(this,DIRECTORY_DOWNLOADS,name+".jpg");
        downloadManager.enqueue(request);
    }
}