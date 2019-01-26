package com.example.user.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.quietmodem.Quiet.FrameTransmitter;
import org.quietmodem.Quiet.FrameTransmitterConfig;
import org.quietmodem.Quiet.ModemException;

import java.io.IOException;

public class RequestProof extends AppCompatActivity {

    private String plusCode;
    private FrameTransmitter transmitter; // Transmitter object



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
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() { // Find the transmit button
            @Override
            public void onClick(View v) {  // What happens when you click the button
                handleSendClick();
            }
        });

    }

    private void setupTransmitter() {
        FrameTransmitterConfig transmitterConfig;
        try {
            transmitterConfig = new FrameTransmitterConfig(
                    this, "ultrasonic");
            transmitter = new FrameTransmitter(transmitterConfig);
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

    private void handleSendClick() {
        if (transmitter == null) {
            setupTransmitter();
        }
        sendMessage();
    }



}
