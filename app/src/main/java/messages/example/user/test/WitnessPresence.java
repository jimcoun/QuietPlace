package messages.example.user.test;

public class WitnessPresence {

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
