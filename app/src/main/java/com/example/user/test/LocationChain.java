package com.example.user.test;

import java.security.SecureRandom;
import static com.example.user.test.Crypto.sha256;


public class LocationChain {

    // MAIN for tests

    public static void main(String[] args) {
        String K0 = randomGenerator();
        System.out.printf("K0 = %s%n", K0);

        String L0 = "8G95XQHMM6";
        System.out.printf("L0 = %s%n", L0);

        String[] Kchain = buildChain(K0, L0);
        
        for (String Ki : Kchain)
            System.out.printf("%s%n", Ki);

        System.out.printf("Calling checkChain with: %n%s%n%s%n%s%n", Kchain[2], "8G95XQ", Kchain[5]);
        boolean result = checkChain(Kchain[2], "8G95XQ", Kchain[5]);
        System.out.println(result);

    }

    // Method that returns random number K0

    public static String randomGenerator() {
        SecureRandom random = new SecureRandom();
        int randomInteger = random.nextInt(1000000);
        String result = Integer.toHexString(randomInteger);
        return result;
    }

    // Method that creates the chain head K given L0, K0

    public static String[] buildChain(String K0, String L0) {

        // !!! Need to check the validity of L0 using plus codes library

        String currentL = L0;
        String[] Kchain = new String[6];
        Kchain[0] = K0;
        
        // Convert to for --------------

        int i = 1;
        while (currentL.length() >= 2) {
            String conc = Kchain[i-1].concat(currentL);
            Kchain[i] = sha256(conc);
            currentL = currentL.substring(0, currentL.length() - 2);
            System.out.println(currentL);

            i++;
        }

        return Kchain;
    }

    // Method that returns true if chain is valid, false if not
    public static boolean checkChain(String Ki, String Li, String K) {
        String currentK = Ki;
        String currentL = Li;

        while (currentL.length()>=2){
            String conc = currentK.concat(currentL);
            currentK = sha256(conc);
            currentL = currentL.substring(0, currentL.length() - 2);
            System.out.println(currentK);

        }
        System.out.printf("checkChain calculated the head of the chain: %s%n", currentK);

        if (currentK.equals(K))
            return true;
        else
            return false;

    }

}