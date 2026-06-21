package demo1;

public class PosIllegal extends RuntimeException {
    public PosIllegal(){
        super();
    }
    public PosIllegal(String message) {
        super(message);
    }
}
