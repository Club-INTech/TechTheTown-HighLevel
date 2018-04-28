package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class DeposeCubes extends AbstractScript {

    /**
     * Eléments appelés par la config
     */
    private int distancePenetrationZone; //on pénètre la zone de construction de cette distance
    private int dimensionPorte;
    private int radius;
    private boolean basicDetect;

    public DeposeCubes(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        super.versions = new int[]{0, 1};
        updateConfig();
    }

    /**
     * Cette méthode dépose les cubes pris par les deux bras
     * @param state
     * @throws ExecuteException
     * @throws UnableToMoveException
     */
    @Override
    public void execute(int version, GameState state) throws ExecuteException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution DeposeCubes version "+version+" //////////");
        int numberTowersToDepose=0;
        //Ou exclusif
        if (state.isTourAvantRemplie()^state.isTourArriereRemplie()){
            numberTowersToDepose=1;
        }
        else if (state.isTourAvantRemplie() && state.isTourArriereRemplie()){
            numberTowersToDepose=2;
        }
        else if (!(state.isTourAvantRemplie()) && !(state.isTourArriereRemplie())){
            numberTowersToDepose=0;
        }


        if (numberTowersToDepose>0) {
            Vec2 directionToGo = null;
            double prodScal = 0;
            try {
                directionToGo = (this.entryPosition(version, state.robot.getPosition()).getCenter()).plusNewVector(new Vec2(0, -50)).minusNewVector(state.robot.getPosition());
                prodScal = directionToGo.dot(new Vec2(100.0, state.robot.getOrientation()));
            } catch (BadVersionException e) {
                e.printStackTrace();
                log.debug("BadVersionException: version " + version + " specified");
            }

            //On ne dépose qu'une tour
            if (numberTowersToDepose == 1) {
                if (state.isTourAvantRemplie()) {
                    state.robot.turn(-Math.PI / 2);
                    state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, false);
                    state.robot.moveLengthwiseWithoutDetection(distancePenetrationZone);
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.moveLengthwise(-(distancePenetrationZone + dimensionPorte));
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);

                    state.addObtainedPoints(calculScore(true, state.isCubeBonusAvantPresent(), state));
                    if (state.isCubeBonusAvantPresent()) {
                        state.setCubeBonusAvantPresent(false);
                    }
                } else if (state.isTourArriereRemplie()) {
                    state.robot.turn(Math.PI / 2);
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, true);
                    state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    state.robot.moveLengthwiseWithoutDetection(-distancePenetrationZone);
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.moveLengthwise(distancePenetrationZone + dimensionPorte);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);

                    state.addObtainedPoints(calculScore(false, state.isCubeBonusArrierePresent(), state));
                    if (state.isCubeBonusArrierePresent()) {
                        state.setCubeBonusArrierePresent(false);
                    }
                }
            }

            //On dépose les deux tours
            else {
                if (prodScal > 0) {
                    //On se tourne vers la zone à détecter
                    state.robot.turn(-Math.PI / 2);
                    //On ralentit pour éviter de faire tomber la tour de cubes
                    state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    //On ouvre la porte
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, false);
                    //On rentre dans la zone
                    state.robot.moveLengthwiseWithoutDetection(distancePenetrationZone);
                    //On recule de la largeur de la porte + de la longueur avancée dans la zone
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    /*
                    on recule tout en détectant (si on est en basicDetection on va s'arrêter, vu qu'on l'a
                    pas désactivée au début du execute et qu'elle est réactivée à la fin des execute des autres
                    scripts et que tous les mouvements qu'on fait avant de reculer dans le déposeCube sont
                    without detection)
                    */
                    //on est orienté vers -Pi/2 et c'est là qu'on recule, d'où l'intérêt de détecter
                    state.robot.moveLengthwise(-(distancePenetrationZone + dimensionPorte));
                    //On ferme la porte
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);

                    //On calcule les points
                    state.addObtainedPoints(calculScore(true, state.isCubeBonusAvantPresent(), state));
                    if (state.isCubeBonusAvantPresent()) {
                        //On considère que le cube bonus n'est plus présent, afin de ne pas biaiser la prochaine exécution de TakeCubes
                        state.setCubeBonusAvantPresent(false);
                    }


                    state.robot.turn(Math.PI / 2);
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, true);
                    state.robot.setLocomotionSpeed(Speed.VERY_SLOW_ALL);
                    state.robot.moveLengthwise(-(distancePenetrationZone + 2*dimensionPorte));
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.moveLengthwiseWithoutDetection(2*dimensionPorte + distancePenetrationZone);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);

                    state.addObtainedPoints(calculScore(false, state.isCubeBonusArrierePresent(), state));
                    if (state.isCubeBonusArrierePresent()) {
                        state.setCubeBonusArrierePresent(false);
                    }
                } else {
                    state.robot.turn(Math.PI / 2);
                    state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, false);
                    state.robot.moveLengthwiseWithoutDetection(-distancePenetrationZone);
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.moveLengthwise(distancePenetrationZone + dimensionPorte);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);

                    state.addObtainedPoints(calculScore(false, state.isCubeBonusAvantPresent(), state));
                    if (state.isCubeBonusAvantPresent()) {
                        state.setCubeBonusAvantPresent(false);
                    }


                    state.robot.turn(-Math.PI / 2);
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, true);
                    state.robot.setLocomotionSpeed(Speed.VERY_SLOW_ALL);
                    state.robot.moveLengthwise(distancePenetrationZone + 2*dimensionPorte);
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.moveLengthwiseWithoutDetection(-(2*dimensionPorte + distancePenetrationZone));
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);

                    state.addObtainedPoints(calculScore(true, state.isCubeBonusArrierePresent(), state));
                    if (state.isCubeBonusArrierePresent()) {
                        state.setCubeBonusArrierePresent(false);
                    }
                }
            }
            state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
        }
        //Les deux premières sont déposées
        state.setTourAvantRemplie(false);
        state.setTourArriereRemplie(false);

        //On reset les réussites de tours avant et arrières
        for (int i=0; i<4; i++) {
            state.setReussitesTourArrière(-1,i);
            state.setReussitesTourAvant(-1,i);
        }

        log.debug("////////// End DeposeCubes version "+version+" //////////");
    }

    private int calculScore(boolean pourTourAvant, boolean cubeBonusPresent, GameState state){
        //On assume que le pattern a été correctement reconnu par la reconnaissance
        int score=0;
        int[] reussitesTour;

        //On récupère les réussites de la tour qui nous intéresse
        if (pourTourAvant){
            reussitesTour=state.getReussitesTourAvant();
        }
        else{
            reussitesTour=state.getReussitesTourArriere();
        }
        log.debug(reussitesTour[0]+" "+reussitesTour[1]+" "+reussitesTour[2]+" "+reussitesTour[3]);

        //On check si on a essayé de construire la tour
        if (reussitesTour[1]!=-1) {

            //Calcul des points du pattern
            if (cubeBonusPresent) {
                //Cas où le cube bones est présent
                //On a besoin que du premier, troisieme et quatrieme cube pour réaliser le pattern
                //La réalisation du pattern ne dépend pas de la présence du premier cube
                if (reussitesTour[0] == 1 && reussitesTour[2] == 1 && reussitesTour[3] == 1) {
                    score += 30;
                }
            } else {
                //Cas où le cube bonus n'est pas présent
                //Il faut absolument que les trois premiers cubes qu'on a essayé de prendre soient présents dans la tour
                if (reussitesTour[0] == 1 && reussitesTour[1] == 1 && reussitesTour[2] == 1) {
                    score += 30;
                }
            }

            //Calcul des points de la construction de la tour
            int sum = reussitesTour[0] + reussitesTour[1] + reussitesTour[2] + reussitesTour[3];
            score += sum * (sum + 1) / 2;

            return score;
        }
        else{
            return 0;
        }
    }


    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        //Zone de dépose des cubes proche de la base
        if (version==0) {
            int xentry = 970;
            int yentry = 150 + radius;
            Vec2 position = new Vec2(xentry, yentry);
            return new Circle(position);
        }
        //Zone de dépose des cubes proche du pattern
        else if (version==1) {
            int xEntry = 600;
            int yEntry = 150 + radius;
            Vec2 positionentree = new Vec2(xEntry, yEntry);
            return new Circle(positionentree);
        } else {
            throw new BadVersionException();
        }
    }

    @Override
    public int remainingScoreOfVersion(int version, final GameState state) {
        return 0;
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {
    }

    @Override
    public int[] getVersion(GameState stateToConsider) {
        return versions;
    }

    @Override
    public void updateConfig() {
        super.updateConfig();
        distancePenetrationZone = config.getInt(ConfigInfoRobot.DISTANCE_PENETRATION_ZONE_DEPOSE_CUBES);
        dimensionPorte = config.getInt(ConfigInfoRobot.DIMENSION_PORTES);
        radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        basicDetect=config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
    }
}
