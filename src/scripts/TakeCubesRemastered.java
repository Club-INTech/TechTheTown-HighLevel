package scripts;

import enums.*;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PatternNotRecognizedException;
import exceptions.PatternNotYetCalculatedException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import tests.container.A;
import utils.Log;


public class TakeCubesRemastered extends AbstractScript {
    private int largeurCubes;
    private int longueurBras;
    private String direction;

    public TakeCubesRemastered(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        this.largeurCubes=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        this.longueurBras=config.getInt(ConfigInfoRobot.LONGUEUR_BRAS);
    }

    public void execute(int indicePattern, TasCubes tas, BrasUtilise bras, Cubes additionalCube, GameState stateToConsider)
            throws InterruptedException, ExecuteException, UnableToMoveException, PatternNotYetCalculatedException, PatternNotRecognizedException{
        stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, true);
        if (indicePattern != -2){
            if (indicePattern != -1) {
                int[][] successivesPositionsList;
                if (additionalCube.getColor() == Colors.NULL) {
                    successivesPositionsList = new int[3][2];
                } else {
                    successivesPositionsList = new int[4][2];
                    successivesPositionsList[3][0] = tas.getCoords()[0] + additionalCube.getRelativeCoords()[0] * largeurCubes;
                    successivesPositionsList[3][1] = tas.getCoords()[1] + additionalCube.getRelativeCoords()[1] * largeurCubes;
                }

                Colors[] pattern = Patterns.getPatternFromID(indicePattern);
                for (int i = 0; i < 3; i++) {
                    successivesPositionsList[i][0] = tas.getCoords()[0] + Cubes.findRelativeCoordsWithColor(pattern[i])[0] * largeurCubes;
                    successivesPositionsList[i][1] = tas.getCoords()[1] + Cubes.findRelativeCoordsWithColor(pattern[i])[1] * largeurCubes;
                }

                Vec2 firstPosition = new Vec2(successivesPositionsList[0][0], successivesPositionsList[0][1]);
                Vec2 secondPosition = new Vec2(successivesPositionsList[1][0], successivesPositionsList[1][1]);
                Vec2 thirdPosition = new Vec2(successivesPositionsList[2][0], successivesPositionsList[2][1]);
                stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,true);
                stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,true);
                stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE, true);
                if (bras==BrasUtilise.ARRIERE){
                    direction="backward";
                    stateToConsider.robot.turnRelatively(Math.PI);
                }
                else{
                    direction="forward";
                }
                stateToConsider.robot.moveNearPoint(firstPosition, longueurBras, direction);
                takethiscube(stateToConsider, bras.getSide());
                stateToConsider.robot.moveNearPoint(secondPosition, longueurBras, direction);
                takethiscube(stateToConsider, bras.getSide());
                stateToConsider.robot.moveNearPoint(thirdPosition, longueurBras, direction);
                takethiscube(stateToConsider, bras.getSide());
                if (additionalCube.getColor() != Colors.NULL){
                    Vec2 fourthPosition = new Vec2(successivesPositionsList[3][0], successivesPositionsList[3][1]);
                    stateToConsider.robot.moveNearPoint(fourthPosition, longueurBras, direction);
                    takethiscube(stateToConsider, bras.getSide());
                }
                stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, true);
            }
            else{
                log.debug("Le pattern n'a pas été reconnu");
                throw new PatternNotRecognizedException("Le pattern n'a pas été reconnu");
            }
        }
        else{
            log.debug("Exécution script de récupération des cubes avant que le pattern ait été calculé");
            throw new PatternNotYetCalculatedException("Le pattern n'a pas encore été calculé");
        }

    }


    public void takethiscube(GameState stateToConsider, String bras) throws InterruptedException{
        if (bras.equals("avant")) {
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
        }
        if (bras.equals("arriere")) {
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
        }

    }

    public void execute(int placeHolder, GameState placeHolder2){

    }

    public Circle entryPosition(int version, int rayon, Vec2 robotPosition){
        return new Circle(robotPosition,0);
    }

    public void finalize(GameState state, Exception e) throws UnableToMoveException {

    }

    public int remainingScoreOfVersion(int version, final GameState state) {
        return 0;
    }

    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[]{};
    }

    public Integer[][] getVersion2(GameState stateToConsider) {
        return versions2;
    }
}
