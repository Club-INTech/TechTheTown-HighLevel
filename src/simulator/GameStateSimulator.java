package simulator;

import container.Service;
import enums.TurningStrategy;
import enums.UnableToMoveReason;
import exceptions.Locomotion.UnableToMoveException;
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
    private Vec2 position;

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
        this.position = new Vec2(0,0);
    }

    /** Update la position du robot */
    public void moveLengthwise(float distance) throws InterruptedException, UnableToMoveException{

        int done = 0;
        Vec2 finalAim = position.plusNewVector(new Vec2(distance, orientation));
        // Divisé par 100 car move delay en ms, translationSpeed en mm/s et distanceLoop en mm
        float distanceLoop = (float) translationSpeed * moveDelay/(float)1000;

        this.setRobotMoving(true);
        this.setMoveAbnormal(false);

        while (done < Math.abs(distance) && !this.isMustStop()) {
            Thread.sleep(moveDelay);
            position.plus(new Vec2(distanceLoop, orientation));
            done += distanceLoop;

            if(table.getObstacleManager().isObstructed(position) || !table.getObstacleManager().isRobotInTable(position)){
                throw new UnableToMoveException(finalAim, UnableToMoveReason.PHYSICALLY_BLOCKED);
            }
        }
        this.setRobotMoving(false);

        if(!this.isMustStop()) {
            position = finalAim;
        }

    }

    /** Update l'orientation du robot */
    public void turn(float orientationAim, TurningStrategy strat) throws InterruptedException, UnableToMoveException{

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

        while (done < angleToTurn && !this.isMustStop()){
            Thread.sleep(moveDelay);
            orientation = (float) Geometry.moduloSpec((double)(orientation + angleStep), Math.PI);
            done+=Math.abs(angleStep);
        }
        this.setRobotMoving(false);

        if(!this.isMustStop()) {
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
