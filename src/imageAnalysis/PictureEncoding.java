package imageAnalysis;

public enum PictureEncoding {
    RGB("RGB"),
    BGR("BGR"),
    HSB("HSB");

    private String label;

    PictureEncoding(String label){
        this.label=label;
    }

    @Override
    public String toString(){
        return this.label;
    }

    public String getLabel() {
        return this.label;
    }
}
