package messages.example.user.test;

public class ProverRequest {

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