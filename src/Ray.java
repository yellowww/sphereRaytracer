import java.util.Vector;

public class Ray {
    public Vector<Double> direction = new Vector<Double>(3);
    public Vector<Float> origin = new Vector<Float>(3);
    public AssetObject lastColided = null;
    public AssetObject saveColided = null;
    public Ray(double a, double b, float x, float y, float z) {
        a*=0.0174533d;
        b*=0.0174533d;
        direction.add(Math.cos(a));
        direction.add(Math.sin(b));
        direction.add(Math.sin(a)*Math.cos(b));
        origin.add(x);
        origin.add(y);
        origin.add(z);
    }

    public float getSphereHit(AssetObject assetObject) {
        float[] sPosArr = new float[3];
        sPosArr[0] = (float)assetObject.position.get(0);
        sPosArr[1] = (float)assetObject.position.get(1);
        sPosArr[2] = (float)assetObject.position.get(2);
        float[] rOrrArr = new float[3];
        rOrrArr[0] = (float)origin.get(0);
        rOrrArr[1] = (float)origin.get(1);
        rOrrArr[2] = (float)origin.get(2);
        float[] rDirArr = new float[3];
        rDirArr[0] = direction.get(0).floatValue();
        rDirArr[1] = direction.get(1).floatValue();
        rDirArr[2] = direction.get(2).floatValue();

        float[] oc = {rOrrArr[0]-sPosArr[0],rOrrArr[1]-sPosArr[1],rOrrArr[2]-sPosArr[2]};

        float a = dotProduct(rDirArr, rDirArr);
        float b = 2.0f * dotProduct(oc, rDirArr);
        float c = dotProduct(oc,oc) - assetObject.radius*assetObject.radius;
        float discriminant = b*b - 4*a*c;
        if(discriminant>0) {
            float x1 = ((b*-1) - (float)Math.sqrt(discriminant)) / 2f;
            float x2 = ((b*-1) + (float)Math.sqrt(discriminant)) / 2f;
            if(x1 >= 0 && x2 >= 0) return x1;
            if(x1 < 0 && x2 >= 0) return x2;
        }
        return -1f;
    }
    public float dotProduct(float[] vec0, float[] vec1) {
        float m0 = vec0[0]*vec1[0];
        float m1 = vec0[1]*vec1[1];
        float m2 = vec0[2]*vec1[2];
        return m0+m1+m2;
    }
    public float getAllColitions(boolean save) {
        AssetObject currentClosestObject = null;
        float currentClosestDist = Float.POSITIVE_INFINITY;
        boolean hasColided = false;
        for(int i=0;i<Main.allObjects.size();i++) {
            float dist = -1f;
            if(Main.allObjects.get(i).assetType == "sphere") {
                dist = getSphereHit(Main.allObjects.get(i));
            }
            if(dist<currentClosestDist && dist>0) {
                hasColided = true;
                currentClosestDist = dist;
                currentClosestObject = Main.allObjects.get(i);
            }
        }
        
        if(currentClosestObject != null && save) {
            if(lastColided != null) {
                if(lastColided.position.get(0) == currentClosestObject.position.get(0)) {
                    return -1;
                }
            }
            if(lastColided != null && saveColided == null) saveColided = lastColided;
            lastColided = currentClosestObject;
        }
        
        if(!hasColided) return -1f;
        return currentClosestDist;
    }
}