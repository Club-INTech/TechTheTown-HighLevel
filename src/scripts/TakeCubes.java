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

    /**Les cubes sont positionnés ainsi
     *            ______|Noir |_____
     *            Orange|Jaune| Vert|
     *                  |Bleu |
     *On se positionne toujours en face du cube orange comme position d'entrée
     * les mouvements à faire sont soit :
     * on tourne de 15 degrés pour prendre un autre cube si on n'a pas à avancer
     * on avance de l (58 mm : la longueur d'un cube) s'il faut le faire
    * */
    public void execute(int versionToExecute, GameState actualState) throws UnableToMoveException, ExecuteException, BlockedActuatorException{}

    //méthode prenant en compte les dépassements

    public void execute(int versionToExecute, GameState stateToConsider, double alpha, double beta) throws ExecuteException, UnableToMoveException {
        stateToConsider.robot.turn(Math.PI);
        stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE,true);
        int l=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        int d=-(int)Math.round(l*alpha);//dépassement translation(alpha=2,5%)
        int drotation=(int)Math.round(Math.PI/12*beta);//dépassement rotation(beta=4,5%)
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
        if (versionToExecute==0 ) {
            //prend lecube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            //test permettant de corriger les erreurs de dépassements
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation); //fait tourner le robot relativement
            //prend le cube noir
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube vert
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            //prend le cube jaune
            takethiscube(stateToConsider);
        }
        if(versionToExecute==1 || versionToExecute==2){
            //prend lecube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            //test permettant de corriger les erreurs de dépassements
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation); //fait tourner le robot relativement
            //prend le cube noir
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube vert
            takethiscube(stateToConsider);
        }
        /**ici c'est la version (jaune, noir,bleu)*/
        if (versionToExecute==10 ){
            stateToConsider.robot.moveLengthwise(l+d);
            //prendre le cube jaune
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation);
            //prendre le cube noir
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(-Math.PI/6+2*drotation);
            // prendre le cube bleu
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation);
            //prendre le cube orange
            takethiscube(stateToConsider);
        }
        if(versionToExecute==11 || versionToExecute==12){
            stateToConsider.robot.moveLengthwise(l+d);
            //prendre le cube jaune
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation);
            //prendre le cube noir
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(-Math.PI/6+2*drotation);
            // prendre le cube bleu
            takethiscube(stateToConsider);
        }
        /**(Bleu,Vert,Orange)*/
        if (versionToExecute==20 ) {
            //On prend le cube orange d'abord (on peut inverser l'ordre)
            // prendre le cube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(2*(l+d));
            // prendre le cube vert
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prendre le cube bleu
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation);
            //prendre le cube jaune
            takethiscube(stateToConsider);

        }
        if(versionToExecute==21 || versionToExecute==22 ){
            //On prend le cube orange d'abord (on peut inverser l'ordre)
            // prendre le cube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(2*(l+d));
            // prendre le cube vert
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prendre le cube bleu
            takethiscube(stateToConsider);
        }
        /**(jaune,vert,noir)*/
        if (versionToExecute==30 ||versionToExecute==31 || versionToExecute==32 ){
            stateToConsider.robot.moveLengthwise(l+d);
            // prend le cube jaune
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            //prend le cube vert
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation);
            // prend le cube noir
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube orange
            takethiscube(stateToConsider);
        }
        /**(bleu,jaune,orange)*/
        if (versionToExecute==40 ||versionToExecute==41 || versionToExecute==42 ){
            //on inverse l'odre : ca demandera moins de mouvements vu la position d'entrée choisie
            // prend le cube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            // prendre le cube jaune
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube bleu
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(-Math.PI/6+2*drotation);
            //prend le cube noir
            takethiscube(stateToConsider);
        }
        /**(vert,jaune,bleu)*/
        if (versionToExecute==50 ||versionToExecute==51 || versionToExecute==52 ){
            stateToConsider.robot.moveLengthwise(2*(l+d));
            //prend le cube vert
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            //prend le cube jaune
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube bleu
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(Math.PI/6-2*drotation);
            //prend le cube noir
            takethiscube(stateToConsider);
        }
        /**(bleu,orange,noir)*/
        if (versionToExecute==60 ||versionToExecute==61 || versionToExecute==62 ){
            stateToConsider.robot.moveLengthwise(l+d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube bleu
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation);
            //prend le cube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation);
            //prend le cube noir
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube jaune
            takethiscube(stateToConsider);
        }
        /**(vert,orange,jaune)*/
        if (versionToExecute==70  ){
            //on inverse l'ordre
            stateToConsider.robot.moveLengthwise(l+d);
            //prend le cube jaune
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            //prend le cube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(2*(l+d));
            //prend le cube vert
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube bleu
            takethiscube(stateToConsider);
        }
        if(versionToExecute==71 || versionToExecute==72){
            //on inverse l'ordre
            stateToConsider.robot.moveLengthwise(l+d);
            //prend le cube jaune
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            //prend le cube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(2*(l+d));
            //prend le cube vert
            takethiscube(stateToConsider);
        }
        /**(Noir,Bleu,Vert)*/
        if (versionToExecute==80  ){
            //on inverse l'odre : on prend le vert d'abord
            stateToConsider.robot.moveLengthwise(2*(l+d));
            //prend le cube vert
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube bleu
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(Math.PI/6-2*drotation);
            //prend le cube noir
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube jaune
            takethiscube(stateToConsider);
        }
        if(versionToExecute==81 || versionToExecute==82){
            //on inverse l'odre : on prend le vert d'abord
            stateToConsider.robot.moveLengthwise(2*(l+d));
            //prend le cube vert
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(-l-d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube bleu
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(Math.PI/6-2*drotation);
            //prend le cube noir
            takethiscube(stateToConsider);
        }
        /**(orange,bleu,jaune)*/
        if (versionToExecute==90  ){
            //prend le cube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube bleu
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation);
            //prend le cube jaune
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            //prend le cube vert
            takethiscube(stateToConsider);
        }
        if(versionToExecute==91 || versionToExecute==92){
            //prend le cube orange
            takethiscube(stateToConsider);
            stateToConsider.robot.moveLengthwise(l+d);
            stateToConsider.robot.turnRelatively(-Math.PI/12+drotation);
            //prend le cube bleu
            takethiscube(stateToConsider);
            stateToConsider.robot.turnRelatively(Math.PI/12-drotation);
            //prend le cube jaune
            takethiscube(stateToConsider);
        }

    }
    public void takethiscube(GameState stateToConsider){
        stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS,true);
        stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
        stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS,true);
        stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
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