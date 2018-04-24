package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import hook.HookNames;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class ActiveAbeille extends AbstractScript {

    /** Active l'abeille */
    private int securityDistance; //distance de sécurité pour ne pas cogner le mur en tournant
    private int xEntry; //position d'entrée permettant de toucher l'abeille, on y va en goto
    private int yEntry;
    private int xExit; //position de sortie permettant au pathfinding d'être lancé
    private int yExit;
    private boolean basicDetection;

    /** Eléments appelés par la config */
    private int radius; //rayon du robot
    private int distanceAbeille;

    public ActiveAbeille(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        updateConfig();
        this.securityDistance=60;
        this.xEntry=1300;
        this.yEntry=1733;
        this.xExit=1500-radius-securityDistance;
        this.yExit=2000-radius-securityDistance;
        /**2 versions, l'une pour l'IA(si jamais on a à appeler le pathfinding pour
         * aller à l'abeille) et l'autre pour le MatchScript*/
        versions = new Integer[]{0,1};
    }
    @Override
    public void updateConfig() {
        super.updateConfig();
        distanceAbeille = config.getInt(ConfigInfoRobot.DISTANCE_ABEILLE);
        radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        basicDetection=config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
    }

    @Override
    public void execute(int versionToExecute, GameState actualState) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution ActiveAbeille version "+versionToExecute+" //////////");
        Vec2 corner = new Vec2(1500, 2000);
        Vec2 directionToGo = (corner.minusNewVector(actualState.robot.getPosition()));
        double prodScal = directionToGo.dot(new Vec2(100.0, actualState.robot.getOrientation()));
        if(versionToExecute==0) {
            //On vérifie quel bras de l'abeille on va devoir utiliser, à l'aide d'un produit scalaire
            if (prodScal > 0) {
                //ON UTILISE LE BRAS AVANT
                //On disable le hook pour le bras arrière et la basicDetection pour ne pas détecter notre bras
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE,HookNames.BASIC_DETECTION_DISABLE);
                //On enable le hook pour le bras avant
                hookFactory.enableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
                actualState.robot.goTo(new Vec2(xEntry, yEntry));
                /*
                On se tourne pour pousser l'abeille avec le bras arrière : on est en train d'exécuter
                le script de l'abeille, on est au bord de la table, ça ne sert plus à rien de détecter:
                on est sûr de ne pas recevoir le basicDetectionTriggered vu que le mustDetect est à false
                 */
                actualState.robot.turnWithoutDetection(Math.PI/2,true, false);
                actualState.addObtainedPoints(50);
                //On relève le bras avant
                actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, false);
            } else {
                //ON UTILISE LE BRAS ARRIERE
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE,HookNames.BASIC_DETECTION_DISABLE);
                //On enable le kook pour le bras arrière
                hookFactory.enableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
                //On va vers l'abeille : on y va en détectant vu que l'accès est scripté
                actualState.robot.goTo(new Vec2(xEntry, yEntry));
                actualState.robot.turnWithoutDetection(-Math.PI/2,true, false);
                actualState.addObtainedPoints(50);
                //On relève le bras arrière
                actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, false);
            }
            //On retourne à une position atteignable par le pathfinding
            Vec2 aim = new Vec2(xExit, yExit);
            actualState.robot.goTo(aim);
            if(basicDetection){
                actualState.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
            } else {
                actualState.robot.useActuator(ActuatorOrder.SUS_ON,true);
                actualState.setCapteursActivés(true);
            }
            actualState.setAbeilleLancee(true);
            log.debug("////////// End ActiveAbeille version " + versionToExecute + " //////////");
        }
        /**On arrive à la position d'entrée,le hook to enable est enabled au niveau de l'IA
         *  on avance un petit peu
         * et on fait la même suite d'actions*/
        else if(versionToExecute==1){
            if(prodScal>0) {
                actualState.robot.turn(Math.PI/4);
                actualState.robot.moveLengthwise(distanceAbeille);
                //Le hook est normalement activé par l'IA
                actualState.robot.turn(Math.PI/2);
                actualState.addObtainedPoints(50);
                actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
                actualState.robot.goTo(new Vec2(xExit,yExit));
            }
           else{
                actualState.robot.turn(5*Math.PI/4);
                actualState.robot.moveLengthwise(-distanceAbeille);
                //Le hook est normalement activé par l'IA
                actualState.robot.turn(-Math.PI/2);
                actualState.addObtainedPoints(50);
                actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
                actualState.robot.goTo(new Vec2(xExit,yExit));
            }
        }
    }

        @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        log.debug("Position d'entrée ActiveAbeille : "+robotPosition);
        /**accès scripté*/
        if(version==0){
            return new Circle(robotPosition);
        }
        /**accès avec le pathfinding*/
        else if(version ==1){
            return new Circle(new Vec2(xExit,yExit));
        }
        else{
            throw new BadVersionException();
        }
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {}


    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[0];
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 50;
    }



}
