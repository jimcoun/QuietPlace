package messages.example.user.test;

public class Start {

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
