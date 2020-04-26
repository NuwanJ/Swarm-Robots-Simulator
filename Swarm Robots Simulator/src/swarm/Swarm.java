package swarm;

import robot.Robot;
import java.util.ArrayList;
import view.Obstacle;

/**
 *
 * @author Nadun
 */
public abstract class Swarm {

    private ArrayList<Robot> robots;
    private ArrayList<Obstacle> obstacles;
    
    private String name;

    public Swarm(String name) {
        this.name = name;
        robots = new ArrayList<>();
        obstacles = new ArrayList<>();
        create();
    }
    
    public abstract void create();
    
    public void join(Robot robot) {
        robots.add(robot);
    }

    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
    
    public void addObstacle(Obstacle obstacle) {
        this.obstacles.add(obstacle);
    }

}
