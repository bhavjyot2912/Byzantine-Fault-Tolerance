package demo;

import common.Machine;
import common.Game;
import java.util.*;

public class Game_0116 extends Game {
    private ArrayList<Machine> machines = new ArrayList<Machine>();
    private int numFaulty;

    public void addMachines(ArrayList<Machine> machines, int numFaulty) {
        this.numFaulty = numFaulty;
        this.machines = machines;
    }

    public void startPhase() {
        Random random = new Random();
        int temp = 0, x, leader;
        ArrayList<Integer> faulty = new ArrayList<Integer>();
        while (true) {
            // Now we need to make an array of faulty machines
            // This task has to be done by randomly assigning
            // machines as faulty till the size of the array reaches numfaulty
            
            x = random.nextInt(machines.size());
            if (!faulty.contains(x))
                faulty.add(x);
            if (faulty.size() == numFaulty)
                break;
        }

        // Now we have chosen the array for faulty machines, we now need
        // to set their state as faulty and the remaining machines
        // as correct ones

        for (int i = 0; i < machines.size(); i++) {
            if (faulty.contains(i) == false) // Correct machine
                machines.get(i).setState(true);
            else if (faulty.contains(i) == true) // Faulty machine
                machines.get(i).setState(false);

            // Finally we have our array of machines assigned as faulty and correct ones and we can now start our phase by first calling setMachines()
            
            machines.get(i).setMachines(machines);
        }

        // Assigning their states to the machines we are now left with picking up a
        // leader which can either be faulty or correct. So do we it randomly.
        leader = random.nextInt(machines.size());
        machines.get(leader).setLeader();
    }

    @Override
    public void startPhase(int leaderId, ArrayList<Boolean> areCorrect){
        for(int i = 0; i < machines.size(); i++)
        {
            //Setting the state of the machines wrt to their occurance in the areCorrect arraylist
            if(areCorrect.get(i) == true)
                machines.get(i).setState(true);
            else if(areCorrect.get(i) == false)
                machines.get(i).setState(false);
        }
        //Calling setMachines after assigning all the states to the machines
        for(int i = 0; i < machines.size(); i++)
            machines.get(i).setMachines(this.machines);
        //Selecting the leader as we now have the leaderID
        machines.get(leaderId).setLeader();
    }
}