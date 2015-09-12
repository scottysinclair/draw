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


	  double maxMagnitude = 0d;

	//  double speedOfEarthInKmpHour = 101765.855555;
	  double speedOfEarthInKmpHour = 0;
      double speedOfEarthMetersPerSec = ((speedOfEarthInKmpHour * 1000) * 30f);

      //double speedOfMoonInKmpHour = speedOfEarthInKmpHour;
	  double speedOfMoonInKmpHour = speedOfEarthInKmpHour + (3683);
      double speedOfMoonInMetersPerSec = ((speedOfMoonInKmpHour * 1000) * 30f);

     // long distanceFromSunm = 149600000L * 1000L;
      long distanceFromSunm = 0L;
      long distanceFromMoonToEarth = 384400 * 1000L;
//      long distanceFromMoonToEarth = (long)(distanceFromSunm / 1.5525555e3);
      long distanceFromMoonToSun = distanceFromSunm + distanceFromMoonToEarth;

      final double scale = height / (distanceFromMoonToEarth * 3.0); 

     WindowControl wc = new WindowControl(width, height);
   
			// Create a window for full-screen mode; add a button to leave full-screen mode

			try {

        float dTime = 1f / 1000f; 

        Map<String,Dot> dots = new HashMap<String,SolarSystem.Dot>();
        List<MObject> world = new LinkedList<MObject>();
        MObject sun = new MObject("Sun", Color.YELLOW, 10, 1.9891e30, new Vector(0f, 0f), new Vector(0f, 0f)); 
  //      world.add(sun);
        MObject earth = new MObject("earth",
					Color.BLUE, 
					10, 
					5.97219e24, 
					new Vector(0f, speedOfEarthMetersPerSec), //going fully up or down (at 90 degrees)
					new Vector(distanceFromSunm, 0f)); //fully to the right 
          world.add(earth);

          MObject moon = new MObject("moon",
  					Color.WHITE, 
  					5, 
  					7.34767309e22, 
  					new Vector(0f, speedOfMoonInMetersPerSec), //going fully up or down (at 90 degrees)
  					new Vector(distanceFromMoonToSun, 0f)); //fully to the right 
            world.add(moon);
          
          
//        MObject mars = new MObject("mars",
//					Color.RED, 
//					15, 
//					5.97219e24, 
//					new Vector(0f, (-speedOfEarthMetersPerSec)), //going fully up or down (at 90 degrees)
//					new Vector(-distanceFromSunm, 0f));  
//          world.add(mars);

          int drawEvery = 1;
          long countLoop = 0;
          long countOrbit = 0;
          boolean belowSun = true;
          
          int screenCenterX = (int)(width / 2);
          int screenCenterY = (int)(height / 2);
          
				while(true) {
				  for (MObject mo: world) {
            mo.calc(world);
					}

				  for (MObject mo: world) {
            mo.update(dTime);
			  if (mo == earth) {
				  String key = mo.getDrawXPos(screenCenterX, scale) + "" +  mo.getDrawYPos(screenCenterY, scale);
				  dots.put(key, new Dot(mo.getPos(), mo.getSpeed()));
				  if (mo.getSpeed().magnitude() > maxMagnitude) {
					  maxMagnitude = mo.getSpeed().magnitude();
				  }
			  }
           
					}
				  
				  if (earth.getPos().x > sun.getPos().x && !belowSun) {
					  if (earth.getPos().y > sun.getPos().y) {
						  countOrbit++;
						  belowSun = true;
					  }
				  }
				  else if (earth.getPos().y < sun.getPos().y) {
					  belowSun = false;
				  }

				  if ((countLoop % drawEvery) == 0) {
						Graphics g = wc.getGraphics();
						g.setColor(Color.black);
						g.fillRect(0, 0, wc.getWidth(), wc.getHeight());

						g.setColor(Color.WHITE);
						g.drawString("Orbits: " + countOrbit, 30, 30);

					  for (MObject mo: world) {
						  mo.draw(g, screenCenterX, screenCenterY, scale);
						}
					  for (Dot d: dots.values()) {
						  d.draw(g, (int)(width / 2), (int)(height / 2), scale, maxMagnitude);
					  }
						g.dispose();
						wc.show();
				  }
       // System.out.println(dots.size());
										
					countLoop++;
					//Thread.sleep(200);
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
  public Vector(Vector source) {
	    this.x = source.x;
	    this.y = source.y;
	 }
  
  public double magnitude() {
	  return Math.sqrt((x * x) + (y * y));
  }
}
static Map<String,Color> cache = new HashMap<String, Color>();

static class Dot {
	final Vector pos;
	final double speedMagnitude;
	public Dot(Vector pos, Vector speed) {
		this.pos = new Vector(pos);
		this.speedMagnitude = speed.magnitude();
	}
	public void draw(Graphics gr, int xoff, int yoff, double scale, double maxMagnitude) {
		double fracOfMagnitude =  speedMagnitude / maxMagnitude;
		
		int r = (int)Math.round(fracOfMagnitude * 255);
		int g = 255 - r;
		int b = 0;
		String key = r + "" + g + "" +  b;
		Color c = cache.get(key);
		if (c == null) {
			c = new Color(r, g, b);
			cache.put(key, c);
		}
		gr.setColor(c);
		gr.fillOval((xoff + (int)(pos.x * scale)), ((int)(yoff) + (int)(pos.y * scale)),  2, 2);
	}
	
	public Vector getPos() {
		return pos;
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
   
  public Vector getPos() {
	return pos;
  }
 
	public Vector getSpeed() {
		return speed;
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
   //  print();
     Vector a = new Vector(tf.x / mass, tf.y / mass);
     Vector dSpeed = new Vector(a.x * dTime, a.y * dTime);
     speed.x += dSpeed.x;
     speed.y += dSpeed.y;
     pos.x += (speed.x * dTime);
     pos.y += (speed.y * dTime);     
  }

  public void draw(Graphics g, int xoff, int yoff, double scale) {
     g.setColor(color);
     g.fillOval(getDrawXPos(xoff, scale), getDrawYPos(yoff, scale), radius, radius);
  }
  
  public int getDrawXPos(int xoff, double scale) {
	  int x = xoff + (int)(pos.x * scale);
	  return (int)(x - (radius / 2.0) + 0.5);
  }

  public int getDrawYPos(int yoff, double scale) {
	  int y = yoff + (int)(pos.y * scale);
	  return (int)(y - (radius / 2.0) + 0.5);
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
