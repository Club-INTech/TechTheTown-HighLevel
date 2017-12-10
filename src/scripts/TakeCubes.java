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
        /**Cas où c'est le pattern 0 qui est retourné par le code de reconnaissance de couleur*/
        if (versionToExecute == 0) {


        }
        if (versionToExecute == 1) {

        }
        if (versionToExecute == 2) {

        }
        if (versionToExecute == 3) {

        }
        if (versionToExecute == 4) {

        }
        if (versionToExecute == 5) {

        }
    }
    /**attention à l'appel de cette méthode, get la bonne version*/
    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {
        int d=160; //distance entre le robot et l'amas de cubes pour faire descendre le bras
        int rayonRobot=config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        try{
            if (version == 0) {
                int xEntry=650;
                int yEntry=540;
                Vec2 position=new Vec2(xEntry,yEntry);
                return new Circle(position);
            }
            else {
                if (version == 1) {
                    int xEntry = 650 - (rayonRobot + d);
                    int yEntry = 540;
                    Vec2 position = new Vec2(xEntry, yEntry);
                    return new Circle(position);
                }
                else{
                    if (version == 2) {
                        int xEntry = 1200 - (rayonRobot + d);
                        int yEntry = 1190;
                        Vec2 position = new Vec2(xEntry, yEntry);
                        return new Circle(position);
                    }
                    else{
                        if (version == 3) {
                            int xEntry = 400 - (rayonRobot + d);
                            int yEntry = 1500;
                            Vec2 position = new Vec2(xEntry, yEntry);
                            return new Circle(position);
                        }
                        else{
                            if (version == 4) {
                                int xEntry = -1200 + (rayonRobot + d);
                                int yEntry = 1190;
                                Vec2 position = new Vec2(xEntry, yEntry);
                                return new Circle(position);
                            }
                            else {
                                if (version == 5) {
                                    int xEntry = -400 + (rayonRobot + d);
                                    int yEntry = 1500;
                                    Vec2 position = new Vec2(xEntry, yEntry);
                                    return new Circle(position);
                                }
                            }}}}}
        }
    catch(Exception e){

    }
        System.out.println("Version invalide");
        Vec2 position=new Vec2();
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
    public Integer[][] getVersion2(GameState stateToConsider){
        return versions2;
}
    @Override
    public Integer[] getVersion(GameState stateToConsider){
        return new Integer[]{};


}
}