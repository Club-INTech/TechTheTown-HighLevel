package enums;

//Enum de tous les patterns possibles
public enum Patterns {

    //Les noms des patterns ont été créés comme ceci :
    //Prenons le pattern (blue, orange, black)
    //On prend la première et ledernière lettre du nom anglais de la lettre
    //D'où B lu E, O rang E, B lac K
    //En gardant ces lettres, on trouve : BEOEBK, ce qui correspond au pattern 6
    NULL(-1, new Colors[] {Colors.ORANGE, Colors.YELLOW, Colors.GREEN}),
    OEBKGN(0, new Colors[] {Colors.ORANGE, Colors.BLACK, Colors.GREEN}),
    YWBKBE(1, new Colors[] {Colors.BLUE, Colors.BLACK, Colors.YELLOW}), //Ordre des couleurs inversés pour éviter que le jaune soit en premier
    BEGNOE(2, new Colors[] {Colors.BLUE, Colors.GREEN, Colors.ORANGE}),
    YWGNBK(3, new Colors[] {Colors.BLACK, Colors.GREEN, Colors.YELLOW}), //Ordre des couleurs inversés pour éviter que le jaune soit en premier
    BKYWOE(4, new Colors[] {Colors.BLACK, Colors.YELLOW, Colors.ORANGE}),
    GNYWBE(5, new Colors[] {Colors.GREEN, Colors.YELLOW, Colors.BLUE}),
    BEOEBK(6, new Colors[] {Colors.BLUE, Colors.ORANGE, Colors.BLACK}),
    GNOEYW(7, new Colors[] {Colors.GREEN, Colors.ORANGE, Colors.YELLOW}),
    BKBEGN(8, new Colors[] {Colors.BLACK, Colors.BLUE, Colors.GREEN}),
    OEBEYW(9, new Colors[] {Colors.ORANGE, Colors.BLUE, Colors.YELLOW}),
    OEBKGN2(10, new Colors[] {Colors.GREEN, Colors.BLACK, Colors.ORANGE}),
    YWBKBE2(11, new Colors[] {Colors.YELLOW, Colors.BLACK, Colors.BLUE}),  //Ordre des couleurs inversés pour éviter que le jaune soit en premier dans le pattern 1
    BEGNOE2(12, new Colors[] {Colors.ORANGE, Colors.GREEN, Colors.BLUE}),
    YWGNBK2(13, new Colors[] {Colors.YELLOW, Colors.GREEN, Colors.BLACK}),  //Ordre des couleurs inversés pour éviter que le jaune soit en premier dans le pattern 3
    BKYWOE2(14, new Colors[] {Colors.ORANGE, Colors.YELLOW, Colors.BLACK}),
    GNYWBE2(15, new Colors[] {Colors.BLUE, Colors.YELLOW, Colors.GREEN}),
    BEOEBK2(16, new Colors[] {Colors.BLACK, Colors.ORANGE, Colors.BLUE}),
    GNOEYW2(17, new Colors[] {Colors.YELLOW, Colors.ORANGE, Colors.GREEN}),
    BKBEGN2(18, new Colors[] {Colors.GREEN, Colors.BLUE, Colors.BLACK}),
    OEBEYW2(19, new Colors[] {Colors.YELLOW, Colors.BLUE, Colors.ORANGE});

    private Colors[] pattern;
    private int id;

    //Constructeur
    Patterns(int id, Colors[] pattern){
        this.id=id;
        this.pattern=pattern;
    }

    //Renvoie l'id du pattern
    public int getNumber() { return id; }

    //Renvoie la suite de couleurs composant le pattern
    public Colors[] getPattern(){
        return pattern;
    }

    //Renvoie la suite de couleurs composant du pattern correspondant au nom donné
    public static Colors[] getPatternFromID(int givenID){
        Colors[] pattern = {};
        for (Patterns iteratingPattern : Patterns.values()){
            if (givenID==iteratingPattern.id){
                pattern=iteratingPattern.pattern;
                break;
            }
        }
        return pattern;
    }
}
