package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.ScriptNames;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
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
    private int[] xEntry;
    private int[] yEntry;
    private int shift;       //Décalage entre les deux versions pour ne pas rentrer dans les obstacles.

    public DeposeCubes(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        updateConfig();
        versions = new int[]{0, 1};
        this.shift =  380;
        this.xEntry=new int[]{970, 600};
        this.yEntry=new int[]{150+radius, 600-shift};
    }

    /**
     * Cette méthode dépose les cubes pris par les deux bras
     * @param state
     * @throws ExecuteException
     * @throws UnableToMoveException
     */
    @Override
    public void execute(int version, GameState state) throws UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution DeposeCubes version "+version+" //////////");
        int numberTowersToDepose=0;
        //Ou exclusif
        if (state.isTourAvantRemplie()^state.isTourArriereRemplie()){
            numberTowersToDepose=1;
            log.debug("DeposeCubes : "+numberTowersToDepose+" tour à déposer");
        }
        else if (state.isTourAvantRemplie() && state.isTourArriereRemplie()){
            numberTowersToDepose=2;
            log.debug("DeposeCubes : "+numberTowersToDepose+" tours à déposer");
        }
        else if (!(state.isTourAvantRemplie()) && !(state.isTourArriereRemplie())){
            numberTowersToDepose=0;
            log.debug("DeposeCubes : "+numberTowersToDepose+" tours à déposer");
        }


        if (numberTowersToDepose>0) {
            if (version == 1) {
                state.robot.goToWithoutDetection(new Vec2(this.xEntry[version], this.yEntry[0]));
            }
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
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, false);
                    state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    state.robot.goToWithoutDetection(new Vec2(this.xEntry[version],this.yEntry[0]-distancePenetrationZone));
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.goTo(new Vec2(this.xEntry[version], this.yEntry[0]+dimensionPorte));
                    state.addObtainedPoints(calculScore(true, state.isCubeBonusAvantPresent(), state));
                    resetTour(true,state);
                    if (version==0){
                        state.setDeposeCubes0Done(true);
                    }
                    else if(version==1){
                        state.setDeposeCubes1Done(true);
                    }
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);
                } else if (state.isTourArriereRemplie()) {
                    state.robot.turn(Math.PI / 2);
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, false);
                    state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    state.robot.goToWithoutDetection(new Vec2(this.xEntry[version],this.yEntry[0]-distancePenetrationZone));
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.goTo(new Vec2(this.xEntry[version], this.yEntry[0]+dimensionPorte));
                    state.addObtainedPoints(calculScore(false, state.isCubeBonusArrierePresent(), state));
                    resetTour(false,state);
                    if (version==0){
                        state.setDeposeCubes0Done(true);
                    }
                    else if(version==1){
                        state.setDeposeCubes1Done(true);
                    }
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);

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
                    state.robot.goToWithoutDetection(new Vec2(this.xEntry[version],this.yEntry[0]-distancePenetrationZone));
                    //On recule de la largeur de la porte + de la longueur avancée dans la zone
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    /*
                    on recule tout en détectant (si on est en basicDetection on va s'arrêter, vu qu'on l'a
                    pas désactivée au début du exception et qu'elle est réactivée à la fin des exception des autres
                    scripts et que tous les mouvements qu'on fait avant de reculer dans le déposeCube sont
                    without detection)
                    */
                    //on est orienté vers -Pi/2 et c'est là qu'on recule, d'où l'intérêt de détecter
                    state.robot.goTo(new Vec2(this.xEntry[version], this.yEntry[0]+dimensionPorte));
                    //On calcule les points
                    state.addObtainedPoints(calculScore(true, state.isCubeBonusAvantPresent(), state));
                    resetTour(true,state);
                    if (version==0){
                        state.setDeposeCubes0Done(true);
                    }
                    else if(version==1){
                        state.setDeposeCubes1Done(true);
                    }
                    //On ferme la porte
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);



                    state.robot.turn(Math.PI / 2);
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, true);
                    state.robot.setLocomotionSpeed(Speed.VERY_SLOW_ALL);
                    state.robot.goToWithoutDetection(new Vec2(this.xEntry[version], this.yEntry[0]-distancePenetrationZone));
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.goTo(new Vec2(this.xEntry[version], this.yEntry[0]+2*dimensionPorte));
                    state.addObtainedPoints(calculScore(false, state.isCubeBonusArrierePresent(), state));
                    resetTour(false,state);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);

                } else {
                    state.robot.turn(Math.PI / 2);
                    state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, false);
                    state.robot.goToWithoutDetection(new Vec2(this.xEntry[version],this.yEntry[0]-distancePenetrationZone));
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.goTo(new Vec2(this.xEntry[version], this.yEntry[0]+dimensionPorte));
                    state.addObtainedPoints(calculScore(false, state.isCubeBonusArrierePresent(), state));
                    resetTour(false,state);
                    if (version==0){
                        state.setDeposeCubes0Done(true);
                    }
                    else if(version==1){
                        state.setDeposeCubes1Done(true);
                    }
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);



                    state.robot.turn(-Math.PI / 2);
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, true);
                    state.robot.setLocomotionSpeed(Speed.VERY_SLOW_ALL);
                    state.robot.goToWithoutDetection(new Vec2(this.xEntry[version],this.yEntry[0]-distancePenetrationZone));
                    state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
                    state.robot.goTo(new Vec2(this.xEntry[version], this.yEntry[0]+2*dimensionPorte));
                    state.addObtainedPoints(calculScore(true, state.isCubeBonusAvantPresent(), state));
                    resetTour(true,state);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);
                }
            }
            state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
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

    private void resetTour(boolean tourAvant, GameState state){
        if (tourAvant){
            if (state.isCubeBonusAvantPresent()) {
                state.setCubeBonusAvantPresent(false);
            }
            state.setTourAvantRemplie(false);
            for (int i=0; i<4; i++) {
                state.setReussitesTourAvant(-1, i);
            }
        }
        else{
            if (state.isCubeBonusArrierePresent()) {
                state.setCubeBonusArrierePresent(false);
            }
            state.setTourArriereRemplie(false);
            for (int i=0; i<4; i++) {
                state.setReussitesTourArrière(-1, i);
            }
        }
    }


    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        //Zone de dépose des cubes proche de la base
        if (version==0) {
            int xEntry = this.xEntry[0];
            int yEntry = this.yEntry[0];
            Vec2 positionEntree = new Vec2(xEntry, yEntry);
            return new Circle(positionEntree);
        }
        //Zone de dépose des cubes proche du pattern
        else if (version==1) {
            int xEntry = this.xEntry[1];
            int yEntry = this.yEntry[1];
            Vec2 positionEntree = new Vec2(xEntry, yEntry);
            return new Circle(positionEntree);
        } else {
            throw new BadVersionException();
        }
    }

    @Override
    public int remainingScoreOfVersion(int version, final GameState state) {
        return 0;
    }

    @Override
    public void finalize(GameState state, Exception e) {
        state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,false);
        state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE,false);
    }

    @Override
    public int[] getVersion(GameState stateToConsider) {
        return versions;
    }

    @Override
    public void goToThenExec(int versionToExecute, GameState state) throws PointInObstacleException, BadVersionException, NoPathFound, ExecuteException, BlockedActuatorException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        state.setLastScript(ScriptNames.DEPOSE_CUBES);
        state.setLastScriptVersion(versionToExecute);
        super.goToThenExec(versionToExecute, state);
    }

    @Override
    public void updateConfig() {
        super.updateConfig();
        distancePenetrationZone = config.getInt(ConfigInfoRobot.DISTANCE_PENETRATION_ZONE_DEPOSE_CUBES);
        dimensionPorte = config.getInt(ConfigInfoRobot.DIMENSION_PORTES);
        radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
    }
}
