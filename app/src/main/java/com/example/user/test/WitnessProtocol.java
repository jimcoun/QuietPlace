package com.example.user.test;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import static com.example.user.test.LocationChain.checkChain;

public class WitnessProtocol extends Protocol {

    private boolean received;
    private boolean receivedHash;
    private String proverIdentity;
    private Handler handler = new Handler();
    private long timeChallengeSent;
    private String challengeSignature;
    private int n1, n2;

    private Lps lps;
    private ListMessage list;

    public WitnessProtocol(android.content.Context c, String exactLocation){
        super(c, exactLocation);

    }

    @Override
    public void runProtocol() {

        Log.d("MYPROTO", "Witness protocol initiated.");
        lps = new Lps(); // Initiate LPS message
        lps.setId(identity); // Set the identity in the LPS
        lps.setRw(commitmentSeed); // Set the commitment seed in the LPS
        proverIdentity = "";
        proverRequestListener(); // Start listening for Prover Requests

        Runnable r = new Runnable() {
            public void run() {
                if(proverIdentity.equals("")){
                    Log.d("MYPROTO", "No PR received");
                    frameSubscription.unsubscribe();
                    frameSubscription = Subscriptions.empty();
                }

            }
        };
        handler.postDelayed(r, 10*1000);


    }

    // Listens for Prover Requests
    private void proverRequestListener(){
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            try{
                ProverRequest pr = (ProverRequest) parser.fromJSON(receivedText, ProverRequest.class);
                // Perform checks
                long timeDifference = time - pr.getTimestamp();
                if (exactLocation.equals(pr.getLocation()) && timeDifference < 5){
                    proverIdentity = pr.getCommitment();
                    K0 = pr.getk0();
                    Log.d("MYPROTO", "Received valid PR from: " + proverIdentity);
                    sendWitnessPresence();
                }
                else{
                    Log.d("MYPROTO", "Not a valid PR. Wrong location or delayed answer.");
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.d("MYPROTOEXC", "Not a ProverRequest");
            }
            receivedMessages.add(receivedText);
            receivedTimestamps.add(time);
        }, error -> {
            receivedMessages.add("error");
            receivedTimestamps.add(new Long(0));
        });
    }

    private void sendWitnessPresence(){
        // Start listening for WP messages to find neighboring witnesses
        witnessPresenceListener();
        // Prepare and send WitnessPresence
        WitnessPresence wp = new WitnessPresence();
        wp.setCommitment(committedIdentity);
        wp.setNonce(12345);

        Log.d("MYPROTO", "Sending WP: " + parser.toJSON(wp));
        sendMessage(parser.toJSON(wp));

        // Wait for possible witnesses to send their WP
        Runnable r = new Runnable() {
            public void run() {
                frameSubscription.unsubscribe();
                frameSubscription = Subscriptions.empty();
                Log.d("MYPROTO", "Arrived here");
                waitForStart();
            }
        };
        handler.postDelayed(r, 6*1000);
    }

    private void witnessPresenceListener() {
        frameSubscription.unsubscribe();
        frameSubscription = Subscriptions.empty();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            WitnessPresence wp;
            try {
                wp = (WitnessPresence) parser.fromJSON(receivedText, WitnessPresence.class);
                String witnessIdentity = wp.getCommitment();
                Log.d("MYPROTOWIT", witnessIdentity);
                witnessesDiscovered.add(wp.getCommitment());
            }
            catch(Exception e){e.printStackTrace();}
            receivedMessages.add(receivedText);
            receivedTimestamps.add(time);
        }, error -> {
            receivedMessages.add("error");
            receivedTimestamps.add(new Long(0));
        });
    }

    private void waitForStart(){
        received = false; // We haven't received Start yet
        startListener();
        Runnable r = new Runnable() {
            public void run() {
                if(!received) {
                    frameSubscription.unsubscribe();
                    frameSubscription = Subscriptions.empty();
                    Log.d("MYPROTO", "Not valid Start received!");
                }
            }
        };
        handler.postDelayed(r, 10*1000);
    }

    // Listens for Start message and if received sends Challenge
    private void startListener(){
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            try{
                String start = fromSignHash(receivedText);
                Start st = (Start) parser.fromJSON(start, Start.class);
                Kn = st.getLocationChain();
                long timeDifference = time - st.getTimestamp();
                boolean chainValid = LocationChain.checkChain(K0, exactLocation, Kn);
                if (proverIdentity.equals(st.getCommitment()) && timeDifference < 10 && chainValid) {
                    Log.d("MYPROTO", "Received start message");
                    received = true;
                    lps.setStHash(Crypto.sha256Base64(receivedText)); // Save the hash of the Start message
                    frameSubscription.unsubscribe();
                    frameSubscription = Subscriptions.empty();
                    sendChallenge();
                    }
                else{
                    Log.d("MYPROTO","Not a valid Start message!");
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.d("MYPROTOEXC", "Not a start message");
            }
            receivedMessages.add(receivedText);
            receivedTimestamps.add(time);
        }, error -> {
            receivedMessages.add("error");
            receivedTimestamps.add(new Long(0));
        });
    }

    private void sendChallenge(){
        SecureRandom secureRandom = new SecureRandom();
        n1 = secureRandom.nextInt(1000000000);
        n2 = secureRandom.nextInt(1000);
        received = false; // No response received yet
        responseListener(); // Start listening for responses
        Challenge ch = new Challenge();
        ch.setCommitment(committedIdentity);
        ch.setN1(n1);
        ch.setN2(n2);
        timeChallengeSent = System.currentTimeMillis() / 1000;
        String challenge = parser.toJSON(ch);
        challengeSignature = Crypto.signBase64(challenge, privateKey);

        String challengeHs = getSignHash(challenge, challengeSignature);
        lps.setChal(challengeHs); // Set the Challenge in the LPS
        lps.setChSign(challengeSignature); // Set the challenge signature in the LPS

        Log.d("MYPROTO", "Sending Challenge: " + challengeHs);
        sendMessage(challengeHs);

        Runnable r = new Runnable() {
            public void run() {
                if(!received) {
                    frameSubscription.unsubscribe();
                    frameSubscription = Subscriptions.empty();
                    Log.d("MYPROTO", "No valid response received");
                }
            }
        };
        handler.postDelayed(r, 10*1000);
    }

    // Listens for Start message and if received sends Challenge
    private void responseListener(){
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            try{
                String response = fromSignHash(receivedText);
                Response re = (Response) parser.fromJSON(response, Response.class);
                // Perform checks
                long timeDifference = time - timeChallengeSent;
                // Check if the reponse comes from the right Prover
                if (proverIdentity.equals(re.getCommitment())) {
                    lps.setRes(receivedText); // Set the Response in the LPS
                    // Check if the response is correct
                    if ((timeDifference < 10) && (n1 == re.getN1()) && (n2 == re.getN2())) {
                        received = true;
                        lps.setAcc(true); // Add accepted to the LPS
                        Log.d("MYPROTO", "Response is OK");
                        waitForHash();

                    }
                    else{
                        received = true;
                        Log.d("MYPROTO", "Prover sent Response but it is wrong!");
                        lps.setAcc(false); // Add not accepted to the LPS
                        waitForHash();
                    }
                }
                else{
                    Log.d("MYPROTO","Not a response from the expected Prover!");
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.d("MYPROTOEXC", "Not a response message");
            }
            receivedMessages.add(receivedText);
            receivedTimestamps.add(time);
        }, error -> {
            receivedMessages.add("error");
            receivedTimestamps.add(new Long(0));
        });
    }

    private void waitForHash(){
        receivedHash = false;
        hashListener();

        Runnable r = new Runnable() {
            public void run() {
                if(!receivedHash) {
                    frameSubscription.unsubscribe();
                    frameSubscription = Subscriptions.empty();
                    Log.d("MYPROTO", "No valid VideoHash received");
                }
            }
        };
        handler.postDelayed(r, 10*1000);
    }

    private void hashListener(){
        Log.d("MYPROTO","Waiting for video hash");
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            try{
                String videoHash = fromSignHash(receivedText);
                VideoHash vh = (VideoHash) parser.fromJSON(videoHash, VideoHash.class);
                // Perform checks
                if (proverIdentity.equals(vh.getCommitment())) {
                    receivedHash = true;
                    Log.d("MYPROTO", "Received video hash");
                    lps.setvH(receivedText); // Set the videoHash in the LPS
                    frameSubscription.unsubscribe();
                    frameSubscription = Subscriptions.empty();
                    Log.d("MYPROTO", "LPS is: " + parser.toJSON(lps));
                    sendLPS();
                }
                else{
                    Log.d("MYPROTO","Not a valid VideoHash message!");
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.d("MYPROTOEXC", "Not a VideoHash message");
                Log.d("MYPROTOEXC", e.toString());
            }
            receivedMessages.add(receivedText);
            receivedTimestamps.add(time);
        }, error -> {
            receivedMessages.add("error");
            receivedTimestamps.add(new Long(0));
        });
    }

    private void sendLPS(){

        lpsListener();
        String encryptedLps = getEncryptedMessage(parser.toJSON(lps), caPublicKey, 0, false);
        Log.d("MYPROTO", "LPS: " + parser.toJSON(lps));
        Log.d("MYPROTO", "Sending encrypted LPS: " + encryptedLps);
        Log.d("MYPROTO", "LPS size: " + Integer.toString(encryptedLps.getBytes().length));
        sendMessage(encryptedLps);

        Runnable r = new Runnable() {
            public void run() {
                sendList();
            }
        };
        handler.postDelayed(r, 17*1000);

    }

    private void lpsListener(){
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            Log.d("MYPROTOSPECIAL", Integer.toString(receivedText.getBytes().length));
            long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            try{
                EncryptedMessage encLps = (EncryptedMessage) parser.fromJSON(receivedText, EncryptedMessage.class);
                // Perform checks
                if (encLps.getType() == 0) {
                    witnessesSentLPS.add(encLps.getCommitment());
                    Log.d("MYPROTO", "Got LPS from: " + encLps.getCommitment());
                    }
                else{
                    Log.d("MYPROTO","Not a valid LPS message!");
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.d("MYPROTOEXC", "Not an LPS message");
            }
            receivedMessages.add(receivedText);
            receivedTimestamps.add(time);
        }, error -> {
            receivedMessages.add("error");
            receivedTimestamps.add(new Long(0));
        });
    }

    private void sendList(){
        ListMessage list = new ListMessage();
        list.setDiscovered(witnessesDiscovered);
        list.setSentLps(witnessesSentLPS);

        String encList = getEncryptedMessage(parser.toJSON(list), caPublicKey, 1, true);

        Log.d("MYPROTO", "Sending LIST message: " + encList);
        sendMessage(encList);
        Log.d("MYPROTO", "End of witness protocol.");

        frameSubscription.unsubscribe();
        frameSubscription = Subscriptions.empty();
    }
}
