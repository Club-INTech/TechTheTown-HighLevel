package enums;

/**
 * Enum qui rassemble les headers (plus propre pour le Simulateur & le ThreadEth)
 */
public enum CommunicationHeaders {

    EVENT((char) 0x13, (char) 0x37),
    ULTRASON((char) 0x01, (char) 0x10),
    DEBUG((char) 0x02, (char) 0x20),
    ;

    /** Le seul contenu de cette enum... */
    private char firstHeader;
    private char secondHeader;

    /**
     * Constructeur
     * @param firstHeader
     * @param secHeader
     */
    CommunicationHeaders(char firstHeader, char secHeader){
        this.firstHeader = firstHeader;
        this.secondHeader = secHeader;
    }

    /** Getters */
    public char getFirstHeader() {
        return firstHeader;
    }
    public char getSecondHeader() {
        return secondHeader;
    }
}
