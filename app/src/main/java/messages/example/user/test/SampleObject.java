package messages.example.user.test;

public class SampleObject
{
    private String commitment;
    private String location;
    private String seed; // K0 random number
    private long timestamp;
    private String CaPublicKey;


    public SampleObject(String commitment, String location, String seed,
                        long timestamp, String CaPublicKey) {


        this.commitment = commitment;
        this.location = location;
        this.seed = seed;
        this.timestamp = timestamp;
        this.CaPublicKey = CaPublicKey;
    }

}
