package scripts;

import enums.ActuatorOrder;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Callback;
import hook.Hook;
import hook.methods.CatchModuleD;
import hook.methods.CatchModuleG;
import hook.types.HookFactory;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * Created by rem on 25/01/17.
 * Initialise tout les actionneurs du robot
 * Version 0: Ne sort pas de la zone de départ et se contente d'initialiser les actionneurs
 * Version 1: Sort de la zone de départ par l'avant en prenant le module multicolor et en le mettant dans la zone de départ
 * Version 2: Sort de la zone de départ par l'arrière en prenant le module multicolor et en le mettant dans la zone de départ
 * Version 3: Initialise les actionneurs sans replier les attrapes-modules (version pour les tests des fusées)
 */
public class InitialisationRobot extends AbstractScript {

    private boolean detect= false;

    protected InitialisationRobot(HookFactory hookFactory, Config config, Log log){
        super(hookFactory, config, log);

        versions = new Integer[]{0,1,2,3,4};
    }

    @Override
    public void execute(int versionToExecute, GameState gameState, ArrayList<Hook> hookToConsider) throws UnableToMoveException, ExecuteException, SerialConnexionException, BlockedActuatorException {
        try
        {
            updateConfig();
            log.debug("Execution de l'Initialisation robot version " + versionToExecute);

            Hook catchMD = hookFactory.newPositionHook(Table.entryPosition.plusNewVector(new Vec2(440, 2.319)), (float)(2.319), 10, 100);
            catchMD.addCallback(new Callback(new CatchModuleD(), true, gameState));
            Hook catchMG = hookFactory.newPositionHook(new Vec2(480, 320), (float)(-Math.PI + 2.41), 8, 50);
            catchMG.addCallback(new Callback(new CatchModuleG(), true, gameState));

            Hook replibrasD= hookFactory.newPositionHook(new Vec2(540, 520), 0, 10, 10000);
            replibrasD.addCallback(new Callback(new CatchModuleD(), true, gameState));

            hookToConsider.add(catchMD);
            hookToConsider.add(catchMG);
            hookToConsider.add(replibrasD);

            // Initialisation des actionneurs
           if(versionToExecute <= 4) {
                gameState.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, false);
                gameState.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, false);
                gameState.robot.useActuator(ActuatorOrder.BAISSE_ASC, true);
                gameState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);
                gameState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, false);
            }
            if (versionToExecute == 3) {
                gameState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, false);
                gameState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_G, false);
            }
            else {
                gameState.robot.useActuator(ActuatorOrder.PREND_MODULE_G, false);
                gameState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);
            }

            if(detect){
                gameState.robot.switchSensor(); // Capteurs on
            }

            // Se dégage de la zone de départ

            if (versionToExecute == 1) {

                // Avec le Hook pour prendre le module multicolore pret de la zone de départ
                gameState.robot.moveLengthwise(80);
                gameState.robot.turn(2*Math.PI/3);   // 250, 580 <- 578, 208
                gameState.robot.moveLengthwise(600);
                gameState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);

                gameState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                gameState.robot.moveLengthwise(-547, hookToConsider);
                gameState.robot.turn(Math.PI/2);
                gameState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                gameState.robot.moveLengthwise(250);

                // gameState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                // gameState.robot.moveLengthwise(-400, hookToConsider);
                //gameState.robot.turn(5 * Math.PI / 8);

                gameState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, true);

                // départ à l'endroit (pelleteuse vers PI)
                // gameState.robot.turn(13 * Math.PI / 16);
                // gameState.robot.moveLengthwise(210);


                //départ à l'envers (pelleteuse vers 0)
            }
            else if (versionToExecute == 2) {
                gameState.robot.moveLengthwise(-50);
                gameState.robot.turn(-3 * Math.PI / 16);
                gameState.robot.moveLengthwise(-100);
            }
            else if (versionToExecute == 4) {
                // gameState.robot.turn(Math.PI/2 +0.3);
                gameState.robot.moveLengthwise(-650);


            }
        }
        catch (Exception e){
            finalize(gameState, e);
        }
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 0;
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {

        if(version==0 || version==1 || version==2 || version==4){

            return new Circle(robotPosition,10);
        }
        else {
            log.debug("mauvaise version de script");
            throw new BadVersionException();
        }
    }

    @Override
    public void updateConfig(){
        try{
            detect = Boolean.parseBoolean(config.getProperty("capteurs_on"));
        }catch(ConfigPropertyNotFoundException e){
            log.debug("Revoir le code : impossible de trouver la propriété :" + e.getPropertyNotFound());
        }
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException
    {
        if(e != null) log.debug("Exception " + e + " dans InitialisationRobot : Lancement du Finalize !");
        else log.debug("Exception null dans InitialisationRobot : Lancement du Finalize !");

        state.robot.setBasicDetection(false);
    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return versions;
    }
}