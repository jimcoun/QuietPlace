package com.example.user.test;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Verifier {

    private final String CAPUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgoybgh/rOCFohUBeOLtUHD+PYadgbi49+TPQdgVVi27niPH2eKcQskcf/dPYYn/83N5u33+IXrLbsZFqObRksP1wIlXnrxbPZBHJqJ0NX6bWdePIwGHvGm3gVlKHLN1tHY3q8WJRwl0fwnJVm3IlVYYpBHZgbDNJUnXrkMPTkYwIDAQAB";
    private final String PROVERPUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMbxrcYL890PHI7Ls7x2ScD+GojZgPQaebiYnQKtVjJYy65piQLzksvxVe2ldLoUgP7zUBsu9o/G0YGse9+I4b1pBoFZITPMZJAGGM9WpyP/dMY29WldLOQtYEuDvMI8QLrHbb/At7QivlasvC4/711FDok4gcbPKOGGfZNe9weQIDAQAB";
    private final String PVTKET = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALXLnlotygstpWFBsu+cid+oE+/LXf6FarRXVGuG2gu3iJuES1vnBFJ30I8bKabg3ZKva7WaKNoXuqXyB7oYWv5CMRkVY/BXL6dv07q6RrDjy3QWyCV1aPxF2o8SEXof5WF1Gx/jLpv47oYfCSrpRnSlg3ueevyl7NZ/f9mDDefLAgMBAAECgYAsoqjnpVR1OCjh3zqq4Dlt45u5hao3YtSXINYGbJ6+v7pXzLyf/PGvueFIMbghVBay/EUq2oXrPmZCdlF1XKOCXgEzprsmffrHq7BvH0t9+enu12pRsDfAkwcgQTZfW53O0hyostqAlytjmFqUgSchBDBIXIFkkAMqpNe35CCj+QJBAODazzFtuiM/su5hflcYcB1e3Umd6Lvsdae4DJe7tHRROu2Jo3cdZrLXDJHpdGtr8NQWBCwpUDKX6ErWuqZDR30CQQDO+fSNi2AzscEka/BP++k39Lb7F9wtVR8M3qrBYICDAb6DsdVExHxAWUOvjJ2hGVbB3QPuUltOOBugkbCo4t7nAkEAvW04vbBt7oIcUJzLnb4VvXiMvhcM2TxEhi4UVcOpK5C/LxwUHSYkvadTjJz7GgBOW63MiBOp32WAKYQqrCTj1QJBAM0oQ/j9eykie8EfYL/Xgo+deLahKvSgWDvIhtdRUXEKDI67ehtE66hZEtdyeViTJkTZT6kuZyS8Rt49Ky/sWWcCQHMV+yrWekof4wcBghaW1hru7nxXpGD3C/hwEFI69yi2Miar73USW7VhLmkUwej9cschBbt1T265WLh8gS2ssSs=";
    private final String PUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1y55aLcoLLaVhQbLvnInfqBPvy13+hWq0V1RrhtoLt4ibhEtb5wRSd9CPGymm4N2Sr2u1mijaF7ql8ge6GFr+QjEZFWPwVy+nb9O6ukaw48t0FsgldWj8RdqPEhF6H+VhdRsf4y6b+O6GHwkq6UZ0pYN7nnr8pezWf3/Zgw3nywIDAQAB";
    private PublicKey caPublicKey;
    private PublicKey proverPublicKey;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Verifier(){
        try{
            caPublicKey = Crypto.getPublicFromString(CAPUBKEY);
            proverPublicKey = Crypto.getPublicFromString(PROVERPUBKEY);
            privateKey = Crypto.getPrivateFromString(PVTKET);
            publicKey = Crypto.getPublicFromString(PUBKEY);
        } catch (Exception e) {
            Log.d("MYPROTOVER", "Error loading key/keys");
            e.printStackTrace();
        }

    }

    public void verify(String locationProofJSON){
        ParseJSON parser = new ParseJSON();
        String commitment; // Commitment in encrypted LP

        try {
            EncryptedMessage encryptedLP = (EncryptedMessage) parser.fromJSON(locationProofJSON, EncryptedMessage.class);
            commitment = encryptedLP.getCommitment();
            Log.d("MYPROTOVER", "LP belongs to user with committed ID: " + commitment);

            String lpJSON = decryptEncryptedMessage(encryptedLP, privateKey);
            Log.d("MYPROTOVER", "Decrypted LP: " + lpJSON);
            LocationProof locationProof = (LocationProof) parser.fromJSON(lpJSON, LocationProof.class);

            EncryptedMessage encryptedVS = (EncryptedMessage) parser.fromJSON(locationProof.getVerifierSegment(), EncryptedMessage.class);
            Log.d("MYPROTOVER", "VS belongs to CA with ID: " + encryptedVS.getCommitment());
            // Now we can find the public key of the CA, from its ID in the EncryptedMessage
            String vsJSON = decryptEncryptedMessage(encryptedVS, caPublicKey);
            Log.d("MYPROTOVER", "Decrypted VS: " + vsJSON);
            String caSignature = encryptedVS.getSign();
            boolean isSignatureValid = Crypto.signVerifyBase64(vsJSON, caSignature, caPublicKey);
            Log.d("MYPROVOVER", "CA signature of VS is " + ((isSignatureValid) ? "valid" : "invalid"));
            VerifierSegment verifierSegment = (VerifierSegment) parser.fromJSON(vsJSON, VerifierSegment.class);

            Start start = (Start) parser.fromJSON(verifierSegment.getStart(), Start.class);
            boolean doCommitmentsMatch = (commitment.equals(start.getCommitment()));
            Log.d("MYPROTOVER", "Commitments in Start and LP " + ((doCommitmentsMatch) ? "match." : "DO NOT match."));
            Log.d("MYPROTOVER", "Prover with ID " + verifierSegment.getpId() + " wants to verify location " + locationProof.getLi() +
                    " at time " + start.getTimestamp());
            boolean isLocationValid = LocationChain.checkChain(locationProof.getKi(), locationProof.getLi(), start.getLocationChain());
            Log.d("MYPROTOVER", "Location chain check was " + ((isLocationValid) ? "valid" : "invalid"));

        } catch (Exception e) {
            Log.d("MYPROTOVER", "Wrong message format.");
            e.printStackTrace();
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

}
