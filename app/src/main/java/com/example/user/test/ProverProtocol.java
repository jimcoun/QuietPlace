package com.example.user.test;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;


import com.inaka.galgo.Galgo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class ProverProtocol extends Protocol {

    private String[] Kchain;
    private Handler handler = new Handler();
    private List<String> receivedChallenges = new ArrayList<>();
    private long timeStarted;
    private boolean receivedAll = false;
    private boolean receivedAllLPS = false;

    // Things to keep during execution
    private String start; // The start message
    private List<String> lpsReceived = new ArrayList<>(); // All LPSs received
    private List<String> listReceived = new ArrayList<>(); // All LISTs received

    // Signatures generated during protocol execution
    private String startSignature;
    private List<String> responseSignatures = new ArrayList<>(); // Signatures of responses
    private String videoHashSignature;

    private final TextView numberToSpeak = (TextView) ((Activity)c).findViewById(R.id.numberToSpeak);

    public ProverProtocol(Context c, String exactLocation){

        super(c, exactLocation);

        K0 = LocationChain.random();
        Kchain = LocationChain.buildChain(K0, exactLocation);
        Kn = Kchain[5]; // The head of the chain

    }


    @Override
    public void runProtocol() {

        Log.d("MYPROTO", "Prover protocol initiated.");
        ProverRequest pr = new ProverRequest();
        pr.setCommitment(committedIdentity);
        pr.setLocation(exactLocation);
        pr.setk0(K0);
        pr.setCa(caId);
        pr.setTimestamp(System.currentTimeMillis() / 1000);

        String proverRequest = parser.toJSON(pr);
        sendMessage(proverRequest);
        Log.d("MYPROTOS", "Sending PR: " + proverRequest);
        answerListener(); // Start listening for WP messages

        Runnable r = new Runnable() {
            public void run() {
                frameSubscription.unsubscribe(); //Stop receiver
                frameSubscription = Subscriptions.empty();
                if(!witnessesDiscovered.isEmpty()){ // If we have received something

                    // Compose and send Start message
                    long currentTime = System.currentTimeMillis() / 1000;
                    Start st = new Start();
                    st.setCommitment(committedIdentity);
                    st.setLocationChain(Kn);
                    st.setTimestamp(currentTime);
                    st.setFrameHash("SAMPLEHASH");
                    start = parser.toJSON(st);
                    // Sign the start message
                    startSignature = Crypto.signBase64(start, privateKey);

                    String startHs = getSignHash(start, startSignature);
                    sendMessage(startHs); //Send start message
                    Log.d("MYPROTOS", "Sending S: " + startHs);
                    crPhase(); // Continue to challenge-response phase


                }
                else{ // No answer received
                    Log.d("MYPROTOR", "No answer");
                }

            }
        };
        handler.postDelayed(r, 10*1000);

    }

    // Listens for any answer and adds it to receivedMessages list
    private void answerListener() {
        frameSubscription.unsubscribe();
        frameSubscription = Subscriptions.empty();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            Long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            WitnessPresence wp;
            try {
                wp = (WitnessPresence) parser.fromJSON(receivedText, WitnessPresence.class);
                Log.d("MYPROTOWIT", "Discovered witness:" + wp.getCommitment());
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

    // Challenge-Response phase
    private void crPhase(){
        Log.d("MYPROTO", "entered c-r phase");
        receivedAll = false;
        timeStarted = System.currentTimeMillis() / 1000;
        challengeListener();

        Runnable r = new Runnable() {
            public void run() {
                // After timeout check for no answer
                if (receivedAll == false){
                    Log.d("MYPROTO", "No Challenge received");
                    frameSubscription.unsubscribe();
                }
                getLPSPhase();

            }
        };
        handler.postDelayed(r, 13*1000);

    }

    // Listens for Challenges and immediately answers with Response
    private void challengeListener(){
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            Long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTOCHALLENGE", receivedText);
            try{
                String challenge = fromSignHash(receivedText); // Get the challenge from signed-hashed message
                Challenge ch = (Challenge) parser.fromJSON(challenge, Challenge.class);
                // Build and send response
                if (!ch.getCommitment().equals(committedIdentity)) { // If i didn't send this message
                    Response re = new Response();
                    re.setCommitment(committedIdentity);
                    re.setN1(ch.getN1());
                    re.setN2(ch.getN2());
                    String response = parser.toJSON(re);
                    String resSignature = Crypto.signBase64(response, privateKey);
                    responseSignatures.add(resSignature);

                    String responseHs = getSignHash(response, resSignature);
                    sendMessage(responseHs);
                    numberToSpeak.setText(Integer.toString(ch.getN2()));
                    Log.d("MYPROTOS", "Sending Response: " + responseHs);
                    receivedChallenges.add(ch.getCommitment());
                    if(receivedChallenges.equals(witnessesDiscovered)){
                        receivedAll = true;
                        frameSubscription.unsubscribe();
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.d("MYPROTOEXC", "maybe my response");
            }
            receivedMessages.add(receivedText);
            receivedTimestamps.add(time);
        }, error -> {
            receivedMessages.add("error");
            receivedTimestamps.add(new Long(0));
        });
    }

    // Send the video hash and wait for LPS/LISTs
    private void getLPSPhase(){
        receivedAllLPS = false;
        Log.d("MYPROTO", "Sending video hash");
        VideoHash vh = new VideoHash();
        vh.setCommitment(committedIdentity);
        vh.setHash("VIDEOHASH");
        String videoHash = parser.toJSON(vh);
        videoHashSignature = Crypto.signBase64(videoHash, privateKey);

        String videoHashHs = getSignHash(videoHash, videoHashSignature);
        sendMessage(videoHashHs);
        Log.d("MYPROTOS", videoHashHs);

        Log.d("MYPROTO", "Start listening for LPS");
        lpsListListener(); // Start listening for LPSs and LISTs

        Runnable r = new Runnable() {
            public void run() {
                // After timeout check for no answer
                if (receivedAllLPS == false){
                    Log.d("MYPROTO", "Did not receive all expected LPS");
                    frameSubscription.unsubscribe();
                }

            }
        };
        handler.postDelayed(r, 30*1000);
    }

    private void lpsListListener() {
        frameSubscription.unsubscribe();
        frameSubscription = Subscriptions.empty();
        frameSubscription = FrameReceiverObservable.create(c, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            String receivedText = new String(buf, Charset.forName("UTF-8"));
            Long time = System.currentTimeMillis() / 1000;
            Log.d("MYPROTO", receivedText);
            EncryptedMessage em;
            try {
                em = (EncryptedMessage) parser.fromJSON(receivedText, EncryptedMessage.class);
                Log.d("MYPROTOWIT", em.getCommitment());
                if(em.getType() == 0) {
                    witnessesSentLPS.add(em.getCommitment());
                    lpsReceived.add(receivedText);
                }
                else if(em.getType() == 1){
                    witnessesSentList.add(em.getCommitment());
                    listReceived.add(receivedText);
                }
            }
            catch(Exception e){e.printStackTrace();}
            receivedMessages.add(receivedText);
            receivedTimestamps.add(time);
            if(witnessesSentLPS.equals(witnessesDiscovered) && witnessesSentList.equals(witnessesDiscovered)){
                receivedAllLPS = true;
                frameSubscription.unsubscribe();
                createLpa();
            }
        }, error -> {
            receivedMessages.add("error");
            receivedTimestamps.add(new Long(0));
        });
    }

    private void createLpa() {
        receivedAll = false;
        Log.d("MYPROTO", "Now we have everything we need!! :D");

        Lpa lpa = new Lpa();
        lpa.setpId(identity);
        lpa.setRp(commitmentSeed);
        lpa.setStart(start);
        lpa.setStartSignature(startSignature);
        lpa.setLps(lpsReceived);
        lpa.setList(listReceived);
        lpa.setResponseSignatures(responseSignatures);
        lpa.setVideoHashSignature(videoHashSignature);

        String lpaJSON = parser.toJSON(lpa); // JSON representation of LPA
        Log.d("MYPROTO", "LPA: " + lpaJSON);
        String encryptedLpa = getEncryptedMessage(lpaJSON, caPublicKey, 2, false);
        largeLog("MYPROTO", encryptedLpa);

        AuxInfo auxInfo = new AuxInfo();
        auxInfo.setCommittedIdentity(committedIdentity);
        auxInfo.setLocation(exactLocation);
        auxInfo.setKchain(Arrays.asList(Kchain));
        String auxInfoJSON = parser.toJSON(auxInfo);

        Log.d("MYPROTO", "auxInfo: " + auxInfoJSON);

    }



    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }



}
