package simulator;

import container.Service;
import enums.CommunicationHeaders;
import enums.TurningStrategy;
import pfg.config.Config;
import smartMath.Geometry;
import smartMath.PreciseVec2;
import smartMath.Vect;
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
    private PreciseVec2 position;

    /** Orientation du robot */
    private double orientation = 0;

    /** Vitesse du robot */
    private int translationSpeed = 500;
    private double rotationnalSpeed = (2*Math.PI/3);

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
        this.position = new PreciseVec2(0,0);
    }

    /** Update la position du robot */
    public void moveLengthwise(float distance) throws InterruptedException{

        long timeRef = System.currentTimeMillis();
        double done = 0;
        // Divisé par 100 car move delay en ms, translationSpeed en mm/s et distanceLoop en mm
        double distanceLoop = translationSpeed * moveDelay/(double)1000;
        boolean isMovingBackward=false;
        if (distance<0){
            isMovingBackward=true;
        }
        this.setRobotMoving(true);
        this.setMoveAbnormal(false);

        log.debug(String.format("Move delay : %d, DistancePerDelay : %s", moveDelay, distanceLoop));

        while (done < Math.abs(distance)) {
            Thread.sleep(moveDelay);
            if (done+distanceLoop>Math.abs(distance)){
                double distanceToAdd=Math.abs(distance)-done;
                if (isMovingBackward){
                    distanceToAdd*=-1;
                }
                position.plus(new PreciseVec2(distanceToAdd, orientation));
                done=Math.abs(distance);
            }
            else {
                double distanceToAdd=distanceLoop;
                if (isMovingBackward){
                    distanceToAdd*=-1;
                }
                position.plus(new PreciseVec2(distanceToAdd, orientation));
                done += distanceLoop;
            }
            simulator.communicate(CommunicationHeaders.POSITION, String.format("%d %d %s", Math.round(position.getX()), Math.round(position.getY()), orientation));

            if(table.getObstacleManager().isObstructed(position.toVec2()) || !table.getObstacleManager().isRobotInTable(position.toVec2())){
                log.critical("SIMULATOR : Robot dans un obstacle (theorique) : UnableToMoveException / Position : "+position);
            }
        }
        this.setRobotMoving(false);

        log.debug(String.format("Fin du mouvement, position : (%d, %d), temps : %d", Math.round(position.getX()), Math.round(position.getY()), (System.currentTimeMillis() - timeRef)));
    }

    /** Update l'orientation du robot */
    public void turn(float orientationAim, TurningStrategy strat) throws InterruptedException{

        double done = 0;
        double angleToTurn = orientationAim - orientation;
        double angleStep = rotationnalSpeed*moveDelay/1000;
        boolean angleStepNegative=false;

        if (strat == TurningStrategy.RIGHT_ONLY && orientation > orientationAim){
            //TODO : tester avec cette stratégie
            angleToTurn = (2 * Math.PI - Math.abs(angleToTurn));
        }
        else if(strat == TurningStrategy.LEFT_ONLY){
            //TODO : tester avec cette stratégie
            if(orientation < orientationAim) {
                angleToTurn = (2 * Math.PI - Math.abs(angleToTurn));
            }
            angleToTurn = Math.abs(angleToTurn);
            angleStep = -angleStep;
        }
        else{
            if((Math.abs(angleToTurn) < Math.PI && orientationAim < orientation) || (Math.abs(angleToTurn) > Math.PI && orientationAim > orientation)){
                angleStep = -angleStep;
                angleStepNegative=true;
            }
            angleToTurn = Math.min(Math.abs(angleToTurn), Math.abs((2*Math.PI - Math.abs(angleToTurn))));
        }

        this.setRobotMoving(true);
        this.setMoveAbnormal(false);

        while (done < angleToTurn){
            Thread.sleep(moveDelay);
            if (done+Math.abs(angleStep)>angleToTurn){
                double angleToAdd=(angleToTurn-done);
                if (angleStepNegative){
                    angleToAdd*=-1;
                }
                done=angleToTurn;
                orientation = Geometry.moduloSpec((orientation + angleToAdd), Math.PI);
            }
            else {
                done+=Math.abs(angleStep);
                orientation = Geometry.moduloSpec((orientation + angleStep), Math.PI);
            }
            simulator.communicate(CommunicationHeaders.POSITION, String.format("%d %d %s", Math.round(position.getX()), Math.round(position.getY()), orientation));
        }
        this.setRobotMoving(false);

    }

    /** Getters & Setters */
    public void setSimulator(ThreadSimulator simulator) {
        this.simulator = simulator;
    }

    /** Position */
    public synchronized Vect getPosition() {
        return position.toVec2();
    }
    public synchronized float getOrientation() {
        return (float)orientation;
    }
    public synchronized void setPosition(Vect position) {
        this.position = new PreciseVec2(position.getR(), position.getA());
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
