package scott.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Gradient {

    public static void main(String args[]) {
        try {
            final int width = 1920;
            final int height = 1080;

            WindowControl wc = new WindowControl(width, height);

            // Create a window for full-screen mode; add a button to leave
            // full-screen mode
            try {

                while (true) {
                    Graphics g = wc.getGraphics();
                    g.setColor(Color.white);
                    g.fillRect(0, 0, wc.getWidth(), wc.getHeight());

                    draw(g, wc.getWidth(), wc.getHeight());

                    g.dispose();
                    wc.show();

                    Thread.sleep(10);
                }
            } finally {
                wc.close(false);
            }

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void draw(Graphics gr, int width, int height) {
        float r = 0, b = 0, g = 0;
        float diff = 255f / (width / 2);
        for (int x = 0; x < width; x++) {
            System.out.println(r + " " + b + " " + g + " " + diff);
            Color c = new Color((int) (r + 0.5f), (int) (g + 0.5f), 0);
            gr.setColor(c);
            for (int y = 0; y < height; y++) {
                gr.drawLine(x, y, x, y);
            }
            if (diff > 0 && (r >= 255 || b >= 255 || g >= 255)) {
                diff *= -1;
            }
            if (diff < 0 && (r <= 0 || b <= 0 || g <= 0)) {
                diff *= -1;
            }
            r += diff;
            b += diff;
            g += diff;
        }
    }

    static class WindowControl {
        private JFrame frame;
        private GraphicsDevice device;
        private BufferStrategy bufferStrategy;

        public WindowControl(int width, int height) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            device = ge.getDefaultScreenDevice();
            GraphicsConfiguration deviceConfig = device.getDefaultConfiguration();

            this.frame = new JFrame(deviceConfig);
            MyMouse mymouse = new MyMouse(this);
            // frame.addKeyListener(new MyKeys(view, mymouse));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.addMouseListener(mymouse);
            // frame.addMouseMotionListener(mymouse);
            frame.setSize(width, height);

            device.setFullScreenWindow(frame);
            frame.validate();
            // Create the back buffer
            int numBuffers = 2; // Includes front buffer
            frame.createBufferStrategy(numBuffers);
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
