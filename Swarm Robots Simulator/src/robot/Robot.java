package robot;

import communication.Communication;
import communication.Message;
import communication.MessageType;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import robot.behaviors.BasicBehaviors;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import utility.Constants;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import robot.behaviors.PairBehaviors;
import robot.sensors.IRSensor;
import robot.sensors.SharpReadings;
import robot.sensors.SharpSensor;
import utility.Utility;
import view.Field;

/**
 *
 * @author Nadun
 */
public class Robot extends Ellipse2D.Double implements BasicBehaviors, RobotBrain, SharpReadings,
        ActionListener, Communication, PairBehaviors {

    protected double angle;

    private Thread wheelThread, randomMoveThread;
    private boolean wheelStop = true;
    private boolean forward = true;
    private SharpSensor sharp;
    private IRSensor iRSensor;
    private BufferedImage image;
    private int id;
    
    private Color ledColor;

    private static int nextId = 0;

    public Robot(double x, double y, double angle) {
        super(x, y, 2 * Constants.ROBOT_RADIUS, 2 * Constants.ROBOT_RADIUS);
        this.angle = angle;

        try {
            image = ImageIO.read(new File("images/robo_icon.png"));
        } catch (IOException ex) {
            Logger.getLogger(Field.class.getName()).log(Level.SEVERE, null, ex);
        }

        sharp = new SharpSensor(this);
        iRSensor = new IRSensor(Constants.IR_MAX_COVERAGE, this);

        this.id = nextId;

        nextId++;
        
//        wheelThread = new Thread();
//        wheelThread.start();

    }

    public Robot() {
        this(0, 0, 0);

        int x = Utility.randomInRange(20, Constants.FEILD_WIDTH - 5 * Constants.ROBOT_RADIUS);
        int y = Utility.randomInRange(20, Constants.FEILD_HEIGHT - 5 * Constants.ROBOT_RADIUS);
        this.angle = Utility.randomInRange(-360, 360);

        setX(x);
        setY(y);
    }

    public void setLedColor(Color ledColor) {
        this.ledColor = ledColor;
    }

    public IRSensor getiRSensor() {
        return iRSensor;
    }

    public int getId() {
        return id;
    }

    public void draw(Graphics2D g2d) {
        Rectangle frame = new Rectangle((int) x, (int) y,
                2 * Constants.ROBOT_RADIUS, 2 * Constants.ROBOT_RADIUS);

        TexturePaint roboImage = new TexturePaint(image, frame);

        Color color = g2d.getColor();
//        
        //g2d.setStroke(new BasicStroke(5));
        //g2d.setColor(Color.GREEN);
        //g2d.draw(this);
        //g2d.setStroke(new BasicStroke(1));
        //g2d.setColor(color);
        
        //g2d.draw(this.getBounds());
        g2d.setPaint(roboImage);
        g2d.rotate(Math.toRadians(angle), getCenterX(), getCenterY());
        g2d.fill(this);
        g2d.rotate(Math.toRadians(-angle), getCenterX(), getCenterY());

        sharp.draw(g2d);
        iRSensor.draw(g2d);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getAngle() {
        return angle;
    }

    public Thread getWheelThread() {
        return wheelThread;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        loop();
    }

    @Override
    public void loop() {
    }

    @Override
    public void moveForward(int delay) {

        long start = System.currentTimeMillis();
        while (true) {
            double xdelta = Math.sin(Math.toRadians(angle));
            double ydelta = Math.cos(Math.toRadians(angle));
//            int R = 200; 
//            if(x + xdelta >= Constants.FEILD_WIDTH || x + xdelta <= 0) {
//                turnRightAngle(180 - 2*angle);
//            }
//            
//            if(y - ydelta >= Constants.FEILD_HEIGHT || y - ydelta <= 0) {
//                turnRightAngle(180 - 2*angle);
//            }

            x += xdelta;
            y -= ydelta;

//            if(sharp.isHit()) {
//                System.out.println("");
//            }
            long end = System.currentTimeMillis();
            if (end - start >= delay) {
                break;
            }
            try {
                Thread.sleep(100 - Constants.ROBOT_SPEED);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void moveBackward(int delay) {

        long start = System.currentTimeMillis();
        while (true) {
            x -= Math.sin(Math.toRadians(angle));
            y += Math.cos(Math.toRadians(angle));

            long end = System.currentTimeMillis();
            if (end - start >= delay) {
                break;
            }
            try {
                Thread.sleep(100 - Constants.ROBOT_SPEED);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void moveForward() {
        if (wheelStop) {
            if (wheelThread == null) {
                wheelThread = new Thread(new WheelThread());
                wheelStop = false;
                wheelThread.start();

            }
        }
        
            
    }

    @Override
    public void moveBackward() {
        forward = false;
        moveForward();
    }

    @Override
    public void moveStop() {
        wheelStop = true;
        if (wheelThread != null) {
            wheelThread.stop();
        }
        wheelThread = null;
        if (randomMoveThread != null) {
            randomMoveThread.stop();
        }
        randomMoveThread = null;
    }

    @Override
    public void turnRight(int delay) {
        long start = System.currentTimeMillis();
        while (true) {
            angle++;

            long end = System.currentTimeMillis();
            if (end - start >= delay) {
                break;
            }
            try {
                Thread.sleep(100 - Constants.ROBOT_SPEED);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void turnLeft(int delay) {
        long start = System.currentTimeMillis();
        while (true) {
            angle--;

            long end = System.currentTimeMillis();
            if (end - start >= delay) {
                break;
            }
            try {
                Thread.sleep(100 - Constants.ROBOT_SPEED);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void turnRightAngle(double angle) {
        for (int i = 0; i < angle; i++) {
            this.angle++;
            try {
                Thread.sleep(100 - Constants.ROBOT_SPEED);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void turnLeftAngle(double angle) {
        for (int i = 0; i < angle; i++) {
            this.angle--;
            try {
                Thread.sleep(100 - Constants.ROBOT_SPEED);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void randomTurn(int min, int max) {
        double angle = Utility.randomInRange(min, max);
        int randomInt = Utility.randomInRange(0, 1);
        if (randomInt == 0) { // left turn
            turnLeftAngle(angle);
        } else { // right turn
            turnRightAngle(angle);
        }
    }

    @Override
    public void angularTurn(double angle) {
        if (angle < 0) { // left turn
            turnLeftAngle(-angle);
        } else { // right turn
            turnRightAngle(angle);
        }
    }

    @Override
    public double measureDistance() {
        return sharp.readDistance();
    }

    @Override
    public void avoidObstacles() {
        double distance = measureDistance();
        if (distance < 50 && distance > 0) {
            moveStop();
            randomTurn(90, 120);
        }
    }

    @Override
    public void moveForwardDistance(int distance) {

        double d = 0;
        while (true) {
            if (d >= distance) {
                break;
            }
            x += Math.sin(Math.toRadians(angle));
            y -= Math.cos(Math.toRadians(angle));

            d++;
            try {
                Thread.sleep(100 - Constants.ROBOT_SPEED);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void moveRandom() {
        if (wheelStop) {
            if (randomMoveThread == null) {
                randomMoveThread = new Thread(new RandomMoveThread());
                wheelStop = false;
                randomMoveThread.start();

            }
        }
    }

    @Override
    public void broadcastMessage(MessageType header) {
        iRSensor.setBroadcastMsg(new Message(header, this));
    }

    @Override
    public Message recieveMessage() {
        return iRSensor.getRecieveMsg();
    }

    @Override
    public void comeCloser(double heading, int dist) {
        
        if(heading > 0) {
            angularTurn(heading);		
        }
        else if(heading > 0) {
            angularTurn(3600 - heading);
        }
        moveForwardDistance(dist);
        moveStop();

    }

    @Override
    public Color readColor() {
        return null;
    }

    public class WheelThread implements Runnable {

        @Override
        public void run() {
            wheelStop = false;
            while (!wheelStop) {
                double xdelta = Math.sin(Math.toRadians(angle));
                double ydelta = Math.cos(Math.toRadians(angle));

                x += forward ? xdelta : -xdelta;
                y -= forward ? ydelta : -ydelta;

                try {
                    Thread.sleep(100 - Constants.ROBOT_SPEED);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public class RandomMoveThread implements Runnable {

        @Override
        public void run() {
            while (!wheelStop) {

                moveForwardDistance(150);
                int randomAngle = Utility.randomInRange(0, 30);
                turnRightAngle(randomAngle);

                try {
                    Thread.sleep(100 - Constants.ROBOT_SPEED);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
