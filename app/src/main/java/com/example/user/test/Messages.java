package com.example.user.test;

// ProverRequest

class ProverRequest {

    private String commitment; // The committed identity
    private String location; // Location with best accuracy
    private String seed; // K0 random number
    private long timestamp; // Current time
    private String CAPublicKey; // CAs public key

    // Constructor
    public ProverRequest(String commitment, String location, String seed,
                         long timestamp, String CaPublicKey) {

        this.commitment = commitment;
        this.location = location;
        this.seed = seed;
        this.timestamp = timestamp;
        this.CAPublicKey = CaPublicKey;
    }

    // Getters
    public String getCommitment(){
        return this.commitment;
    }

    public String getLocation(){
        return this.location;
    }

    public String getSeed(){
        return this.seed;
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public String getCaPublicKey(){
        return this.CAPublicKey;
    }

    // Setters
    public void setCommitment(String commitment){
        this.commitment = commitment;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setSeed(String seed){
        this.seed = seed;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    public void setCApublicKey(String CAPublicKey){
        this.CAPublicKey = CAPublicKey;
    }

}

// WitnessPresence

class WitnessPresence {

    private String commitment;
    private int nonce;

    // Constructor
    public WitnessPresence(String commitment, int nonce){

        this.commitment = commitment;
        this.nonce = nonce;
    }

    // Getters
    public String getCommitment(){
        return this.commitment;
    }

    public int getNonce(){
        return this.nonce;
    }

    // Setters
    public void setCommitment(String commitment){
        this.commitment = commitment;
    }

    public void setNonce(int nonce){
        this.nonce = nonce;
    }
}

// Start

class Start {

    private String commitment; // Prover committed identity
    private String locationChain; // Kn head of location chain
    private long timestamp; // Current time
    private String frameHash; // Hash of first frame

    // Constructor
    public Start(String commitment, String locationChain, long timestamp, String frameHash){
        this.commitment = commitment;
        this.locationChain = locationChain;
        this.timestamp = timestamp;
        this.frameHash = frameHash;
    }

    // Getters
    public String getCommitment(){
        return this.commitment;
    }

    public String getLocationChain(){
        return this.locationChain;
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public String getFrameHash(){
        return this.frameHash;
    }

    // Setters
    public void setCommitment(String commitment){
        this.commitment = commitment;
    }

    public void setLocationChain(String locationChain){
        this.locationChain = locationChain;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    public void setFrameHash(String frameHash){
        this.frameHash = frameHash;
    }
}

// Challenge

class Challenge {

    private String commitment; // Witness committed identity
    private int n1; // first random number
    private int n2; // second random number

    // Constructor
    public Challenge(String commitment, int n1, int n2){

        this.commitment = commitment;
        this.n1 = n1;
        this.n2 = n2;
    }

    // Getters
    public String getCommitment(){
        return this.commitment;
    }

    public int getN1(){
        return this.n1;
    }

    public int getN2(){
        return this.n2;
    }

    // Setters
    public void setCommitment(String commitment){
        this.commitment = commitment;
    }

    public void setN1(int n1){
        this.n1 = n1;
    }

    public void setN2(int n2){
        this.n2 = n2;
    }
}

// Response

class Response {

    private String commitment; // Prover committed identity
    private int n1; // first random number
    private int n2; // second random number

    // Constructor
    public Response(String commitment, int n1, int n2){

        this.commitment = commitment;
        this.n1 = n1;
        this.n2 = n2;
    }

    // Getters
    public String getCommitment(){
        return this.commitment;
    }

    public int getN1(){
        return this.n1;
    }

    public int getN2(){
        return this.n2;
    }

    // Setters
    public void setCommitment(String commitment){
        this.commitment = commitment;
    }

    public void setN1(int n1){
        this.n1 = n1;
    }

    public void setN2(int n2){
        this.n2 = n2;
    }
}


