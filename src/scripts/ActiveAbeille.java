package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class ActiveAbeille extends AbstractScript {

    /** Active l'abeille */
    private int xEntryReal; //position d'entrée permettant de toucher l'abeille, on y va en goto
    private int yEntryReal;
    private int xEntryPathfindingAvaible; //position de sortie permettant au pathfinding d'être lancé
    private int yEntryPathfindingAvaible;
    private boolean basicDetection;
    private boolean usingIA;

    /** Eléments appelés par la config */
    private int radius; //rayon du robot

    public ActiveAbeille(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        updateConfig();
        this.xEntryReal=1280;
        this.yEntryReal=1780;
        this.xEntryPathfindingAvaible=1200;
        this.yEntryPathfindingAvaible=1700;
        versions = new int[]{0};
    }
    @Override
    public void updateConfig() {
        super.updateConfig();
        radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        basicDetection=config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
        usingIA=config.getBoolean(ConfigInfoRobot.USING_IA);
    }

    @Override
    public void execute(int versionToExecute, GameState state) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution ActiveAbeille version "+versionToExecute+" //////////");
        Vec2 corner = new Vec2(1500, 2000);
        Vec2 directionToGo = (corner.minusNewVector(state.robot.getPosition()));
        double prodScal = directionToGo.dot(new Vec2(100.0, state.robot.getOrientation()));
        //On vérifie quel bras de l'abeille on va devoir utiliser, à l'aide d'un produit scalaire
        if (usingIA) {
            state.robot.useActuator(ActuatorOrder.SUS_OFF,true);
            state.setCapteursActivated(false);
        }
        if(basicDetection){
            state.robot.useActuator(ActuatorOrder.BASIC_DETECTION_DISABLE,true);
        }
        state.robot.goToWithoutDetection(new Vec2(xEntryReal, yEntryReal));
        if (prodScal > 0) {
            //ON UTILISE LE BRAS AVANT
            state.robot.useActuator(ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE, true);
            state.robot.turnWithoutDetection(Math.PI/2,true, false);
            state.addObtainedPoints(50);
            state.setAbeilleLancee(true);
            state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, false);

        } else {
            //ON UTILISE LE BRAS ARRIERE
            state.robot.useActuator(ActuatorOrder.ACTIVE_BRAS_ARRIERE_POUR_ABEILLE, true);
            state.robot.turnWithoutDetection(-Math.PI/2,true, false);
            state.addObtainedPoints(50);
            state.setAbeilleLancee(true);
            state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, false);
        }
        //On retourne à une position atteignable par le pathfinding
        Vec2 aim = new Vec2(xEntryPathfindingAvaible, yEntryPathfindingAvaible);
        state.robot.goTo(aim);
        if (usingIA) {
            state.robot.useActuator(ActuatorOrder.SUS_ON,true);
            state.setCapteursActivated(true);
        }
        if(basicDetection){
            state.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
        }
        log.debug("////////// End ActiveAbeille version " + versionToExecute + " //////////");
    }


    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        log.debug("Position d'entrée ActiveAbeille : "+robotPosition);
        return new Circle(new Vec2(xEntryPathfindingAvaible, yEntryPathfindingAvaible));
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {}


    @Override
    public int[] getVersion(GameState stateToConsider) {
        return versions;
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 50;
    }



}
