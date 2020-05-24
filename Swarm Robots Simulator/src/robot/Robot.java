package robot;

import communication.Communication;
import communication.Data;
import communication.Message;
import communication.MessageType;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import robot.behaviors.BasicBehaviors;
import configs.Settings;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import robot.behaviors.PairBehaviors;
import robot.ledstript.LedStript;
import robot.sensors.IRSensor;
import robot.behaviors.SupportiveFunctions;
import robot.console.Console;
import robot.sensors.SharpSensor;
import helper.Utility;
import view.Field;

/**
 *
 * @author Nadun
 */
public class Robot extends Ellipse2D.Double implements BasicBehaviors, RobotBrain, SupportiveFunctions,
        Communication, PairBehaviors {

    protected double angle;
    public boolean wheelStop = true;
    private boolean forward = false;
    private SharpSensor sharp;
    private ArrayList<IRSensor> iRSensors;
    private LedStript ledStript;
    private BufferedImage image;
    private int id;
    public Console console;

    public enum State {

        SEARCHING,
        INCLUSTER,
        AGGREGATE
    }
    public boolean rotationOff = false;

    private Color ledColor;

    private static int nextId = 0;

    public Robot(double x, double y, double angle) {

        super(x, y, 2 * Settings.ROBOT_RADIUS, 2 * Settings.ROBOT_RADIUS);
        this.angle = angle;

        try {
            image = ImageIO.read(new File("images/robo_icon.png"));
        } catch (IOException ex) {
            Logger.getLogger(Field.class.getName()).log(Level.SEVERE, null, ex);
        }

        sharp = new SharpSensor(this);
        iRSensors = new ArrayList<>();

        int n = Settings.NUM_OF_IR_SENSORS;
        for (int i = 0; i < n; i++) {
            iRSensors.add(new IRSensor(i, this, i * 360 / n));
        }

        ledStript = new LedStript(this);

        this.id = nextId;

        nextId++;

        this.console = new Console(id);
        this.console.setVisible(Settings.CONSOLE_LOGGER);
    }

    public Robot(double x, double y) {
        this(0, 0, 0);

        this.angle = Utility.randomInRange(-360, 360);

        setX(x);
        setY(y);
    }

    public Robot() {
        this(0, 0);

        int x = Utility.randomInRange(20, Settings.FEILD_WIDTH - 5 * Settings.ROBOT_RADIUS);
        int y = Utility.randomInRange(20, Settings.FEILD_HEIGHT - 5 * Settings.ROBOT_RADIUS);
        this.angle = Utility.randomInRange(-360, 360);

        setX(x);
        setY(y);
    }

    public void swithOnLedStript(Color ledColor) {
        this.ledColor = ledColor;
    }

    public void swithOffLedStript() {
        this.ledColor = null;
    }

    public Color getLedColor() {
        return ledColor;
    }

    public ArrayList<IRSensor> getiRSensors() {
        return iRSensors;
    }

    public int getId() {
        return id;
    }

    public void draw(Graphics2D gd) {

        moveRobot();

        Graphics2D g2d = (Graphics2D) gd.create();

        Rectangle frame = new Rectangle((int) x, (int) y,
                2 * Settings.ROBOT_RADIUS, 2 * Settings.ROBOT_RADIUS);

        TexturePaint roboImage = new TexturePaint(image, frame);

        for (IRSensor iRSensor : iRSensors) {
            iRSensor.draw(g2d);
        }

        g2d.setPaint(roboImage);
        g2d.rotate(Math.toRadians(angle), getCenterX(), getCenterY());
        g2d.fill(this);
        g2d.rotate(Math.toRadians(-angle), getCenterX(), getCenterY());

        sharp.draw(g2d);

        ledStript.draw(g2d);

        g2d.dispose();
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

    @Override
    public void loop() {

    }

    @Override
    public void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void moveForward() {
        wheelStop = false;
        forward = true;
    }

    @Override
    public void moveBackward() {
        wheelStop = false;
        forward = false;
    }

    @Override
    public void moveStop() {
        wheelStop = true;
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
                Thread.sleep(100 - Settings.ROBOT_SPEED);
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
                Thread.sleep(100 - Settings.ROBOT_SPEED);
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
                Thread.sleep(100 - Settings.ROBOT_SPEED);
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
                Thread.sleep(100 - Settings.ROBOT_SPEED);
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
    public double findDistance() {
        return sharp.readDistance();
    }

    @Override
    public void avoidObstacles() {
        double distance = findDistance();
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
                Thread.sleep(100 - Settings.ROBOT_SPEED);
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void moveRandom() {
        wheelStop = false;
        forward = true;
        moveForwardDistance(10);
        int randomAngle = Utility.randomInRange(0, 3);
        angularTurn(randomAngle);
    }

    @Override
    public void broadcastMessage(Message message) {
        for (IRSensor iRSensor : iRSensors) {
            iRSensor.setBroadcastMsg(message);
        }

    }

    @Override
    public void broadcastMessage(MessageType header) {
        for (IRSensor iRSensor : iRSensors) {
            iRSensor.setBroadcastMsg(new Message(header, this));
        }
    }

    @Override
    public void sendMessage(MessageType message, Robot receiver, Data data) {
        Message msg = new Message(message, this, receiver);
        msg.setData(data);
        broadcastMessage(msg);
    }

    @Override
    public void sendMessage(MessageType message, Robot receiver) {
        sendMessage(message, receiver, null);
    }

    @Override
    public Message recieveMessage(int index) {
        return iRSensors.get(index).getRecieveMsg();
    }

    @Override
    public void resetReceivers(int index) {
        iRSensors.get(index).setRecieveMsg(null);
    }

    @Override
    public synchronized void processMessage(Message message, int sensorId) {
    }

    @Override
    public void comeCloser(double heading, int dist) {

        if (heading > 0) {
            angularTurn(heading);
        } else if (heading > 0) {
            angularTurn(3600 - heading);
        }
        moveForwardDistance(dist);
        moveStop();

    }

    @Override
    public Color findColor() {
        return sharp.readColor();
    }

    @Override
    public void rotateToRobot() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void moveRobot() {
        if (!wheelStop) {

            double xdelta = Math.sin(Math.toRadians(angle));
            double ydelta = Math.cos(Math.toRadians(angle));

            x += forward ? xdelta : -xdelta;
            y -= forward ? ydelta : -ydelta;

        }
    }

}
