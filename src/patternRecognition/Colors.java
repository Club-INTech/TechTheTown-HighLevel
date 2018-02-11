package patternRecognition;

import java.util.Objects;

//Enum de couleurs des cubes (id, nom et RGB pour chaque couleur) et donc des patterns
public enum Colors {
    //Couleurs données par les règles de la coupe
    /*ORANGE(0,"orange", new int[]{208, 93, 40}),
    YELLOW(1,"yellow", new int[]{247,181,0}),
    BLUE(2,"blue", new int[]{0,124,176}),
    BLACK(3,"black", new int[]{14,14,16}),
    GREEN(4,"green", new int[]{97,153,59});*/

    /*
    //Couleurs calibrées par rapport à la photo ImageRaspberry1.png
    ORANGE(0,"orange", new int[]{252, 88, 46}),
    YELLOW(1,"yellow", new int[]{254,221,0}),
    BLUE(2,"blue", new int[]{38,109,150}),
    BLACK(3,"black", new int[]{66,68,26}),
    GREEN(4,"green", new int[]{142,196,45});
    */


    //Couleurs calibrées par rapport à une photo normale au local Intech
    ORANGE(0,"orange", new int[]{147, 80, 28}),
    YELLOW(1,"yellow", new int[]{153,142,0}),
    BLUE(2,"blue", new int[]{23,54,76}),
    BLACK(3,"black", new int[]{18,34,10}),
    GREEN(4,"green", new int[]{83,125,20});

    /*
    //Couleurs RGB calibrées par rapport à la photo ImageRaspberry5.png
    ORANGE(0,"orange", new int[]{170, 40, 11}),
    YELLOW(1,"yellow", new int[]{229,149,0}),
    BLUE(2,"blue", new int[]{0,51,90}),
    BLACK(3,"black", new int[]{9,15,0}),
    GREEN(4,"green", new int[]{84,129,7});
    */

    private int[] RGB;      //int[3] R,G et B, calibrés
    private String name;    //nom de la couleur
    private int id;         //id de la couleur

    //Constructeur
    Colors(int id, String name, int[] RGB){
        this.name=name;
        this.RGB=RGB;
        this.id=id;
    }

    //Renvoie le code RGB de la couleur
    public int[] getRGB(){
        return RGB;
    }
    //Renvoie le nom de la couleur
    public String getName() {
        return name;
    }

    //renvoie l'ID de la couleur
    public int getID() { return id; }

    //Renvoie le RGB d'une couleur en fonction d'un nom de couleur donné
    public static int[] getRGBFromName(String givenName){
        int[] RGB = {};
        for (Colors color : Colors.values()){
            if (Objects.equals(givenName, color.name)){
                RGB=color.RGB;
                break;
            }
        }
        return RGB;
    }

    //Renvoie le RGB d'une couleur en fonction d'un ID donné
    public static int[] getRGBFromID(int id){
        int[] RGB = {};
        for (Colors color : Colors.values()){
            if (Objects.equals(id, color.id)){
                RGB=color.RGB;
                break;
            }
        }
        return RGB;
    }

    public static String getNameFromID(int id){
        String name="";
        for (Colors color : Colors.values()){
            if (Objects.equals(id, color.id)){
                name=color.name;
                break;
            }
        }
        return name;
    }
}
