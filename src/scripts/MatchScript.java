package scripts;

import enums.ConfigInfoRobot;
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

public class MatchScript extends AbstractScript {

    public MatchScript(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        versions = new int[]{0,1,2,3,42};
        updateConfig();
    }

    @Override
    public void execute(int version,GameState gameState) throws UnableToMoveException, BadVersionException, ExecuteException, BlockedActuatorException, PointInObstacleException, ImmobileEnnemyForOneSecondAtLeast, NoPathFound {
        log.debug("////////// Execution MatchScript version "+version+" //////////");
        hookFactory.configureHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE, HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE,HookNames.BASIC_DETECTION_DISABLE);
        if(version==0){

            //On active le panneau domotique
            ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
            actPD.goToThenExec(0,gameState);

            //On prend le tas de cubes 2
            TakeCubes tk2 = new TakeCubes(config,log,hookFactory);
            tk2.goToThenExec(2,gameState);

            //On prend le tas de cubes 1
            TakeCubes tk1=new TakeCubes(config,log,hookFactory);
            tk1.goToThenExec(1,gameState);

            //On évite que les cubes soient poussés vers la zone de construction
            gameState.robot.goTo(new Vec2(970,1400));

            //On dépose les cubes à la première position
            DeposeCubes dpCubes0 = new DeposeCubes(config, log, hookFactory);
            dpCubes0.goToThenExec(0, gameState);

            //On prend le tas de cubes 0
            TakeCubes tk0=new TakeCubes(config,log,hookFactory);
            tk0.goToThenExec(0,gameState);

            //On dépose les cubes à la deuxième position
            DeposeCubes dpCubes2=new DeposeCubes(config,log,hookFactory);
            dpCubes2.goToThenExec(2, gameState);

        }

        if(version==1){

            //Pile cube n°1
            TakeCubes tk1=new TakeCubes(config,log,hookFactory);
            tk1.goToThenExec(1,gameState);

            //Pile de cube n°2
            TakeCubes tk2=new TakeCubes(config,log,hookFactory);
            tk2.goToThenExec(2,gameState);

            //On dépose les cubes à la première position
            DeposeCubes dpCubes0 = new DeposeCubes(config, log, hookFactory);
            dpCubes0.goToThenExec(0, gameState);

            //Pile cube n°0
            TakeCubes tk0=new TakeCubes(config,log,hookFactory);
            tk0.goToThenExec(0,gameState);
            //Pile cube n°0

            //Interrupteur
            ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
            actPD.goToThenExec(0,gameState);

            //On dépose les cubes à la deuxième position
            DeposeCubes dpCubes2=new DeposeCubes(config,log,hookFactory);
            dpCubes2.goToThenExec(2, gameState);
        }

        if(version==2){

            //On prend le tas de cubes 2
            TakeCubes tk2 = new TakeCubes(config,log,hookFactory);
            tk2.goToThenExec(2,gameState);

            //On prend le tas de cubes 1
            TakeCubes tk1=new TakeCubes(config,log,hookFactory);
            tk1.goToThenExec(1,gameState);

            //On active le panneau domotique
            ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
            actPD.goToThenExec(0,gameState);

            //On dépose les cubes à la première position
            DeposeCubes dpCubes0 = new DeposeCubes(config, log, hookFactory);
            dpCubes0.goToThenExec(0, gameState);

            //On prend le tas de cubes 0
            TakeCubes tk0=new TakeCubes(config,log,hookFactory);
            tk0.goToThenExec(0,gameState);

            //On dépose les cubes à la deuxième position
            DeposeCubes dpCubes2=new DeposeCubes(config,log,hookFactory);
            dpCubes2.goToThenExec(2, gameState);

        }

        if(version==3){

            //On active le panneau domotique
            ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
            actPD.goToThenExec(0,gameState);

            //On prend le tas de cubes 0
            TakeCubes tk0 = new TakeCubes(config,log,hookFactory);
            tk0.goToThenExec(0,gameState);

            //On prend le tas de cubes 1
            TakeCubes tk1=new TakeCubes(config,log,hookFactory);
            tk1.goToThenExec(1,gameState);

            //On évite que les cubes soient poussés vers la zone de construction
            gameState.robot.goTo(new Vec2(970,1400));

            //On dépose les cubes à la première position
            DeposeCubes dpCubes0 = new DeposeCubes(config, log, hookFactory);
            dpCubes0.goToThenExec(0, gameState);

            //On prend le tas de cubes 2
            TakeCubes tk2=new TakeCubes(config,log,hookFactory);
            tk2.goToThenExec(2, gameState);

            //On dépose les cubes à la deuxième position
            DeposeCubes dpCubes1=new DeposeCubes(config,log,hookFactory);
            dpCubes1.goToThenExec(1, gameState);
        }

        // Script de la victoire !
        if (version == 42) {

            TakeCubes takeCubes2 = new TakeCubes(config, log, hookFactory);
            takeCubes2.goToThenExec(2,gameState);

            TakeCubes takeCubes0 = new TakeCubes(config, log, hookFactory);
            takeCubes0.goToThenExec(0, gameState);

            //On évite de pousser un cube entre le robot et le panneau domotique
            gameState.robot.goTo(new Vec2(320,500)); //Position bonne

            ActivationPanneauDomotique activationPanneauDomotique = new ActivationPanneauDomotique(config, log, hookFactory);
            activationPanneauDomotique.goToThenExec(0,gameState);

            DeposeCubes deposeCubes2 = new DeposeCubes(config, log, hookFactory);
            deposeCubes2.goToThenExec(2,gameState);

            //On évite qu'on pousse le cube du tas 0 en allant vers le tas 1
            gameState.robot.goTo(new Vec2(900,560));
            //TODO : Position à corriger

            TakeCubes takeCubes1 = new TakeCubes(config, log, hookFactory);
            takeCubes1.goToThenExec(1, gameState);

            DeposeCubes deposeCubes0 = new DeposeCubes(config, log, hookFactory);
            deposeCubes0.goToThenExec(0, gameState);

        }
        log.debug("////////// End MatchScript version "+version+" //////////");
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
    public void finalize(GameState state, Exception e) throws UnableToMoveException { }

    @Override
    public int[] getVersion(GameState stateToConsider) {
        return versions;
    }

}
