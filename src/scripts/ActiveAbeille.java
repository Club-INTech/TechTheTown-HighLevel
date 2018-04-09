package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import hook.HookNames;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import sun.awt.image.ImageAccessException;
import utils.Log;

public class ActiveAbeille extends AbstractScript {

    /** Active l'abeille */
    private int securityDistance; //distance de sécurité pour ne pas cogner le mur en tournant
    private int xEntry; //position d'entrée permettant de toucher l'abeille, on y va en goto
    private int yEntry;
    private int xExit; //position de sortie permettant au pathfinding d'être lancé
    private int yExit;

    /** Eléments appelés par la config */
    private int radius; //rayon du robot
    private int distanceAbeille;

    public ActiveAbeille(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        updateConfig();
        this.securityDistance=60;
        this.xEntry=1300;
        this.yEntry=1765;
        this.xExit=1500-radius-securityDistance;
        this.yExit=2000-radius-securityDistance;
    }
    @Override
    public void updateConfig() {
        super.updateConfig();
        distanceAbeille = config.getInt(ConfigInfoRobot.DISTANCE_ABEILLE);
        radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
    }

    @Override
    public void execute(int versionToExecute, GameState actualState) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution ActiveAbeille version "+versionToExecute+" //////////");
        //On vérifie quel bras de l'abeille on va devoir utiliser, à l'aide d'un produit scalaire
        Vec2 entry=new Vec2(this.xEntry,this.yEntry);
        Vec2 directionToGo = (entry.minusNewVector(actualState.robot.getPosition()));
        double prodScal=directionToGo.dot(new Vec2(100.0,actualState.robot.getOrientation()));
        if (prodScal>0) {
            //ON UTILISE LE BRAS AVANT

            //On disable le hook pour le bras arrière
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE_SYMETRIQUE);
            //On enable le kook pour le bras avant
            hookFactory.enableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
            hookFactory.enableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE_SYMETRIQUE);
            //On va vers l'abeille
            actualState.robot.goTo(new Vec2(xEntry, yEntry));

            //On se tourne pour pousser l'abeille avec le bras avant
            actualState.robot.turn(Math.PI/2,true);
            //On relève le bras avant
            actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, false);
            //On disable le hook du bras avant
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE_SYMETRIQUE);
        }
        else{
            //ON UTILISE LE BRAS ARRIERE
            //On disable le hook pour le bras avant
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE_SYMETRIQUE);
            //On enable le kook pour le bras arrière
            hookFactory.enableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
            hookFactory.enableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE_SYMETRIQUE);
            //On va vers l'abeille
            actualState.robot.goTo(new Vec2(xEntry,yEntry));
            //On se tourne our pousser l'abeille avec le bras arrière
            actualState.robot.turn(-Math.PI/2,true);
            //On relève le bras arrière
            actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, false);
            //On disable le hook du bras arrière
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE_SYMETRIQUE);
        }
        //On retourne à une position atteignable par le pathfinding
        Vec2 aim = new Vec2(xExit,yExit);
        actualState.robot.goTo(aim);
        log.debug("////////// End ActiveAbeille version "+versionToExecute+" //////////");
    }

        @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        log.debug("Position d'entrée ActiveAbeille : "+robotPosition);
        /** Version utilisée pour les match scriptés. */
        if(version == 0){
            return new Circle(robotPosition);
        }
        /** Version utilisée pour l'IA. */
        if (version == 1){
            return new Circle(new Vec2(1200,1750));
        }else {
            throw new BadVersionException();
        }
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {}


    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[0];
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 0;
    }


}
