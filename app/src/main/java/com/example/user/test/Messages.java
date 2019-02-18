package com.example.user.test;

// ProverRequest

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"commitment", "location", "k0", "ca", "timestamp"})
class ProverRequest {

    private String commitment; // The committed identity
    private String location; // Location with best accuracy
    private String k0; // k0 random number
    private String ca;
    private long timestamp; // Current time

    // Getters
    public String getCommitment(){
        return this.commitment;
    }

    public String getLocation(){
        return this.location;
    }

    public String getk0(){
        return this.k0;
    }

    public String getCa(){
        return this.ca;
    }

    public long getTimestamp(){
        return this.timestamp;
    }


    // Setters
    public void setCommitment(String commitment){
        this.commitment = commitment;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setk0(String k0){
        this.k0 = k0;
    }

    public void setCa(String ca){
        this.ca = ca;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

}

// WitnessPresence
@JsonPropertyOrder({"commitment", "nonce"})
class WitnessPresence {

    private String commitment;
    private int nonce;

    // Constructor
    /*
    public WitnessPresence(String commitment, int nonce){

        this.commitment = commitment;
        this.nonce = nonce;
    }
    */

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
@JsonPropertyOrder({"commitment", "locationChain", "timestamp", "frameHash"})
class Start {

    private String commitment; // Prover committed identity
    private String locationChain; // Kn head of location chain
    private long timestamp; // Current time
    private String frameHash; // Hash of first frame

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
@JsonPropertyOrder({"commitment", "n1", "n2"})
class Challenge {

    private String commitment; // Witness committed identity
    private int n1; // first random number
    private int n2; // second random number

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
@JsonPropertyOrder({"commitment", "n1", "n2"})
class Response {

    private String commitment; // Prover committed identity
    private int n1; // first random number
    private int n2; // second random number

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
@JsonPropertyOrder({"commitment", "hash"})
class VideoHash{
    private String commitment;
    private String hash;

    // Getters
    public String getCommitment() {
        return commitment;
    }

    public String getHash() {
        return hash;
    }

    // Setters

    public void setCommitment(String commitment) {
        this.commitment = commitment;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}

@JsonPropertyOrder({"stHash", "chal", "res", "rw", "chSign", "vH", "acc"})
class Lps{
    private String stHash; // The hash of the start message
    private String chal; // Challenge
    private String res; // Response
    private String rw; // Random number for commitment
    private String chSign; // Challenge signature
    private String vH; // videoHash received from prover
    private boolean acc; // Whether accepted or not

    // Getters
    public String getStHash() {
        return stHash;
    }

    public String getChal() {
        return chal;
    }

    public String getRes() {
        return res;
    }

    public String getRw() {
        return rw;
    }

    public String getChSign() {
        return chSign;
    }

    public String getvH() {
        return vH;
    }

    public boolean isAcc() {
        return acc;
    }

    // Setters
    public void setStHash(String stHash) {
        this.stHash = stHash;
    }

    public void setChal(String chal) {
        this.chal = chal;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }

    public void setChSign(String chSign) {
        this.chSign = chSign;
    }

    public void setvH(String vH) {
        this.vH = vH;
    }

    public void setAns(boolean ans) {
        this.acc = acc;
    }
}

@JsonPropertyOrder({"discovered", "sentLps"})
class ListMessage{
    private List<String> discovered; // Discovered witnesses
    private List<String> sentLps; // Witnesses that sent LPS

    // Getters
    public List<String> getDiscovered() {
        return discovered;
    }

    public List<String> getSentLps() {
        return sentLps;
    }

    // Setters
    public void setDiscovered(List<String> discovered) {
        this.discovered = discovered;
    }

    public void setSentLps(List<String> sentLps) {
        this.sentLps = sentLps;
    }
}

@JsonPropertyOrder({"commitment", "type", "data"})
class EncryptedMessage{
    private String commitment;
    private int type; // 0 = LPS, 1 = LIST
    private String data; // The encrypted message (LPS or LIST)

    // Getters
    public String getCommitment() {
        return commitment;
    }

    public int getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    // Setters
    public void setCommitment(String commitment) {
        this.commitment = commitment;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }
}

@JsonPropertyOrder({"data", "hs"})
class SignHash{

    private String data; // The data of the signed hash message
    private String hs; // The hashed signature of the data

    // Getters
    public String getData() {
        return data;
    }

    public String getHs() {
        return hs;
    }

    // Setters

    public void setData(String data) {
        this.data = data;
    }

    public void setHs(String hs) {
        this.hs = hs;
    }
}

@JsonPropertyOrder({"identity", "privateKey", "publicKey", "caPublicKey", "timeout", "retryCount"})
class ProtocolConfig{

    private String identity;
    private String privateKey;
    private String publicKey;
    private String caPublicKey;
    private int timeout;
    private int retryCount;


    // Getters
    public String getIdentity(){
        return this.identity;
    }

    public String getPrivateKey(){
        return this.privateKey;
    }

    public String getPublicKey(){
        return this.publicKey;
    }

    public String getCaPublicKey(){
        return this.caPublicKey;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    // Setters
    public void setIdentity(String identity){
        this.identity = identity;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setCaPublicKey(String caPublicKey) {
        this.caPublicKey = caPublicKey;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}