package com.example.user.test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.quietmodem.Quiet.FrameReceiver;
import org.quietmodem.Quiet.FrameReceiverConfig;
import org.quietmodem.Quiet.FrameTransmitter;
import org.quietmodem.Quiet.FrameTransmitterConfig;
import org.quietmodem.Quiet.ModemException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class RequestProof extends AppCompatActivity {

    private String plusCode;
    private FrameTransmitter transmitter; // Transmitter object
    private FrameReceiver receiver;

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_proof);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        plusCode = extras.getString("EXTRA_PLUS_CODE");

        TextView textView = findViewById(R.id.plusCodeText);
        textView.setText(plusCode);

        setupTransmitter();
        setupReceiver();
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() { // Find the transmit button
            @Override
            public void onClick(View v) {  // What happens when you click the button
                handleSendClick();
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

    private void setupTransmitter() {
        FrameTransmitterConfig transmitterConfig;
        try {
            transmitterConfig = new FrameTransmitterConfig(
                    this, "audible");
            transmitter = new FrameTransmitter(transmitterConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ModemException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupReceiver() {
        FrameReceiverConfig receiverConfig;
        try {
            receiverConfig = new FrameReceiverConfig(
                    this, "audible");
            receiver = new FrameReceiver(receiverConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ModemException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage() {
        try {
            transmitter.send(plusCode.getBytes());
        } catch (IOException e) {
            // our message might be too long or the transmit queue full
        }
    }

    private void receiveMessage() {
        receiver.setBlocking(1, 0);

        byte[] buf = new byte[1024];
        long recvLen = 0;
        try {
            recvLen = receiver.receive(buf);
        } catch (IOException e) {
            // read timed out
        }


        try {
            String receivedString = new String(buf, "UTF-8");
            TextView receivedText = findViewById(R.id.receivedText);
            receivedText.setText(receivedString);
        }
        catch(UnsupportedEncodingException e){  }
    }

    private void handleSendClick() {
        if (transmitter == null) {
            setupTransmitter();
        }
        sendMessage();
    }

    private void handleReceiveClick() {
        receiveMessage();
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
