package scripts;

import enums.TasCubes;
import enums.Patterns;
import enums.Colors;
import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.Cubes;
import enums.BrasUtilise;
import exceptions.ExecuteException;
import exceptions.PatternNotYetCalculatedException;
import exceptions.BadVersionException;
import exceptions.PatternNotRecognizedException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

/** Script permettant de récupérer les cubes de n'importe quel tas, selon n'importe quel pattern, dans n'importe quelle direction
 */
public class TakeCubes extends AbstractScript {
    private int largeurCubes;
    private int longueurBras;
    private Vec2 entryPositionPoint;

    public TakeCubes(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        this.updateConfig();
    }

    /** Execution du script de récupération des cubes
     * @param indiceTas le numéro du tas à récupérer
     * @param stateToConsider le GameState permettant de connaître l'état de la partie
     */
    @Override
    public void execute(int indiceTas, GameState stateToConsider)
            throws InterruptedException, ExecuteException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast,UnexpectedObstacleOnPathException {
        log.debug("////////// Execution TakeCubes version "+indiceTas+" //////////");
        if (indiceTas<6){
            this.normalVersions(indiceTas, stateToConsider);
        }
        else{
            this.specialVersions(indiceTas, stateToConsider);
        }
        log.debug("////////// End TakeCubes version "+indiceTas+" //////////");
    }

    private void normalVersions(int indiceTas, GameState stateToConsider) throws InterruptedException, ExecuteException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast,UnexpectedObstacleOnPathException {
        //TODO mettre un choix par orientation du robot par rapport au tas
        BrasUtilise bras=stateToConsider.getTakeCubesBras();

        //On récupère le tas correspondant à l'indice
        TasCubes tas = TasCubes.getTasFromID(indiceTas);
        Cubes additionalCube;
        String direction;
        if(!(config.getBoolean(ConfigInfoRobot.SIMULATION))){
            while(!stateToConsider.isRecognitionDone()){
                Thread.sleep(10);
            }
        }
        else{
            stateToConsider.setIndicePattern(config.getInt(ConfigInfoRobot.INDICE_PATTERN_SIMULATION));
        }

        //On récupère l'indice du pattern
        int indicePattern=stateToConsider.getIndicePattern();

        //On regarde quel bras on utilise
        if (bras==BrasUtilise.AVANT){
            //On gère le cas où le cube bonus est encore présent
            //stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU,false);
            if (stateToConsider.isCubeBonusAvantPresent()){
                additionalCube=Cubes.NULL;
            }
            else{
                additionalCube=Cubes.getCubeNotInPattern(indicePattern);
            }
        }
        else{
            //stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU,false);
            //On gère le cas où le cube bonus est encore présent
            if (stateToConsider.isCubeBonusArrierePresent()){
                additionalCube=Cubes.NULL;
            }
            else{
                additionalCube=Cubes.getCubeNotInPattern(indicePattern);
            }
        }

        //Grâce à la config, on passe au pathfinding quel tas on a pris
        if(indiceTas==0){
            config.override(ConfigInfoRobot.TAS_BASE_PRIS,true);
            stateToConsider.setTas_base_pris(true);
        }
        else if(indiceTas==1){
            config.override(ConfigInfoRobot.TAS_CHATEAU_PRIS,true);
            stateToConsider.setTas_chateau_eau_pris(true);
        }
        else if(indiceTas==2){
            config.override(ConfigInfoRobot.TAS_STATION_EPURATION_PRIS,true);
            stateToConsider.setTas_station_epuration_pris(true);
        }
        else if(indiceTas==3){
            config.override(ConfigInfoRobot.TAS_BASE_ENNEMI_PRIS,true);
            stateToConsider.setTas_base_ennemi_pris(true);
        }
        else if(indiceTas==4){
            config.override(ConfigInfoRobot.TAS_CHATEAU_ENNEMI_PRIS,true);
            stateToConsider.setTas_chateau_ennemi_eau_pris(true);
        }
        else if(indiceTas==5){
            config.override(ConfigInfoRobot.TAS_STATION_EPURATION_ENNEMI_PRIS,true);
            stateToConsider.setTas_station_epuration_ennemi_pris(true);
        }

        //Si indicePattern==-2, c'est que le pattern n'a pas encore été calculé
        if (indicePattern != -2){
            //Si indicePattern==-1, c'est que le pattern n'a pas pu être identifié
            if (indicePattern != -1) {

                //On active la pompe, et ouvre les électrovannes
                stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
                stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,true);
                stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE, false);

                Vec2[] successivesPositionsList;
                int currentIdealPositionInTower=0;

                int otherSideMultiplier=1;
                if (tas.getID()>2) {
                    otherSideMultiplier=-1;
                }


                //Si additionalCube.getColor()==Colors.NULL, c'est qu'on a choisi de ne prendre que 3 cubes
                //Sinon, la couleur de additionalCube sera correspondra au cube qui sera pris après le pattern
                if (additionalCube.getColor() == Colors.NULL) {
                    successivesPositionsList = new Vec2[3];
                    //On sait que le premier cube dans la pile est le cube bonus, donc on l'indique dans les réussites de la tour
                    if (bras==BrasUtilise.AVANT) {
                        stateToConsider.setReussitesTourAvant(1, currentIdealPositionInTower);
                    }
                    else{
                        stateToConsider.setReussitesTourArrière(1, currentIdealPositionInTower);
                    }
                    currentIdealPositionInTower++;
                } else {
                    successivesPositionsList = new Vec2[4];
                    //On calcule les positions du cube additionnel pour x et y :
                    // position = position du tas + position relative du cube choisi par rapport au tas

                    //La position X relative par rapport au tas change si on passe de l'autre côté de la table
                    Vec2 additionalCubeRelativePosition = additionalCube.getRelativeCoordsVec2().dotFloat(this.largeurCubes);
                    additionalCubeRelativePosition.setX(additionalCubeRelativePosition.getX()*otherSideMultiplier);

                    successivesPositionsList[3]=tas.getCoordsVec2().plusNewVector(additionalCubeRelativePosition);
                }

                //On récupère les couleurs composant le pattern reconnu (le pattern reconnu est identifié grâce à indicePattern)
                Colors[] pattern = Patterns.getPatternFromID(indicePattern);
                for (int i = 0; i < 3; i++) {
                    //On calcule les positions des cubes pour x et y :
                    // position = position du tas + position relative du cube choisi par rapport au tas

                    //La position X relative par rapport au tas change si on passe de l'autre côté de la table
                    Vec2 cubeRelativePosition = Cubes.getCubeFromColor(pattern[i]).getRelativeCoordsVec2().dotFloat(this.largeurCubes);
                    cubeRelativePosition.setX(cubeRelativePosition.getX()*otherSideMultiplier);

                    successivesPositionsList[i]= tas.getCoordsVec2().plusNewVector(cubeRelativePosition);
                }

                if (bras==BrasUtilise.ARRIERE){
                    direction="backward";
                    stateToConsider.setTourArriereRemplie(true);
                }
                else{
                    direction="forward";
                    stateToConsider.setTourAvantRemplie(true);
                }


                for (int i=0; i<3; i++) {
                    //On fait aller le robot à la position pour prendre le premier cube du pattern
                    log.debug("Essaye de prendre le cube "+pattern[i].getName());
                    stateToConsider.robot.moveNearPoint(successivesPositionsList[i], longueurBras, direction);
                    //Le robot execute les actions pour prendre le cube
                    takeThisCube(stateToConsider, bras, currentIdealPositionInTower);
                    currentIdealPositionInTower++;
                }

                //Si un cube additionnel a été précisé
                if (additionalCube.getColor()!=Colors.NULL){
                    log.debug("Essaye de prendre le cube "+additionalCube.getColor().getName());
                    //On fait aller le robot à la position pour prendre le cube additionnel.
                    stateToConsider.robot.moveNearPoint(successivesPositionsList[3], longueurBras, direction);
                    //Le robot execute les actions pour prendre le cube
                    takeThisCube(stateToConsider, bras, currentIdealPositionInTower);
                }


                stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, false);
                stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
                stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);

                if(bras.equals(BrasUtilise.AVANT)){
                    stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,false);
                }
                if(bras.equals(BrasUtilise.ARRIERE)){
                    stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE,false);
                }



//////////////////// LE SCRIPT A FINI SES MOUVEMENTS //////////////////////////



                //On se décale du tas
                Circle aimArcCircle;
                if (indiceTas==0){
                    aimArcCircle = new Circle(tas.getCoordsVec2(), this.longueurBras+this.largeurCubes*1.5+10, 0, Math.PI, true);
                }
                else if (indiceTas==1) {
                    aimArcCircle = new Circle(tas.getCoordsVec2(), this.longueurBras+this.largeurCubes*1.5+10, Math.PI / 2, 3 * 9 * Math.PI / 20, true);
                }
                else if (indiceTas==2) {
                    aimArcCircle = new Circle(tas.getCoordsVec2(), this.longueurBras+this.largeurCubes*1.5+10, -Math.PI, 0, true);
                }
                else if (indiceTas==3) {
                    aimArcCircle = new Circle(tas.getCoordsVec2(), this.longueurBras+this.largeurCubes*1.5+10, -Math.PI, 0, true);
                }
                else if (indiceTas==4) {
                    aimArcCircle = new Circle(tas.getCoordsVec2(), this.longueurBras+this.largeurCubes*1.5+10, - 9 * Math.PI / 20, Math.PI / 2, true);
                }
                else{
                    aimArcCircle = new Circle(tas.getCoordsVec2(), this.longueurBras+this.largeurCubes*1.5+10, -Math.PI / 2, Math.PI / 2, true);
                }
                Vec2 aim = smartMath.Geometry.closestPointOnCircle(stateToConsider.robot.getPosition(),aimArcCircle);
                //On ne sort seulement si la distance nous séparant de la position de sortie est supérieure à 2cm
                if (stateToConsider.robot.getPosition().distance(aim)>20) {
                    stateToConsider.robot.goTo(aim);
                }
            }
            else{
                log.critical("Le pattern n'a pas été reconnu");
                throw new ExecuteException(new PatternNotRecognizedException("Le pattern n'a pas été reconnu"));
            }
        }
        else{
            log.critical("Exécution script de récupération des cubes avant que le pattern ait été calculé");
            throw new ExecuteException(new PatternNotYetCalculatedException("Le pattern n'a pas encore été calculé"));
        }
    }





    /** VERSIONS SPECIALES */






    private void specialVersions(int indiceTas, GameState state)  throws InterruptedException, ExecuteException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast,UnexpectedObstacleOnPathException {
        if (indiceTas==120){
            if(!(config.getBoolean(ConfigInfoRobot.SIMULATION))){
                while(!state.isRecognitionDone()){
                    Thread.sleep(10);
                }
            }
            else{
                state.setIndicePattern(config.getInt(ConfigInfoRobot.INDICE_PATTERN_SIMULATION));
            }

            //On récupère l'indice du pattern
            int indicePattern=state.getIndicePattern();

            if (indicePattern !=-2){
                if (indicePattern != -1) {

                    //On active la pompe, et ouvre les électrovannes
                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,false);
                    state.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE, false);

                    boolean cubeBonusAvantPresent = state.isCubeBonusAvantPresent();
                    boolean cubeBonusArrierePresent = state.isCubeBonusArrierePresent();

                    Colors[] pattern = Patterns.getPatternFromID(indicePattern);
                    Vec2 firstCubeTas1 = TasCubes.getTasFromID(1).getCoordsVec2().plusNewVector(Cubes.getCubeFromColor(pattern[0]).getRelativeCoordsVec2().dotFloat(this.largeurCubes));
                    Vec2 secondCubeTas1 = TasCubes.getTasFromID(1).getCoordsVec2().plusNewVector(Cubes.getCubeFromColor(pattern[1]).getRelativeCoordsVec2().dotFloat(this.largeurCubes));
                    Vec2 thirdCubeTas1 = TasCubes.getTasFromID(1).getCoordsVec2().plusNewVector(Cubes.getCubeFromColor(pattern[2]).getRelativeCoordsVec2().dotFloat(this.largeurCubes));

                    Vec2 firstCubeTas2 = TasCubes.getTasFromID(2).getCoordsVec2().plusNewVector(Cubes.getCubeFromColor(pattern[0]).getRelativeCoordsVec2().dotFloat(this.largeurCubes));
                    Vec2 secondCubeTas2 = TasCubes.getTasFromID(2).getCoordsVec2().plusNewVector(Cubes.getCubeFromColor(pattern[1]).getRelativeCoordsVec2().dotFloat(this.largeurCubes));
                    Vec2 thirdCubeTas2 = TasCubes.getTasFromID(2).getCoordsVec2().plusNewVector(Cubes.getCubeFromColor(pattern[2]).getRelativeCoordsVec2().dotFloat(this.largeurCubes));

                    Vec2[] successivesPositionsList;
                    if (!cubeBonusAvantPresent) {
                        Vec2 forthCubeTas1 = TasCubes.getTasFromID(1).getCoordsVec2().plusNewVector(Cubes.getCubeNotInPattern(indicePattern).getRelativeCoordsVec2().dotFloat(this.largeurCubes));
                        if (!cubeBonusArrierePresent) {
                            Vec2 forthCubeTas2 = TasCubes.getTasFromID(1).getCoordsVec2().plusNewVector(Cubes.getCubeNotInPattern(indicePattern).getRelativeCoordsVec2().dotFloat(this.largeurCubes));
                            successivesPositionsList = new Vec2[]{firstCubeTas1, firstCubeTas2, secondCubeTas1, secondCubeTas2, thirdCubeTas1, thirdCubeTas2, forthCubeTas1, forthCubeTas2};
                        } else {
                            successivesPositionsList = new Vec2[]{firstCubeTas1, firstCubeTas2, secondCubeTas1, secondCubeTas2, thirdCubeTas1, thirdCubeTas2, forthCubeTas1};
                        }
                    } else {
                        if (!cubeBonusArrierePresent) {
                            Vec2 forthCubeTas2 = TasCubes.getTasFromID(1).getCoordsVec2().plusNewVector(Cubes.getCubeNotInPattern(indicePattern).getRelativeCoordsVec2().dotFloat(this.largeurCubes));
                            successivesPositionsList = new Vec2[]{firstCubeTas1, firstCubeTas2, secondCubeTas1, secondCubeTas2, thirdCubeTas1, thirdCubeTas2, forthCubeTas2};
                        } else {
                            successivesPositionsList = new Vec2[]{firstCubeTas1, firstCubeTas2, secondCubeTas1, secondCubeTas2, thirdCubeTas1, thirdCubeTas2};
                        }
                    }

                    state.setTourAvantRemplie(true);
                    state.setTourArriereRemplie(true);
                    state.setTas_station_epuration_pris(true);
                    state.setTas_chateau_eau_pris(true);

                    int currentIdealPositionInFrontTower = 0;
                    int currentIdealPositionInBackTower = 0;

                    state.robot.moveNearPoint(successivesPositionsList[0], longueurBras, "forward");
                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, false);
                    state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);
                    state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_AVANT, false);
                    if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAV()){
                        state.setReussitesTourAvant(1,currentIdealPositionInFrontTower);
                        state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAV(false);
                    }
                    else{
                        state.setReussitesTourAvant(0,currentIdealPositionInFrontTower);
                    }
                    currentIdealPositionInFrontTower++;

                    state.robot.moveNearPoint(successivesPositionsList[1], longueurBras, "backward");
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_ARRIERE, false);
                    if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAR()){
                        state.setReussitesTourArrière(1,currentIdealPositionInBackTower);
                        state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAR(false);
                    }
                    else{
                        state.setReussitesTourArrière(0,currentIdealPositionInBackTower);
                    }
                    currentIdealPositionInBackTower++;

                    state.robot.moveNearPoint(successivesPositionsList[2], longueurBras, "forward");
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_AVANT, false);
                    if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAV()){
                        state.setReussitesTourAvant(1,currentIdealPositionInFrontTower);
                        state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAV(false);
                    }
                    else{
                        state.setReussitesTourAvant(0,currentIdealPositionInFrontTower);
                    }
                    currentIdealPositionInFrontTower++;

                    state.robot.moveNearPoint(successivesPositionsList[3], longueurBras, "backward");
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_ARRIERE, false);
                    if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAR()){
                        state.setReussitesTourArrière(1,currentIdealPositionInBackTower);
                        state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAR(false);
                    }
                    else{
                        state.setReussitesTourArrière(0,currentIdealPositionInBackTower);
                    }
                    currentIdealPositionInBackTower++;

                    state.robot.moveNearPoint(successivesPositionsList[4], longueurBras, "forward");
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_AVANT, false);
                    if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAV()){
                        state.setReussitesTourAvant(1,currentIdealPositionInFrontTower);
                        state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAV(false);
                    }
                    else{
                        state.setReussitesTourAvant(0,currentIdealPositionInFrontTower);
                    }
                    currentIdealPositionInFrontTower++;

                    state.robot.moveNearPoint(successivesPositionsList[5], longueurBras, "backward");
                    state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU, false);
                    state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_ARRIERE, false);
                    if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAR()){
                        state.setReussitesTourArrière(1,currentIdealPositionInBackTower);
                        state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAR(false);
                    }
                    else{
                        state.setReussitesTourArrière(0,currentIdealPositionInBackTower);
                    }
                    currentIdealPositionInBackTower++;

                    if (successivesPositionsList.length > 6) {
                        state.robot.moveNearPoint(successivesPositionsList[6], longueurBras, "forward");
                        state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU, false);
                        state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
                        state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, true);
                        state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU, false);
                        state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
                        state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
                        state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_AVANT, false);
                        if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAV()){
                            state.setReussitesTourAvant(1,currentIdealPositionInFrontTower);
                            state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAV(false);
                        }
                        else{
                            state.setReussitesTourAvant(0,currentIdealPositionInFrontTower);
                        }
                        currentIdealPositionInFrontTower++;
                    }

                    if (successivesPositionsList.length > 7) {
                        state.robot.moveNearPoint(successivesPositionsList[7], longueurBras, "backward");
                        state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU, false);
                        state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
                        state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, true);
                        state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU, false);
                        state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
                        state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
                        state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_ARRIERE, false);
                        if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAR()){
                            state.setReussitesTourArrière(1,currentIdealPositionInBackTower);
                            state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAR(false);
                        }
                        else{
                            state.setReussitesTourArrière(0,currentIdealPositionInBackTower);
                        }
                        currentIdealPositionInBackTower++;
                    }

                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
                    state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
                    state.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, true);
                    state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
                    state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);

                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,false);
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE,false);



//////////////////// LE SCRIPT A FINI SES MOUVEMENTS //////////////////////////



                    Vec2 exitPoint = new Vec2(800,1300);
                    if (state.robot.getPosition().distance(exitPoint)>20) {
                        state.robot.goTo(exitPoint);
                    }
                }
                else{
                    log.critical("Le pattern n'a pas été reconnu");
                    throw new ExecuteException(new PatternNotRecognizedException("Le pattern n'a pas été reconnu"));
                }
            }
            else{
                log.critical("Exécution script de récupération des cubes avant que le pattern ait été calculé");
                throw new ExecuteException(new PatternNotYetCalculatedException("Le pattern n'a pas encore été calculé"));
            }
        }
    }



    private void takeThisCube(GameState stateToConsider, BrasUtilise bras, int idealPositionInTower) throws InterruptedException{
        //Vazy wesh si t'as besoin d'explications pour ça c'est que tu sais pas lire
        stateToConsider.robot.useActuator(ActuatorOrder.BASIC_DETECTION_DISABLE,true);
        if (bras==BrasUtilise.AVANT) {
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_AVANT,false);
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU, true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU,false);
            if(stateToConsider.robot.getmLocomotion().getThEvent().getCubeTakenBrasAV()){
                stateToConsider.setReussitesTourAvant(1,idealPositionInTower);
                stateToConsider.robot.getmLocomotion().getThEvent().setCubeTakenBrasAV(false);
            }
            else{
                stateToConsider.setReussitesTourAvant(0,idealPositionInTower);
            }
        }
        else if (bras==BrasUtilise.ARRIERE) {
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,false);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_ARRIERE, false);
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU, true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE_UNPEU,false);
            if(stateToConsider.robot.getmLocomotion().getThEvent().getCubeTakenBrasAR()){
                stateToConsider.setReussitesTourArrière(1,idealPositionInTower);
                stateToConsider.robot.getmLocomotion().getThEvent().setCubeTakenBrasAR(false);
            }
            else{
                stateToConsider.setReussitesTourArrière(0,idealPositionInTower);
            }
        }
        stateToConsider.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException{
        if (version>5 || version<0){
            if (version == 120){
                return new Circle(new Vec2(1000,1500));
            }
            else {
                throw new BadVersionException("Bad version exception : la version doit être comprise entre 0 et 5 (bornes incluses)");
            }
        }
        TasCubes tas = TasCubes.getTasFromID(version);
        Vec2 coordsTas = tas.getCoordsVec2();

        Circle aimArcCircle;
        if (version==0){
            aimArcCircle = new Circle(coordsTas, this.longueurBras, 0, Math.PI, true);
        }
        else if (version==1) {
            aimArcCircle = new Circle(coordsTas, this.longueurBras, Math.PI / 2, 3 * 9 * Math.PI / 20, true);
        }
        else if (version==2) {
            aimArcCircle = new Circle(coordsTas, this.longueurBras, -Math.PI, 0, true);
        }
        else if (version==3) {
            aimArcCircle = new Circle(coordsTas, this.longueurBras, -Math.PI, 0, true);
        }
        else if (version==4) {
            aimArcCircle = new Circle(coordsTas, this.longueurBras, - 9 * Math.PI / 20, Math.PI / 2, true);
        }
        else if (version==5) {
            aimArcCircle = new Circle(coordsTas, this.longueurBras, -Math.PI / 2, Math.PI / 2, true);
        }
        else{
            aimArcCircle = new Circle(coordsTas, this.longueurBras);
        }
        Vec2 aim = smartMath.Geometry.closestPointOnCircle(robotPosition,aimArcCircle);
        log.debug("Point d'entrée TakeCubes (version:"+version+") : "+aim);
        this.entryPositionPoint=aim;
        return new Circle(aim);
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException { }

    @Override
    public int remainingScoreOfVersion(int version, final GameState state) {
        return 40;
    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[]{};
    }

    @Override
    public void updateConfig() {
        super.updateConfig();
        this.largeurCubes=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        this.longueurBras=config.getInt(ConfigInfoRobot.LONGUEUR_BRAS);
    }
}
