package scripts;

import enums.ConfigInfoRobot;
import enums.Speed;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class Recalage extends AbstractScript {

    private int robotRaduis;
    private int distanceAvant;      //distance du centre du robot à l'avant du robot.

    public Recalage (Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        updateConfig();
        this.distanceAvant = 250;
    }


    @Override
    public void execute(int version,GameState gameState) throws ImmobileEnnemyForOneSecondAtLeast, UnableToMoveException {
        log.debug("////////// Execution Recalage version "+version+" //////////");
        log.debug("Position actuelle : "+ gameState.robot.getPosition()+" "+gameState.robot.getOrientation());
        try {
            //On s'oriente vers le mur coté extérieur.
            gameState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
            gameState.robot.turn(0);
            //On s'avance vers le mur jusqu'a l'impact.
            gameState.robot.moveLengthwise(2000);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
            log.debug("Unable to move exeption : On vient de taper le mur.");
            //On s'oriente vers le mur coté abeille.
            gameState.robot.setOrientation(0);
            gameState.robot.setPosition(new Vec2(gameState.robot.getPosition().getX(),2000- distanceAvant));
            log.debug("Robot recalé en y et en orientation");
            gameState.robot.moveLengthwise(-robotRaduis);
            gameState.robot.turn(Math.PI/2);
            try {
                //On s'avance vers le mur jusqu'a l'impact.
                gameState.robot.moveLengthwise(2000);
            } catch (UnableToMoveException e1) {
                e1.printStackTrace();
                log.debug("Unable to move exeption : On vient de taper le mur.");
                gameState.robot.setOrientation(Math.PI/2);
                gameState.robot.setPosition(new Vec2(1500 - distanceAvant, gameState.robot.getPosition().getY()));
                log.debug("Robot recalé en x et en orientation.");
                log.debug("Position actuelle : "+ gameState.robot.getPosition()+" "+gameState.robot.getOrientation());
            }
        }


        log.debug("////////// End Recalage version "+version+" //////////");
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) {
        return new Circle(new Vec2(1280, 1780));
    }

    @Override
    public int remainingScoreOfVersion(int version, final GameState state) {
        return 0;
    }

    @Override
    public void finalize(GameState state, Exception e) { }

    @Override
    public int[] getVersion(GameState stateToConsider) {
        return versions;
    }

    public void updateConfig(){
        this.robotRaduis = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
    }

}
