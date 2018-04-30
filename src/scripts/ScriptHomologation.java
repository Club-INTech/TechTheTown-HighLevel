package scripts;

import enums.*;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;


public class ScriptHomologation extends AbstractScript {

    private int longueurCube;
    private int longueurBrasAv;
    private int longueurBrasAR;

    public ScriptHomologation(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        updateConfig();
    }


    @Override
    public void execute(int versionToExecute, GameState actualState) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException, BadVersionException, PointInObstacleException, ImmobileEnnemyForOneSecondAtLeast {
        //On y va avec la basicDetection
        actualState.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
        //on se dirige vers le tas de cube numéro 4
        TasCubes tasEnnemi = TasCubes.getTasFromID(4);
        Vec2 coordTasCubes=tasEnnemi.getCoordsVec2();
        Vec2 cubeYellowRelativePosition = Cubes.getCubeFromColor(Colors.YELLOW).getRelativeCoordsVec2(tasEnnemi).dotFloat(longueurCube);
        Vec2 directionToGo = coordTasCubes.minusNewVector(actualState.robot.getPosition());
        double prodScal = directionToGo.dot(new Vec2(100.0, actualState.robot.getOrientation()));
        if(prodScal>0){
            //On avance vers le cube jaune
            actualState.robot.moveNearPoint(coordTasCubes.plusNewVector(cubeYellowRelativePosition),longueurBrasAv,"forward");
            //On prend le cube jaune
            takeThisCube(actualState,"forward");
        }
        else{
            //On recule vers le cube jaune
            actualState.robot.moveNearPoint(coordTasCubes.plusNewVector(cubeYellowRelativePosition),longueurBrasAR,"backward");
            //On prend le cube jaune
            takeThisCube(actualState,"backward");
        }
        //On dépose le cube jaune qu'on a pris
        DeposeCubes dpCubes=new DeposeCubes(config,log,hookFactory);
        dpCubes.goToThenExec(1,actualState);

    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        Circle aimArcCircle = new Circle(TasCubes.getTasFromID(4).getCoordsVec2(), (longueurBrasAR+longueurBrasAv)/2, - 9 * Math.PI / 20, Math.PI / 2, true);
        Vec2 aim = smartMath.Geometry.closestPointOnCircle(robotPosition,aimArcCircle);
        return new Circle(aim);
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {

    }

    @Override
    public int[] getVersion(GameState stateToConsider) {
        return new int[0];
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 0;
    }

    public void takeThisCube(GameState state,String direction){

        state.robot.useActuator(ActuatorOrder.BASIC_DETECTION_DISABLE,true);
        if(direction.equals("forward")){
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
            state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);
            state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
            state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
            state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU, true);
            state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_AVANT,false);
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
            state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU,false);
        }
        else{
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,false);
            state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
            state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
            state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
            state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU, true);
            state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_ARRIERE, false);
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
            state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE_UNPEU,false);
        }
        state.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, false);
        state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
        state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);
        state.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
    }

    @Override
    public void updateConfig() {
        this.longueurCube=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        this.longueurBrasAv=config.getInt(ConfigInfoRobot.LONGUEUR_BRAS_AVANT);
        this.longueurBrasAR=config.getInt(ConfigInfoRobot.LONGUEUR_BRAS_ARRIERE);
    }
}
