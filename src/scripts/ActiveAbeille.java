package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import hook.HookFactory;
import hook.HookNames;
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
    private int xEntry2; //Position d'entrée version 2
    private int yEntry2;
    private boolean usingBasicDetection;
    private boolean usingAdvancedDetection;

    /** Eléments appelés par la config */
    private int radius; //rayon du robot

    public ActiveAbeille(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        updateConfig();
        this.xEntryReal=1280;
        this.yEntryReal=1780;
        this.xEntryPathfindingAvaible=1200;
        this.yEntryPathfindingAvaible=1700;
        this.xEntry2=780;
        this.yEntry2=1180;
        versions = new int[]{0,1};
    }
    @Override
    public void updateConfig() {
        super.updateConfig();
        radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        usingBasicDetection =config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
        usingAdvancedDetection =config.getBoolean(ConfigInfoRobot.ADVANCED_DETECTION);
    }

    @Override
    public void execute(int versionToExecute, GameState state) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution ActiveAbeille version " + versionToExecute + " //////////");
        Vec2 corner = new Vec2(1500, 2000);
        Vec2 directionToGo = (corner.minusNewVector(state.robot.getPosition()));
        double prodScal = directionToGo.dot(new Vec2(100.0, state.robot.getOrientation()));
        if(versionToExecute==0 || versionToExecute==2){
            if (prodScal > 0) {
                //ON UTILISE LE BRAS AVANT
                //On disable le hook pour le bras arrière
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
                //On enable le kook pour le bras avant
                hookFactory.enableHook(HookNames.BASIC_DETECTION_DISABLE,HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
                //On va vers l'abeille
                state.robot.goTo(new Vec2(xEntryReal, yEntryReal));

                //On se tourne pour pousser l'abeille avec le bras avant
                state.robot.turn(Math.PI/2,true);
                //On relève le bras avant
                state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, false);
                //On disable le hook du bras avant
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);

            } else {
                //ON UTILISE LE BRAS AVANT
                //On disable le hook pour le bras arrière
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
                //On enable le kook pour le bras avant
                hookFactory.enableHook(HookNames.BASIC_DETECTION_DISABLE,HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
                //On va vers l'abeille
                state.robot.goTo(new Vec2(xEntryReal, yEntryReal));

                //On se tourne pour pousser l'abeille avec le bras avant
                state.robot.turn(Math.PI/2,true);
                //On relève le bras avant
                state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, false);
                //On disable le hook du bras avant
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
            }
            if(usingBasicDetection){
                state.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
            }
            Vec2 aim = new Vec2(xEntryPathfindingAvaible,yEntryPathfindingAvaible);
            state.robot.goTo(aim);
            log.debug("////////// End ActiveAbeille version "+versionToExecute+" //////////");
        }
        if(versionToExecute==1) {
            //On vérifie quel bras de l'abeille on va devoir utiliser, à l'aide d'un produit scalaire
            if (usingAdvancedDetection) {
                state.robot.useActuator(ActuatorOrder.SUS_OFF, true);
                state.setCapteursActivated(false);
            }
            if (usingBasicDetection) {
                state.robot.setBasicDetection(false);
            }

            if (prodScal > 0) {
                //ON UTILISE LE BRAS AVANT
                state.robot.useActuator(ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE, false);
                state.robot.goToWithoutDetection(new Vec2(xEntryReal, yEntryReal));
                state.robot.turnWithoutDetection(Math.PI / 2, true, false);
                state.addObtainedPoints(50);
                state.setAbeilleLancee(true);
                state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, false);

            } else {
                //ON UTILISE LE BRAS ARRIERE
                state.robot.useActuator(ActuatorOrder.ACTIVE_BRAS_ARRIERE_POUR_ABEILLE, false);
                state.robot.goToWithoutDetection(new Vec2(xEntryReal, yEntryReal));
                state.robot.turnWithoutDetection(-Math.PI / 2, true, false);
                state.addObtainedPoints(50);
                state.setAbeilleLancee(true);
                state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, false);
            }
            //On retourne à une position atteignable par le pathfinding
            Vec2 aim = new Vec2(xEntryPathfindingAvaible, yEntryPathfindingAvaible);
            state.robot.goTo(aim);
            if (usingAdvancedDetection) {
                state.robot.useActuator(ActuatorOrder.SUS_ON, true);
                state.setCapteursActivated(true);
            }
            if (usingBasicDetection) {
                state.robot.setBasicDetection(true);
            }
            log.debug("////////// End ActiveAbeille version " + versionToExecute + " //////////");
        }
    }


    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        log.debug("Position d'entrée ActiveAbeille : "+robotPosition);
        if(version==0){
            return new Circle(robotPosition);
        }
        else if(version==1) {
            return new Circle(new Vec2(xEntryPathfindingAvaible, yEntryPathfindingAvaible));
        }
        else if (version==2){
            return new Circle(new Vec2(xEntry2,yEntry2));
        }
        else{
            throw new BadVersionException();
        }
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {}

    @Override
    public void goToThenExec(int versionToExecute, GameState state) throws PointInObstacleException, BadVersionException, NoPathFound, ExecuteException, BlockedActuatorException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        state.setLastScript(ScriptNames.ACTIVE_ABEILLE);
        state.setLastScriptVersion(versionToExecute);
        super.goToThenExec(versionToExecute, state);
    }


    @Override
    public int[] getVersion(GameState stateToConsider) {
        return versions;
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 50;
    }



}
