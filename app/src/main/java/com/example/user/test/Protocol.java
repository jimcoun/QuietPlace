package com.example.user.test;

import android.os.Handler;
import android.util.Log;
import android.content.Context;

import org.quietmodem.Quiet.FrameReceiver;
import org.quietmodem.Quiet.FrameReceiverConfig;
import org.quietmodem.Quiet.FrameTransmitter;
import org.quietmodem.Quiet.FrameTransmitterConfig;
import org.quietmodem.Quiet.ModemException;
import org.quietmodem.Quiet.NetworkInterface;
import org.quietmodem.Quiet.NetworkInterfaceConfig;

import java.io.InputStream;
import java.net.SocketException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public abstract class Protocol {

    private FrameTransmitterConfig transmitterConfig;
    private FrameReceiverConfig receiverConfig;

    private FrameTransmitter transmitter;

    protected Subscription frameSubscription = Subscriptions.empty();
    protected ParseJSON parser = new ParseJSON(); // Instantiate JSON parser

    protected String identity; // User's identity
    protected String committedIdentity; // Committed identity
    protected String commitmentSeed; // Random number for commitment generation
    protected PrivateKey privateKey; // User's private key
    protected PublicKey publicKey; // User's public key
    protected String caPublicKey; // Certificate Authority's public key

    protected String exactLocation; // Accurate location in plus code format
    protected String K0; // Random number used to build location chain
    protected String Kn; // Head of the location chain

    protected final int RETRY_COUNT = 3;
    protected final int TIMEOUT = 10;
    protected List<String> receivedMessages = new ArrayList<>(); // Holds the received messages
    protected List<Long> receivedTimestamps = new ArrayList<>(); // Holds the timestamps of received messages
    protected List<String> witnessesDiscovered = new ArrayList<>();
    protected List<String> witnessesSentLPS = new ArrayList<>();
    protected List<String> witnessesSentList = new ArrayList<>();

    protected Context c;

    // Constructor
    public Protocol(Context c, String exactLocation){

        this.c = c; // Set the android context
        setupTransmitter(c);
        loadConfig(c);
        this.exactLocation = exactLocation;

        commitmentSeed = Crypto.random256();
        committedIdentity = Crypto.commit(identity, commitmentSeed);

    }

    private void setupTransmitter(Context c) {
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


    protected void sendMessage(String message) {
        try {
            transmitter.send(message.getBytes());
        } catch (IOException e) {
            // our message might be too long or the transmit queue full
        }
    }

    protected void sendAndWait(String message, int timeout){
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                frameSubscription.unsubscribe(); //Stop receiver
                if(!receivedMessages.isEmpty()){
                    Log.d("MYPROTO", receivedMessages.get(0));
                }
                else{
                    Log.d("MYPROTO", "Malakies");
                }

            }
        };

        sendMessage(message); // Sends the message
        subscribeToFrames();

        handler.postDelayed(r, timeout*1000);

    }

    private void loadConfig(Context c){

        ParseJSON parser = new ParseJSON();

        // Open the configuration file located in res/raw folder
        InputStream config = c.getResources().openRawResource(
                c.getResources().getIdentifier("protocol_config",
                        "raw", c.getPackageName()));

        // Scanner s = new Scanner(config).useDelimiter("\\A");
        // String configJSON = s.hasNext() ? s.next() : "";

        try{
            byte[] fbytes = new byte[(int) config.available()];
            config.read(fbytes);
            String configJSON = new String(fbytes, "UTF-8");
            Log.d("MYPROTO", configJSON);

            ProtocolConfig pc = parser.fromJSON(configJSON);

            this.identity = pc.getIdentity();
            this.privateKey = Crypto.getPrivateFromString(pc.getPrivateKey());
            this.publicKey = Crypto.getPublicFromString(pc.getPublicKey());
            this.caPublicKey = pc.getCaPublicKey();
        }
        catch (IOException e){}
        catch (Exception e) {}
    }

    protected void subscribeToFrames() {
        // frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            Long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            receivedMessages.add(receivedText);
            receivedTimestamps.add(time);
        }, error -> {
            receivedMessages.add("error");
            receivedTimestamps.add(new Long(0));
        });
    }

    // Runs the corresponding protocol
    public abstract void runProtocol();

    protected String getSignHash(String data, String signature){

        String hashedSignature = Crypto.sha256Base64(signature);
        SignHash sh = new SignHash();
        sh.setData(data);
        sh.setHs(hashedSignature);

        return parser.toJSON(sh);
    }

    protected String fromSignHash(String data){
        try {
            SignHash signHash = (SignHash) parser.fromJSON(data, SignHash.class);
            return signHash.getData();
        }
        catch(Exception e)
        {
            return "Error";
        }
    }
}
