package demo;

import common.Location;
import common.Machine;
import java.util.*;
public class Machine_0001 extends Machine {

	public Machine_0001() {
		id = nextId++;
	}

	@Override
	public void setMachines(ArrayList<Machine> machines)
	{
	 mach.addAll(machines);
	 numMach= machines.size();
	}

	@Override
	public void setStepSize(int stepSize) {
		step = stepSize;
	}

	@Override
	public void setState(boolean isCorrect) {
		state = isCorrect;
		if(!isCorrect) numFaulty++; // number of faulty machines is recorded
	}

	@Override
	public void setLeader() {
		lstate =true;
		phase++;
		//System.out.println(phase);
		
		//Round 0
		this.sendMessage(id,phase,0,69);

		//Round 1: all the machines send all the messages received by them to the other machines
        for(Machine temp:mach) temp.sendMessage((int)temp.getId(),phase,1,69);

		//Round 2
		for(Machine temp:mach) temp.sendMessage((int)temp.getId(),phase,2,69);

		numFaulty=0;
		lstate=false;
	}

	@Override
	public void sendMessage(int sourceId, int phaseNum, int roundNum, int decision) {
	
		if(roundNum==0 && decision ==69) // if the leader is not faulty then the same message is sent to all of the machines whereas if the leader is faulty then the leader sends random messages to atleast 2t+1 messages.
		{
		 int count=0;
		 Random rand = new Random();
		 int x  = rand.nextInt(2);
		 System.out.println("decision of leader is: " + x);
		 for(Machine machine:mach)
		  {
		   int k = rand.nextInt(2);
		   if(state == false && k==1 && count<numFaulty)
		    {
			 count++;
             continue;
			}
		   machine.sendMessage(id,phase,0,x);
		  }
		 count=0;
		}
		else if(roundNum==0 && decision!=69 )  // receiving messages by machines in round 0
		{
		 if(decision==0){ r++; first_r++;}
		 else if(decision==1) { l++; first_l++;} 
		} 
		else if(roundNum==1 && decision==69) // if a machine is not faulty then it just transmits the message if recived to all of the other machines in this round whereas if the machine is faulty then it can either send same random messasge to all of the machines or it can remain silent for this round
		{
		 Random rand = new Random();
		 int x=99;
		 if(state == true) 
		  {
			if(first_l==1) x=1;
			else if(first_r==1) x=0;
			else return ;
		  }
		  if(state == false)
		   {
			int k = rand.nextInt(2);
			if(k==0) x= rand.nextInt(2);  // send same random message to all the other Machines
            else if(k==1) return;
		   }
		 for(Machine machine:mach)
		  {
		   machine.sendMessage(id,phase,1,x);
		  }
		}
		else if(roundNum==1 && decision !=69)  // receiving messages in round 1 by all of the machines
		{
		 if(decision==0) r++;
		 else if(decision==1) l++; 
		}
		else if(roundNum==2 && decision ==69) // regardless if the machine is faulty or not, each machine transmits its majority decision to all of the other machines
		{
		 int x=99;
		 Random rand = new Random();
		 if(state == true)
		  {
		   if(l>r) x=1;
		   else if(r>l) x=0;
		  }
		 else
		  {
		   x = rand.nextInt(2);
		  }
		 for(Machine machine:mach)
		  {
		   machine.sendMessage(id,phase,2,x);
		  }
		}
		else if(roundNum==2 && decision !=69) // receiving messages by all of the machines in round 2
		{
		 if(decision ==1) finaldecision_l++;
		 else if(decision == 0) finaldecision_r++;
		}
	}

	@Override
	public
	void move() {
		int k= -50;
        // decision is taken based on the majority value and and error statement is printed if 2t+1 identical values arent received by every machine
		if(finaldecision_l > finaldecision_r)
		 {
          k=finaldecision_l;
		  System.out.println("Machine: " + id + " state: " + state + " final decision: left");
		  if(k < ((2*numFaulty)+1) && phase !=0)
		   {
		    System.out.println("Error " + phase );
		   }
		  if(dir.getX()==0) dir.setLoc(dir.getY()*-1, 0);
		  else if(dir.getY() ==0) dir.setLoc(0,dir.getX());
		 }
		else if(finaldecision_l < finaldecision_r)
		 {
		  k=finaldecision_r;
		  System.out.println("Machine: " + id + " state: " + state + " final decision: right");
		  if(k < ((2*numFaulty)+1) && phase !=0)
		   {
		    System.out.println("Error " + phase );
		   }
		  if(dir.getX()==0) dir.setLoc(dir.getY(), 0);
		  else if(dir.getY() ==0) dir.setLoc(0,dir.getX()*-1);
		 }
		//System.out.println(k + " " + numFaulty);
		//System.out.println(l+" " +r+" " +finaldecision_r+" "+finaldecision_l);
		l=0;
		r=0;
		first_r=0;
		first_l=0;
		finaldecision_r=0;
		finaldecision_l=0;
		pos.setLoc(pos.getX() + dir.getX()*step, pos.getY() + dir.getY()*step);	
	}

	@Override
	public
	String name() {
		return "0001_"+id;
	}

	@Override
	public Location getPosition() {
		
		return new Location(pos.getX(), pos.getY());
	}

	private int step;
	private boolean state;
	// if x==0 then right and if x==1 then left and if x==2 then no message is sent to that machine
	private int l=0;
	private int first_l=0;
	private int first_r=0;
	private int finaldecision_l=0;
	private int finaldecision_r=0;
	private int r=0;
	private ArrayList<Machine> mach = new ArrayList<Machine>();
	private static int numFaulty=0;
	private static int phase=0;
	private int numMach;
	private boolean lstate=false;
	private Location pos = new Location(0,0);
	private Location dir = new Location(0,1); // using Location as a 2d vector. Bad!
	private static int nextId = 0;
	private int id;	
}
