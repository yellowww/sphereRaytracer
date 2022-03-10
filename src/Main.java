import java.util.Vector;
import java.awt.Color;

public class Main {
  public static Vector<Ray> rays = new Vector<Ray>(3000000);
  public static Vector<AssetObject> allObjects = new Vector<AssetObject>(1);
  public static Tracer tracer = new Tracer(800,700);
  public static void main(String[] args) {
    Window window = new Window();


    AssetObject newSphere = new AssetObject("sphere", -4f,-4f,15f,3.5f,1f,new Color(255,0,0));
    allObjects.add(newSphere);
    newSphere = new AssetObject("sphere", -4f,4f,16f,3.5f,1f,new Color(0,255,0));
    allObjects.add(newSphere);
    newSphere = new AssetObject("sphere", 4f, 4f,17f,3.5f,1f,new Color(255,255,255));
    allObjects.add(newSphere);
    newSphere = new AssetObject("sphere", 4f, -4f,18f,3.5f,0.05f,new Color(0,255,255));
    allObjects.add(newSphere);
    newSphere = new AssetObject("sphere", 0f, 0f,23f,3.5f,1f,new Color(255,255,0));
    allObjects.add(newSphere);

    loadRayChunk(55f,90f);
    Vector<Ray> reflections = new Vector<Ray>(2000000);
    reflections.addAll(Main.tracer.traceAllRays(rays));
    rays = new Vector<Ray>(5000000);
    loadRayChunk(90f,125f);
    reflections.addAll(Main.tracer.traceAllRays(rays));
    rays = null;
    Main.tracer.traceAllRays(reflections);
    reflections = null;

  }
  public static void loadRayChunk(float start, float end) {
    for(float i=start;i<end;i+=0.025f) {
      for(float j=-35f;j<35f;j+=0.025f) {
        Ray ray = new Ray(i,j,0,0,0);
       // Ray ray = new Ray(90,0,2f,0f,0f);
        float intersects = ray.getAllColitions(false);
        if(intersects>0) {
          rays.add(ray);
        }
      }
    }
  }
}