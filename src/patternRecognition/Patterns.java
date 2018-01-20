package patternRecognition;

import java.util.Objects;

//Enum de tous les patterns possibles
public enum Patterns {

    //Les noms des patterns ont été créés comme ceci :
    //Prenons le pattern (blue, orange, black)
    //On prend la première et ledernière lettre du nom anglais de la lettre
    //D'où B lu E, O rang E, B lac K
    //En gardant ces lettres, on trouve : BEOEBK, ce qui correspond au pattern 6
    OEBKGN(0, new Colors[] {Colors.ORANGE, Colors.BLACK, Colors.GREEN}),
    YWBKBE(1, new Colors[] {Colors.YELLOW, Colors.BLACK, Colors.BLUE}),
    BEGNOE(2, new Colors[] {Colors.BLUE, Colors.GREEN, Colors.ORANGE}),
    YWGNBK(3, new Colors[] {Colors.YELLOW, Colors.GREEN, Colors.BLACK}),
    BKYWOE(4, new Colors[] {Colors.BLACK, Colors.YELLOW, Colors.ORANGE}),
    GNYWBE(5, new Colors[] {Colors.GREEN, Colors.YELLOW, Colors.BLUE}),
    BEOEBK(6, new Colors[] {Colors.BLUE, Colors.ORANGE, Colors.BLACK}),
    GNOEYW(7, new Colors[] {Colors.GREEN, Colors.ORANGE, Colors.YELLOW}),
    BKBEGN(8, new Colors[] {Colors.BLACK, Colors.BLUE, Colors.GREEN}),
    OEBEYW(9, new Colors[] {Colors.ORANGE, Colors.BLUE, Colors.YELLOW});

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
            if (Objects.equals(givenID, iteratingPattern.id)){
                pattern=iteratingPattern.pattern;
                break;
            }
        }
        return pattern;
    }
}
