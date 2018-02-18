package scripts;

import enums.*;
import exceptions.*;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

/** Script permettant de récupérer les cubes de n'importe quel tas, selon n'importe quel pattern, dans n'importe quelle direction
 */
public class TakeCubes extends AbstractScript {
    private int largeurCubes;
    private int longueurBras;
    private String direction;

    public TakeCubes(Config config, Log log, HookFactory hookFactory) {
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

        //On récupère l'indice du pattern
        int indicePattern=stateToConsider.indicePattern;

        //On récupère le tas correspondant à l'indice
        TasCubes tas = TasCubes.getTasFromID(indiceTas);

        //On regarde si la tour avant est remplie
        if (!stateToConsider.tourAvantRemplie){
            stateToConsider.tourAvantRemplie=true;
            bras=BrasUtilise.AVANT;
        }
        //On regarde si la tour arrièr est remplie
        else if (!stateToConsider.tourArriereRemplie){
            stateToConsider.tourArriereRemplie=false;
            bras=BrasUtilise.ARRIERE;
        }
        //Si les deux tours sont remplies, on renvoie une exception et n'execute pas le script
        else{
            throw new ExecuteException(new BothTowersFullException("Les deux tours sont remplies"));
        }

        //On regarde quel bras on utilise
        if (bras==BrasUtilise.AVANT){
            //On gère le cas où le cube bonus est encore présent
            if (stateToConsider.cubeAvantPresent){
                additionalCube=Cubes.NULL;
            }
            else{
                additionalCube=Cubes.getCubeNotInPattern(indicePattern);
            }
        }
        else{
            //On gère le cas où le cube bonus est encore présent
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
                takeThisCube(stateToConsider, bras);

                //On fait aller le robot à la position pour prendre le deuxième cube du pattern
                stateToConsider.robot.moveNearPoint(secondPosition, longueurBras, direction);
                //Le robot execute les actions pour prendre le cube
                takeThisCube(stateToConsider, bras);

                //On fait aller le robot à la position pour prendre le troisième cube du pattern
                stateToConsider.robot.moveNearPoint(thirdPosition, longueurBras, direction);
                //Le robot execute les actions pour prendre le cube
                takeThisCube(stateToConsider, bras);

                //Si un cube additionnel a été précisé
                if (additionalCube.getColor() != Colors.NULL){
                    //On définit le Vec2 de la position permettant de prendre le cube additionnel
                    Vec2 fourthPosition = new Vec2(successivesPositionsList[3][0], successivesPositionsList[3][1]);

                    //On fait aller le robot à la position pour prendre le cube additionnel.
                    stateToConsider.robot.moveNearPoint(fourthPosition, longueurBras, direction);
                    //Le robot execute les actions pour prendre le cube
                    takeThisCube(stateToConsider, bras);
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

    public void takeThisCube(GameState stateToConsider, BrasUtilise bras) throws InterruptedException{
        //Vazy wesh si t'as besoin d'explications pour ça c'est que tu sais pas lire
        if (bras==BrasUtilise.AVANT) {
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
        }
        else if (bras==BrasUtilise.ARRIERE) {
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
        }
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException{
        if (version>5 || version<0){
            throw new BadVersionException("Bad version exception : la version doit être comprise entre 0 et 5 (bornes incluses)");
        }
        TasCubes tas = TasCubes.getTasFromID(version);
        int[] coords = tas.getCoords();
        Vec2 coordsTas=new Vec2(coords[0],coords[1]);
        //TODO : ajuster le rayon du cercle si il est trop petit
        Circle aimCircle = new Circle(coordsTas,this.longueurBras+this.largeurCubes+30);
        Vec2 aim = smartMath.Geometry.closestPointOnCircle(robotPosition,aimCircle);
        return new Circle(aim);
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
    public void updateConfig() {
        super.updateConfig();
        this.largeurCubes=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        this.longueurBras=config.getInt(ConfigInfoRobot.LONGUEUR_BOUT_DU_BRAS);
    }
}
