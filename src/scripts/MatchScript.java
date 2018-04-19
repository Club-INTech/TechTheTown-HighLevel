package scripts;

import enums.ActuatorOrder;
import enums.BrasUtilise;
import enums.ConfigInfoRobot;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import hook.HookFactory;
import hook.HookNames;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class MatchScript extends AbstractScript {

    private boolean basicDetection;

    public MatchScript(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        updateConfig();
    }

    @Override
    public void execute(int version,GameState gameState) throws UnableToMoveException, BadVersionException, ExecuteException, BlockedActuatorException, PointInObstacleException, ImmobileEnnemyForOneSecondAtLeast,UnexpectedObstacleOnPathException {
        log.debug("////////// Execution MatchScript version "+version+" //////////");
        if(version==0){
            hookFactory.configureHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE, HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);

            //On active le panneau domotique
            ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
            if(basicDetection){
                gameState.robot.useActuator(ActuatorOrder.BASIC_DETECTION_DISABLE,true);
            } else {
                gameState.robot.useActuator(ActuatorOrder.SUS_OFF,true);
            }
            actPD.goToThenExec(0,gameState);
            if(basicDetection) {
                gameState.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE, true);
            } else {
                gameState.robot.useActuator(ActuatorOrder.SUS_ON,true);
            }
            //On prend le tas de cubes 2
            gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
            TakeCubes tk2 = new TakeCubes(config,log,hookFactory);
            tk2.goToThenExec(2,gameState);

            //On active l'abeille
            ActiveAbeille activeAbeille=new ActiveAbeille(config,log,hookFactory);
            activeAbeille.goToThenExec(0,gameState);

            //On prend le tas de cubes 1
            gameState.setTakeCubesBras(BrasUtilise.AVANT);
            TakeCubes tk1=new TakeCubes(config,log,hookFactory);
            tk1.goToThenExec(1,gameState);

            //On évite que les cubes soient poussés vers la zone de construction
            gameState.robot.goTo(new Vec2(970,1400));

            //On dépose les cubes à la première position
            DeposeCubes dpCubes0 = new DeposeCubes(config, log, hookFactory);
            dpCubes0.goToThenExec(0, gameState);

            //On prend le tas de cubes 0
            gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
            TakeCubes tk0=new TakeCubes(config,log,hookFactory);
            tk0.goToThenExec(0,gameState);

            //On dépose les cubes à la deuxième position
            DeposeCubes dpCubes1=new DeposeCubes(config,log,hookFactory);
            dpCubes1.goToThenExec(1, gameState);
        }

        if(version==1){
            hookFactory.configureHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE, HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);

            //Pile cube n°1
            gameState.setTakeCubesBras(BrasUtilise.AVANT);
            TakeCubes tk1=new TakeCubes(config,log,hookFactory);
            tk1.goToThenExec(1,gameState);
            //Pile cube n°1

            //On évite que les cubes soient poussés vers l'abeille
            gameState.robot.goTo(new Vec2(970,1100));

            //On active l'abeille
            ActiveAbeille activeAbeille=new ActiveAbeille(config,log,hookFactory);
            activeAbeille.goToThenExec(0,gameState);

            //Pile de cube n°2
            gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
            TakeCubes tk2=new TakeCubes(config,log,hookFactory);
            tk2.goToThenExec(2,gameState);
            //Pile de cube n°2

            //On dépose les cubes à la première position
            DeposeCubes dpCubes0 = new DeposeCubes(config, log, hookFactory);
            dpCubes0.goToThenExec(0, gameState);

            //Pile cube n°0
            gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
            TakeCubes tk0=new TakeCubes(config,log,hookFactory);
            tk0.goToThenExec(0,gameState);
            //Pile cube n°0

            //Interrupteur
            ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
            if(config.getBoolean(ConfigInfoRobot.BASIC_DETECTION)){
                gameState.robot.useActuator(ActuatorOrder.BASIC_DETECTION_DISABLE,true);
            } else {
                gameState.robot.useActuator(ActuatorOrder.SUS_ON,true);
            }
            actPD.goToThenExec(0,gameState);
            if(config.getBoolean(ConfigInfoRobot.BASIC_DETECTION)){
                gameState.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
            } else {
                gameState.robot.useActuator(ActuatorOrder.SUS_OFF,true);
            }
            //On dépose les cubes à la deuxième position
            DeposeCubes dpCubes1=new DeposeCubes(config,log,hookFactory);
            dpCubes1.goToThenExec(1, gameState);
        }

        if (version==99){
            //Pile cube n°1 et n°2
            TakeCubes tk12 = new TakeCubes(config,log,hookFactory);
            tk12.goToThenExec(120,gameState);
            //Pile cube n°1 et n°2

            hookFactory.configureHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE, HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
            //On active l'abeille
            ActiveAbeille activeAbeille=new ActiveAbeille(config,log,hookFactory);
            activeAbeille.goToThenExec(0,gameState);

            //On dépose les cubes à la première position
            DeposeCubes dpCubes0 = new DeposeCubes(config, log, hookFactory);
            dpCubes0.goToThenExec(0, gameState);

            //Pile cube n°0
            gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
            TakeCubes tk0 = new TakeCubes(config,log,hookFactory);
            tk0.goToThenExec(0,gameState);
            //Pile cube n°0

            //Interrupteur
            ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
            if(config.getBoolean(ConfigInfoRobot.BASIC_DETECTION)){
                gameState.robot.useActuator(ActuatorOrder.BASIC_DETECTION_DISABLE,true);
            } else {
                gameState.robot.useActuator(ActuatorOrder.SUS_ON,true);
            }
            actPD.goToThenExec(0,gameState);
            if(config.getBoolean(ConfigInfoRobot.BASIC_DETECTION)){
                gameState.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
            } else {
                gameState.robot.useActuator(ActuatorOrder.SUS_OFF,true);
            }
            //Interrupteur

            //On dépose les cubes à la deuxième position
            DeposeCubes dpCubes1=new DeposeCubes(config,log,hookFactory);
            dpCubes1.goToThenExec(1, gameState);

        }
        log.debug("////////// End MatchScript version "+version+" //////////");
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        return new Circle(robotPosition);
    }

    public void updateConfig(){
        this.basicDetection=config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
    }
    @Override
    public int remainingScoreOfVersion(int version, final GameState state) {
        return 0;
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {

    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return versions;
    }

}
