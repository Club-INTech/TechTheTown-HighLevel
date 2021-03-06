package enums;

//Enum de couleurs des cubes (id, nom et RGB pour chaque couleur) et donc des patterns
public enum Colors {

    //Couleurs données par les règles de la coupe
    /*ORANGE(0,"orange", new int[]{208, 93, 40}),
    YELLOW(1,"yellow", new int[]{247,181,0}),
    BLUE(2,"blue", new int[]{0,124,176}),
    BLACK(3,"black", new int[]{14,14,16}),
    GREEN(4,"green", new int[]{97,153,59});*/

    ORANGE(0,"orange", new int[3]),
    YELLOW(1,"yellow", new int[3]),
    BLUE(2,"blue", new int[3]),
    BLACK(3,"black", new int[3]),
    GREEN(4,"green", new int[3]),
    NULL(-1,"null",new int[3]);

    private int[] RGB;      //int[3] R,G et B, calibrés
    private String name;    //nom de la couleur
    private int id;         //id de la couleur
    private ConfigInfoRobot configInfoRobot;

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

    //Renvoie l'ID de la couleur
    public int getID() { return id; }

    //Renvoie le RGB d'une couleur en fonction d'un nom de couleur donné
    public static int[] getRGBFromName(String givenName){
        int[] RGB = {};
        for (Colors color : Colors.values()){
            if (givenName.equals(color.name)){
                RGB=color.RGB;
                break;
            }
        }
        return RGB;
    }

    public static Colors getColorFromName(String givenName){
        Colors colorFound=Colors.NULL;
        for (Colors color : Colors.values()){
            if (givenName.equals(color.name)){
                colorFound=color;
                break;
            }
        }
        return colorFound;
    }

    //Renvoie le RGB d'une couleur en fonction d'un ID donné
    public static int[] getRGBFromID(int id){
        int[] RGB = {};
        for (Colors color : Colors.values()){
            if (id==color.id){
                RGB=color.RGB;
                break;
            }
        }
        return RGB;
    }

    public static String getNameFromID(int id){
        String name="";
        for (Colors color : Colors.values()){
            if (id==color.id){
                name=color.name;
                break;
            }
        }
        return name;
    }

    public void setRGB(int r, int g, int b) {
        this.RGB[0]=r;
        this.RGB[1]=g;
        this.RGB[2]=b;
    }
}
