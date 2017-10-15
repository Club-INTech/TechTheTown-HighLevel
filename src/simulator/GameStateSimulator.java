package simulator;

import container.Service;
import enums.TurningStrategy;
import pfg.config.Config;
import smartMath.Geometry;
import smartMath.Vec2;
import table.Table;
import utils.Log;

/**
 * GameState du LL, destiné à rassembler et gérer les données que le LL est censé avoir
 * ATTENTION : Utiliser uniquement par le simulateur
 */
public class GameStateSimulator implements Service {

    /** La Table du simulateur (Qu'on instanci de manière auto dans cette classe)
     * On n'utilise pas la container : le simulateur a sa propre table ! */
    private Table table;

    /** Position du robot */
    private Vec2 position = new Vec2(0,0);

    /** Orientation du robot */
    private float orientation = 0;

    /** Vitesse du robot */
    private int translationSpeed = 500;
    private float rotationnalSpeed = (float) (2*Math.PI/3);

    /** Dimensions du robot */
    private int robotRay;
    private int robotWidth;
    private int robotLength;

    /** True si le robot doit s'arreter */
    private boolean mustStop;

    /** Le premier booléen indique si le robot bouge, le second si cela est attendue ou non */
    private boolean isRobotMoving;
    private boolean isMoveAbnormal;

    /** Temps à attendre entre chaque loop d'update de la position (en ms) */
    private final int moveDelay = 50;

    /** La config et le log, qui sont les seuls champs partagé avec le HL */
    private Config config;
    private Log log;

    /**
     * Constructeur du coeur du simulateur !
     * @param config
     * @param log
     */
    public GameStateSimulator(Config config, Log log){
        this.config = config;
        this.log = log;
        table = new Table(log, config);
    }

    /** Update la position du robot */
    public void moveLengthwise(float distance) throws InterruptedException{

        int done = 0;
        Vec2 finalAim = position.plusNewVector(new Vec2(distance, orientation));
        float distanceLoop = (float) translationSpeed * moveDelay/(float)1000;

        this.setRobotMoving(true);
        this.setMoveAbnormal(false);

        while (done < Math.abs(distance) && !mustStop) {
            Thread.sleep(moveDelay);
            position.plus(new Vec2(distanceLoop, orientation));
            done += distanceLoop;
        }
        this.setRobotMoving(false);

        if(!mustStop) {
            position = finalAim;
        }
    }

    /** Update l'orientation du robot */
    public void turn(float orientationAim, TurningStrategy strat) throws InterruptedException{

        float done = 0;
        float angleToTurn = orientationAim - orientation;
        float angleStep = rotationnalSpeed*moveDelay/1000;

        if (strat == TurningStrategy.RIGHT_ONLY && orientation > orientationAim){
            angleToTurn = (float) (2 * Math.PI - Math.abs(angleToTurn));
        }else if(strat == TurningStrategy.LEFT_ONLY){
            if(orientation < orientationAim) {
                angleToTurn = (float) (2 * Math.PI - Math.abs(angleToTurn));
            }
            angleToTurn = Math.abs(angleToTurn);
            angleStep = -angleStep;
        }else{
            if((Math.abs(angleToTurn) < Math.PI && orientationAim < orientation) || (Math.abs(angleToTurn) > Math.PI && orientationAim > orientation)){
                angleStep = -angleStep;
            }
            angleToTurn = Math.min(Math.abs(angleToTurn), Math.abs((float)(2*Math.PI - Math.abs(angleToTurn))));
        }

        this.setRobotMoving(true);
        this.setMoveAbnormal(false);

        while (done < angleToTurn && !mustStop){
            Thread.sleep(moveDelay);
            orientation = (float) Geometry.moduloSpec((double)(orientation + angleStep), Math.PI);
            done+=Math.abs(angleStep);
        }
        this.setRobotMoving(false);

        if(!mustStop) {
            orientation = orientationAim;
        }
    }

    /** Getters & Setters */
    /** Position */
    public synchronized Vec2 getPosition() {
        return position;
    }
    public synchronized float getOrientation() {
        return orientation;
    }
    public synchronized void setPosition(Vec2 position) {
        this.position = position;
    }
    public synchronized void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    /** Vitesses */
    public void setTranslationSpeed(int translationSpeed) {
        this.translationSpeed = translationSpeed;
    }
    public void setRotationnalSpeed(float rotationnalSpeed) {
        this.rotationnalSpeed = rotationnalSpeed;
    }

    /** Motion Infos */
    public synchronized boolean isMustStop() {
        return mustStop;
    }
    public synchronized void setMustStop(boolean mustStop) {
        this.mustStop = mustStop;
    }
    public synchronized int isRobotMoving() {
        return (isRobotMoving) ? 1:0;
    }
    public synchronized void setRobotMoving(boolean robotMoving) {
        isRobotMoving = robotMoving;
    }
    public synchronized int isMoveNormal() {
        return (isMoveAbnormal) ? 1:0;
    }
    public synchronized void setMoveAbnormal(boolean moveAbnormal) {
        isMoveAbnormal = moveAbnormal;
    }

    @Override
    public void updateConfig(){}
}
