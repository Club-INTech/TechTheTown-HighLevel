package scripts;

import enums.ConfigInfoRobot;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pathfinder.Pathfinding;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleManager;
import utils.Log;

import java.util.ArrayList;

public class TakeCubes extends AbstractScript {
    public TakeCubes(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        /**Les versions de TakeCubes dépendent du pattern
         * Elles sont donc stockées dans une matrice[i][j] telle que i est l'entier retourné par le code
         des patterns et j la version à exécuter suivant l'une des six positions d'entrées
         La version[1][2] par exemple correspond au deuxième pattern pour le troisième tas de cubes
         Les versions commençant par 0 sont au fait non pas 00 jusqu'a 05 mais 0 jusqu'a 5
         */
        Integer[][] versions = new Integer[10][6];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 6; j++) {
                String x = Integer.toString(i);
                String y = x + Integer.toString(j);
                versions[i][j] = Integer.parseInt(y);
            }
        }
    }

    //Il faut faire appel au code de reconnaissance de couleur
    @Override
    public void execute(int versionToExecute, GameState stateToConsider) throws ExecuteException, UnableToMoveException {
        ObstacleManager obstacleManager = new ObstacleManager(log, config);
        /*connaitre l'orientation du robot ?? ici le robot est supposé orienté devant le premier cube
        à prendre
         */
        int l=127; //longueur d'un cube (a verifier)
        /**Les version toexecute seront :
         * soit (0,1,...5) (les 6 positions d'entrée possibles si la reconnaissance de couleur
         * renvoit 0
         * soit(10,11,...15) (les 6 positions si la reconnaissance de couleur renvoit1)
         * etc etc (ça dépend du résultat du test de reconnaissance de couleur)
         */
        /**Cas où c'est le pattern 0 qui est retourné par le code de reconnaissance de couleur*/
        if (versionToExecute==0 ||versionToExecute==1 || versionToExecute==2 ||versionToExecute==3
                || versionToExecute==4 ||versionToExecute==5) {
            //to do : ajouter ordre prendCube
            // stateToConsider.robot.useActuator(prendCube,true);
            //to do :Envoyer l'ordre de prendre un cube (à définir avec le bas niveau): le bas niveau activera tous les actionneurs pour cela
            stateToConsider.robot.turn(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(Math.PI/2);
            //to do :Envoyer une autre fois l'ordre de prendre un cube
            stateToConsider.robot.turn(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            //to do :Envoyer une autre fois l'ordre de prendre un cube
            //le quatrième cube?
        }
        if (versionToExecute==10 ||versionToExecute==11 || versionToExecute==12 ||versionToExecute==13
                || versionToExecute==14 ||versionToExecute==15){
            stateToConsider.robot.moveLengthwise(l);
            //to do : envoyer l'ordre de prendre un cube
            stateToConsider.robot.turn(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(Math.PI/2);
            //to do : envoyer l'ordre de prendre un cube
            stateToConsider.robot.turn(Math.PI/2);
            stateToConsider.robot.moveLengthwise(2*l);
            stateToConsider.robot.turn(-Math.PI/2);
            //to do :envoyer l'ordre de prendre un cube
            //le quatrième cube ?
        }
        if (versionToExecute==20 ||versionToExecute==21 || versionToExecute==22 ||versionToExecute==23
                || versionToExecute==24 ||versionToExecute==25) {
            //On prend le cube orange d'abord (on peut inverser l'ordre)
            // to do :envoyer l'ordre de prendre un cube
            stateToConsider.robot.moveLengthwise(2*l);
            // to do :envoyer l'ordre de prendre un cube
            stateToConsider.robot.turn(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(-l);
            // to do :envoyer l'ordre de prendre un cube
            // quatrième cube ?

        }
        if (versionToExecute==30 ||versionToExecute==31 || versionToExecute==32 ||versionToExecute==33
                || versionToExecute==34 ||versionToExecute==35){
            stateToConsider.robot.moveLengthwise(l);
            // to do :envoyer l'ordre de prendre un cube
            stateToConsider.robot.moveLengthwise(l);
            // to do :envoyer l'ordre de prendre un cube
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turn(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(Math.PI/2);
            //to do : envoyer l'ordre de prendre un cube
            //quatrièmle cube ?
        }
        if (versionToExecute==40 ||versionToExecute==41 || versionToExecute==42 ||versionToExecute==43
                || versionToExecute==44 ||versionToExecute==45){
            //on inverse l'odre : ca demandera moins de mouvements vu la position d'entrée choisie
            //to do : envoyer l'ordre de prendre un cube
            stateToConsider.robot.moveLengthwise(l);
            //to do : envoyer l'ordre de prendre un cube
            stateToConsider.robot.turn(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(-Math.PI/2);
            //to do : envoyer l'ordre de prendre un cube
            //quatrième cube ?
        }
        if (versionToExecute==50 ||versionToExecute==51 || versionToExecute==52 ||versionToExecute==53
                || versionToExecute==54 ||versionToExecute==55){
            stateToConsider.robot.moveLengthwise(2*l);
            //to do : envoyer l'ordre de prendre un cube
            stateToConsider.robot.moveLengthwise(-l);
            //to do : envoyer l'ordre de prendre un cube
            stateToConsider.robot.turn(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(-Math.PI/2);
            //to do : envoyer l'ordre de prendre un cube
            //quatrième cube ?
        }
        if (versionToExecute==60 ||versionToExecute==61 || versionToExecute==62 ||versionToExecute==63
                || versionToExecute==64 ||versionToExecute==65){
            stateToConsider.robot.turn(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(-Math.PI/2);
            //to do : envoyer l'ordre de prendre un cube
            stateToConsider.robot.turn(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(Math.PI/2);
            //to do : envoyer l'ordre de prendre un cube
            stateToConsider.robot.turn(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turn(Math.PI/2);
            //to do : envoyer l'ordre de prendre un cube
            // 4 ème cube ?





        }

    }

    /**
     * attention à l'appel de cette méthode, get la bonne version : ici version c'est une position d'entrée
     * parmi les 6 possibles
     */
    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {
        int d = 160; //distance entre le robot et l'amas de cubes pour faire descendre le bras
        int rayonRobot = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        try {
            if (version == 0) {
                int xEntry = 650;
                int yEntry = 540;
                Vec2 position = new Vec2(xEntry, yEntry);
                return new Circle(position);
            } else {
                if (version == 1) {
                    int xEntry = 650 - (rayonRobot + d);
                    int yEntry = 540;
                    Vec2 position = new Vec2(xEntry, yEntry);
                    return new Circle(position);
                } else {
                    if (version == 2) {
                        int xEntry = 1200 - (rayonRobot + d);
                        int yEntry = 1190;
                        Vec2 position = new Vec2(xEntry, yEntry);
                        return new Circle(position);
                    } else {
                        if (version == 3) {
                            int xEntry = 400 - (rayonRobot + d);
                            int yEntry = 1500;
                            Vec2 position = new Vec2(xEntry, yEntry);
                            return new Circle(position);
                        } else {
                            if (version == 4) {
                                int xEntry = -1200 + (rayonRobot + d);
                                int yEntry = 1190;
                                Vec2 position = new Vec2(xEntry, yEntry);
                                return new Circle(position);
                            } else {
                                if (version == 5) {
                                    int xEntry = -400 + (rayonRobot + d);
                                    int yEntry = 1500;
                                    Vec2 position = new Vec2(xEntry, yEntry);
                                    return new Circle(position);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        System.out.println("Version invalide");
        Vec2 position = new Vec2();
        return new Circle(position);
    }


    @Override
    public int remainingScoreOfVersion(int version, final GameState state) {
        return 0;
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {

    }

    @Override
    public Integer[][] getVersion2(GameState stateToConsider) {
        return versions2;
    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[]{};


    }
}