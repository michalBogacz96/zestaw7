package serialize;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CircularPanel extends JPanel implements Runnable {

    private static int windowsSize = 600;
    int pointer = 0;

    //To create a triangle we use rects
    //with lines and 3 points
    private int[] xRectLines;
    private int[] yRectLines;
    private int[][] xRectPoints;
    private int[][] yRectPoints;


    private Circular circular;
    private int bufferSize;
    private int centerPoint;

    public CircularPanel(Circular circularObject) {

        circular = circularObject;
        bufferSize = circular.capacity();
        centerPoint = windowsSize / 2;

        xRectLines = new int[bufferSize];
        yRectLines = new int[bufferSize];

        xRectPoints = new int[bufferSize][3];
        yRectPoints = new int[bufferSize][3];


        for(int i = 0; i < bufferSize; i++) {

            //360 divided by size to create pizza like circle
            double  circleAngle = i * (360 / bufferSize);

            xRectLines[i] = (int) (centerPoint
                    + centerPoint
                    * Math.cos(Math.toRadians(circleAngle)));
            yRectLines[i] = (int) (centerPoint
                    + centerPoint
                    * Math.sin(Math.toRadians(circleAngle)));
        }


        for(int i = 0; i < bufferSize; i++) {
            xRectPoints[i][0] = centerPoint;
            xRectPoints[i][1] = xRectLines[i];
            xRectPoints[i][2] = xRectLines[(i+1) % bufferSize];

            yRectPoints[i][0] = centerPoint;
            yRectPoints[i][1] = yRectLines[i];
            yRectPoints[i][2] = yRectLines[(i+1) % bufferSize];
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(0,0,600,600);

        for(int i = 0; i < bufferSize; i++) {
            if(circular.getItem(i) != null) {
                if(i == circular.readIndex)
                    g.setColor(Color.red);
                else
                    g.setColor(Color.blue);
                g.fillPolygon(xRectPoints[i], yRectPoints[i], 3);
            }
            g.setColor(Color.black);
            g.drawLine(centerPoint, centerPoint, xRectLines[i], yRectLines[i]);
        }

    }

    public static void main(String [] args) {

        Circular<Integer> circular = new Circular<>(10);
        CircularPanel panel = new CircularPanel(circular);

        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setSize(windowsSize, windowsSize);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {}
        });

        Thread adder = new Adder(circular);
        Thread taker = new Taker(circular);
        adder.start();
        taker.start();
        panel.run();

    }

    @Override
    public void run() {
        while(true) {
            this.repaint();
            try {
                Thread.sleep(300);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
