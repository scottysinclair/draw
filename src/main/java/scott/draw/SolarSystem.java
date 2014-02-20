package scott.draw;

import java.awt.BufferCapabilities;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.*;

import javax.swing.JFrame;

public class SolarSystem {


 

public static void main(String args[]) {
	try {
			final int width = 1366;
			final int height = 768;

      long speedOfEarthInKmpHour = 100000;
      double speedOfEarthMetersPerSec = ((speedOfEarthInKmpHour * 1000 * 30));
//speedOfEarthMetersPerSec = 0;
      long distanceFromSunm = 149600000L * 1000L;


      final double scale = height / (distanceFromSunm * 2.3); 

     WindowControl wc = new WindowControl(width, height);
   
			// Create a window for full-screen mode; add a button to leave full-screen mode
			try {

        float dTime = 1f / 2; 

        List<MObject> world = new LinkedList();
        MObject sun = new MObject("Sun", Color.YELLOW, 10, 1.9891e30, new Vector(0f, 0f), new Vector(0f, 0f)); 
        world.add(sun);
        MObject earth = new MObject("earth",
					Color.BLUE, 
					10, 
					5.97219e24, 
					new Vector(0f, speedOfEarthMetersPerSec), //going fully up or down (at 90 degrees)
					new Vector(distanceFromSunm, 0f)); //fully to the right 
          world.add(earth);

        MObject mars = new MObject("mars",
					Color.RED, 
					15, 
					5.97219e24, 
					new Vector(0f, (-speedOfEarthMetersPerSec)), //going fully up or down (at 90 degrees)
					new Vector(-distanceFromSunm, 0f));  
          world.add(mars);


				while(true) {
					Graphics g = wc.getGraphics();
					g.setColor(Color.white);
					g.fillRect(0, 0, wc.getWidth(), wc.getHeight());

 					
				  for (MObject mo: world) {
            mo.calc(world);
					}

				  for (MObject mo: world) {
            mo.update(dTime);
					}

				  for (MObject mo: world) {
            mo.draw(g, (int)(width / 2), (int)(height / 2), scale);
					}
        
										
					g.dispose();
					wc.show();
					
					Thread.sleep(10);
				}
			}
			finally {
				wc.close(false);
			}

	}
  catch(Exception x) {
    x.printStackTrace();
  }
}

static class Vector {
  public double x;
  public double y;
  public Vector() {
    this(0f,0f);
  }
  public Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }
}

static class MObject {
  private String name;
  private Color color;
  private int radius;
  private double mass;
  private Vector speed;
  private Vector pos;
  private Vector tf;

  public MObject(String name, Color color, int radius, double mass, Vector speed, Vector pos){
    this.name = name;
    this.color = color;
    this.radius = radius;
    this.mass = mass;
    this.speed = speed;
    this.pos = pos;
  }

  public void calc(Collection<MObject> world) {
     tf = new Vector();
     for (MObject mo: world) {
        if (mo == this) continue;
        double dx = Math.abs(pos.x - mo.pos.x);
        double dxsq = dx * dx;
        double dy = Math.abs(pos.y - mo.pos.y);
        double dysq = dy * dy;
        double dh = Math.sqrt(dxsq + dysq);
        if (dh != 0d) {
	        double fh = (mass * mo.mass) /  (dh * dh);
					double angle = Math.atan(dy / dh);
					double fy= Math.sin(angle) * fh; 
		      double fx= Math.cos(angle) * fh;
          if (mo.pos.x < pos.x) fx *= -1d;
          if (mo.pos.y < pos.y) fy *= -1d;
          tf.x += fx;
          tf.y += fy;
				}
     }
  }
  public void update(float dTime) {
     print();
     Vector a = new Vector(tf.x / mass, tf.y / mass);
     Vector dSpeed = new Vector(a.x * dTime, a.y * dTime);
     speed.x += dSpeed.x;
     speed.y += dSpeed.y;
     pos.x += (speed.x * dTime);
     pos.y += (speed.y * dTime);     
  }

  public void draw(Graphics g, int xoff, int yoff, double scale) {
     g.setColor(color);
     g.fillOval(xoff + (int)(pos.x * scale), yoff + (int)(pos.y * scale), radius, radius);
  }

  public void print() {
     System.out.println(name + " force is " + tf.x + "," + tf.y);     
     System.out.println(name + " speed is " + speed.x + "," + speed.y);     
  }

}



static class WindowControl  {
		private JFrame frame;
		private GraphicsDevice device;
		private BufferStrategy bufferStrategy;
		public WindowControl(int width, int height) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			device = ge.getDefaultScreenDevice();
			GraphicsConfiguration deviceConfig = device.getDefaultConfiguration();
			
			this.frame = new JFrame( deviceConfig );
			MyMouse mymouse = new MyMouse(this);
//			frame.addKeyListener(new MyKeys(view, mymouse));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.addMouseListener(mymouse);
//			frame.addMouseMotionListener(mymouse);
			frame.setSize(width, height);
			
			device.setFullScreenWindow(frame);
			frame.validate();
			 // Create the back buffer
			int numBuffers = 2;  // Includes front buffer
			frame.createBufferStrategy( numBuffers );
			bufferStrategy = frame.getBufferStrategy();
		}
		
		public int getWidth() {
			return frame.getWidth();
		}

		public int getHeight() {
			return frame.getHeight();
		}
		
		public void show() {
			bufferStrategy.show();
		}

		public Graphics getGraphics() {
			 return bufferStrategy.getDrawGraphics();			
		}

	
		public void close(boolean andExit) {
			device.setFullScreenWindow(null);
			if (andExit) {
				System.exit(0);
			}
		}
	}

	
	static class MyMouse extends MouseAdapter {
		private WindowControl winControl;
		
		public MyMouse(WindowControl winControl) {
			this.winControl = winControl;
		}
	    public void mousePressed(MouseEvent evt) {
         winControl.close(true);
	    }

		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}
	
}
