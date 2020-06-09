
import communication.Message;
import communication.MessageType;
import communication.messageData.general.DistanceData;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class MorseCodes {

    static int index = 0;

    public static void main(String[] args) {

        String msg = ".. -- .- --. . / .--. .-. --- -.-. . ... ... .. -. --.";
        char[] chs = msg.toCharArray();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                index++;
                if (index >= chs.length) {
                    index = 0;
                }

            }
        }, 0, 2000);

        Swarm swarm = new Swarm("Image Processing") {

            @Override
            public void create() {

                for (int i = 0; i < 5; i++) {

                    join(new Robot() {

                        @Override
                        public void loop() {
                            moveRandom();
                            avoidObstacles();

                            if (chs[index] == '.') {
                                swithOnLedStript(Color.yellow);
                            } else if (chs[index] == '-') {
                                swithOnLedStript(Color.red);
                            } else {
                                swithOffLedStript();
                            }
                        }

                    });

                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.start();
    }
}
