import java.util.Vector;
import java.awt.Color;

public class AssetObject {
    public Vector<Float> position = new Vector<Float>(3);
    public float radius;
    public String assetType;
    public float smoothness;
    public float metalic;
    public Color color;    public AssetObject (String assetName, float x,float y,float z,float r, float smoothnessA, Color colorA) {
        position.add(x);
        position.add(y);
        position.add(z);
        radius = r;
        assetType = assetName;
        smoothness = smoothnessA;
        color = colorA;
    }
}