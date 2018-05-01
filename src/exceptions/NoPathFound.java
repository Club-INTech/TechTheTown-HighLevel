package exceptions;

import smartMath.Vec2;

/**
 * Exception levée qu'un noeud est dans un obstacle.
 */

public class NoPathFound extends Exception {

    private String message;
    private Vec2 aim;

    public NoPathFound(final Vec2 aim) {
        this(aim,"NoPathFound");
    }

    public NoPathFound(final Vec2 aim, final String m) {
        this.aim=aim;
        this.message=m;
    }

    public String getMessage(){
        return this.message;
    }
    public Vec2 getAim(){
        return this.aim;
    }
}
