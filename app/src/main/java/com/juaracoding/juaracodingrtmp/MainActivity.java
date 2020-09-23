package com.juaracoding.juaracodingrtmp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.takusemba.rtmppublisher.Publisher;
import com.takusemba.rtmppublisher.PublisherListener;

public class MainActivity extends AppCompatActivity implements PublisherListener {
    private Publisher publisher ;
    private GLSurfaceView glSurfaceView;
    private RelativeLayout container;
    private Button publishButton;
    private ImageView cameraButton ;
    private TextView label;

    private String url = "rtmp://192.168.43.206:1935/live";
    private Handler handler ;
    private Thread thread;
    private boolean isCounting = false;

    boolean authorized = false;

    private static final int REQUEST_STREAM = 1;
    private static String[] PERMISSIONS_STREAM = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyPermissions();

        glSurfaceView = findViewById(R.id.surface_view);
        container = findViewById(R.id.container);
        publishButton = findViewById(R.id.toggle_publish);
        cameraButton = findViewById(R.id.toggle_camera);
        label = findViewById(R.id.live_label);



            publisher = new  Publisher.Builder(MainActivity.this)
                    .setGlView(glSurfaceView)
                    .setUrl(url)
                    .setSize(Publisher.Builder.DEFAULT_WIDTH, Publisher.Builder.DEFAULT_HEIGHT)

                    .setAudioBitrate(Publisher.Builder.DEFAULT_AUDIO_BITRATE)
                    //    .setVideoBitrate(Publisher.Builder.DEFAULT_VIDEO_BITRATE)
                    .setVideoBitrate(Publisher.Builder.DEFAULT_VIDEO_BITRATE *20)
                    .setCameraMode(Publisher.Builder.DEFAULT_MODE)
                    .setListener(this)
                    .build();


            publishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (publisher.isPublishing()) {
                        publisher.stopPublishing();
                    } else {
                        publisher.startPublishing();
                    }
                }
            });

           cameraButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   publisher.switchCamera();
               }
           });



    }

    long startedAt;
    long updatedAt;
    private void startCounting() {



        label.setVisibility( View.VISIBLE);





    }

    private void stopCounting() {

        label.setVisibility( View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateControl();
    }

    @Override
    public void onStarted() {

        Toast.makeText(MainActivity.this,R.string.started_publishing,Toast.LENGTH_SHORT).show();
        updateControl();
        startCounting();
    }

    @Override
    public void onStopped() {
        Toast.makeText(MainActivity.this,R.string.stopped_publishing,Toast.LENGTH_SHORT).show();
        updateControl();
        stopCounting();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(MainActivity.this,R.string.disconnected_publishing,Toast.LENGTH_SHORT).show();
        updateControl();
        stopCounting();
    }

    @Override
    public void onFailedToConnect() {
        Toast.makeText(MainActivity.this,R.string.failed_publishing,Toast.LENGTH_SHORT).show();
        updateControl();
        stopCounting();
    }

    public void verifyPermissions() {
        int CAMERA_permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        int RECORD_AUDIO_permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);
        int WRITE_EXTERNAL_STORAGE_permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (CAMERA_permission != PackageManager.PERMISSION_GRANTED ||
                RECORD_AUDIO_permission != PackageManager.PERMISSION_GRANTED ||
                WRITE_EXTERNAL_STORAGE_permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STREAM,
                    REQUEST_STREAM
            );
            authorized = false;
        } else {
            authorized = true;
        }
    }

    public void updateControl(){
        if (publisher.isPublishing()){
            publishButton.setText( R.string.stop_publishing);
        }else{
            publishButton.setText(R.string.start_publishing);
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STREAM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                authorized = true;
            }
        }
    }
}