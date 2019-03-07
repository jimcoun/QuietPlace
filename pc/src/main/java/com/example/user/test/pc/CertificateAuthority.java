package com.example.user.test.pc;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CertificateAuthority {

    private final String PUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgoybgh/rOCFohUBeOLtUHD+PYadgbi49+TPQdgVVi27niPH2eKcQskcf/dPYYn/83N5u33+IXrLbsZFqObRksP1wIlXnrxbPZBHJqJ0NX6bWdePIwGHvGm3gVlKHLN1tHY3q8WJRwl0fwnJVm3IlVYYpBHZgbDNJUnXrkMPTkYwIDAQAB"; // CAs public key in Base64
    private final String PVTKEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKCjJuCH+s4IWiFQF44u1QcP49hp2BuLj35M9B2BVWLbueI8fZ4pxCyRx/909hif/zc3m7ff4hestuxkWo5tGSw/XAiVeevFs9kEcmonQ1fptZ148jAYe8abeBWUocs3W0djerxYlHCXR/CclWbciVVhikEdmBsM0lSdeuQw9ORjAgMBAAECgYBmZjqVLPmLNzXFQJoTb/UqUE3NGgPB42Awgfunh1eX8jt3ZVoqZbOBOdkFFlj+b5ewaklgmF42+0mMPZigbBl73UZMnW09IVg8WAu/6XhBaruCwMivIUNhaDR10nSYvXn0pu6tt+v0ZNZpUoJghOPFewjZ5MBJu6tyxq6Irh2p+QJBAPRf+ray6Xry1woywbsg2TMzBprX0E7akqEl3Y6aOkulVBdece37Hi4VkzJcCF9BNmAiY7PQizYUDR0yGbBrpXcCQQCoR2h4ldxdF5UzonsTVg4EYd7DUNtS6Da1DCjrSqCTpn7PG6fRgS7EVhhQAVSXZYH1PXR7z/2f0aV+hhW9tSN1AkEApvvGp3zNidPSGQfh1Wp2wEOHqdr6XkeAllj3ce/1EaehNG62zvUfBEJ92JWGqwA2la4qDr7bwVImO+L2JtUHtQJASUrQtbSPxfv/f/3EnSdd84qs+2S8IeAB89jX+aMHTUe2832YGiepttnIvQ/XSBluAOJDWam9TV6CNhkwXvAGAQJAWBsS2YJZL7CnoHvjRLvmY1U2YxsKpEGtSZ6yANhQoMZWCNgVJC3rgGCXAfBjFQS74CNbMb7FibpPsphZS4d/gQ==";
    private final String PROVER_PUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMbxrcYL890PHI7Ls7x2ScD+GojZgPQaebiYnQKtVjJYy65piQLzksvxVe2ldLoUgP7zUBsu9o/G0YGse9+I4b1pBoFZITPMZJAGGM9WpyP/dMY29WldLOQtYEuDvMI8QLrHbb/At7QivlasvC4/711FDok4gcbPKOGGfZNe9weQIDAQAB";

    // private final String PVTKEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMxvGtxgvz3Q8cjsuzvHZJwP4aiNmA9Bp5uJidAq1WMljLrmmJAvOSy/FV7aV0uhSA/vNQGy72j8bRgax734jhvWkGgVkhM8xkkAYYz1anI/90xjb1aV0s5C1gS4O8wjxAusdtv8C3tCK+Vqy8Lj/vXUUOiTiBxs8o4YZ9k173B5AgMBAAECgYA9H78tQzQK/I0+YSG+RujbDJiQ9/0OGrhNdfshpZz1rwV74HSfL69tpJh0Kt5M+6T7Nq9nmaOhhU/tFBzCvS1ntfNFLFnVCLA1LfYdJwnVWAmpSRj6+MM7F0O7d8OP4WjW3HwxAdLFyNGECQvCiBxp4IHoRv21HYV5tRwHvo2efQJBAPxPWoDcSor8d3UZsZGXfNldJrE53Ddm2HFAhCE9mWH3tiIS5Cn/tdzvfP8VkN0aM0pZg1v1+G5Fny3m8VNMij8CQQDPbIEQP5wp5paiPo20wlk6j7sG9Ih+tvPkSByCQlWnH79wCeGpreugsFg9Tx8fy5xdhgl6pP3Kg5tprWfU+qdHAkBewN5YLmLAN3gVPgT1jFKSvuzc+cG9/J2kSnpUkXGc3Q5FVZriOuntgvMKSOsSXdiNP3iZfJJDt1nEP0q54bC5AkAtkPuFU0P+HG7I847zv6IUcFC4xW1a0NwhMQo6P1JLpXjLpxAQ02ko4rRvu3rt5C/Uh8Z7T9WE8IZqn7JooiuvAkAva++UwW2+KsNfR6JdyHY5Y4EMJmfIE2y6tPpR13RCwycjsnAsSEjG5lmdYe01gCrZzFN8qZ/Y0esn4/0RqTJu";
    // private final String PUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMbxrcYL890PHI7Ls7x2ScD+GojZgPQaebiYnQKtVjJYy65piQLzksvxVe2ldLoUgP7zUBsu9o/G0YGse9+I4b1pBoFZITPMZJAGGM9WpyP/dMY29WldLOQtYEuDvMI8QLrHbb/At7QivlasvC4/711FDok4gcbPKOGGfZNe9weQIDAQAB";
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private PublicKey proverPublicKey;

    private String commitment; // The commitment in the encrypted message
    private Lpa lpa; // The LPA message received from the Prover
    private Start start; // The start message
    private List<String> lpsJSON; // LPSs in JSON
    private List<String> listJSON; // LISTs in JSON

    private ParseJSON parser;

    public static void main(String[] args){
        System.out.println("Starting Certificate Authority emulation.");

        CertificateAuthority ca = new CertificateAuthority();
        ca.run();

    }

    public CertificateAuthority(){

        parser = new ParseJSON();
        try {
            this.publicKey = Crypto.getPublicFromString(this.PUBKEY);
            this.privateKey = Crypto.getPrivateFromString(this.PVTKEY);
            this.proverPublicKey = Crypto.getPublicFromString(this.PROVER_PUBKEY);
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private void run(){
        /* // Testing the key pair
        String test = "Clear text.";
        String encrypted = Crypto.encryptText(test, privateKey);
        String decrypted = Crypto.decryptText(encrypted, publicKey);
        System.out.println(decrypted);

        encrypted = Crypto.encryptText(test, publicKey);
        decrypted = Crypto.decryptText(encrypted, privateKey);
        System.out.println(decrypted);
        */
        String keybase64 = "0cVkKD8xYX0Ch28zpLG5tg==";
        String encryptedKey = Crypto.encryptText(keybase64, publicKey);
        System.out.println(encryptedKey);
        String decryptedKeyb = Crypto.decryptText(encryptedKey, privateKey);
        System.out.println(decryptedKeyb);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Input encrypted LPA to check:");
        // String encryptedLpaJSON = scanner.nextLine();
        String encryptedLpaJSON = "{\"commitment\":\"3d8178b50bc450d7d4ee31bc4eced23712de5ae73f128f97e0ec23936c55ed\",\"type\":2,\"data\":\"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\",\"ivSpec\":\"qEn5F4CGQj4oxtBA1Xlr3g==\",\"key\":\"cDOtVMgc6EdpWrPai+aXGprGQgHVJrUgmpx/OpNSHhI3GAhi+tmxdZoiXI3+us5N8XG86x3b6R6vYySMhDxbAoorFq5kz1vskUVZTE0R9c1PjAUGwUHdgNjfLQVEpEEW/MPDrCQ+37YjxGyPcoxRDjFyaiOxFb5Q91+/lV+qH78=\",\"sign\":null}";
        System.out.println(encryptedLpaJSON);
        try{
            EncryptedMessage encryptedLpa = (EncryptedMessage) parser.fromJSON(encryptedLpaJSON, EncryptedMessage.class);
            commitment = encryptedLpa.getCommitment();
            System.out.println("The LPA belongs to prover with committed identity: " + commitment);
            System.out.println("data: " + encryptedLpa.getData());
            System.out.println("key: " + encryptedLpa.getKey());


            // Decrypt symmetric key using my private key
            String decryptedKeyBase64 = Crypto.decryptText(encryptedLpa.getKey(), privateKey);
            System.out.println("DecryptedkeyBase64: " + decryptedKeyBase64);
            byte[] decryptedKey = Base64.getDecoder().decode(decryptedKeyBase64);
            SecretKey symmetricKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");

            // Get IVSpec
            byte[] ivSpecBytes = Base64.getDecoder().decode(encryptedLpa.getIvSpec());
            IvParameterSpec ivSpec = new IvParameterSpec(ivSpecBytes);
            // Decrypt the data
            String decryptedData = Crypto.symmetricDecrypt(encryptedLpa.getData(), symmetricKey, ivSpec);
            lpa = (Lpa) parser.fromJSON(decryptedData, Lpa.class);
            System.out.println("Successfully extracted LPA.");

            boolean isCommitmentValid = Crypto.deCommit(commitment, lpa.getpId(), lpa.getRp());
            if (isCommitmentValid){
                System.out.println("Commitment is valid.");
                checkStart();
            }
            else{
                System.out.println("Invalid commitment. Stopping.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkStart(){
        System.out.println("Checking Start message.");

        try {
            SignHash startSh = (SignHash) parser.fromJSON(lpa.getStart(), SignHash.class);
            start = (Start) parser.fromJSON(startSh.getData(), Start.class);
            if(start.getCommitment() == commitment){
                System.out.println("Commitment in Start message is valid");

                // Check the signature
                boolean isSignatureValid = Crypto.signVerifyBase64(startSh.getData(), lpa.getStartSignature(), proverPublicKey);
                boolean isSignatureHashValid = Crypto.sha256Base64(lpa.getStartSignature()) == startSh.getHs();
                if(isSignatureValid && isSignatureHashValid){
                    System.out.println("Signature of Start message and hash of signature is valid.");
                    checkLPS();
                }
                else{
                    System.out.println("Prover signature or hash is invalid. Stopping.");
                }

            }
            else{
                System.out.println("Commitment in Start is invalid. Stopping.");
            }
        } catch (Exception e) {
            System.out.println("The start message cannot be loaded.");
            e.printStackTrace();
        }

    }

    private void checkLPS(){
        System.out.println("Entered checklps");
    }

    protected String getEncryptedMessage(String data, Key publicKey, int type, boolean isSignatureRequired) {
        try {
            // Generate symmetric key
            SecretKey secretKey = Crypto.getKey();
            // Convert key to Base64
            String secretKeyBase64 = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            // Generate initialization vector and encode to Base64
            IvParameterSpec ivSpec = Crypto.getIvSpec();
            String ivSpecBase64 = Base64.getEncoder().encodeToString(ivSpec.getIV());
            // Encrypt data using symmetric key
            String encryptedData = Crypto.symmetricEncrypt(data, secretKey, ivSpec);
            // Encrypt symmetric key using CAs public key
            String encryptedKey = Crypto.encryptText(secretKeyBase64, publicKey);

            EncryptedMessage em = new EncryptedMessage();
            em.setCommitment("5722ce818b37ec831eba635ef8eca6baee968798e56a894cbb1f09223d86577");
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
