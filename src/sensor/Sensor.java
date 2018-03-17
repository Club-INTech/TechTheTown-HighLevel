package sensor;

import enums.ConfigInfoRobot;
import smartMath.Vec2;

public class Sensor{
    private int id;                                     //ID du capteur
    private double x;                                   //en mm
    private double y;                                   //en mm
    private Vec2 vecteur = new Vec2(x,y);
    private double detectionWideness;                   //en radians, angle du cône de détection (peut etre de 0 radians si capteur en ligne droite)
    private double sensorOrientation;                   //en radians, angle du milieu du cône de détection avec la face avant du robot (sens trigonométrique)
    private double maximalValidDetectionDistance;       //en mm
    private double detectedDistance;                    //en mm
    private double uncertainty;                           //en mm (uncertainty of 2mm <==> +- 2mm)
    private double robotSize;                           //en mm

    public Sensor(int id, double xRelativeToRobotCenter, double yRelativeToRobotCenter, double sensorOrientation, double detectionWideness, double maximalValidDetectionDistance, double uncertainty){
        this.id=id;
        this.x=xRelativeToRobotCenter;
        this.y=yRelativeToRobotCenter;
        this.sensorOrientation = sensorOrientation;
        this.detectionWideness=detectionWideness;
        this.maximalValidDetectionDistance=maximalValidDetectionDistance;
        this.detectedDistance=0;
        this.uncertainty=uncertainty;
        this.robotSize=Double.parseDouble(ConfigInfoRobot.ROBOT_RADIUS.getDefaultValue().toString());
    }

    public Sensor(int id, int xRelativeToRobotCenter, int yRelativeToRobotCenter, int sensorOrientation, double detectionWideness, int maximalValidDetectionDistance, double uncertainty){
        this.id=id;
        this.x=(double)xRelativeToRobotCenter;
        this.y=(double)yRelativeToRobotCenter;
        this.sensorOrientation =(double) sensorOrientation;
        this.detectionWideness=detectionWideness;
        this.maximalValidDetectionDistance=(double)maximalValidDetectionDistance;
        this.detectedDistance=0;
        this.uncertainty=uncertainty;
        this.robotSize=Double.parseDouble(ConfigInfoRobot.ROBOT_RADIUS.getDefaultValue().toString());
    }

    public Sensor(int id, int xRelativeToRobotCenter, int yRelativeToRobotCenter, double sensorOrientation, double detectionWideness, int maximalValidDetectionDistance, double uncertainty){
        this.id=id;
        this.x=(double)xRelativeToRobotCenter;
        this.y=(double)yRelativeToRobotCenter;
        this.sensorOrientation = sensorOrientation;
        this.detectionWideness=detectionWideness;
        this.maximalValidDetectionDistance=(double)maximalValidDetectionDistance;
        this.detectedDistance=0;
        this.uncertainty=uncertainty;
        this.robotSize=Double.parseDouble(ConfigInfoRobot.ROBOT_RADIUS.getDefaultValue().toString());
    }

    public Sensor(int id, int xRelativeToRobotCenter, int yRelativeToRobotCenter, double sensorOrientation, double detectionWideness, double maximalValidDetectionDistance, double uncertainty){
        this.id=id;
        this.x=(double)xRelativeToRobotCenter;
        this.y=(double)yRelativeToRobotCenter;
        this.sensorOrientation = sensorOrientation;
        this.detectionWideness=detectionWideness;
        this.maximalValidDetectionDistance=(double)maximalValidDetectionDistance;
        this.detectedDistance=0;
        this.uncertainty=uncertainty;
        this.robotSize=Double.parseDouble(ConfigInfoRobot.ROBOT_RADIUS.getDefaultValue().toString());
    }

    /**Fonction permettant d'affecter la valeur réelle mesurée par un capteur à l'objet Sensor.
     * @param detectedDistance
     */
    public void setDetectedDistance(double detectedDistance){
        this.detectedDistance=detectedDistance;
    }

    public int getID(){
        return this.id;
    }

    public double getX(){ return this.x; }
    public int getIntX(){
        Double a = this.x;
        return a.intValue();
    }
    public double getY(){ return this.y; }
    public int getIntY(){
        Double a = this.y;
        return a.intValue();
    }
    public Vec2 getVecteur(){ return this.vecteur; }

    public double getSensorOrientation(){ return this.sensorOrientation; }

    public double getDetectedDistance() { return this.detectedDistance; }

    public double getDetectionWideness() { return this.detectionWideness; }

    public int getIntDetectedDistance() {
        Double a = this.detectedDistance;
        return a.intValue();
    }

    public String getStringDetectedDistance(){
        Double a = this.detectedDistance;
        return a.toString();
    }

    //En cas de symétrie
    public void switchValues(Sensor s2){
        double temp=this.detectedDistance;
        this.detectedDistance=s2.getDetectedDistance();
        s2.setDetectedDistance(temp);
    }
}
