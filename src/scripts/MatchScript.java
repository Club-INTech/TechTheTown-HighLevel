package scripts;

import enums.BrasUtilise;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import hook.HookNames;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class MatchScript extends AbstractScript {

    public MatchScript(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
    }

    @Override
    public void execute(int versionToexecute,GameState gameState) throws UnableToMoveException, BadVersionException, ExecuteException, BlockedActuatorException, PointInObstacleException {
        log.debug("////////// Execution MatchScript version "+versionToexecute+" //////////");
        if(versionToexecute==0){
            hookFactory.configureHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE, HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);

            //On active le panneau domotique
            ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
            actPD.goToThenExec(0,gameState);

            //On palie la reconnaissance de couleurs qui est actuellement en travaux
            gameState.setRecognitionDone(true);
            gameState.setIndicePattern(0);

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

            log.debug("Fin MatchScript");
        }

        if(versionToexecute==1){
            hookFactory.configureHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE, HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);

            //On palie la reconnaissance de couleurs qui est actuellement en travaux
            gameState.setRecognitionDone(true);
            gameState.setIndicePattern(0);

            //Pile cube n°1
            gameState.setTakeCubesBras(BrasUtilise.AVANT);
            TakeCubes tk1=new TakeCubes(config,log,hookFactory);
            tk1.goToThenExec(1,gameState);
            //Pile cube n°1

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
            actPD.goToThenExec(0,gameState);
            //Interrupteur

            //On dépose les cubes à la deuxième position
            DeposeCubes dpCubes1=new DeposeCubes(config,log,hookFactory);
            dpCubes1.goToThenExec(1, gameState);


            log.debug("////////// End MatchScript version "+versionToexecute+" //////////");
        }
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        return new Circle(robotPosition);
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
