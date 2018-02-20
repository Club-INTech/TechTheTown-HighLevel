package exceptions;

public class PatternNotRecognizedException extends Exception{

    public PatternNotRecognizedException()
    {
        super();
    }

    public PatternNotRecognizedException(String m)
    {
        super(m);
    }
}
