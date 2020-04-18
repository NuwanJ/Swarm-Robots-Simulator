package swarm;

import robot.Robot;
import java.util.ArrayList;

/**
 *
 * @author Nadun
 */
public abstract class Swarm {

    private ArrayList<Robot> robots;
    
    private String name;

    public Swarm(String name) {
        this.name = name;
        robots = new ArrayList<>();
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

}
