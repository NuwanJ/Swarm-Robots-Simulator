package robot.behaviors;


/**
 *
 * @author Nadun
 */
public interface BasicBehaviors {
    
    public void moveForward(int delay);
    
    public void moveForwardDistance(int distance);
    
    public void moveRandom();
    
    public void moveBackward(int delay);
    
    public void moveForward();
    
    public void moveBackward();
    
    public void turnRight(int delay);
    
    public void turnLeft(int delay);
    
    public void turnRightAngle(double angle);
    
    public void turnLeftAngle(double angle);
    
    public void randomTurn(int min, int max);
    
    public void angularTurn(double angle);
    
    public void moveStop();
    
    public void avoidObstacles();
    
}
