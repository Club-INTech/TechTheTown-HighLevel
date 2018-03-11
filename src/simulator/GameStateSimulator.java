package simulator;

import container.Service;
import enums.CommunicationHeaders;
import enums.TurningStrategy;
import enums.UnableToMoveReason;
import exceptions.Locomotion.UnableToMoveException;
import org.opencv.core.Mat;
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
    private ThreadSimulator simulator;

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

        long timeRef = System.currentTimeMillis();
        float done = 0;
        Vec2 finalAim = position.plusNewVector(new Vec2(distance, orientation));
        System.out.println(finalAim);
        // Divisé par 100 car move delay en ms, translationSpeed en mm/s et distanceLoop en mm
        float distanceLoop = (float) translationSpeed * moveDelay/(float)1000;

        this.setRobotMoving(true);
        this.setMoveAbnormal(false);

        log.debug(String.format("Move delay : %d, DistancePerDelay : %s", moveDelay, distanceLoop));

        while (done < Math.abs(distance) && !this.isMustStop()) {
            Thread.sleep(moveDelay);
            if (done+distanceLoop>Math.abs(distance)){
                float distanceToAdd=distance-done;
                position.plus(new Vec2(distanceToAdd, orientation));
                done=distance;
            }
            else {
                System.out.println(orientation+" "+done);
                position.plus(new Vec2(distanceLoop, orientation));
                done += distanceLoop;
            }
            simulator.communicate(CommunicationHeaders.POSITION, String.format("%d %d %s", position.getX(), position.getY(), orientation));

            if(table.getObstacleManager().isObstructed(position) || !table.getObstacleManager().isRobotInTable(position)){
                log.debug("SIMULATOR : UnableToMoveException / Position : "+position);
                throw new UnableToMoveException(finalAim, UnableToMoveReason.PHYSICALLY_BLOCKED);
            }
        }
        this.setRobotMoving(false);

        if(!this.isMustStop()) {
            position = finalAim;
        }
        log.debug(String.format("Fin du mouvement, position : (%d, %d), temps : %d", position.getX(), position.getY(), (System.currentTimeMillis() - timeRef)));
    }

    /** Update l'orientation du robot */
    public void turn(float orientationAim, TurningStrategy strat) throws InterruptedException, UnableToMoveException{

        float done = 0;
        float angleToTurn = orientationAim - orientation;
        float angleStep = rotationnalSpeed*moveDelay/1000;

        if (strat == TurningStrategy.RIGHT_ONLY && orientation > orientationAim){
            angleToTurn = (float) (2 * Math.PI - Math.abs(angleToTurn));
        }
        else if(strat == TurningStrategy.LEFT_ONLY){
            if(orientation < orientationAim) {
                angleToTurn = (float) (2 * Math.PI - Math.abs(angleToTurn));
            }
            angleToTurn = Math.abs(angleToTurn);
            angleStep = -angleStep;
        }
        else{
            if((Math.abs(angleToTurn) < Math.PI && orientationAim < orientation) || (Math.abs(angleToTurn) > Math.PI && orientationAim > orientation)){
                angleStep = -angleStep;
            }
            angleToTurn = Math.min(Math.abs(angleToTurn), Math.abs((float)(2*Math.PI - Math.abs(angleToTurn))));
        }

        this.setRobotMoving(true);
        this.setMoveAbnormal(false);

        while (done < angleToTurn && !this.isMustStop()){
            Thread.sleep(moveDelay);
            if (done+Math.abs(angleStep)>angleToTurn){
                float angleToAdd=angleToTurn-done;
                done=angleToTurn;
                orientation = (float) Geometry.moduloSpec((double) (orientation + angleToAdd), Math.PI);
            }
            else {
                done+=Math.abs(angleStep);
                orientation = (float) Geometry.moduloSpec((double) (orientation + angleStep), Math.PI);
            }
            simulator.communicate(CommunicationHeaders.POSITION, String.format("%d %d %s", position.getX(), position.getY(), orientation));
        }
        this.setRobotMoving(false);

        if(!this.isMustStop()) {
            orientation = orientationAim;
        }
    }

    /** Getters & Setters */
    public void setSimulator(ThreadSimulator simulator) {
        this.simulator = simulator;
    }

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
