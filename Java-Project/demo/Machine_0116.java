package demo;

import java.util.*;
import common.Location;
import common.Machine;

public class Machine_0116 extends Machine {

    private boolean isCorrect, leader, round1_done, round2_done;

    private int id, l_after_round1, r_after_round1, l_after_round2, r_after_round2, stepSize, numFaulty, decision,
            phaseNum;

    private String name;

    private String position;

    private Location location;

    private ArrayList<Machine> machines = new ArrayList<Machine>();

    public Machine_0116() {
        location = new Location(0, 0); // This is where the machine starts
        phaseNum = 0; //Starting from the initial phase
        leader = false; // No one is the leader right now and we are yet to decide for a leader

        round1_done = false;
        round2_done = false;//These variables show whether the Round1 and Round2 are completed or not respectively

        //The count of machines moving towards left and right after Round1 and Round2 
        l_after_round1 = 0;
        r_after_round1 = 0;
        l_after_round2 = 0;
        r_after_round2 = 0;

        decision = 0; // Setting the initial decision for all (move towards left)
        name = "0116";
        position = "y+"; // Machine is initially moving towards (0,1) from (0,0)

    }

    @Override
    public void setMachines(ArrayList<Machine> machines) {
        // machines.addAll(machines);
        this.machines = machines;
        numFaulty = (int) (machines.size() / 3); //Setting the number of faulty machines
        for (int i = 0; i < machines.size(); i++) {
            if (machines.get(i) == this)
                id = i; //This assigns ID te each machine corresponding to its position in the arraylist
        }
    }

    @Override
    public void setState(boolean isCorrect) {
        this.isCorrect = isCorrect; //Whether the macine is a correct one or a faulty one
        
        //Variables set to their initial state in the beginning.
        //Leader is yet to be decided and so does it means that the phase is yet to start
        leader = false;
        round1_done = false;
        round2_done = false;
        l_after_round1 = 0;
        r_after_round1 = 0;
        l_after_round2 = 0;
        r_after_round2 = 0;

    }

    @Override
    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    // A faulty leader can not send his message to at max t machines
    // so we need an array of faulty machines which can have its size
    // anywhere from 0 to t (random integer)
    @Override
    public void setLeader() {
        leader = true;
        // int size;
        ArrayList<Integer> faulty = new ArrayList<Integer>(); //This is the array of faulty machines
        
        Random random = new Random();
        int faisla = random.nextInt(2);

        if (isCorrect == false) //If the machines is faulty
        {
            int x, /* faisla, */ temp = 0;
            int size = 0;
            if (numFaulty != 0)
                size = random.nextInt(numFaulty); //The size of this array can be anywhere ranging from 0 to t;
            else if (numFaulty == 0)
                size = 0;

            // Now we need to have a list of machines which are faulty and can have atmost t size. When the size of this array becomes t, we will then break the loop, till then we will continue to add machines to it.

            while (true) {
                x = random.nextInt(machines.size());
                if (!faulty.contains(x))
                    faulty.add(x);
                if (faulty.size() == size)
                    break;
            }

            // Faulty leader sending his message to atleast 2t+1 machines

            //faisla = random.nextInt(2); // Decision to be sent to faulty machine
            for (int i = 0; i < machines.size(); i++) {
                if (faulty.contains(i) == false)
                {
                    machines.get(i).sendMessage(id, phaseNum, 0, 0);
                    /* if(faisla == 0)
                        machines.get(i).sendMessage(id, phaseNum, 0, 1);
                    else
                        machines.get(i).sendMessage(id, phaseNum, 0, 0);  */ 
                }
                else if(faulty.contains(i) == true)
                {
                    machines.get(i).sendMessage(id, phaseNum, 0, faisla);
                    //machines.get(i).sendMessage(id, phaseNum, 0, faisla);
                }

            }
        }

        // Correct leader sending his message to everyone else. Sending the same message
        // to all
        else if (isCorrect == true) {
            //int faisla1 = random.nextInt(2);
            for (int i = 0; i < machines.size(); i++)
                machines.get(i).sendMessage(id, phaseNum, 0, 0);
        }

    }

    @Override
    public void sendMessage(int sourceId, int phaseNum, int roundNum, int decision) {
        Random random = new Random();
        if (roundNum == 0) {
            if (isCorrect == false) {
                decision = random.nextInt(2);
            } // A faulty machine

            for (int i = 0; i < machines.size(); i++) {
                if (decision == 0 || decision == 1)
                    machines.get(i).sendMessage(this.id, phaseNum, 1, decision); // Sending that leaders decision to the machines and then sending them to Round1
            }
        }

        //We now eneter the Round1. First we calculate the number of machines which are moving towards left and right respectively (those who got the message as left or right from the leader)
        //We then calculate if the leader has sent the message to atleast 2t+1 machines.
        //Now a faulty machine can communicate any decision with the other machines
        //Now we check for the decisionwhich was in majority for the machines whther to move to left or right

        else if (roundNum == 1) {
            if (decision == 0)
                l_after_round1++;
            else if (decision == 1)
                r_after_round1++;
            if (l_after_round1 + r_after_round1 >= 2 * numFaulty + 1) {
                if (isCorrect == false) 
                {
                    this.decision = random.nextInt(3) - 1;
                } 
                else if (isCorrect == true) 
                {
                    if (l_after_round1 > r_after_round1)
                        this.decision = 0;
                    else if (r_after_round1 > l_after_round1)
                        this.decision = 1;
                }

                //Sending all the machines to Round2 with the chosen decision
                if ((this.decision == 0 || this.decision == 1) && round1_done == false) 
                {
                    for (int i = 0; i < machines.size(); i++)
                        machines.get(i).sendMessage(this.id, phaseNum, 2, this.decision);
                    round1_done = true; //Round1 is done here
                }
            }
        }

        //We begin with Round2. First we need to calculate the number of machines which got the decision as left or right. Now if any decision is greater than 2t+1, that decision will be chosen as the final decision for that phase and for that particular machine
        //A faulty machine can choose any random decision
        else if (roundNum == 2) {
            if (decision == 0)
                l_after_round2++;
            else if (decision == 1)
                r_after_round2++;
            if (l_after_round2 >= 2 * numFaulty + 1)
                this.decision = 0;
            else if (r_after_round2 >= 2 * numFaulty + 1)
                this.decision = 1;
            if (isCorrect == false)
                //this.decision = 0;
                this.decision = random.nextInt(2);
            round2_done = true; //Round2 is done here
        }
    }

    //Turning the machine now to the specified direction according to the decison obtained from round 2. After this, round 2 will end and the next phase will be obtained and we will print the current location of the machine after that phase.
    @Override
    public void move() {
        if (round2_done)
        {
            if (position == "y+" && decision == 0) 
                position = "x-";

            if (position == "y+" && decision == 1)
                position = "x+";

            if (position == "x+" && decision == 0)
                position = "y+";

            if (position == "x+" && decision == 1)
                position = "y-";

            if (position == "y-" && decision == 0)
                position = "x+";

            if (position == "y-" && decision == 1)
                position = "x-";

            if (position == "x-" && decision == 0)
                position = "y-";

            if (position == "x-" && decision == 1)
                position = "y+";

            round2_done = true; //Round2 is done here and we move to the next phase
        }
        if (position == "x+")
            location.setLoc(location.getX() + stepSize, location.getY());
        if (position == "x-")
            location.setLoc(location.getX() - stepSize, location.getY());
        if (position == "y+")
            location.setLoc(location.getX(), location.getY() + stepSize);
        if (position == "y-")
            location.setLoc(location.getX(), location.getY() - stepSize);
        phaseNum++;
    }

    //Returns name of the machine
    @Override
    public String name() {
        return "Machine_" + name;
    }

    //Return the current location of the machine at the end of a phase
    @Override
    public Location getPosition() {
        return location;
    }

}