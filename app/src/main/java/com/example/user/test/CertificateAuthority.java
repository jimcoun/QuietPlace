package com.example.user.test;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CertificateAuthority {

    private final String CA_ID = "CA identity";
    private final String PUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgoybgh/rOCFohUBeOLtUHD+PYadgbi49+TPQdgVVi27niPH2eKcQskcf/dPYYn/83N5u33+IXrLbsZFqObRksP1wIlXnrxbPZBHJqJ0NX6bWdePIwGHvGm3gVlKHLN1tHY3q8WJRwl0fwnJVm3IlVYYpBHZgbDNJUnXrkMPTkYwIDAQAB"; // CAs public key in Base64
    private final String PVTKEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKCjJuCH+s4IWiFQF44u1QcP49hp2BuLj35M9B2BVWLbueI8fZ4pxCyRx/909hif/zc3m7ff4hestuxkWo5tGSw/XAiVeevFs9kEcmonQ1fptZ148jAYe8abeBWUocs3W0djerxYlHCXR/CclWbciVVhikEdmBsM0lSdeuQw9ORjAgMBAAECgYBmZjqVLPmLNzXFQJoTb/UqUE3NGgPB42Awgfunh1eX8jt3ZVoqZbOBOdkFFlj+b5ewaklgmF42+0mMPZigbBl73UZMnW09IVg8WAu/6XhBaruCwMivIUNhaDR10nSYvXn0pu6tt+v0ZNZpUoJghOPFewjZ5MBJu6tyxq6Irh2p+QJBAPRf+ray6Xry1woywbsg2TMzBprX0E7akqEl3Y6aOkulVBdece37Hi4VkzJcCF9BNmAiY7PQizYUDR0yGbBrpXcCQQCoR2h4ldxdF5UzonsTVg4EYd7DUNtS6Da1DCjrSqCTpn7PG6fRgS7EVhhQAVSXZYH1PXR7z/2f0aV+hhW9tSN1AkEApvvGp3zNidPSGQfh1Wp2wEOHqdr6XkeAllj3ce/1EaehNG62zvUfBEJ92JWGqwA2la4qDr7bwVImO+L2JtUHtQJASUrQtbSPxfv/f/3EnSdd84qs+2S8IeAB89jX+aMHTUe2832YGiepttnIvQ/XSBluAOJDWam9TV6CNhkwXvAGAQJAWBsS2YJZL7CnoHvjRLvmY1U2YxsKpEGtSZ6yANhQoMZWCNgVJC3rgGCXAfBjFQS74CNbMb7FibpPsphZS4d/gQ==";
    private final String PROVER_PUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMbxrcYL890PHI7Ls7x2ScD+GojZgPQaebiYnQKtVjJYy65piQLzksvxVe2ldLoUgP7zUBsu9o/G0YGse9+I4b1pBoFZITPMZJAGGM9WpyP/dMY29WldLOQtYEuDvMI8QLrHbb/At7QivlasvC4/711FDok4gcbPKOGGfZNe9weQIDAQAB";


    // private final String PVTKEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMxvGtxgvz3Q8cjsuzvHZJwP4aiNmA9Bp5uJidAq1WMljLrmmJAvOSy/FV7aV0uhSA/vNQGy72j8bRgax734jhvWkGgVkhM8xkkAYYz1anI/90xjb1aV0s5C1gS4O8wjxAusdtv8C3tCK+Vqy8Lj/vXUUOiTiBxs8o4YZ9k173B5AgMBAAECgYA9H78tQzQK/I0+YSG+RujbDJiQ9/0OGrhNdfshpZz1rwV74HSfL69tpJh0Kt5M+6T7Nq9nmaOhhU/tFBzCvS1ntfNFLFnVCLA1LfYdJwnVWAmpSRj6+MM7F0O7d8OP4WjW3HwxAdLFyNGECQvCiBxp4IHoRv21HYV5tRwHvo2efQJBAPxPWoDcSor8d3UZsZGXfNldJrE53Ddm2HFAhCE9mWH3tiIS5Cn/tdzvfP8VkN0aM0pZg1v1+G5Fny3m8VNMij8CQQDPbIEQP5wp5paiPo20wlk6j7sG9Ih+tvPkSByCQlWnH79wCeGpreugsFg9Tx8fy5xdhgl6pP3Kg5tprWfU+qdHAkBewN5YLmLAN3gVPgT1jFKSvuzc+cG9/J2kSnpUkXGc3Q5FVZriOuntgvMKSOsSXdiNP3iZfJJDt1nEP0q54bC5AkAtkPuFU0P+HG7I847zv6IUcFC4xW1a0NwhMQo6P1JLpXjLpxAQ02ko4rRvu3rt5C/Uh8Z7T9WE8IZqn7JooiuvAkAva++UwW2+KsNfR6JdyHY5Y4EMJmfIE2y6tPpR13RCwycjsnAsSEjG5lmdYe01gCrZzFN8qZ/Y0esn4/0RqTJu";
    // private final String PUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMbxrcYL890PHI7Ls7x2ScD+GojZgPQaebiYnQKtVjJYy65piQLzksvxVe2ldLoUgP7zUBsu9o/G0YGse9+I4b1pBoFZITPMZJAGGM9WpyP/dMY29WldLOQtYEuDvMI8QLrHbb/At7QivlasvC4/711FDok4gcbPKOGGfZNe9weQIDAQAB";
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private PublicKey proverPublicKey;

    private String commitment; // The Prover commitment in the encrypted LPA
    private String proverIdentity;
    private Lpa lpa; // The LPA message received from the Prover
    private Start start; // The start message
    private String startJSON;
    private String videoHashContent;
    private List<Lps> lpsArray; // LPSs
    private List<String> listArray; // LISTs
    private List<String> responseSignaturesArray; // Responses


    private ParseJSON parser;


    public CertificateAuthority(){

        parser = new ParseJSON();
        try {
            this.publicKey = Crypto.getPublicFromString(this.PUBKEY);
            this.privateKey = Crypto.getPrivateFromString(this.PVTKEY);
            this.proverPublicKey = Crypto.getPublicFromString(this.PROVER_PUBKEY);
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    public void run(){
        /* // Testing the key pair
        String test = "Clear text.";
        String encrypted = Crypto.encryptText(test, privateKey);
        String decrypted = Crypto.decryptText(encrypted, publicKey);
        System.out.println(decrypted);

        encrypted = Crypto.encryptText(test, publicKey);
        decrypted = Crypto.decryptText(encrypted, privateKey);
        System.out.println(decrypted);
        */

        // String encryptedLpaJSON = "{\"commitment\":\"3d8178b50bc450d7d4ee31bc4eced23712de5ae73f128f97e0ec23936c55ed\",\"type\":2,\"data\":\"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\",\"ivSpec\":\"qEn5F4CGQj4oxtBA1Xlr3g==\",\"key\":\"cDOtVMgc6EdpWrPai+aXGprGQgHVJrUgmpx/OpNSHhI3GAhi+tmxdZoiXI3+us5N8XG86x3b6R6vYySMhDxbAoorFq5kz1vskUVZTE0R9c1PjAUGwUHdgNjfLQVEpEEW/MPDrCQ+37YjxGyPcoxRDjFyaiOxFb5Q91+/lV+qH78=\",\"sign\":null}";
        // String encryptedLpaJSON = "{\"commitment\":\"eb21c6ed275fef14e62574b30d96aa26a9297687449f06239d04abf92e4c7ad\",\"type\":2,\"data\":\"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\\nHjZJCg==\\n\",\"ivSpec\":\"7kemBILnFLAOB2+Sqk0j+Q==\\n\",\"key\":\"ify0PRmTjM0thRqZ9us+jzMvLo1pG4WGr92CIvrD7PcCLxuhsSPd01Ll/iy75U7rCmlw9t5xOlqY\\ns75/FJX42F/tW1t7B/T9CHkDAz0z3evEfHBuWUNwlHIuQIfbe3u0QCfWM/Xn/y+OAQNOwXWta2bo\\n3fHxA/JzMJl4eY0jq3o=\\n\",\"sign\":null}";
        String encryptedLpaJSON = "{\"commitment\":\"4cdfbbad15da3269fafc33122d881279b34e2a440fe247d408365d38e892675\",\"type\":2,\"data\":\"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\\n\",\"ivSpec\":\"H+zuxcXYVhXlcgk8CWaKig==\\n\",\"key\":\"gLhXVwrbFeglHWqFOxw8F45ievq/0lcxThPuMljeaz0qjeaqDJI4XSo5E7OIGoCt8p77msR2LWQg\\ny/wdlUMIqgPAeL/hCIG+JLL4XRqlfLMJzBKi9jgSxgOOddmOkNOqVQNEKmXjf0QHmixlNXvywTtE\\nVBZfiKhUTnbtZLIIkVE=\\n\",\"sign\":null}";

        try{
            EncryptedMessage encryptedLpa = (EncryptedMessage) parser.fromJSON(encryptedLpaJSON, EncryptedMessage.class);
            commitment = encryptedLpa.getCommitment();
            Log.d("MYPROTOCA","The LPA belongs to prover with committed identity: " + commitment);

            String decryptedLpa = decryptEncryptedMessage(encryptedLpa, privateKey);
            largeLog("MYPROTOCA", "Decrypted LPA: " + decryptedLpa);
            lpa = (Lpa) parser.fromJSON(decryptedLpa, Lpa.class);
            Log.d("MYPROTOCA", "Successfully extracted LPA.");


            boolean isCommitmentValid = Crypto.deCommit(commitment, lpa.getpId(), lpa.getRp());
            if (isCommitmentValid){
                proverIdentity = lpa.getpId(); // We can't be sure about the identity before we check the signatures below
                Log.d("MYPROTOCA","Commitment is valid. User ID is: " + proverIdentity);
                // Now that I know the user I can load its public key
                checkStart();
            }
            else{
                Log.d("MYPROTOCA","Invalid commitment. Stopping.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkStart(){
        Log.d("MYPROTOCA","Checking Start message.");

        try {
            // SignHash startSh = (SignHash) parser.fromJSON(lpa.getStart(), SignHash.class);
            startJSON = lpa.getStart();
            Log.d("MYPROTOCA", "Start: " + startJSON);
            start = (Start) parser.fromJSON(startJSON, Start.class);
            if(start.getCommitment().equals(commitment)){
                Log.d("MYPROTOCA","Commitment in Start message is valid");

                // Check the signature
                boolean isSignatureValid = Crypto.signVerifyBase64(startJSON, lpa.getStartSignature(), proverPublicKey);
                // boolean isSignatureHashValid = Crypto.sha256Base64(lpa.getStartSignature()) == startSh.getHs();
                if(isSignatureValid){
                    Log.d("MYPROTOCA","Signature of Start message and hash of signature is valid.");
                    checkLPS();
                }
                else{
                    Log.d("MYPROTOCA","Prover signature or hash is invalid. Stopping.");
                }

            }
            else{
                Log.d("MYPROTOCA","Commitment in Start is invalid. Stopping.");
            }
        } catch (Exception e) {
            System.out.println("The start message cannot be loaded.");
            e.printStackTrace();
        }

    }

    private void checkLPS(){
        Log.d("MYPROTOCA","Now extracting LPS and LIST messages.");
        List<String> encryptedLpsJSONArray = lpa.getLps();
        lpsArray = new ArrayList<>();
        // listJSONArray = lpa.getList();
        // responseSignaturesJSONArray = lpa.getResponseSignatures();

        List<ListMessage> listArray = new ArrayList<>();


        for (String encryptedLpsJSON : encryptedLpsJSONArray){
            try{
                EncryptedMessage encryptedLps = (EncryptedMessage) parser.fromJSON(encryptedLpsJSON, EncryptedMessage.class);
                Log.d("MYPROTOCA","LPS from: " + encryptedLps.getCommitment());

                String decryptedLps = decryptEncryptedMessage(encryptedLps, privateKey);
                largeLog("MYPROTOCA", "Decrypted LPS: " + decryptedLps);

                Lps lps = (Lps) parser.fromJSON(decryptedLps, Lps.class);
                lpsArray.add(lps);

                // Check witness commitment
                boolean isCommitmentValid = Crypto.deCommit(encryptedLps.getCommitment(), lps.getId(), lps.getRw());
                Log.d("MYPROTOCA", "Witness commitment is " + ((isCommitmentValid) ? "valid." : "invalid."));

                // Load LPS contents

                // Challenge checks
                SignHash chalSh = (SignHash) parser.fromJSON(lps.getChal(), SignHash.class);
                boolean flag = checkSignHash(chalSh, lps.getChSign(), proverPublicKey); // Must be witnessPublicKey
                Log.d("MYPROTOCA", "Challenge signature is " + ((flag) ? "valid." : "invalid."));
                Challenge chal = (Challenge) parser.fromJSON(chalSh.getData(), Challenge.class);

                boolean isChallengeCommitmentValid = encryptedLps.getCommitment().equals(chal.getCommitment());
                Log.d("MYPROTOCA", "Challenge commitment is " + ((isChallengeCommitmentValid) ? "valid." : "invalid."));

                // Response checks
                SignHash resSh = (SignHash) parser.fromJSON(lps.getRes(), SignHash.class);
                flag = checkSignHash(resSh, lpa.getResponseSignatures().get(0), proverPublicKey); // Fix needed
                Log.d("MYPROTOCA", "Response signature is " + ((flag) ? "valid." : "invalid."));
                Response res = (Response) parser.fromJSON(resSh.getData(), Response.class);

                boolean isResponseCommitmentValid = commitment.equals(res.getCommitment());
                Log.d("MYPROTOCA", "Response commitment is " + ((isResponseCommitmentValid) ? "valid." : "invalid."));

                boolean n1equality = chal.getN1() == res.getN1();
                boolean n2equality = chal.getN2() == res.getN2();
                Log.d("MYPROTOCA", "Challenge and Response n1, n2 " + ((n1equality && n2equality) ? "match." : "DO NOT match."));

                // VideoHash checks

                SignHash videoHashSh = (SignHash) parser.fromJSON(lps.getvH(), SignHash.class);
                flag = checkSignHash(videoHashSh, lpa.getVideoHashSignature(), proverPublicKey); // Fix needed
                Log.d("MYPROTOCA", "VideoHash signature is " + ((flag) ? "valid." : "invalid."));
                VideoHash videoHash = (VideoHash) parser.fromJSON(videoHashSh.getData(), VideoHash.class);

                boolean isVideoHashCommitmentValid = commitment.equals(videoHash.getCommitment());
                Log.d("MYPROTOCA", "VideoHash commitment is " + ((isVideoHashCommitmentValid) ? "valid." : "invalid."));
                videoHashContent = videoHash.getHash(); // Fix needed

                Log.d("MYPROTOCA", "Got VS: " + createVerifierSegment(true));

            } catch (Exception e) {
                Log.d("MYPROTOCA", "Cannot extract LPS");
                e.printStackTrace();
            }
        }

        /*
        for (String listJSON : listJSONArray){
            try{
                ListMessage list = (ListMessage) parser.fromJSON(listJSON, ListMessage.class);
                listArray.add(list);
            } catch (Exception e) {
                Log.d("MYPROTOCA", "Cannot extract LIST");
                e.printStackTrace();
            }

        }
        */



    }

    private String createVerifierSegment(boolean decision){
        VerifierSegment vs = new VerifierSegment();
        vs.setpId(proverIdentity);
        vs.setStart(startJSON);
        vs.setVideoHash(videoHashContent);
        vs.setAccepted(decision);

        String verifierSegment = parser.toJSON(vs);

        String encryptedVS = getEncryptedMessage(verifierSegment, privateKey, 3, true);
        return encryptedVS;

    }

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

    private String decryptEncryptedMessage(EncryptedMessage encryptedMessage, Key key) {
        String decryptedKeyBase64 = Crypto.decryptText(encryptedMessage.getKey(), key);
        byte[] decryptedKey = Base64.decode(decryptedKeyBase64, Base64.DEFAULT);
        SecretKey symmetricKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");

        // Get IVSpec
        byte[] ivSpecBytes = Base64.decode(encryptedMessage.getIvSpec(), Base64.DEFAULT);
        IvParameterSpec ivSpec = new IvParameterSpec(ivSpecBytes);
        // Decrypt the data
        String decryptedData = Crypto.symmetricDecrypt(encryptedMessage.getData(), symmetricKey, ivSpec);
        return decryptedData;
    }


    private boolean checkSignHash(SignHash signHash, String signature, PublicKey publicKey){
        try{

            String calculatedHash = Crypto.sha256Base64(signature);
            String givenHash = signHash.getHs();
            // Need to trim for unknown reason
            boolean isHashValid = givenHash.trim().equals(calculatedHash.trim());
            // Log.d("MYPROTOCA", calculatedHash);
            // Log.d("MYPROTOCA", givenHash);
            // Log.d("MYPROTOCA", Boolean.toString(isHashValid));
            boolean isSignatureValid = Crypto.signVerifyBase64(signHash.getData(), signature, publicKey);
            // Log.d("MYPROTOCA", Boolean.toString(isSignatureValid));
            return (isHashValid && isSignatureValid);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getEncryptedMessage(String data, Key rsaKey, int type, boolean isSignatureRequired) {
        try {
            // Generate symmetric key
            SecretKey secretKey = Crypto.getKey();
            // Convert key to Base64
            String secretKeyBase64 = android.util.Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
            Log.d("MYPROTOCRYPTO", "secretKeyBase64: " + secretKeyBase64);
            // Generate initialization vector and encode to Base64
            IvParameterSpec ivSpec = Crypto.getIvSpec();
            String ivSpecBase64 = android.util.Base64.encodeToString(ivSpec.getIV(), Base64.DEFAULT);
            // Encrypt data using symmetric key
            String encryptedData = Crypto.symmetricEncrypt(data, secretKey, ivSpec);
            // Encrypt symmetric key using CAs public key
            String encryptedKey = Crypto.encryptText(secretKeyBase64, rsaKey);
            Log.d("MYPROTOCRYPTO", "encryptedSecretKeyBase64: " + encryptedKey);

            EncryptedMessage em = new EncryptedMessage();
            em.setCommitment(CA_ID); // Instead of commitment CA uses its ID
            em.setType(type);
            em.setData(encryptedData);
            em.setKey(encryptedKey);
            em.setIvSpec(ivSpecBase64);

            if(isSignatureRequired){
                em.setSign(Crypto.signBase64(data, privateKey));
            }

            return parser.toJSON(em);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Error";
        }
    }


}
