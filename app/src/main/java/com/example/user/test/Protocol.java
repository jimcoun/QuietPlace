package com.example.user.test;

import org.quietmodem.Quiet.FrameReceiver;
import org.quietmodem.Quiet.FrameReceiverConfig;
import org.quietmodem.Quiet.FrameTransmitter;
import org.quietmodem.Quiet.FrameTransmitterConfig;
import org.quietmodem.Quiet.ModemException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Protocol {

    private FrameTransmitter transmitter;
    private FrameReceiver receiver;

    private String myID; // User's identity
    private String privateKey; // User's private key
    private String publicKey; // User's public key
    private String CAkey; // Certificate Authority's public key

    private String exactLocation; // Accurate location in plus code format

    private List<String> witnessesDiscovered = new ArrayList<>();
    private List<String> witnessesSentLPS = new ArrayList<>();

    // Constructor
    public Protocol(android.content.Context c, String exactLocation, String protocolConfig){
        this.exactLocation = exactLocation;

        setupTransmitter(c);
        setupReceiver(c);

    }

    private void setupTransmitter(android.content.Context c) {
        FrameTransmitterConfig transmitterConfig;
        try {
            transmitterConfig = new FrameTransmitterConfig(
                    c, "ultrasonic-experimental");
            transmitter = new FrameTransmitter(transmitterConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ModemException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupReceiver(android.content.Context c) {
        FrameReceiverConfig receiverConfig;
        try {
            receiverConfig = new FrameReceiverConfig(
                    c, "ultrasonic-experimental");
            receiver = new FrameReceiver(receiverConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ModemException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendMessage(String message) {
        try {
            transmitter.send(message.getBytes());
        } catch (IOException e) {
            // our message might be too long or the transmit queue full
        }
    }

    public abstract void init(); // Prepares needed values for execution

    public abstract void run(); // Runs the corresponding protocol


}
