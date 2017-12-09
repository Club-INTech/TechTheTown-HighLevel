package scripts;

import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import pathfinder.Pathfinding;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleManager;
import utils.Log;

public class TakeCubes extends AbstractScript {
    public TakeCubes(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        /**Les versions de TakeCubes dépendent du pattern
         * Elles sont donc stockées dans une matrice[i][j] telle que i est l'entier retourné par le code
         des patterns et j la version à exécuter suivant l'une des six positions d'entrées
         La version[1][2] par exemple correspond au deuxième pattern pour le troisième tas de cubes
         Les versions commençant par 0 sont au fait non pas 00 jusqu'a 05 mais 0 jusqu'a 5
        */
        Integer[][] versions=new Integer[10][6];
        for(int i=0;i<10;i++){
            for(int j=0;j<6;j++){
                String x=Integer.toString(i);
                String y=x+Integer.toString(j);
                versions[i][j]=Integer.parseInt(y);
            }
        }
    }
    //Il faut faire appel au code de reconnaissance de couleur
    @Override
    public void execute(int versionToExecute, GameState stateToConsider) throws ExecuteException, UnableToMoveException
    { ObstacleManager obstacleManager=new ObstacleManager(log,config);
        if (versionToExecute==0){

        }
    }
    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException{
        if(version==0){
            int xEntry=
        }

    }



}