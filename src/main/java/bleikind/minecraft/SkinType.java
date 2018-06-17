package bleikind.minecraft;

public enum SkinType {
    DEFAULT, SLIM;

    @Override
    public String toString() {
        return this == DEFAULT ? "" : "SLIM";
    }
}
