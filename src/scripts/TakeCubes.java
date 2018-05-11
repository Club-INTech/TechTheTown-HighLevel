package scripts;

import enums.*;
import exceptions.*;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Vec2;
import strategie.GameState;
import tests.container.A;
import utils.Log;


/** Script permettant de récupérer les cubes de n'importe quel tas, selon n'importe quel pattern, dans n'importe quelle direction
 */
public class TakeCubes extends AbstractScript {
    private int largeurCubes;
    private int longueurBrasAvant;
    private int longueurBrasArriere;
    private int longueurBrasUtilise;

    private boolean usingBasicDetection;
    private boolean usingAdvancedDetection;

    private int indicePattern;
    private TasCubes currentTas;
    private String directionRobot;
    private BrasUtilise brasUtilise;
    private int currentIdealPositionInTower;
    private int timeAfterTakeCubesMustBeStopped;

    private boolean alreadyRemovedObstacle;

    private boolean alreadyTriedCorrection;
    private Vec2 correctionVectorTas;
    private Vec2 correctionVectorTas2;

    /**
     * @param config
     * @param log
     * @param hookFactory
     */
    public TakeCubes(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        this.updateConfig();
        this.longueurBrasUtilise = (this.longueurBrasAvant+this.longueurBrasArriere)/2;
        this.indicePattern=-2;
        this.timeAfterTakeCubesMustBeStopped=90000;
    }

    /** Execution du script de récupération des cubes
     * @param indiceTas le numéro du this.currentTas à récupérer
     * @param state le GameState permettant de connaître l'état de la partie
     */
    @Override
    public void execute(int indiceTas, GameState state)
            throws InterruptedException, ExecuteException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution TakeCubes version "+indiceTas+" //////////");
        this.alreadyTriedCorrection=false;
        this.alreadyRemovedObstacle=false;
        this.currentIdealPositionInTower=0;
        this.correctionVectorTas = new Vec2(0,0);
        this.correctionVectorTas2 = new Vec2(0,0);
        log.debug("Execute: AlreadyTriedCorrection; "+this.alreadyTriedCorrection);
        this.normalVersions(indiceTas, state);
        log.debug("////////// End TakeCubes version "+indiceTas+" //////////");
    }

    /**
     * @param indiceTas
     * @param state
     * @throws InterruptedException
     * @throws ExecuteException
     * @throws UnableToMoveException
     * @throws ImmobileEnnemyForOneSecondAtLeast
     */
    private void normalVersions(int indiceTas, GameState state) throws InterruptedException, ExecuteException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        //TODO mettre un choix par orientation du robot par rapport au tas

        //On récupère le tas correspondant à l'indice
        this.currentTas = TasCubes.getTasFromID(indiceTas);


        if (state.isTourAvantRemplie()){
            if (state.isTourArriereRemplie()){
                return;
            }
            else{
                this.brasUtilise=BrasUtilise.ARRIERE;
            }
        }
        else {
            if (state.isTourArriereRemplie()){
                this.brasUtilise=BrasUtilise.AVANT;
            }
            else{
                Vec2 directionToGo = this.currentTas.getCoordsVec2().minusNewVector(state.robot.getPosition());
                double prodScal = directionToGo.dot(new Vec2(100.0, state.robot.getOrientation()));
                if (prodScal > 0) {
                    this.brasUtilise=BrasUtilise.AVANT;
                } else {
                    this.brasUtilise=BrasUtilise.ARRIERE;
                }
            }
        }


        Cubes additionalCube;
        if(!(config.getBoolean(ConfigInfoRobot.SIMULATION))){
            while(!state.isRecognitionDone()){
                Thread.sleep(10);
            }
        }
        else{
            state.setIndicePattern(config.getInt(ConfigInfoRobot.INDICE_PATTERN_SIMULATION));
        }

        //On récupère l'indice du pattern
        this.indicePattern=state.getIndicePattern();

        //On regarde quel bras on utilise
        if (this.brasUtilise.equals(BrasUtilise.AVANT)){
            //On gère le cas où le cube bonus est encore présent
            if (state.isCubeBonusAvantPresent()){
                additionalCube=Cubes.NULL;
            }
            else{
                additionalCube=Cubes.getCubeNotInPattern(this.indicePattern);
            }
        }
        else{
            //On gère le cas où le cube bonus est encore présent
            if (state.isCubeBonusArrierePresent()){
                additionalCube=Cubes.NULL;
            }
            else{
                additionalCube=Cubes.getCubeNotInPattern(this.indicePattern);
            }
        }

        //Si indicePattern==-2, c'est que le pattern n'a pas encore été calculé
        if (this.indicePattern != -2){
            //Si indicePattern==-1, c'est que le pattern n'a pas pu être identifié
            if (this.indicePattern != -1) {


                //On active la pompe, et ouvre les électrovannes
                state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
                state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,true);
                state.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE, false);

                Vec2[] successivesPositionsList;


                //Si additionalCube.getColor()==Colors.NULL, c'est qu'on a choisi de ne prendre que 3 cubes
                //Sinon, la couleur de additionalCube sera correspondra au cube qui sera pris après le pattern
                if (additionalCube.getColor().equals(Colors.NULL)) {
                    successivesPositionsList = new Vec2[3];
                    //On sait que le premier cube dans la pile est le cube bonus, donc on l'indique dans les réussites de la tour
                    if (this.brasUtilise.equals(BrasUtilise.AVANT)) {
                        state.setReussitesTourAvant(1, this.currentIdealPositionInTower);
                    }
                    else{
                        state.setReussitesTourArrière(1, this.currentIdealPositionInTower);
                    }
                    this.currentIdealPositionInTower++;
                } else {
                    successivesPositionsList = new Vec2[4];
                    //On calcule les positions du cube additionnel pour x et y :
                    // position = position du tas + position relative du cube choisi par rapport au tas
                    //La position X relative par rapport au tas change si on passe de l'autre côté de la table
                    Vec2 additionalCubeRelativePosition = additionalCube.getRelativeCoordsVec2(this.currentTas).dotFloat(this.largeurCubes);
                    successivesPositionsList[3]=this.currentTas.getCoordsVec2().plusNewVector(additionalCubeRelativePosition);
                }

                //On récupère les couleurs composant le pattern reconnu (le pattern reconnu est identifié grâce à indicePattern)
                Colors[] pattern = Patterns.getPatternFromID(this.indicePattern);
                for (int i = 0; i < 3; i++) {
                    //On calcule les positions des cubes pour x et y :
                    // position = position du tas + position relative du cube choisi par rapport au tas
                    //La position X relative par rapport au tas change si on passe de l'autre côté de la table
                    Vec2 cubeRelativePosition = Cubes.getCubeFromColor(pattern[i]).getRelativeCoordsVec2(this.currentTas).dotFloat(this.largeurCubes);
                    successivesPositionsList[i]= this.currentTas.getCoordsVec2().plusNewVector(cubeRelativePosition);
                }

                if (this.brasUtilise.equals(BrasUtilise.ARRIERE)){
                    this.directionRobot="backward";
                    state.setTourArriereRemplie(true);
                    this.longueurBrasUtilise=this.longueurBrasArriere;
                }
                else{
                    this.directionRobot="forward";
                    state.setTourAvantRemplie(true);
                    this.longueurBrasUtilise=this.longueurBrasAvant;
                }

                if (indiceTas == 0) {
                    config.override(ConfigInfoRobot.TAS_BASE_PRIS, true);
                    state.setTas_base_pris(true);
                } else if (indiceTas == 1) {
                    config.override(ConfigInfoRobot.TAS_CHATEAU_PRIS, true);
                    state.setTas_chateau_eau_pris(true);
                } else if (indiceTas == 2) {
                    config.override(ConfigInfoRobot.TAS_STATION_EPURATION_PRIS, true);
                    state.setTas_station_epuration_pris(true);
                } else if (indiceTas == 3) {
                    config.override(ConfigInfoRobot.TAS_BASE_ENNEMI_PRIS, true);
                    state.setTas_base_ennemi_pris(true);
                } else if (indiceTas == 4) {
                    config.override(ConfigInfoRobot.TAS_CHATEAU_ENNEMI_PRIS, true);
                    state.setTas_chateau_ennemi_eau_pris(true);
                } else if (indiceTas == 5) {
                    config.override(ConfigInfoRobot.TAS_STATION_EPURATION_ENNEMI_PRIS, true);
                    state.setTas_station_epuration_ennemi_pris(true);
                }

                for (int i=0; i<3; i++) {
                    //Si on n'a plus le temps pour prendre le reste des cubes, on va déposer ce qu'on a déjà
                    if(state.getTimeEllapsed()<this.timeAfterTakeCubesMustBeStopped) {
                        //On fait aller le robot à la position pour prendre le premier cube du pattern
                        log.debug("Essaye de prendre le cube " + pattern[i].getName());
                        log.debug("AlreadyTriedCorrection; "+this.alreadyTriedCorrection);
                        state.robot.moveNearPoint(successivesPositionsList[i].plusNewVector(this.correctionVectorTas), longueurBrasUtilise, this.directionRobot);
                        //Le robot exception les actions pour prendre le cube
                        Cubes currentCube = Cubes.getCubeFromColor(pattern[i]);
                        boolean cubeSuccessfullyTaken = takeThisCube(state, currentCube);
                        this.currentIdealPositionInTower++;
                    }
                    else {
                        if (brasUtilise==BrasUtilise.AVANT) {
                            state.setReussitesTourAvant(0,this.currentIdealPositionInTower);
                        }
                        else {
                            state.setReussitesTourArrière(0,this.currentIdealPositionInTower);
                        }
                        this.currentIdealPositionInTower++;
                    }
                }

                //Si un cube additionnel a été précisé
                if (additionalCube.getColor()!=Colors.NULL){
                    //Si on n'a plus le temps pour prendre le reste des cubes, on va déposer ce qu'on a déjà
                    if(state.getTimeEllapsed()<this.timeAfterTakeCubesMustBeStopped) {
                        log.debug("Essaye de prendre le cube " + additionalCube.getColor().getName());
                        //On fait aller le robot à la position pour prendre le cube additionnel.
                        state.robot.moveNearPoint(successivesPositionsList[3].plusNewVector(this.correctionVectorTas), longueurBrasUtilise, this.directionRobot);
                        //Le robot exception les actions pour prendre le cube
                        takeThisCube(state, additionalCube);
                    }
                    else {
                        if (brasUtilise==BrasUtilise.AVANT) {
                            state.setReussitesTourAvant(0,this.currentIdealPositionInTower);
                        }
                        else {
                            state.setReussitesTourArrière(0,this.currentIdealPositionInTower);
                        }
                    }
                }

                state.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, false);
                state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
                state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);

                if(this.brasUtilise.equals(BrasUtilise.AVANT)){
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,false);
                }
                if(this.brasUtilise.equals(BrasUtilise.ARRIERE)){
                    state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE,false);
                }



//////////////////// LE SCRIPT A FINI SES MOUVEMENTS //////////////////////////


                int[] reussitesTour;
                if (brasUtilise==BrasUtilise.AVANT) {
                    reussitesTour = state.getReussitesTourAvant();
                    //Calcul des points du pattern
                    if (additionalCube.getColor().equals(Colors.NULL)) {
                        //Cas où le cube bones est présent
                        //On a besoin que du premier, troisieme et quatrieme cube pour réaliser le pattern
                        //La réalisation du pattern ne dépend pas de la présence du premier cube
                        if (reussitesTour[0] == 1 && reussitesTour[2] == 1 && reussitesTour[3] == 1) {
                            state.setPatternTourAvantReussi(true);
                        } else {
                            state.setPatternTourAvantReussi(false);
                        }
                    } else {
                        //Cas où le cube bonus n'est pas présent
                        //Il faut absolument que les trois premiers cubes qu'on a essayé de prendre soient présents dans la tour
                        if (reussitesTour[0] == 1 && reussitesTour[1] == 1 && reussitesTour[2] == 1) {
                            state.setPatternTourAvantReussi(true);
                        }
                        else{
                            state.setPatternTourAvantReussi(false);
                        }
                    }
                }
                else{
                    reussitesTour=state.getReussitesTourArriere();
                    if (additionalCube.getColor().equals(Colors.NULL)) {
                        //Cas où le cube bones est présent
                        //On a besoin que du premier, troisieme et quatrieme cube pour réaliser le pattern
                        //La réalisation du pattern ne dépend pas de la présence du premier cube
                        if (reussitesTour[0] == 1 && reussitesTour[2] == 1 && reussitesTour[3] == 1) {
                            state.setPatternTourArriereReussi(true);
                        } else {
                            state.setPatternTourArriereReussi(false);
                        }
                    } else {
                        //Cas où le cube bonus n'est pas présent
                        //Il faut absolument que les trois premiers cubes qu'on a essayé de prendre soient présents dans la tour
                        if (reussitesTour[0] == 1 && reussitesTour[1] == 1 && reussitesTour[2] == 1) {
                            state.setPatternTourArriereReussi(true);
                        }
                        else{
                            state.setPatternTourArriereReussi(false);
                        }
                    }
                }



                //On se décale du tas
                Circle aimArcCircle;
                if (indiceTas==0){
                    aimArcCircle = new Circle(this.currentTas.getCoordsVec2(), this.longueurBrasUtilise+this.largeurCubes*1.5+10, 0, Math.PI, true);
                }
                else if (indiceTas==1) {
                    aimArcCircle = new Circle(this.currentTas.getCoordsVec2(), this.longueurBrasUtilise+this.largeurCubes*1.5+10, Math.PI / 2, 3 * 9 * Math.PI / 20, true);
                }
                else if (indiceTas==2) {
                    aimArcCircle = new Circle(this.currentTas.getCoordsVec2(), this.longueurBrasUtilise+this.largeurCubes*1.5+10, -Math.PI, 0, true);
                }
                else if (indiceTas==3) {
                    aimArcCircle = new Circle(this.currentTas.getCoordsVec2(), this.longueurBrasUtilise+this.largeurCubes*1.5+10, -Math.PI, 0, true);
                }
                else if (indiceTas==4) {
                    aimArcCircle = new Circle(this.currentTas.getCoordsVec2(), this.longueurBrasUtilise+this.largeurCubes*1.5+10, - 9 * Math.PI / 20, Math.PI / 2, true);
                }
                else{
                    aimArcCircle = new Circle(this.currentTas.getCoordsVec2(), this.longueurBrasUtilise+this.largeurCubes*1.5+10, -Math.PI / 2, Math.PI / 2, true);
                }
                Vec2 aim = Geometry.closestPointOnCircle(state.robot.getPosition(),aimArcCircle);
                //On ne sort seulement si la distance nous séparant de la position de sortie est supérieure à 2cm
                if (state.robot.getPosition().distance(aim)>20) {
                    state.robot.goTo(aim);
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

















    /**
     *
     * @param state
     * @param currentCube
     * @return
     * @throws InterruptedException
     * @throws UnableToMoveException
     * @throws ImmobileEnnemyForOneSecondAtLeast
     */
    private boolean takeThisCube(GameState state, Cubes currentCube) throws InterruptedException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        boolean cubeSuccessfullyTaken=false;
        if (usingAdvancedDetection) {
            state.robot.useActuator(ActuatorOrder.SUS_OFF,true);
            state.setCapteursActivated(false);
        }
        if(usingBasicDetection){
            state.robot.setBasicDetection(false);
        }
        if (this.brasUtilise.equals(BrasUtilise.AVANT)){
            //Vazy wesh si t'as besoin d'explications pour ça c'est que tu sais pas lire
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
            state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);
            state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
            state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
            state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU, true);
            state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_AVANT,true);
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
            state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT_UNPEU,false);
            if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAV()){
                cubeSuccessfullyTaken=true;
                state.setReussitesTourAvant(1,this.currentIdealPositionInTower);
                state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAV(false);
            }
            else{
                cubeSuccessfullyTaken=false;
                state.setReussitesTourAvant(0,this.currentIdealPositionInTower);
                if (!this.alreadyTriedCorrection){
                    log.debug("Lancement de la correction de position du tas "+currentTas.getID());
                    this.correctionVectorTas = correctPosition(state, currentCube);
                    if (this.correctionVectorTas!=new Vec2(0,0)){
                        cubeSuccessfullyTaken=true;
                    }
                }
            }
        }
        else if (this.brasUtilise.equals(BrasUtilise.ARRIERE)){
            //Vazy wesh si t'as besoin d'explications pour ça c'est que tu sais pas lire
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,false);
            state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
            state.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
            state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
            state.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU, true);
            state.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_ARRIERE, true);
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
            state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE_UNPEU,false);
            if(state.robot.getmLocomotion().getThEvent().getCubeTakenBrasAR()){
                cubeSuccessfullyTaken=true;
                state.setReussitesTourArrière(1,this.currentIdealPositionInTower);
                state.robot.getmLocomotion().getThEvent().setCubeTakenBrasAR(false);
            }
            else{
                cubeSuccessfullyTaken=false;
                state.setReussitesTourArrière(0,this.currentIdealPositionInTower);
                if (!this.alreadyTriedCorrection){
                    log.debug("Lancement de la correction de position du tas "+currentTas.getID());
                    this.correctionVectorTas = correctPosition(state, currentCube);
                    if (this.correctionVectorTas!=new Vec2(0,0)){
                        cubeSuccessfullyTaken=true;
                    }
                }
            }
        }


        if (usingAdvancedDetection) {
            state.robot.useActuator(ActuatorOrder.SUS_ON,true);
            state.setCapteursActivated(true);
        }
        if(usingBasicDetection){
            state.robot.setBasicDetection(true);
        }

        return cubeSuccessfullyTaken;
    }


    /**
     *
     * @param state
     * @param currentCube
     * @return
     * @throws UnableToMoveException
     * @throws ImmobileEnnemyForOneSecondAtLeast
     * @throws InterruptedException
     */
    private Vec2 correctPosition(GameState state, Cubes currentCube) throws UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast, InterruptedException {
        this.alreadyTriedCorrection = true;
        Vec2 relativeCoordsCurrentCube = currentCube.getRelativeCoordsVec2(this.currentTas);
        Vec2 tableCoordsCurrentCube = this.currentTas.getCoordsVec2().plusNewVector(relativeCoordsCurrentCube.dotFloat(this.largeurCubes));
        Vec2[] correctionVectorList = new Vec2[4];
        int val = this.largeurCubes / 3;
        Vec2 finalOffsetVector = new Vec2(0, 0);
        if (relativeCoordsCurrentCube.equals(new Vec2(1, 0))) {
            correctionVectorList[0] = new Vec2(val, val);
            correctionVectorList[1] = new Vec2(val, -val);
            correctionVectorList[2] = new Vec2(-val, -val);
            correctionVectorList[3] = new Vec2(-val, val);
        } else if (relativeCoordsCurrentCube.equals(new Vec2(0, 1))) {
            correctionVectorList[0] = new Vec2(-val, val);
            correctionVectorList[1] = new Vec2(val, val);
            correctionVectorList[2] = new Vec2(val, -val);
            correctionVectorList[3] = new Vec2(-val, -val);
        } else if (relativeCoordsCurrentCube.equals(new Vec2(0, -1))) {
            correctionVectorList[0] = new Vec2(val, -val);
            correctionVectorList[1] = new Vec2(-val, -val);
            correctionVectorList[2] = new Vec2(-val, val);
            correctionVectorList[3] = new Vec2(val, val);
        } else if (relativeCoordsCurrentCube.equals(new Vec2(-1, 0))) {
            correctionVectorList[0] = new Vec2(-val, -val);
            correctionVectorList[1] = new Vec2(-val, val);
            correctionVectorList[2] = new Vec2(val, val);
            correctionVectorList[3] = new Vec2(val, -val);
        } else if (relativeCoordsCurrentCube.equals(new Vec2(0, 0))){
            return finalOffsetVector;
        }

        for (int i = 0; i < correctionVectorList.length; i++) {
            if(state.getTimeEllapsed()<this.timeAfterTakeCubesMustBeStopped) {
                state.robot.moveNearPoint(tableCoordsCurrentCube.plusNewVector(correctionVectorList[i]), this.longueurBrasUtilise, this.directionRobot);
                log.debug("Essai de correction avec le vecteur :"+correctionVectorTas);
                boolean cubeTakenSuccessfully = takeThisCube(state, currentCube);
                if (cubeTakenSuccessfully) {
                    finalOffsetVector = correctionVectorList[i];
                    break;
                }
            }
            else{
                break;
            }
        }

        log.debug("OffsetVector: " + finalOffsetVector);
        return finalOffsetVector;
    }

    /**
     * @param version version dont on veut le point d'entrée
     * @param robotPosition la position actuelle du robot
     * @return
     * @throws BadVersionException
     */
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
        this.currentTas = TasCubes.getTasFromID(version);
        Vec2 coordsTas = this.currentTas.getCoordsVec2();

        Circle aimArcCircle;
        if (version==0){
            aimArcCircle = new Circle(coordsTas, this.longueurBrasUtilise, 0, Math.PI, true);
        }
        else if (version==1) {
            aimArcCircle = new Circle(coordsTas, this.longueurBrasUtilise, Math.PI / 2, 3 * 9 * Math.PI / 20, true);
        }
        else if (version==2) {
            aimArcCircle = new Circle(coordsTas, this.longueurBrasUtilise, -Math.PI, 0, true);
        }
        else if (version==3) {
            aimArcCircle = new Circle(coordsTas, this.longueurBrasUtilise, -Math.PI, 0, true);
        }
        else if (version==4) {
            aimArcCircle = new Circle(coordsTas, this.longueurBrasUtilise, - 9 * Math.PI / 20, Math.PI / 2, true);
        }
        else if (version==5) {
            aimArcCircle = new Circle(coordsTas, this.longueurBrasUtilise, -Math.PI / 2, Math.PI / 2, true);
        }
        else{
            aimArcCircle = new Circle(coordsTas, this.longueurBrasUtilise);
        }
        Vec2 aim = smartMath.Geometry.closestPointOnCircle(robotPosition,aimArcCircle);
        log.debug("Point d'entrée TakeCubes (version:"+version+") : "+aim);
        return new Circle(aim);
    }

    /**
     * @param state : état du jeu au sein duquel il faut finaliser le script
     * @param e : l'exception qui a déclenché le finalize
     * @throws UnableToMoveException
     */
    @Override
    public void finalize(GameState state, Exception e) {
        state.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
        state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE,false);
        state.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT,false);
        state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT,false);
        state.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE,true);
        state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,false);
        state.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE,true);
    }

    /**
     *
     * @param version version dont on veut le score potentiel
     * @param state l'état du jeu ou l'on veut évaluer le nombre de point que rapporterait l'execution de la version fournie de ce script.
     * @return
     */
    @Override
    public int remainingScoreOfVersion(int version, final GameState state) {
        return 40;
    }

    /**
     * @param state état de jeu actuel
     * @return
     */
    @Override
    public int[] getVersion(GameState state) {
        return versions;
    }

    @Override
    public void goToThenExec(int versionToExecute, GameState state) throws PointInObstacleException, BadVersionException, NoPathFound, ExecuteException, BlockedActuatorException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        state.setLastScript(ScriptNames.TAKE_CUBES);
        state.setLastScriptVersion(versionToExecute);
        super.goToThenExec(versionToExecute, state);
    }

    /**
     * Update la config
     */
    @Override
    public void updateConfig() {
        super.updateConfig();
        TasCubes.setSymetry(config.getString(ConfigInfoRobot.COULEUR).equals("orange"));
        TasCubes.setMatchScriptVersion(config.getInt(ConfigInfoRobot.MATCHSCRIPT_TO_EXECUTE));
        this.largeurCubes=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        this.longueurBrasAvant=config.getInt(ConfigInfoRobot.LONGUEUR_BRAS_AVANT);
        this.longueurBrasArriere=config.getInt(ConfigInfoRobot.LONGUEUR_BRAS_ARRIERE);
        this.usingBasicDetection =config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
        this.usingAdvancedDetection =config.getBoolean(ConfigInfoRobot.ADVANCED_DETECTION);
    }
}
