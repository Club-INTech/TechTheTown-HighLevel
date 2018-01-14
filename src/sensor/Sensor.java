package sensor;

import enums.ConfigInfoRobot;

public class Sensor{
    private int id;                                     //ID du capteur
    private double x;                                   //en mm
    private double y;                                   //en mm
    private double detectionWideness;                   //en radians, angle du cône de détection (peut etre de 0 radians si capteur en ligne droite)
    private double detectionAnglePosition;              //en radians, angle du milieu du cône de détection avec la face avant du robot
    private double maximalValidDetectionDistance;       //en mm
    private double detectedDistance;                    //en mm
    private double precision;                           //en mm (precision de 2mm <==> +- 2mm)
    private double robotSize;                           //en mm

    public Sensor(int id, double xRelativeToRobotCenter, double yRelativeToRobotCenter, double detectionAnglePosition, double detectionWideness, double maximalValidDetectionDistance, double precision){
        this.id=id;
        this.x=xRelativeToRobotCenter;
        this.y=yRelativeToRobotCenter;
        this.detectionAnglePosition=detectionAnglePosition;
        this.detectionWideness=detectionWideness;
        this.maximalValidDetectionDistance=maximalValidDetectionDistance;
        this.detectedDistance=0;
        this.precision=0;
        this.robotSize=Double.parseDouble(ConfigInfoRobot.ROBOT_RADIUS.getDefaultValue().toString());
    }

    /**Fonction permettant d'affecter la valeur réelle mesurée par un capteur à l'objet Sensor.
     * @param detectedDistance
     */
    public void setDetectedDistance(double detectedDistance){
        this.detectedDistance=detectedDistance;
    }

    int getID(){
        return this.id;
    }
}
