import java.util.Vector;
import java.awt.Color;

public class Tracer {
    public Vector<Vector<int[]>> pxColor;
    public Vector<Vector<Integer>> overrides;
    public Tracer(int w,int h) {
        initTable(w,h);
    }
    public void initTable(int w,int h) {
        pxColor = new Vector<Vector<int[]>>(w);
        for(int i=0;i<w;i++) {
            Vector<int[]> thisCol = new Vector<int[]>(h);
            for(int j=0;j<h;j++) {
                thisCol.add(new int[] {
                    -1,
                    -1,
                    -1,
                });
            }
            pxColor.add(thisCol);
        }

        overrides = new Vector<Vector<Integer>>(w);
        for(int i=0;i<w;i++) {
            Vector<Integer> thisCol = new Vector<Integer>(h);
            for(int j=0;j<h;j++) {
                thisCol.add(0);
            }
            overrides.add(thisCol);
        }
    }
    public int traceRay(Ray ray) {
        float intTime = ray.getAllColitions(true);
        if(intTime>1) {
            // find coords of colition
            float x = ray.direction.get(0).floatValue()*intTime+ray.origin.get(0);
            float y = ray.direction.get(1).floatValue()*intTime+ray.origin.get(1);
            float z = ray.direction.get(2).floatValue()*intTime+ray.origin.get(2);

            //find 2d coords
            float lx = x*(500/z)+400;
            float ly = y*(500/z)+350;
            //calculate reflection angles
            //find the angles of the bisecting line
            Vector<Float> lastColidedPos = ray.lastColided.position;
            float lastColidedR = ray.lastColided.radius;
            AssetObject lastColided = ray.lastColided;
            //normalize the bisecting vector
            float normalizedX = ((x-lastColidedPos.get(0))/lastColidedR);
            float normalizedY = ((y-lastColidedPos.get(1))/lastColidedR);
            float normalizedZ = ((z-lastColidedPos.get(2))/lastColidedR);
            if(normalizedZ>=0.001) {normalizedZ = 1;}
            else if(normalizedZ<=-0.001) {normalizedZ = -1;}
            else {normalizedZ = 1;}
            // get the angle of the bisecting vector
            float bisAngleA = (float)(Math.acos(normalizedX)/0.0174533f*normalizedZ); // alpha angle
            float bisAngleB = (float)(Math.asin(normalizedY)/0.0174533f*normalizedZ); // beta angle

            // find the angles of the ray
            float rayAngleA = (float)Math.acos(ray.direction.get(0).floatValue()) * (float)(180/Math.PI); //  alpha angle
            float rayAngleB = (float)Math.asin(ray.direction.get(1).floatValue()) * (float)(180/Math.PI); //  beta angle
            
            //find angle and incorporate teture
            float alphaDif = (bisAngleA+rayAngleA)*2+(float)(Math.random()*(1/lastColided.smoothness-1));
            float betaDif = (bisAngleB+rayAngleB)*2+(float)(Math.random()*(1/lastColided.smoothness-1));

            float newAlpha = (alphaDif-rayAngleA);
            float newBeta = (betaDif-rayAngleB)*-1;

            // update ray direction
            ray.direction.set(0, Math.cos(newAlpha*0.0174533f));
            ray.direction.set(1, Math.sin(newBeta*0.0174533f));
            ray.direction.set(2, Math.sin(newAlpha*0.0174533f)*Math.cos(newBeta*0.0174533f));
            // update ray origin with point of colition
            ray.origin.set(0, x+ray.direction.get(0).floatValue()*0.1f);
            ray.origin.set(1, y+ray.direction.get(1).floatValue()*0.1f);
            ray.origin.set(2, z+ray.direction.get(2).floatValue()*0.1f);

            float willReflect = ray.getAllColitions(false);

            float distance = distanceToLine(
                    0,0,0,
                    x,y,z,
                
                    x+(float)Math.cos(newAlpha*0.0174533f)*1000f,
                    y+(float)Math.sin(newBeta*0.0174533f)*1000f,
                    z+(float)Math.sin(newAlpha*0.0174533f)*(float)Math.cos(newBeta*0.0174533f)*1000f
            );



            float endBrightness = 1/distance*3;
            if(endBrightness>1f) endBrightness = 1f;
            if(endBrightness<0f) endBrightness = 0f;

            float brightnessM = 0.3f/distance;
            if(brightnessM>1f)brightnessM=1f;
            if(brightnessM<0f)brightnessM=0f;
            //  if(brightnessM>0 || ray.saveColided == null) {
            boolean canColorOthers = colorPixel(lx,ly,lastColided.color,endBrightness,x,y,z, ray.saveColided, brightnessM);
            if(canColorOthers) {
                colorWithoutReqs(lx+1,ly,lastColided.color,endBrightness, ray.saveColided, brightnessM);
                colorWithoutReqs(lx-1,ly,lastColided.color,endBrightness, ray.saveColided, brightnessM);
                colorWithoutReqs(lx,ly+1,lastColided.color,endBrightness, ray.saveColided, brightnessM);
                colorWithoutReqs(lx,ly-1,lastColided.color,endBrightness, ray.saveColided, brightnessM);
            }                        
            //}
            if(willReflect>0) {
                return 1;
            } else {
                return 0;
            }
        } else {return -1;}

    }
    public void colorWithoutReqs(float lx,float ly, Color color, float brightness, AssetObject saveColided, float brightnessM) {
        if((int)lx>0 && (int)lx<pxColor.size() && (int)ly>0 && (int)ly<pxColor.get(0).size()) {
            int[] thisColor = pxColor.get((int)lx).get((int)ly);
            int thisOverride = overrides.get((int)lx).get((int)ly);
            overrides.get((int)lx).set((int)ly, thisOverride+1);
            if(thisColor[0] == -1) {
                pxColor.get((int)lx).set((int)ly, new int[] {
                    (int)(color.getRed()*brightness),
                    (int)(color.getGreen()*brightness),
                    (int)(color.getBlue()*brightness)
                });
            } else {
                if(saveColided == null) {
                    
                    float r = (color.getRed()*brightness);
                    float g = (color.getGreen()*brightness);
                    float b = (color.getBlue()*brightness);

                    float fr = (thisColor[0]*thisOverride+r)/(thisOverride+1);
                    float fg = (thisColor[1]*thisOverride+g)/(thisOverride+1);
                    float fb = (thisColor[2]*thisOverride+b)/(thisOverride+1);
                    pxColor.get((int)lx).set((int)ly, new int[] {
                        (int)fr,
                        (int)fg,
                        (int)fb
                    });
                } else {
                    pxColor.get((int)lx).set((int)ly, new int[] {
                        (int)((thisColor[0]*(1-brightnessM))+(brightnessM*(saveColided.color.getRed()*brightness))),
                        (int)((thisColor[1]*(1-brightnessM))+(brightnessM*(saveColided.color.getGreen()*brightness))),
                        (int)((thisColor[2]*(1-brightnessM))+(brightnessM*(saveColided.color.getBlue()*brightness)))
                    });
                }  
            }
        }
    }
    public boolean colorPixel(float lx,float ly,Color color,float brightness, float x, float y, float z, AssetObject saveColided, float brightnessM) {
        float distToColition = (float)Math.sqrt( // get the distance from the camera to the colition to normalize the ray direction vectors
            Math.pow(x,2) +
            Math.pow(y,2) +
            Math.pow(z,2)
        );
        
        Ray orderRay = new Ray(0,0,0,0,0);
        orderRay.direction.set(0, (double)(x/distToColition));
        orderRay.direction.set(1, (double)(y/distToColition));
        orderRay.direction.set(2, (double)(z/distToColition));
        float hitObjectDist = orderRay.getAllColitions(false);

        if((int)lx>0 && (int)lx<pxColor.size() && (int)ly>0 && (int)ly<pxColor.get(0).size() && Math.abs(hitObjectDist-distToColition)<0.32) {
            int[] thisColor = pxColor.get((int)lx).get((int)ly);
            int thisOverride = overrides.get((int)lx).get((int)ly);
            overrides.get((int)lx).set((int)ly, thisOverride+1);
            if(thisColor[0] == -1) {
                pxColor.get((int)lx).set((int)ly, new int[] {
                    (int)(color.getRed()*brightness),
                    (int)(color.getGreen()*brightness),
                    (int)(color.getBlue()*brightness)
                });
                return true;
            } else {
                if(saveColided == null) {
                    float r = (color.getRed()*brightness);
                    float g = (color.getGreen()*brightness);
                    float b = (color.getBlue()*brightness);

                    float fr = (thisColor[0]*thisOverride+r)/(thisOverride+1);
                    float fg = (thisColor[1]*thisOverride+g)/(thisOverride+1);
                    float fb = (thisColor[2]*thisOverride+b)/(thisOverride+1);
                    pxColor.get((int)lx).set((int)ly, new int[] {
                        (int)fr,
                        (int)fg,
                        (int)fb
                    });
                } else {
                    pxColor.get((int)lx).set((int)ly, new int[] {
                        (int)((thisColor[0]*(1-brightnessM))+(brightnessM*(saveColided.color.getRed()*brightness))),
                        (int)((thisColor[1]*(1-brightnessM))+(brightnessM*(saveColided.color.getGreen()*brightness))),
                        (int)((thisColor[2]*(1-brightnessM))+(brightnessM*(saveColided.color.getBlue()*brightness)))
                    });
                }     

                return true;
            }
        }
        return false;
    }
    public static float distanceToLine(double x1, double y1, double z1,
                                double x2, double y2, double z2,
                                double x3, double y3, double z3) {
        double b = Math.sqrt(Math.pow((x2 - x3), 2) 
                + Math.pow((y2 - y3), 2) 
                + Math.pow((z2 - z3), 2));

        double S = Math.sqrt(Math.pow((y2 - y1) * (z3 - z1) - (z2 - z1) * (y3 - y1), 2) +
                Math.pow((z2 - z1) * (x3 - x1) - (x2 - x1) * (z3 - z1), 2) +
                Math.pow((x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1), 2)) / 2;

        return 2 * (float)S / (float)b;
    }

    public Vector<Ray> traceAllRays(Vector<Ray> allRays) {
        int totalHits = 0;
        int numOfRays = allRays.size();
        Vector<Ray> reflected = new Vector<Ray>(1000000);
      //  System.out.println(Main.rays.size()+" remain");
        for(int i=0;i<numOfRays;i++) {
           int hitObject = traceRay(allRays.get(i));
           if(hitObject>=0) totalHits++;
           if(hitObject>0) reflected.add(allRays.get(i));
        };
        System.out.println(totalHits+" total hits");
        return reflected;
    }
}