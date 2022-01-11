package demo;

import common.Location;
import common.Machine;
import java.util.ArrayList;
import java.util.Random;
public class Machine_0015 extends Machine {

	public Machine_0015() {
		id = nextId++;
		isCorrect = true;
		phaseNum = 0;
		r1Left = r1Right = r2Left = r2Right = 0;
		isDecisionMade = true;
	}
	
	@Override
	public void setStepSize(int stepSize) {
		step = stepSize;
	}

	@Override
	public void setState(boolean isCorrect) {
		if(!isDecisionMade && isCorrect) System.out.println("Error: Didn't received enough messages");

		//Setting state before start of each phase
		this.isCorrect = isCorrect;
		isDecisionMade = false;
		r1Left = r1Right = r2Left = r2Right = 0;
	}

	@Override
	public void setLeader() {
		decision = random.nextInt(2);
		//System.out.println("leader:" + decision);
		phaseNum++;
		if(isCorrect)
		{
			for(int i=0;i<listOfMachines.size();i++)
			{
				listOfMachines.get(i).sendMessage(id,phaseNum,0,decision);
			}
		}
		else
		{
			for(int i=0;i<2*faulty+1;i++)
			{
				listOfMachines.get(i).sendMessage(id, phaseNum,0, decision);
			}
		}
	}

	@Override
	public void sendMessage(int sourceId, int phaseNum, int roundNum, int decision) {
		this.phaseNum = phaseNum;
		if(isCorrect)
		{
			if(roundNum ==0)
			{

				this.decision = decision;
				for(int i = 0;i<listOfMachines.size();i++)
				{
					listOfMachines.get(i).sendMessage(id, this.phaseNum, 1, this.decision);
				}
			}
			else if(roundNum == 1 && isDecisionMade == false)
			{
				if(decision == 0) r1Left++;
				else if(decision == 1) r1Right++;
				//System.out.println(" id " + id+ " "+ r1Left + " "+ r1Right);
				int tmp_dec=-1;
				if(r1Left > faulty) tmp_dec = 0;
				else if(r1Right > faulty) tmp_dec = 1;
				if(tmp_dec >= 0)	//checking for majority decision
				{
					for(int i=0;i<listOfMachines.size();i++)
					{
						listOfMachines.get(i).sendMessage(id, phaseNum, 2, tmp_dec);
					}
				}
			}
			else if(roundNum == 2 && isDecisionMade == false )
			{
				int flag = 0;
				if(!(r2Left >= 2*faulty +1) && !(r2Right >= 2*faulty + 1))
				{
					if(decision == 0) r2Left++;
					if(decision == 1) r2Right++;
				}
				if(r2Left >= 2*faulty +1) //if decision left has at least 2t+1 count
				{
					this.decision = 0;
					flag = 1;
				}
				else if(r2Right >= 2*faulty + 1) //if decision right has at least 2t+1 count
				{
					this.decision = 1;
					flag = 1;
				}	
				if(flag == 1) //executes when final decision is taken by machine for the phase
				{
					if(this.decision == 0) dir = new Location(-dir.getY(),dir.getX());
					else dir = new Location(dir.getY(),-dir.getX());
					r2Left = r2Right = 0;
					//System.out.println(this.id + " my decision is :"+ this.decision);
					isDecisionMade = true;
				}
			}

		}
	}

	@Override
	public
	void move() {
		pos.setLoc(pos.getX() + dir.getX()*step, 
					pos.getY() + dir.getY()*step);
			
	}

	@Override
	public
	String name() {
		return "0015_"+id;
	}

	@Override
	public Location getPosition() {
		
		return new Location(pos.getX(), pos.getY());
	}

 	@Override
	public void setMachines(ArrayList<Machine> machines)
	{
		listOfMachines.addAll(machines);
		faulty = (listOfMachines.size()-1)/3;
		System.out.println("faulty: "+faulty);
	}

	private int step,phaseNum,decision,r1Left,r1Right,r2Left,r2Right,faulty;
	private	Boolean isDecisionMade;
	private Location pos = new Location(0,0);
	private Location dir = new Location(0,1); // using Location as a 2d vector. Bad!
	private static int nextId = 0;
	private int id;
	private ArrayList<Machine> listOfMachines = new ArrayList<Machine>();
	private boolean isCorrect;
	private Random random = new Random();
}