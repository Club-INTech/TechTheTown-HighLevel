package hook;

import enums.MotionOrder;
import enums.Speed;
import smartMath.Vec2;

/**
 * Contient le nom des hooks et leurs paramètres associés
 * ATTENTION à ne pas mettre deux hooks avec le meme id !!
 */
public enum HookNames {

    SPEED_DOWN("01", new Vec2(50, 50), 5, Speed.SLOW_ALL)
    ;

    /** Ordre du hook */
    private MotionOrder order;

    /** Position de trigger du hook */
    private Vec2 position;

    /** Tolérence sur la position */
    private int tolerency;

    /** Id du hook, utile pour pouvoir l'activer/désactivé manuellement*/
    private String id;

    /** Constructeur */
    HookNames(String id, Vec2 position, int tolerency, MotionOrder order){
        this.id = id;
        this.position = position;
        this.tolerency = tolerency;
        this.order = order;
    }

    /** Getters & Setters */
    public MotionOrder getOrder() {
        return order;
    }
    public Vec2 getPosition() {
        return position;
    }
    public int getTolerency(){
        return tolerency;
    }
    public String getId() {
        return id;
    }
}
