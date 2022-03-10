import java.util.Vector;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.Color;

public class Window {
    public Window(){
        JFrame frame = new JFrame();
        frame.setSize(800,700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Java Raytracer");
        frame.add(new CustomPaintComponent());
        frame.setVisible(true);
    }

    public class CustomPaintComponent extends Component {
         public void paint(Graphics g) {
             Graphics2D g2d = (Graphics2D)g;
             Vector<Vector<int[]>> data = Main.tracer.pxColor;
             
             for(int i=0;i<data.size();i+=1) {
                 for(int j=0;j<data.get(0).size();j+=1) {
                     int[] thisColor = data.get(i).get(j);               
                     if(thisColor[0] == -1) {
                         Color color = new Color(200,200,200);
                         g2d.setColor(color);
                         g2d.fillRect(i,j,1,1);
                     } else {
                         Color color = new Color(thisColor[0],thisColor[1],thisColor[2]);
                         g2d.setColor(color);
                         g2d.fillRect(i,j,1,1);
                     }
                 }
             }
        }
    }
}