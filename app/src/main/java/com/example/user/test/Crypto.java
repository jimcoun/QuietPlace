package com.example.user.test;

import android.util.Base64;

import java.io.FileOutputStream;
import java.io.Writer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.nio.file.Files;

import java.security.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Crypto {

    public static void main(String[] args) {
        System.out.println(sha256("kalimera"));
        System.out.println(random256());

        String random = random256();
        String message = "Top Secret";
        String commitment = commit(message, random);
        System.out.printf("Committed message: %s%n", commitment);

        System.out.println(deCommit(commitment, message, random));

        keyPairGenerator("kleidi");

        System.out.println(fileSha256("78.jpg"));

        try {
//            PrivateKey privateKey = getPrivate("kleidib.key");
//            PublicKey publicKey = getPublic("kleidib.pub");

            PrivateKey privateKey = getPrivateFromString("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMxvGtxgvz3Q8cjsuzvHZJwP4aiNmA9Bp5uJidAq1WMljLrmmJAvOSy/FV7aV0uhSA/vNQGy72j8bRgax734jhvWkGgVkhM8xkkAYYz1anI/90xjb1aV0s5C1gS4O8wjxAusdtv8C3tCK+Vqy8Lj/vXUUOiTiBxs8o4YZ9k173B5AgMBAAECgYA9H78tQzQK/I0+YSG+RujbDJiQ9/0OGrhNdfshpZz1rwV74HSfL69tpJh0Kt5M+6T7Nq9nmaOhhU/tFBzCvS1ntfNFLFnVCLA1LfYdJwnVWAmpSRj6+MM7F0O7d8OP4WjW3HwxAdLFyNGECQvCiBxp4IHoRv21HYV5tRwHvo2efQJBAPxPWoDcSor8d3UZsZGXfNldJrE53Ddm2HFAhCE9mWH3tiIS5Cn/tdzvfP8VkN0aM0pZg1v1+G5Fny3m8VNMij8CQQDPbIEQP5wp5paiPo20wlk6j7sG9Ih+tvPkSByCQlWnH79wCeGpreugsFg9Tx8fy5xdhgl6pP3Kg5tprWfU+qdHAkBewN5YLmLAN3gVPgT1jFKSvuzc+cG9/J2kSnpUkXGc3Q5FVZriOuntgvMKSOsSXdiNP3iZfJJDt1nEP0q54bC5AkAtkPuFU0P+HG7I847zv6IUcFC4xW1a0NwhMQo6P1JLpXjLpxAQ02ko4rRvu3rt5C/Uh8Z7T9WE8IZqn7JooiuvAkAva++UwW2+KsNfR6JdyHY5Y4EMJmfIE2y6tPpR13RCwycjsnAsSEjG5lmdYe01gCrZzFN8qZ/Y0esn4/0RqTJu");
            PublicKey publicKey = getPublicFromString("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMbxrcYL890PHI7Ls7x2ScD+GojZgPQaebiYnQKtVjJYy65piQLzksvxVe2ldLoUgP7zUBsu9o/G0YGse9+I4b1pBoFZITPMZJAGGM9WpyP/dMY29WldLOQtYEuDvMI8QLrHbb/At7QivlasvC4/711FDok4gcbPKOGGfZNe9weQIDAQAB");
            String msg = "This is clear text!";
            String encrypted = encryptText(msg, privateKey);

            String decrypted = decryptText(encrypted, publicKey);
            System.out.println(decrypted);

            encrypted = encryptText(msg, publicKey);
            decrypted = decryptText(encrypted, privateKey);
            System.out.println(decrypted);

            byte[] signature = sign(msg, privateKey);
            
            String signatureHex = byteToHex(signature);
    
            System.out.println(signatureHex);

            // msg = "Oops!";
            System.out.println(signVerify(msg, signature, publicKey));
        }

        catch (Exception e) {
            System.out.println(e);
        }

    }

    // Computes the SHA-256 hash of a string
    public static String sha256(String data) {

        try {
            // Creating the MessageDigest object
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // Use SHA-256 algorithm

            // Passing data to the created MessageDigest Object
            md.update(data.getBytes());

            // Compute the message digest
            byte[] digest = md.digest();

            return byteToHex(digest);

        } catch (NoSuchAlgorithmException e) {
            return e.toString();
        }

    }

    public static String sha256Base64(String data) {

        try {
            // Creating the MessageDigest object
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // Use SHA-256 algorithm

            // Passing data to the created MessageDigest Object
            md.update(data.getBytes());

            // Compute the message digest
            byte[] digest = md.digest();

            return Base64.encodeToString(digest, Base64.DEFAULT);

        } catch (NoSuchAlgorithmException e) {
            return "Error";
        }

    }

    public static String fileSha256(String fileName) {
        File file = new File(fileName);

        try (InputStream in = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] block = new byte[4096];
            int length;
            while ((length = in.read(block)) > 0) {
                digest.update(block, 0, length);
            }
            return byteToHex(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    // Generates a random 256-bit hex string
    public static String random256() {
        SecureRandom random = new SecureRandom();

        byte bytes[] = new byte[32];
        random.nextBytes(bytes);

        String Hex = byteToHex(bytes);

        return Hex;
    }

    // Generate a random 256-bit in Base64 encoding
    public static String random256Base64() {
        SecureRandom random = new SecureRandom();

        byte bytes[] = new byte[32];
        random.nextBytes(bytes);

        String randomBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return randomBase64;
    }

    // Converts bytes to hex
    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // Another method for converting bytes to hex
    public static String byteToHex(byte[] byteData) {
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < byteData.length; i++) {
            hexString.append(Integer.toHexString(0xFF & byteData[i]));
        }

        return hexString.toString();
    }


    // Returns the commitment to message given the random string
    public static String commit(String message, String random) {
        String commitment = sha256(message.concat(random));
        return commitment;

    }

    // Returns true if commitment is valid, false if not
    public static boolean deCommit(String commitment, String message, String random) {
        String commitmentReproduce = sha256(message.concat(random));

        return commitment.equals(commitmentReproduce);
    }

    // Generates RSA key pair
    public static void keyPairGenerator(String fileName) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            // Initialization
            keyPairGenerator.initialize(1024);

            // Generation
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Get the public key
            PublicKey publicKey = keyPair.getPublic();

            // Get the private key
            PrivateKey privateKey = keyPair.getPrivate();

            // Encode keys

            byte[] pub = publicKey.getEncoded();

            byte[] pvt = privateKey.getEncoded();

            // Create key files in Base64 format
            saveKeys(fileName, pub, pvt);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("The algorithm specified does not exist or is not implemented.");
        }
    }

    // Saves the generated keys
    private static void saveKeys(String fileName, byte[] pub, byte[] pvt) {
        // ------Binary format------
        String outFile = fileName + "b";
        try {
            FileOutputStream bOut = new FileOutputStream(outFile + ".key");
            bOut.write(pvt);
            bOut.close();

            bOut = new FileOutputStream(outFile + ".pub");
            bOut.write(pub);
            bOut.close();
        } catch (IOException ioexc) {
            System.out.println("File error!");
        }

        // ------Base64 format------
        // Save private key
        /*
        try {
            Writer out = new FileWriter(fileName + ".key");
            out.write("-----BEGIN RSA PRIVATE KEY-----\n");
            out.write(Base64.encodeToString(pvt, Base64.DEFAULT));
            System.out.println("Private key: " + Base64.encodeToString(pvt, Base64.DEFAULT));
            out.write("\n-----END RSA PRIVATE KEY-----\n");
            out.close();
        } catch (IOException ioexc) {
            System.out.println("File error!");
        }

        try {
            // Save public key
            Writer out = new FileWriter(fileName + ".pub");
            out.write("-----BEGIN RSA PUBLIC KEY-----\n");
            out.write(Base64.encodeToString(pub, Base64.DEFAULT));
            System.out.println("Public key: " + Base64.encodeToString(pub, Base64.DEFAULT));
            out.write("\n-----END RSA PUBLIC KEY-----\n");
            out.close();
        } catch (IOException ioexc) {
            System.out.println("File error!");
        }
        */
        System.out.println("Private key: " + Base64.encodeToString(pvt, Base64.DEFAULT));
        System.out.println("Public key: " + Base64.encodeToString(pub, Base64.DEFAULT));
    }

    // Asymmetric (RSA) cryptography helpers

    // Get keys from file
    public static PrivateKey getPrivate(String fileName) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(fileName).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey getPublic(String fileName) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(fileName).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    // Get keys from Strings in Base64 encoding
    public static PrivateKey getPrivateFromString(String pvt) throws Exception {
        byte[] keyBytes = Base64.decode(pvt, Base64.DEFAULT);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey getPublicFromString(String pub) throws Exception {
        byte[] keyBytes = Base64.decode(pub, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static String encryptText(String msg, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
            UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeToString(cipher.doFinal(msg.getBytes("UTF-8")), Base64.DEFAULT);

    }

    public static String decryptText(String msg, Key key) throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decode(msg, Base64.DEFAULT)), "UTF-8");

    }

    // Signatures SHA-256 with RSA

    // Generate signature of a message
    public static byte[] sign(String message, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException{
        Signature sign = Signature.getInstance("SHA256withRSA");

        sign.initSign(privateKey);
        byte[] messageBytes = message.getBytes();
        sign.update(messageBytes);
        
        byte[] signature = sign.sign();
        return signature;
        // return byteToHex(signature);
        
    }

    public static String signBase64(String message, PrivateKey privateKey){
        try{
            byte[] signatureBytes = sign(message, privateKey);
            return Base64.encodeToString(signatureBytes, Base64.DEFAULT);
        }
        catch(NoSuchAlgorithmException e){
            return "Error";
        }
        catch(InvalidKeyException e){
            return "Error";
        }
        catch(SignatureException e){
            return "Error";
        }
    }

    public static boolean signVerify(String message, byte[] signature, PublicKey publicKey) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
        Signature sign = Signature.getInstance("SHA256withRSA");

        sign.initVerify(publicKey);

        byte[] messageBytes = message.getBytes();
        sign.update(messageBytes);

        //byte[] signatureBytes = signature.getBytes();
        return sign.verify(signature);

    }

    public static boolean signVerifyBase64(String message, String signature, PublicKey publicKey){
        byte[] byteSignature = Base64.decode(signature, Base64.DEFAULT);
        try{
            return signVerify(message, byteSignature, publicKey);
        }
        catch(InvalidKeyException e){
            e.printStackTrace();
            return false;
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return false;
        }
        catch(SignatureException e){
            e.printStackTrace();
            return false;
        }
    }


}