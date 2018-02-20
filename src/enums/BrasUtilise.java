package enums;

public enum BrasUtilise{
    AVANT("avant"),
    ARRIERE("arriere");
    private String side;

    BrasUtilise(String side){
        this.side=side;
    }

    public String getSide() {
        return side;
    }
}