package com.example.user.test;

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

import java.util.Base64;

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
            PrivateKey privateKey = getPrivate("kleidib.key");
            PublicKey publicKey = getPublic("kleidib.pub");

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
        Base64.Encoder encoder = Base64.getEncoder();

        try {
            Writer out = new FileWriter(fileName + ".key");
            out.write("-----BEGIN RSA PRIVATE KEY-----\n");
            out.write(encoder.encodeToString(pvt));
            out.write("\n-----END RSA PRIVATE KEY-----\n");
            out.close();
        } catch (IOException ioexc) {
            System.out.println("File error!");
        }

        try {
            // Save public key
            Writer out = new FileWriter(fileName + ".pub");
            out.write("-----BEGIN RSA PUBLIC KEY-----\n");
            out.write(encoder.encodeToString(pub));
            out.write("\n-----END RSA PUBLIC KEY-----\n");
            out.close();
        } catch (IOException ioexc) {
            System.out.println("File error!");
        }
    }

    // Asymmetric (RSA) cryptography helpers

    public static PrivateKey getPrivate(String fileΝame) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(fileΝame).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey getPublic(String fileΝame) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(fileΝame).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static String encryptText(String msg, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
            UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        Base64.Encoder encoder = Base64.getEncoder(); // Create Base64 encoder
        // return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
        return encoder.encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));

    }

    public static String decryptText(String msg, Key key) throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        Base64.Decoder decoder = Base64.getDecoder(); // Create Base64 decoder
        // return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
        return new String(cipher.doFinal(decoder.decode(msg)), "UTF-8");
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

    public static boolean signVerify(String message, byte[] signature, PublicKey publicKey) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
        Signature sign = Signature.getInstance("SHA256withRSA");

        sign.initVerify(publicKey);

        byte[] messageBytes = message.getBytes();
        sign.update(messageBytes);

        //byte[] signatureBytes = signature.getBytes();
        return sign.verify(signature);

    }


}