package com.example.user.test;

public class ProtocolConfigTest {

    public static void main(String[] args){
        ParseJSON parse = new ParseJSON();

        ProtocolConfig prconf = new ProtocolConfig();
        prconf.setIdentity("ideiq");
        prconf.setPrivateKey("privajoduqhnd");
        prconf.setPublicKey("pubjosaj");
        prconf.setCaPublicKey("josdijqij");


        //String json = "{\"identity\":\"myID\",\"privateKey\":\"qdoiwonijqnw0\",\"publicKey\":\"3d9uhbe99e8dn9\",\"caPublicKey\":\"8ujmdw09nus0\"}";
        String json = parse.toJSON(prconf);
        
        ProtocolConfig pc = parse.fromJSON(json);
        System.out.println(pc.getIdentity());

        WitnessPresence wp = new WitnessPresence();
        wp.setCommitment("TEST");
        wp.setNonce(128);

        String wpjson = parse.toJSON(wp);

        try {
            WitnessPresence wp2 = (WitnessPresence) parse.fromJSON(wpjson, WitnessPresence.class);
            System.out.println(wp.getCommitment());
            System.out.println(wp.getNonce());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
