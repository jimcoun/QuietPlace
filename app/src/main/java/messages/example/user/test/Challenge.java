package messages.example.user.test;

public class Challenge {

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
