package com.example.user.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inaka.galgo.Galgo;
import com.inaka.galgo.GalgoOptions;

import org.quietmodem.Quiet.FrameReceiver;
import org.quietmodem.Quiet.FrameReceiverConfig;
import org.quietmodem.Quiet.FrameTransmitter;
import org.quietmodem.Quiet.FrameTransmitterConfig;
import org.quietmodem.Quiet.ModemException;
import org.w3c.dom.Text;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class RequestProof extends AppCompatActivity {

    private String plusCode;
    private FrameTransmitter transmitter; // Transmitter object
    private FrameReceiver receiver;
    private Subscription frameSubscription = Subscriptions.empty();
    private TextView textView; // The plus code
    private TextView receivedText; // The message received
    private TextView receivedStatus; // The status of the reception

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private Protocol protocolInstance;

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_proof);

        checkPermission();
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        plusCode = extras.getString("EXTRA_PLUS_CODE");

        textView = findViewById(R.id.plusCodeText);
        receivedText = findViewById(R.id.receivedText);
        receivedStatus = findViewById(R.id.receivedStatus);

        textView.setText(plusCode);


        // proverProtocol = new ProverProtocol(this, plusCode); // Instantiate prover protocol
//        setupTransmitter();
//        setupReceiver();
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() { // Find the transmit button
            @Override
            public void onClick(View v) {  // What happens when you click the button
                sendMessage();
            }
        });

        findViewById(R.id.receiveButton).setOnClickListener(new View.OnClickListener() { // Find the receive button
            @Override
            public void onClick(View v) {  // What happens when you click the button
                handleReceiveClick();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     // subscribeToFrames();
                } else {
                    showMissingAudioPermissionToast();
                    finish();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic
            }

        }

    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    /*
    private void setupTransmitter() {
        FrameTransmitterConfig transmitterConfig;
        try {
            transmitterConfig = new FrameTransmitterConfig(
                    this, "ultrasonic-experimental");
            transmitter = new FrameTransmitter(transmitterConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ModemException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupReceiver() {
        if (hasRecordAudioPersmission()) {
            subscribeToFrames();
        } else {
            requestPermission();
        }
    }

    private void subscribeToFrames() {
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(this, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            receivedText.setText(new String(buf, Charset.forName("UTF-8")));
            Long time = System.currentTimeMillis() / 1000;
            String timestamp = time.toString();
            receivedStatus.setText("Received " + buf.length + " @" + timestamp);
        }, error -> {
            receivedStatus.setText("error " + error.toString());
        });
    }
    */

    private void sendMessage(){

        protocolInstance = new ProverProtocol(this, plusCode);
        protocolInstance.runProtocol();
    }


    /*
    private void handleSendClick() {
        if (transmitter == null) {
            setupTransmitter();
        }
        sendMessage();
    }
    */
    private void handleReceiveClick() {
        protocolInstance = new WitnessProtocol(this, plusCode);
        protocolInstance.runProtocol();
    }


    // Record audio permission methods
    boolean hasRecordAudioPersmission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
    }

    private void showMissingAudioPermissionToast() {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, R.string.missing_audio_permission, duration);
        toast.show();
    }


}
