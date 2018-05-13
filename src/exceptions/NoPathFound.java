package exceptions;

import smartMath.Vect;

/**
 * Exception levée qu'un noeud est dans un obstacle.
 */

public class NoPathFound extends Exception {

    private String message;
    private Vect aim;

    public NoPathFound(final Vect aim) {
        this(aim,"NoPathFound");
    }

    public NoPathFound(final Vect aim, final String m) {
        this.aim=aim;
        this.message=m;
    }

    public String getMessage(){
        return this.message;
    }
    public Vect getAim(){
        return this.aim;
    }
}
