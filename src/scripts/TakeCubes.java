package scripts;

import enums.*;
import exceptions.*;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
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
    private int scoreFinalCubes;

    public TakeCubes(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        this.updateConfig();
        this.scoreFinalCubes=0;
    }

    /** Execution du script de récupération des cubes
     * @param indiceTas le numéro du tas à récupérer
     * @param stateToConsider le GameState permettant de connaître l'état de la partie
     */
    @Override
    public void execute(int indiceTas, GameState stateToConsider)
            throws InterruptedException, ExecuteException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution TakeCubes version "+indiceTas+" //////////");

        BrasUtilise bras;
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

        //On récupère le tas correspondant à l'indice
        TasCubes tas = TasCubes.getTasFromID(indiceTas);
        bras=stateToConsider.getTakeCubesBras();

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

                int[][] successivesPositionsList;
                //Si additionalCube.getColor()==Colors.NULL, c'est qu'on a choisi de ne prendre que 3 cubes
                //Sinon, la couleur de additionalCube sera correspondra au cube qui sera pris après le pattern
                int currentIdealPositionInTower=0;
                boolean cubeBonusPresent;
                if (additionalCube.getColor() == Colors.NULL) {
                    cubeBonusPresent=true;
                    successivesPositionsList = new int[3][2];
                    //On sait que le premier cube dans la pile est le cube bonus, donc on l'indique dans les réussites de la tour
                    if (bras==BrasUtilise.AVANT) {
                        stateToConsider.setReussitesTourAvant(1, currentIdealPositionInTower);
                    }
                    else{
                        stateToConsider.setReussitesTourArrière(1, currentIdealPositionInTower);
                    }
                    currentIdealPositionInTower++;
                } else {
                    cubeBonusPresent=false;
                    successivesPositionsList = new int[4][2];
                    //On calcule les positions du cube additionnel pour x et y :
                    // position = position du tas + position relative du cube choisi par rapport au tas
                    successivesPositionsList[3][0] = tas.getCoords()[0] + additionalCube.getRelativeCoords()[0] * largeurCubes;
                    successivesPositionsList[3][1] = tas.getCoords()[1] + additionalCube.getRelativeCoords()[1] * largeurCubes;
                }

                //On récupère les couleurs composant le pattern reconnu (le pattern reconnu est identifié grâce à indicePattern)
                Colors[] pattern = Patterns.getPatternFromID(indicePattern);
                for (int i = 0; i < 3; i++) {
                    //On calcule les positions des cubes pour x et y :
                    // position = position du tas + position relative du cube choisi par rapport au tas
                    successivesPositionsList[i][0] = tas.getCoords()[0] + Cubes.findRelativeCoordsWithColor(pattern[i])[0] * largeurCubes;
                    successivesPositionsList[i][1] = tas.getCoords()[1] + Cubes.findRelativeCoordsWithColor(pattern[i])[1] * largeurCubes;
                }

                if (bras==BrasUtilise.ARRIERE){
                    direction="backward";
                    stateToConsider.setTourArriereRemplie(true);
                }
                else{
                    direction="forward";
                    stateToConsider.setTourAvantRemplie(true);
                }

                //On définit les Vec2 correspondant aux positions où le robot doit aller pour prendre les cubes
                Vec2 firstPosition = new Vec2(successivesPositionsList[0][0], successivesPositionsList[0][1]);
                //On fait aller le robot à la position pour prendre le premier cube du pattern
                stateToConsider.robot.moveNearPoint(firstPosition, longueurBras, direction);
                //Le robot execute les actions pour prendre le cube
                takeThisCube(stateToConsider, bras, currentIdealPositionInTower);
                currentIdealPositionInTower++;

                Vec2 secondPosition = new Vec2(successivesPositionsList[1][0], successivesPositionsList[1][1]);
                //On fait aller le robot à la position pour prendre le deuxième cube du pattern
                stateToConsider.robot.moveNearPoint(secondPosition, longueurBras, direction);
                //Le robot execute les actions pour prendre le cube
                takeThisCube(stateToConsider, bras, currentIdealPositionInTower);
                currentIdealPositionInTower++;

                Vec2 thirdPosition = new Vec2(successivesPositionsList[2][0], successivesPositionsList[2][1]);
                //On fait aller le robot à la position pour prendre le troisième cube du pattern
                stateToConsider.robot.moveNearPoint(thirdPosition, longueurBras, direction);
                //Le robot execute les actions pour prendre le cube
                takeThisCube(stateToConsider, bras, currentIdealPositionInTower);
                currentIdealPositionInTower++;

                //Si un cube additionnel a été précisé
                if (additionalCube.getColor()!=Colors.NULL){
                    //On définit le Vec2 de la position permettant de prendre le cube additionnel
                    Vec2 fourthPosition = new Vec2(successivesPositionsList[3][0], successivesPositionsList[3][1]);
                    //On fait aller le robot à la position pour prendre le cube additionnel.
                    stateToConsider.robot.moveNearPoint(fourthPosition, longueurBras, direction);
                    //Le robot execute les actions pour prendre le cube
                    takeThisCube(stateToConsider, bras, currentIdealPositionInTower);
                }

                stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, false);


                ///// LE SCRIPT A FINI SES MOUVEMENTS /////


                if (bras==BrasUtilise.AVANT){
                    if (cubeBonusPresent){
                        //On considère que le cube bonus n'est plus présent, afin de ne pas biaiser la prochaine exécution de TakeCubes
                        stateToConsider.setCubeBonusAvantPresent(false);
                    }
                    //On calcule le score
                    scoreFinalCubes = calculScore(true, cubeBonusPresent, stateToConsider);
                }
                else{
                    if (cubeBonusPresent){
                        //On considère que le cube bonus n'est plus présent, afin de ne pas biaiser la prochaine exécution de TakeCubes
                        stateToConsider.setCubeBonusArrierePresent(false);
                    }
                    //On calcule le score
                    scoreFinalCubes = calculScore(false, cubeBonusPresent, stateToConsider);
                }


                //On reset les réussites de tours avant et arrières
                for (int i=0; i<4; i++) {
                    stateToConsider.setReussitesTourArrière(-1,i);
                    stateToConsider.setReussitesTourAvant(-1,i);
                }


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
        if(bras.equals(BrasUtilise.AVANT)){
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,false);
        }
        if(bras.equals(BrasUtilise.ARRIERE)){
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE,false);
        }

        log.debug("////////// End TakeCubes version "+indiceTas+" //////////");
    }


    private void takeThisCube(GameState stateToConsider, BrasUtilise bras, int idealPositionInTower) throws InterruptedException{
        //Vazy wesh si t'as besoin d'explications pour ça c'est que tu sais pas lire
        if (bras==BrasUtilise.AVANT) {
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
            stateToConsider.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);
            stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
            stateToConsider.robot.useActuator(ActuatorOrder.CHECK_CAPTEURS_CUBE_AVANT,false);
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU, true);
            stateToConsider.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.FERMER_LA_PORTE_AVANT_UNPEU,false);
            if(stateToConsider.robot.getmLocomotion().getThEvent().getCubeTakenBrasAV()){
                stateToConsider.setReussitesTourArrière(1,idealPositionInTower);
                log.debug("Un cube a été pris avec le bras avant");
                stateToConsider.robot.getmLocomotion().getThEvent().setCubeTakenBrasAV(false);
            }
            else{
                stateToConsider.setReussitesTourArrière(0,idealPositionInTower);
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
            stateToConsider.robot.useActuator(ActuatorOrder.FERMER_LA_PORTE_ARRIERE_UNPEU,false);
            if(stateToConsider.robot.getmLocomotion().getThEvent().getCubeTakenBrasAR()){
                stateToConsider.setReussitesTourArrière(1,idealPositionInTower);
                log.debug("Un cube a été pris avec le bras arrière");
                stateToConsider.robot.getmLocomotion().getThEvent().setCubeTakenBrasAR(false);
            }
            else{
                stateToConsider.setReussitesTourArrière(0,idealPositionInTower);
            }
        }
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException{
        if (version>5 || version<0){
            throw new BadVersionException("Bad version exception : la version doit être comprise entre 0 et 5 (bornes incluses)");
        }
        TasCubes tas = TasCubes.getTasFromID(version);
        int[] coords = tas.getCoords();
        Vec2 coordsTas=new Vec2(coords[0],coords[1]);

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
        return this.scoreFinalCubes;
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

        //Calcul des points du pattern
        if (cubeBonusPresent) {
            //Cas où le cube bones est présent
            //On a besoin que du premier, troisieme et quatrieme cube pour réaliser le pattern
            //La réalisation du pattern ne dépend pas de la présence du premier cube
            if (reussitesTour[0] == 1 && reussitesTour[2] == 1 && reussitesTour[3] == 1) {
                score += 30;
            }
        }
        else{
            //Cas où le cube bonus n'est pas présent
            //Il faut absolument que les trois premiers cubes qu'on a essayé de prendre soient présents dans la tour
            if (reussitesTour[0] == 1 && reussitesTour[1] == 1 && reussitesTour[2] == 1) {
                score += 30;
            }
        }

        //Calcul des points de la construction de la tour
        int sum=reussitesTour[0]+reussitesTour[1]+reussitesTour[2]+reussitesTour[3];
        score+=sum*(sum+1)/2;

        return score;
    }

    public int getScoreFinalCubes() {
        return scoreFinalCubes;
    }
}
