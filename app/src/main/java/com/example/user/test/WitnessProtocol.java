package com.example.user.test;

import android.os.Handler;
import android.util.Log;

import java.nio.charset.Charset;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import static com.example.user.test.LocationChain.checkChain;

public class WitnessProtocol extends Protocol {

    private boolean received;
    private boolean receivedHash;
    private String proverIdentity;
    private Handler handler = new Handler();
    private int N1, N2;
    private long timeChallengeSent;
    private String challengeSignature;

    private Lps lps;

    public WitnessProtocol(android.content.Context c, String exactLocation){
        super(c, exactLocation);

    }

    @Override
    public void runProtocol() {

        Log.d("MYPROTO", "Witness protocol initiated.");
        lps = new Lps(); // Initiate LPS message
        proverIdentity = "";
        N1 = 123;
        N2 = 456;
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
                    Log.d("MYPROTO", "Not a valid PR");
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
        received = false; // No response received yet
        responseListener(); // Start listening for responses
        Challenge ch = new Challenge();
        ch.setCommitment(committedIdentity);
        ch.setN1(N1);
        ch.setN2(N2);
        timeChallengeSent = System.currentTimeMillis() / 1000;
        String challenge = parser.toJSON(ch);
        challengeSignature = Crypto.signBase64(challenge, privateKey);

        String challengeHs = getSignHash(challenge, challengeSignature);
        lps.setChal(challengeHs); // Set the Challenge in the LPS

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
                if (proverIdentity.equals(re.getCommitment()) && (timeDifference < 15) &&
                        (N1 == re.getN1()) && (N2 == re.getN2())) {
                    received = true;
                    Log.d("MYPROTO", "Response is OK");
                    waitForHash();
                }
                else{
                    Log.d("MYPROTO","Not a valid Response message!");
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
                    sendLPS();
                }
                else{
                    Log.d("MYPROTO","Not a valid VideoHash message!");
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.d("MYPROTOEXC", "Not a VideoHash message");
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
        EncryptedMessage lps = new EncryptedMessage();
        lps.setCommitment(committedIdentity);
        lps.setType(0);
        lps.setData("Data content of the LPS");
        Log.d("MYPROTO", "Sending LPS: " + parser.toJSON(lps));
        sendMessage(parser.toJSON(lps));

        Runnable r = new Runnable() {
            public void run() {
                sendList();
            }
        };
        handler.postDelayed(r, 10*1000);

    }

    private void lpsListener(){
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            try{
                EncryptedMessage lps = (EncryptedMessage) parser.fromJSON(receivedText, EncryptedMessage.class);
                // Perform checks
                if (lps.getType() == 0) {
                    witnessesSentLPS.add(lps.getCommitment());
                    Log.d("MYPROTO", "Got LPS from: " + lps.getCommitment());
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
        EncryptedMessage list = new EncryptedMessage();
        list.setCommitment(committedIdentity);
        list.setType(1);
        list.setData("Content of LIST message");

        Log.d("MYPROTO", "Sending LIST message: " + parser.toJSON(list));
        sendMessage(parser.toJSON(list));
        Log.d("MYPROTO", "End of witness protocol.");

        frameSubscription.unsubscribe();
        frameSubscription = Subscriptions.empty();
    }
}
