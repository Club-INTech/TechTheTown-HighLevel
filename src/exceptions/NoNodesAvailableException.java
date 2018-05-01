package exceptions;

public class NoNodesAvailableException extends Exception{

    private String message;
    public NoNodesAvailableException(){
        this("NoNodesAvailableException");

    }
    public NoNodesAvailableException(String m){
        this.message=m;
    }
}
