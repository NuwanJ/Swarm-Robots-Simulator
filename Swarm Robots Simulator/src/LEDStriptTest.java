
import communication.Message;
import communication.MessageType;
import java.awt.Color;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class LEDStriptTest {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("LED Stript Test") {
            @Override
            public void create() {

                for (int i = 0; i < 2; i++) {

                    join(new Robot() {

                        @Override
                        public void loop() {
                            
      
                            double d = findDistance();
                            //System.out.println(getId() + " - " + d);
//                            
                            if(d > 0 && d < 55) {
                                swithOnLedStript(Color.yellow);
                            }
                            
                            console.log("Angle " + angle);
                        }

                    });
                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.start();
    }
}
