package scripts;

import enums.ActuatorOrder;
import enums.Colors;
import enums.Patterns;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PatternNotRecognizedException;
import exceptions.PatternNotYetCalculatedException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import threads.ThreadInterface;
import utils.Log;


enum Cubes{
    ORANGE(1,0,Colors.ORANGE),
    BLUE(0,1,Colors.BLUE),
    GREEN(-1,0,Colors.GREEN),
    BLACK(0,-1,Colors.BLACK),
    YELLOW(0,0,Colors.YELLOW),
    NULL(0,0,Colors.NULL);

    private int xRelative;
    private int yRelative;
    private Colors color;

    Cubes(int xRelative, int yRelative, Colors color){
        this.xRelative=xRelative;
        this.yRelative=yRelative;
        this.color=color;
    }

    public static int[] findRelativeCoordsWithColor(Colors colorToSearch){
        for (Cubes position : Cubes.values()){
            if (colorToSearch==position.color){
                int[] toReturn={position.xRelative, position.yRelative};
                return toReturn;
            }
        }
        return null;
    }
    public Colors getColor(){
        return this.color;
    }
    public int[] getRelativeCoords(){
        int[] toReturn={this.xRelative, this.yRelative};
        return toReturn;
    }
}

enum TasCubes{
    TAS_BASE(-650,540),
    TAS_CHATEAU_EAU(-300,1190),
    TAS_STATION_EPURATION(-400,1500);

    private int x;
    private int y;
    TasCubes(int x, int y){
        this.x=x;
        this.y=y;
    }

    public int[] getCoords(){
        int[] coords={this.x,this.y};
        return getCoords();
    }
}

enum BrasUtilise{
    AVANT("avant"),
    ARRIERE("arriere");
    private String side;

    BrasUtilise(String side){
        this.side=side;
    }

    public String getSide() {
        return side;
    }
}


public class TakeCubesRemastered extends AbstractScript {
    private int largeurCubes=58;
    private int longueurBras=200;

    //TODO : importer largeurCubes de la config
    public TakeCubesRemastered(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
    }

    public void execute(int indicePattern, TasCubes tas, BrasUtilise bras, Cubes additionalCube, GameState stateToConsider)
            throws InterruptedException, ExecuteException, UnableToMoveException, PatternNotYetCalculatedException, PatternNotRecognizedException{
        if (indicePattern == -2){
            if (indicePattern == -1) {
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

                stateToConsider.robot.moveNearPoint(firstPosition, longueurBras);
                takethiscube(stateToConsider, bras.getSide());
                stateToConsider.robot.moveNearPoint(secondPosition, longueurBras);
                takethiscube(stateToConsider, bras.getSide());
                stateToConsider.robot.moveNearPoint(thirdPosition, longueurBras);
                takethiscube(stateToConsider, bras.getSide());
                if (additionalCube.getColor() != Colors.NULL){
                    Vec2 fourthPosition = new Vec2(successivesPositionsList[3][0], successivesPositionsList[3][1]);
                    stateToConsider.robot.moveNearPoint(fourthPosition, longueurBras);
                    takethiscube(stateToConsider, bras.getSide());
                }
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
