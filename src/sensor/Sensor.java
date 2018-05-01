package sensor;

import enums.ConfigInfoRobot;
import smartMath.Vec2;

public class Sensor{
    private int id;                                     //ID du capteur
    private int x;                                   //en mm
    private int y;                                   //en mm
    private Vec2 vecteur;
    private double detectionWideness;                   //en radians, angle du cône de détection (peut etre de 0 radians si capteur en ligne droite)
    private double sensorOrientation;                   //en radians, angle du milieu du cône de détection avec la face avant du robot (sens trigonométrique)
    private int maximalValidDetectionDistance;       //en mm
    private int minimalValidDetectionDistance;       //en mm
    private int detectedDistance;                 //en mm
    private int rawDetectedDistance;                 //en mm
    private double uncertainty;                           //en mm (uncertainty of 2mm <==> +- 2mm)

    public Sensor(int id, int xRelativeToRobotCenter, int yRelativeToRobotCenter, double sensorOrientation, double detectionWideness, int maximalValidDetectionDistance, int minimalValidDetectionDistance, double uncertainty){
        this.id=id;
        this.x=xRelativeToRobotCenter;
        this.y=yRelativeToRobotCenter;
        this.vecteur=new Vec2(this.x,this.y);
        this.sensorOrientation = sensorOrientation;
        this.detectionWideness=detectionWideness;
        this.maximalValidDetectionDistance=maximalValidDetectionDistance;
        this.minimalValidDetectionDistance=minimalValidDetectionDistance;
        this.detectedDistance=0;
        this.rawDetectedDistance=0;
        this.uncertainty=uncertainty;
    }

    /**Fonction permettant d'affecter la valeur réelle mesurée par un capteur à l'objet Sensor.
     * @param detectedDistance
     */
    public void setDetectedDistance(int detectedDistance){
        this.rawDetectedDistance=detectedDistance;
        if (detectedDistance>this.maximalValidDetectionDistance){
            this.detectedDistance=0;
        }
        else if (detectedDistance<this.minimalValidDetectionDistance){
            this.detectedDistance=0;
        }
        else {
            this.detectedDistance = detectedDistance;
        }
    }

    public int getID(){
        return this.id;
    }

    public int getX(){ return this.x; }
    public int getY(){ return this.y; }

    public Vec2 getVecteur(){ return this.vecteur; }

    public int getDetectedDistance() {
        return this.detectedDistance;
    }

    public int getRawDetectedDistance() { return this.rawDetectedDistance; }

    public double getSensorOrientation(){ return this.sensorOrientation; }

    public double getDetectionWideness() { return this.detectionWideness; }

    public int getMaximalValidDetectionDistance() { return this.maximalValidDetectionDistance; }

    public int getMinimalValidDetectionDistance() { return this.minimalValidDetectionDistance; }

    public int getIntDetectedDistance() {
        return this.detectedDistance;
    }

    public String getStringDetectedDistance(){
        Integer a = this.detectedDistance;
        return a.toString();
    }

    //En cas de symétrie
    public void switchValues(Sensor s2){
        int temp=this.detectedDistance;
        this.detectedDistance=s2.getDetectedDistance();
        s2.setDetectedDistance(temp);
    }
}
