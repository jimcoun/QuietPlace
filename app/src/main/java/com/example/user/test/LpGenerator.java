package com.example.user.test;

import android.util.Log;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public class LpGenerator {

    private final String PVTKEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMxvGtxgvz3Q8cjsuzvHZJwP4aiNmA9Bp5uJidAq1WMljLrmmJAvOSy/FV7aV0uhSA/vNQGy72j8bRgax734jhvWkGgVkhM8xkkAYYz1anI/90xjb1aV0s5C1gS4O8wjxAusdtv8C3tCK+Vqy8Lj/vXUUOiTiBxs8o4YZ9k173B5AgMBAAECgYA9H78tQzQK/I0+YSG+RujbDJiQ9/0OGrhNdfshpZz1rwV74HSfL69tpJh0Kt5M+6T7Nq9nmaOhhU/tFBzCvS1ntfNFLFnVCLA1LfYdJwnVWAmpSRj6+MM7F0O7d8OP4WjW3HwxAdLFyNGECQvCiBxp4IHoRv21HYV5tRwHvo2efQJBAPxPWoDcSor8d3UZsZGXfNldJrE53Ddm2HFAhCE9mWH3tiIS5Cn/tdzvfP8VkN0aM0pZg1v1+G5Fny3m8VNMij8CQQDPbIEQP5wp5paiPo20wlk6j7sG9Ih+tvPkSByCQlWnH79wCeGpreugsFg9Tx8fy5xdhgl6pP3Kg5tprWfU+qdHAkBewN5YLmLAN3gVPgT1jFKSvuzc+cG9/J2kSnpUkXGc3Q5FVZriOuntgvMKSOsSXdiNP3iZfJJDt1nEP0q54bC5AkAtkPuFU0P+HG7I847zv6IUcFC4xW1a0NwhMQo6P1JLpXjLpxAQ02ko4rRvu3rt5C/Uh8Z7T9WE8IZqn7JooiuvAkAva++UwW2+KsNfR6JdyHY5Y4EMJmfIE2y6tPpR13RCwycjsnAsSEjG5lmdYe01gCrZzFN8qZ/Y0esn4/0RqTJu";
    private ParseJSON parser = new ParseJSON(); // Instantiate JSON parser
    private PrivateKey privateKey;
    private AuxInfo auxInfo;

    public LpGenerator(String auxInfoJSON){
        try {
            privateKey = Crypto.getPrivateFromString(PVTKEY);
            auxInfo = (AuxInfo) parser.fromJSON(auxInfoJSON, AuxInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateLp(String verifierSegment, int level){

        try{
            List<String> Kchain = auxInfo.getKchain();

            LocationProof lp = new LocationProof();
            lp.setKi(Kchain.get(level));
            lp.setLi(LocationChain.shortenPlusCode(auxInfo.getLocation(), level));
            lp.setVerifierSegment(verifierSegment);
            // lp.setStart(start);
            // lp.setpId(identity);

            Log.d("MYPROTOLP", "Generic LP before encryption" + parser.toJSON(lp));
            return parser.toJSON(lp);
        } catch (Exception e) {
            e.printStackTrace();
            return "Cannot extract AuxInfo";
        }

    }

    public String encryptLp(String locationProof, String verifierKeyString){
        try {
            PublicKey verifierKey = Crypto.getPublicFromString(verifierKeyString);
            String commitedIdentity = auxInfo.getCommittedIdentity();
            String encryptedLp = Crypto.getEncryptedMessage(locationProof, commitedIdentity, verifierKey, privateKey,4, true);
            Log.d("MYPROTOLP", "Encrypted LP: " + encryptedLp);
            return Crypto.getEncryptedMessage(locationProof, commitedIdentity, verifierKey, privateKey,4, true);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
