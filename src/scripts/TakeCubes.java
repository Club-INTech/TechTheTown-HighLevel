package scripts;

import enums.ActuatorOrder;
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
        stateToConsider.robot.turn(Math.PI);
        stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE,true);
        int l=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        /**Les version toexecute seront :
         * soit (0,1,...5) (les 6 positions d'entrée possibles si la reconnaissance de couleur
         * renvoit 0
         * soit(10,11,...15) (les 6 positions si la reconnaissance de couleur renvoit1)
         * etc etc (ça dépend du résultat du test de reconnaissance de couleur)
         */
        /**Cas où c'est le pattern 0 qui est retourné par le code de reconnaissance de couleur*/

        /**
         * Ici c'est la version (orange,noir,vert)
         */
        if (versionToExecute==0 ||versionToExecute==1 || versionToExecute==2 ||versionToExecute==3
                || versionToExecute==4 ||versionToExecute==5) {
            //prendCube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(-Math.PI/2); //fait tourner le robot relativement
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            //Envoyer une autre fois l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            //prendCube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            //prendre un cube
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);

        }
        /**ici c'est la version (jaune, noir,bleu)*/
        if (versionToExecute==10 ||versionToExecute==11 || versionToExecute==12 ||versionToExecute==13
                || versionToExecute==14 ||versionToExecute==15){
            stateToConsider.robot.moveLengthwise(l);
            //prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            // prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(2*l);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            //prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            //prendre un cube
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);

        }
        /**(Bleu,Vert,Orange)*/
        if (versionToExecute==20 ||versionToExecute==21 || versionToExecute==22 ||versionToExecute==23
                || versionToExecute==24 ||versionToExecute==25) {
            //On prend le cube orange d'abord (on peut inverser l'ordre)
            // prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(2*l);
            // prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(-l);
            // prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(l);
            //prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
        }
        /**(jaune,vert,noir)*/
        if (versionToExecute==30 ||versionToExecute==31 || versionToExecute==32 ||versionToExecute==33
                || versionToExecute==34 ||versionToExecute==35){
            stateToConsider.robot.moveLengthwise(l);
            // envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(l);
            // l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            //l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(-l);
            //prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
        }
        /**(bleu,jaune,orange)*/
        if (versionToExecute==40 ||versionToExecute==41 || versionToExecute==42 ||versionToExecute==43
                || versionToExecute==44 ||versionToExecute==45){
            //on inverse l'odre : ca demandera moins de mouvements vu la position d'entrée choisie
            // envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(l);
            // envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            //quatrième cube ?
        }
        /**(vert,jaune,bleu)*/
        if (versionToExecute==50 ||versionToExecute==51 || versionToExecute==52 ||versionToExecute==53
                || versionToExecute==54 ||versionToExecute==55){
            stateToConsider.robot.moveLengthwise(2*l);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(-l);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            // envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            //quatrième cube ?
        }
        /**(bleu,orange,noir)*/
        if (versionToExecute==60 ||versionToExecute==61 || versionToExecute==62 ||versionToExecute==63
                || versionToExecute==64 ||versionToExecute==65){
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            // 4 ème cube ?

        }
        /**(vert,orange,jaune)*/
        if (versionToExecute==70 ||versionToExecute==71 || versionToExecute==72 ||versionToExecute==73
                || versionToExecute==74 ||versionToExecute==75){
            //on inverse l'ordre
            stateToConsider.robot.moveLengthwise(l);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(-l);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(2*l);
            //tenvoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            //4 ème ?
        }
        /**(Noir,Bleu,Vert)*/
        if (versionToExecute==80 ||versionToExecute==81 || versionToExecute==82 ||versionToExecute==83
                || versionToExecute==84 ||versionToExecute==85){
            //on inverse l'odre : on prend le vert d'abord
            stateToConsider.robot.moveLengthwise(2*l);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            //envoyer l'odre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(2*l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            //4ème ?
        }
        /**(orange,bleu,jaune)*/
        if (versionToExecute==90 ||versionToExecute==91 || versionToExecute==92 ||versionToExecute==93
                || versionToExecute==94 ||versionToExecute==95){
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            stateToConsider.robot.turnRelatively(-Math.PI/2);
            stateToConsider.robot.moveLengthwise(l);
            stateToConsider.robot.turnRelatively(Math.PI/2);
            //envoyer l'ordre de prendre un cube
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
            //4 ème cube ?
        }

    }

    /**
     * les cubes sont numérotés de la façon suivant le sens trigonométrique
     */
    @Override
    public Circle entryPosition(int numtasdecubeaprendre, int ray, Vec2 robotPosition) throws BadVersionException {
        int d = 160; //distance entre le robot et l'amas de cubes pour faire descendre le bras
        int rayonRobot = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
            if (numtasdecubeaprendre== 0) {
                int xEntry = 650;
                int yEntry = 540;
                Vec2 position = new Vec2(xEntry, yEntry);
                return new Circle(position);
            } else {
                if (numtasdecubeaprendre== 1) {
                    int xEntry = 650 - (rayonRobot + d);
                    int yEntry = 540;
                    Vec2 position = new Vec2(xEntry, yEntry);
                    return new Circle(position);
                } else {
                    if (numtasdecubeaprendre == 2) {
                        int xEntry = 1200 - (rayonRobot + d);
                        int yEntry = 1190;
                        Vec2 position = new Vec2(xEntry, yEntry);
                        return new Circle(position);
                    } else {
                        if (numtasdecubeaprendre == 3) {
                            int xEntry = 400 - (rayonRobot + d);
                            int yEntry = 1500;
                            Vec2 position = new Vec2(xEntry, yEntry);
                            return new Circle(position);
                        } else {
                            if (numtasdecubeaprendre == 4) {
                                int xEntry = -1200 + (rayonRobot + d);
                                int yEntry = 1190;
                                Vec2 position = new Vec2(xEntry, yEntry);
                                return new Circle(position);
                            } else {
                                if (numtasdecubeaprendre == 5) {
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
        log.debug("erreur : mauvaise version de script");
        throw new BadVersionException();
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