package scripts;

import enums.*;
import exceptions.*;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import tests.container.A;
import utils.Log;

/** Script permettant de récupérer les cubes de n'importe quel tas, selon n'importe quel pattern, dans n'importe quelle direction
 */
public class TakeCubesRemastered extends AbstractScript {
    private int largeurCubes;
    private int longueurBras;
    private String direction;

    public TakeCubesRemastered(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        this.updateConfig();
    }

    /** Execution du script de récupération des cubes
     * @param stateToConsider
     * @throws InterruptedException
     * @throws ExecuteException
     * @throws UnableToMoveException
     * @throws PatternNotYetCalculatedException
     * @throws PatternNotRecognizedException
     */
    @Override
    public void execute(int indiceTas, GameState stateToConsider)
            throws InterruptedException, ExecuteException, UnableToMoveException {
        BrasUtilise bras;
        Cubes additionalCube;
        int indicePattern=stateToConsider.indicePattern;
        TasCubes tas = TasCubes.getTasFromID(indiceTas);

        if (!stateToConsider.tourAvantRemplie){
            stateToConsider.tourAvantRemplie=true;
            bras=BrasUtilise.AVANT;
        }
        else if (!stateToConsider.tourArriereRemplie){
            stateToConsider.tourArriereRemplie=false;
            bras=BrasUtilise.ARRIERE;
        }
        else{
            throw new ExecuteException(new BothTowersFullException("Les deux tours sont remplies"));
        }
        if (bras==BrasUtilise.AVANT){
            if (stateToConsider.cubeAvantPresent){
                additionalCube=Cubes.NULL;
            }
            else{
                additionalCube=Cubes.getCubeNotInPattern(indicePattern);
            }
        }
        else{
            if (stateToConsider.cubeArrierePresent){
                additionalCube=Cubes.NULL;
            }
            else{
                additionalCube=Cubes.getCubeNotInPattern(indicePattern);
            }
        }
        stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, true);
        //Si indicePattern==-2, c'est que le pattern n'a pas encore été calculé
        if (indicePattern != -2){
            //Si indicePattern==-1, c'est que le pattern n'a pas pu être identifié
            if (indicePattern != -1) {
                int[][] successivesPositionsList;
                //Si additionalCube.getColor()==Colors.NULL, c'est qu'on a choisi de ne prendre que 3 cubes
                //Sinon, la couleur de additionalCube sera correspondra au cube qui sera pris après le pattern
                if (additionalCube.getColor() == Colors.NULL) {
                    successivesPositionsList = new int[3][2];
                } else {
                    successivesPositionsList = new int[4][2];
                    //On calcule les positions du cube additionnel pour x et y :
                    // position = position du tas + position relative du cube choisi par rapport au tas
                    successivesPositionsList[3][0] = tas.getCoords()[0] + additionalCube.getRelativeCoords()[0] * largeurCubes;
                    successivesPositionsList[3][1] = tas.getCoords()[1] + additionalCube.getRelativeCoords()[1] * largeurCubes;
                }

                //On récupère les couleurs composant le pattern reconnu (le pattern reconnu est identifié grâce à indicePattern)
                Colors[] pattern = Patterns.getPatternFromID(indicePattern);
                for (int i = 0; i < 3; i++) {
                    //On calcule les positions des cubes pour x et y :
                    // position = position du tas + position relative du cube choisi par rapport au tas
                    successivesPositionsList[i][0] = tas.getCoords()[0] + Cubes.findRelativeCoordsWithColor(pattern[i])[0] * largeurCubes;
                    successivesPositionsList[i][1] = tas.getCoords()[1] + Cubes.findRelativeCoordsWithColor(pattern[i])[1] * largeurCubes;
                }

                //On définit les Vec2 correspondant aux positions où le robot doit aller pour prendre les cubes
                Vec2 firstPosition = new Vec2(successivesPositionsList[0][0], successivesPositionsList[0][1]);
                Vec2 secondPosition = new Vec2(successivesPositionsList[1][0], successivesPositionsList[1][1]);
                Vec2 thirdPosition = new Vec2(successivesPositionsList[2][0], successivesPositionsList[2][1]);

                if (bras==BrasUtilise.ARRIERE){
                    direction="backward";
                    stateToConsider.robot.turnRelatively(Math.PI);
                }
                else{
                    direction="forward";
                }
                //On active la pompe, et ouvre les électrovannes
                stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,true);
                stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,true);
                stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE, true);

                //On fait aller le robot à la position pour prendre le premier cube du pattern
                stateToConsider.robot.moveNearPoint(firstPosition, longueurBras, direction);
                //Le robot execute les actions pour prendre le cube
                takethiscube(stateToConsider, bras.getSide());

                //On fait aller le robot à la position pour prendre le deuxième cube du pattern
                stateToConsider.robot.moveNearPoint(secondPosition, longueurBras, direction);
                //Le robot execute les actions pour prendre le cube
                takethiscube(stateToConsider, bras.getSide());

                //On fait aller le robot à la position pour prendre le troisième cube du pattern
                stateToConsider.robot.moveNearPoint(thirdPosition, longueurBras, direction);
                //Le robot execute les actions pour prendre le cube
                takethiscube(stateToConsider, bras.getSide());

                //Si un cube additionnel a été précisé
                if (additionalCube.getColor() != Colors.NULL){
                    //On définit le Vec2 de la position permettant de prendre le cube additionnel
                    Vec2 fourthPosition = new Vec2(successivesPositionsList[3][0], successivesPositionsList[3][1]);

                    //On fait aller le robot à la position pour prendre le cube additionnel.
                    stateToConsider.robot.moveNearPoint(fourthPosition, longueurBras, direction);
                    //Le robot execute les actions pour prendre le cube
                    takethiscube(stateToConsider, bras.getSide());
                }
                stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, true);
            }
            else{
                log.debug("Le pattern n'a pas été reconnu");
                throw new ExecuteException(new PatternNotRecognizedException("Le pattern n'a pas été reconnu"));
            }
        }
        else{
            log.debug("Exécution script de récupération des cubes avant que le pattern ait été calculé");
            throw new ExecuteException(new PatternNotYetCalculatedException("Le pattern n'a pas encore été calculé"));
        }

    }

    public void takethiscube(GameState stateToConsider, String bras) throws InterruptedException{
        //Vazy wesh si t'as besoin d'explications pour ça c'est que tu sais pas lire
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

    @Override
    public Circle entryPosition(int version, int rayon, Vec2 robotPosition){
        return new Circle(robotPosition,0);
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException { }

    @Override
    public int remainingScoreOfVersion(int version, final GameState state) {
        return 0;
    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[]{};
    }

    @Override
    public Integer[][] getVersion2(GameState stateToConsider) {
        return versions2;
    }


    @Override
    public void updateConfig() {
        super.updateConfig();
        this.largeurCubes=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        this.longueurBras=config.getInt(ConfigInfoRobot.LONGUEUR_BRAS);
    }
}
