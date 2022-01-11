package demo;
import common.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

public class Machine_0059 extends Machine{

    private int stepSize, decision, phaseNum=0, final_msg, t, Round2Count_msg0 = 0, Round2Count_msg1 = 0, Round1Count_msg0 = 0, Round1Count_msg1 = 0, waitMove=0;
    private boolean checkCorrect, isLeader=false;
    private Location pos = new Location(0,0);
    private Location dir = new Location(0,1);
    private ArrayList<Machine> machines = new ArrayList<Machine>();
    private static int nextId = 0;
    private int id;
    private boolean flagError = true;
    Random random;

    public Machine_0059()
    {
        id = nextId++;
        random = new Random();
        random.setSeed(123456789);
    }
    public void setState(boolean isCorrect) {
        waitMove = 0;
        phaseNum++;
        checkCorrect = isCorrect;
        isLeader = false;
        Round2Count_msg0 = 0;       //Counts round2 message 0...
        Round2Count_msg1 = 0;       //Counts round2 message 1...
        Round1Count_msg0 = 0;       //Counts round1 message 0...
        Round1Count_msg1 = 0;       //Counts round1 message 1...
        flagError = true;           //Used to flag error...
    }

    public void setLeader() {
        isLeader = true;
        decision = random.nextInt(2);           //Randomly selects decision...
        System.out.println("Decision : " + decision);
        if(checkCorrect == true)        //If leader is not faulty...
        {
            for(Machine machine : machines)
                machine.sendMessage(machines.indexOf(this), phaseNum, 0, decision);
        }
        else {              //If leader is faulty...
            int count = random.nextInt(machines.size() - ((2 * t) + 1)) + (2 * t) + 1;        //Randomly selects a value which is at least (2t+1)...
            LinkedHashMap<Integer, Integer> machine_check = new LinkedHashMap<Integer, Integer>();
            while (true) {
                if (machine_check.size() == count)
                    break;
                int r = random.nextInt(machines.size());
                while (machine_check.containsKey(r))
                    r = random.nextInt(machines.size());
                machine_check.put(r, 1);
                machines.get(r).sendMessage(machines.indexOf(this), phaseNum, 0, decision);     //Randomly sends message to at least 2t+1 machines...
            }
        }
    }

    public void sendMessage(int sourceId, int phaseNum, int roundNum, int decision) {
        if(roundNum == 1) {             //Counts Round 1 messages...
            if (decision == 0)
                Round1Count_msg0++;
            else if (decision == 1)
                Round1Count_msg1++;
        }
        else if(roundNum == 2)          //Counts Round 2 messages...
        {
            if (decision == 0)
                Round2Count_msg0++;
            else if (decision == 1)
                Round2Count_msg1++;
        }

        if(roundNum == 0) {
            if (checkCorrect == true) {             //If machine is correct...
                for (Machine machine : machines)
                    machine.sendMessage(machines.indexOf(this), phaseNum, 1, decision);     //Sends Round 1 messages...
            }
            else {                          //If machine is faulty...
                int silent = random.nextInt(2);
                if (silent == 0)         //machine will not remain silent
                {
                    int sendCorrectMsg = random.nextInt(2);
                    if (sendCorrectMsg == 0) {          //Faulty machines has decided not to send correct message...
                        if (decision == 0)
                            decision = 1;
                        else
                            decision = 0;
                    }
                    for (Machine machine : machines)
                        machine.sendMessage(machines.indexOf(this), phaseNum, 1, decision);         //Sends Round 1 messages...
                }
            }
        }

        if((Round1Count_msg1 + Round1Count_msg0) == (2*t) + 1) {                // Round 1 will over here...
            if(Round1Count_msg1 > Round1Count_msg0)         //Choose that message which the machine got in majority...
                final_msg = 1;
            else
                final_msg = 0;
            Round1Count_msg1 = Round1Count_msg0 = Integer.MIN_VALUE;        //Count variables are set to minimum integer value...

            /*......Sends Round 2 messages.....*/

            if (checkCorrect == true) {         //If machine is correct...
                for (Machine machine : machines)
                    machine.sendMessage(machines.indexOf(this), phaseNum, 2, final_msg);            //Sends the message with the decision as the message which it got in majority in last round...
            } else {                                                    //If machine is faulty...
                int silent = random.nextInt(2);
                if (silent == 0)         //machine will not remain silent
                {
                    int sendCorrectMsg = random.nextInt(2);
                    if (sendCorrectMsg == 0) {
                        if (final_msg == 0)
                            final_msg = 1;
                        else
                            final_msg = 0;
                    }
                    for (Machine machine : machines)
                        machine.sendMessage(machines.indexOf(this), phaseNum, 2, final_msg);            //Sends the message...
                }
            }
        }

        /*    If the machine got at least (2t+1) identical values, then the round will get over...   */

        if(Round2Count_msg1 == (2*t) + 1) {                 //Round 2 will get over now...
            final_msg = Round2Count_msg1;

            //Sets the direction vector...
            int x = dir.getX();
            int y = dir.getY();
            if((x == 0) && (y == 1))
                dir.setLoc(1, 0);
            else if((x == 1) && (y == 0))
                dir.setLoc(0, -1);
            else if((x == -1) && (y == 0))
                dir.setLoc(0, 1);
            else if((x == 0) && (y == -1))
                dir.setLoc(-1, 0);
            Round2Count_msg0 = Round2Count_msg1 = Integer.MIN_VALUE;
        }
        else if(Round2Count_msg0 == (2*t) + 1)
        {
            final_msg = Round2Count_msg0;

            //Sets the direction vector...
            int x = dir.getX();
            int y = dir.getY();
            if((x == 0) && (y == 1))
                dir.setLoc(-1, 0);
            else if((x == 1) && (y == 0))
                dir.setLoc(0, 1);
            else if((x == -1) && (y == 0))
                dir.setLoc(0, -1);
            else if((x == 0) && (y == -1))
                dir.setLoc(1, 0);
            Round2Count_msg0 = Round2Count_msg1 = Integer.MIN_VALUE;
        }
    }

    public void setStepSize(int step)
    {
        stepSize = step;
    }

    protected void move() {
        pos.setLoc(pos.getX() + dir.getX()*stepSize,
                pos.getY() + dir.getY()*stepSize);
        if(waitMove < 2) {
            if ((Round1Count_msg1 != Integer.MIN_VALUE) || (Round2Count_msg0 != Integer.MIN_VALUE))
                ;
            else
                flagError = false;
            waitMove++;
        }
        else {
            waitMove = 0;
            if (flagError) {
                System.out.println("Error!!!");         //Flags error if the machine didn't got at least (2t+1) identical messages...
            }
        }
    }

    public String name() {
        return "Machine "+id;
    }

    public Location getPosition() {
        return new Location(pos.getX(), pos.getY());
    }

    public void setMachines(ArrayList machineArrayList)
    {
        machines.addAll(machineArrayList);
        t = machines.size()/3;
    }
}
