package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.WrongBrasException;
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

    /**
     * Les cubes sont positionnés ainsi
     * ______|Noir |_____
     * Orange|Jaune| Vert|
     * |Bleu |
     * On se positionne toujours en face du cube orange comme position d'entrée
     * les mouvements à faire sont soit :
     * on tourne de 15 degrés pour prendre un autre cube si on n'a pas à avancer
     * on avance de l (58 mm : la longueur d'un cube) s'il faut le faire
     */

    //méthode prenant en compte les dépassements

    public void execute(int versionToExecute, GameState stateToConsider) throws ExecuteException, UnableToMoveException {
        //considérer le cas où on est de l'autre côté de la table : rajouter un symmetry
        stateToConsider.robot.turn(0);
        stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE, true);
        int l = config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE, true);
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
        if (versionToExecute == 0) {
            //prend lecube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            //test permettant de corriger les erreurs de dépassements
            stateToConsider.robot.turnRelatively(Math.PI / 12 ); //fait tourner le robot relativement
            //prend le cube noir
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l);
            //prend le cube jaune
            takethiscube(stateToConsider, "avant");
        }
        if (versionToExecute == 1) {
            //prend lecube orange
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(l );
            //test permettant de corriger les erreurs de dépassements
            stateToConsider.robot.turnRelatively(Math.PI / 12 ); //fait tourner le robot relativement
            //prend le cube noir
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube vert
            takethiscube(stateToConsider, "arriere");
        }
        if (versionToExecute == 2) {
            //prend lecube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l);
            //test permettant de corriger les erreurs de dépassements
            stateToConsider.robot.turnRelatively(Math.PI / 12 ); //fait tourner le robot relativement
            //prend le cube noir
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12);
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
        }
        /**ici c'est la version (jaune, noir,bleu)*/
        if (versionToExecute == 10) {
            stateToConsider.robot.moveLengthwise(l );
            //prendre le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prendre le cube noir
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(-Math.PI / 6 );
            // prendre le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prendre le cube orange
            takethiscube(stateToConsider, "avant");
        }
        if (versionToExecute == 11) {
            stateToConsider.robot.moveLengthwise(l );
            //prendre le cube jaune
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.turnRelatively(Math.PI / 12);
            //prendre le cube noir
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.turnRelatively(-Math.PI / 6 );
            // prendre le cube bleu
            takethiscube(stateToConsider, "arriere");
        }
        if (versionToExecute == 12) {
            stateToConsider.robot.moveLengthwise(l);
            //prendre le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(Math.PI / 12);
            //prendre le cube noir
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(-Math.PI / 6 );
            // prendre le cube bleu
            takethiscube(stateToConsider, "avant");
        }
        /**(Bleu,Vert,Orange)*/
        if (versionToExecute == 20) {
            //On prend le cube orange d'abord (on peut inverser l'ordre)
            // prendre le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(2 * l);
            // prendre le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turnRelatively(-Math.PI / 12);
            //prendre le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(Math.PI / 12);
            //prendre le cube jaune
            takethiscube(stateToConsider, "avant");

        }
        if (versionToExecute == 21) {
            //On prend le cube orange d'abord (on peut inverser l'ordre)
            // prendre le cube orange
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(2 * (l ));
            // prendre le cube vert
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turnRelatively(-Math.PI / 12);
            //prendre le cube bleu
            takethiscube(stateToConsider, "arriere");
        }
        if (versionToExecute == 22) {
            //On prend le cube orange d'abord (on peut inverser l'ordre)
            // prendre le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(2 * l);
            // prendre le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turnRelatively(-Math.PI / 12);
            //prendre le cube bleu
            takethiscube(stateToConsider, "avant");
        }
        /**(jaune,vert,noir)*/
        if (versionToExecute == 30) {
            stateToConsider.robot.moveLengthwise(l );
            // prend le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l);
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            stateToConsider.robot.turnRelatively(Math.PI / 12);
            // prend le cube noir
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12);
            //prend le cube orange
            takethiscube(stateToConsider, "avant");
        }
        if (versionToExecute == 31) {
            stateToConsider.robot.moveLengthwise(l );
            // prend le cube jaune
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(l );
            //prend le cube vert
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(-l );
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            // prend le cube noir
            takethiscube(stateToConsider, "arriere");
        }

        if (versionToExecute == 32) {
            stateToConsider.robot.moveLengthwise(l );
            // prend le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            // prend le cube noir
            takethiscube(stateToConsider, "avant");
        }
        /**(bleu,jaune,orange)*/
        if (versionToExecute == 40) {
            //on inverse l'odre : ca demandera moins de mouvements vu la position d'entrée choisie
            // prend le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            // prendre le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(-Math.PI / 6 );
            //prend le cube noir
            takethiscube(stateToConsider, "avant");
        }
        if (versionToExecute == 41) {
            //on inverse l'odre : ca demandera moins de mouvements vu la position d'entrée choisie
            // prend le cube orange
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(l );
            // prendre le cube jaune
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "arriere");
        }
        if (versionToExecute == 42) {
            //on inverse l'odre : ca demandera moins de mouvements vu la position d'entrée choisie
            // prend le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            // prendre le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
        }
        /**(vert,jaune,bleu)*/
        if (versionToExecute == 50) {
            stateToConsider.robot.moveLengthwise(2 * l);
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l);
            //prend le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(Math.PI / 6);
            //prend le cube noir
            takethiscube(stateToConsider, "avant");
        }
        if (versionToExecute == 51) {
            stateToConsider.robot.moveLengthwise(2 *l);
            //prend le cube vert
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(-l );
            //prend le cube jaune
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "arriere");
        }
        if (versionToExecute == 52) {
            stateToConsider.robot.moveLengthwise(2 * l);
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            //prend le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
        }
        /**(bleu,orange,noir)*/
        if (versionToExecute == 60) {
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prend le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prend le cube noir
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube jaune
            takethiscube(stateToConsider, "avant");
        }
        if (versionToExecute == 61) {
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(-l );
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prend le cube orange
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prend le cube noir
            takethiscube(stateToConsider, "arriere");
        }
        if (versionToExecute == 62) {
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prend le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prend le cube noir
            takethiscube(stateToConsider, "avant");
        }
        /**(vert,orange,jaune)*/
        if (versionToExecute == 70) {
            //on inverse l'ordre
            stateToConsider.robot.moveLengthwise(l );
            //prend le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            //prend le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(2 *l);
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
        }
        if (versionToExecute == 71) {
            //on inverse l'ordre
            stateToConsider.robot.moveLengthwise(l);
            //prend le cube jaune
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(-l);
            //prend le cube orange
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(2 * l);
            //prend le cube vert
            takethiscube(stateToConsider, "arriere");
        }
        if (versionToExecute == 72) {
            //on inverse l'ordre
            stateToConsider.robot.moveLengthwise(l );
            //prend le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            //prend le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(2 * l);
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
        }
        /**(Noir,Bleu,Vert)*/
        if (versionToExecute == 80) {
            //on inverse l'odre : on prend le vert d'abord
            stateToConsider.robot.moveLengthwise(2 *l );
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(Math.PI / 6 );
            //prend le cube noir
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube jaune
            takethiscube(stateToConsider, "avant");
        }
        if (versionToExecute == 81) {
            //on inverse l'odre : on prend le vert d'abord
            stateToConsider.robot.moveLengthwise(2 * l);
            //prend le cube vert
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(-l);
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.turnRelatively(Math.PI / 6 );
            //prend le cube noir
            takethiscube(stateToConsider, "arriere");
        }
        if (versionToExecute == 82) {
            //on inverse l'odre : on prend le vert d'abord
            stateToConsider.robot.moveLengthwise(2 *l);
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(-l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(Math.PI / 6 );
            //prend le cube noir
            takethiscube(stateToConsider, "avant");
        }
        /**(orange,bleu,jaune)*/
        if (versionToExecute == 90) {
            //prend le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prend le cube jaune
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l);
            //prend le cube vert
            takethiscube(stateToConsider, "avant");
        }
        if (versionToExecute == 91) {
            //prend le cube orange
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "arriere");
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prend le cube jaune
            takethiscube(stateToConsider, "arriere");
        }
        if (versionToExecute == 92) {
            //prend le cube orange
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.moveLengthwise(l );
            stateToConsider.robot.turnRelatively(-Math.PI / 12 );
            //prend le cube bleu
            takethiscube(stateToConsider, "avant");
            stateToConsider.robot.turnRelatively(Math.PI / 12 );
            //prend le cube jaune
            takethiscube(stateToConsider, "avant");
        }
        stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, true);

    }

    public void takethiscube(GameState stateToConsider, String bras) {
        if (bras.equals("avant")) {
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, true);
        }
        if (bras.equals("arriere")) {
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, true);
        }

    }


    /**
     * les cubes sont numérotés de la façon suivant le sens trigonométrique
     */
    @Override
    public Circle entryPosition(int numtasdecubeaprendre, int ray, Vec2 robotPosition) throws BadVersionException {
        int d = 160; //distance entre le robot et l'amas de cubes pour faire descendre le bras
        int rayonRobot = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        if (numtasdecubeaprendre == 0) {
            int xEntry = 650;
            int yEntry = 540;
            Vec2 position = new Vec2(xEntry, yEntry);
            return new Circle(position);
        } else {
            if (numtasdecubeaprendre == 1) {
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
