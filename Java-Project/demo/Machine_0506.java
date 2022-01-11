package demo;

import common.Location;
import common.Machine;
import java.util.*;

public class Machine_0506 extends Machine {

	private int step;
	private boolean state;
	private Location pos = new Location(0, 0);
	private Location dir = new Location(0, 1);
	private int id;
	private String name;
	private Random rand;
	private int round_num;
	private int left_1, left_2, right_1, right_2;
	private ArrayList<Machine> machines;
	private int phase = -1;
	private int faulty;
	private int Totalmachines;

	public Machine_0506(int n) {
		this.name = "Machine_0506" + Integer.toString(n);
		this.rand = new Random();
		this.machines = new ArrayList<Machine>();
		left_1 = left_2 = right_1 = right_2 = 0;
		// pos.setLoc(x, y);
	}

	@Override
	public void setStepSize(int stepSize) {
		step = stepSize;
	}

	@Override
	public void setMachines(ArrayList<Machine> machines) {
		this.machines = machines;
		Totalmachines = machines.size();
		faulty = Totalmachines / 3;
		this.id = machines.indexOf(this);
	}

	@Override
	public void setState(boolean isCorrect) {
		state = isCorrect;
		phase++;
		round_num = 0;
		left_1 = left_2 = right_1 = right_2 = 0;
	}

	@Override
	public void setLeader() {
		round_num = 0;
		int decision = rand.nextInt(2);
		if (state) {
			for (Machine m : machines)
				m.sendMessage(id, phase, round_num, decision);
		} else {
			Set<Integer> Normal_machines = new HashSet<Integer>();
			int n = 2 * faulty + 1 + rand.nextInt(Totalmachines - (2 * faulty + 1) + 1);
			while (Normal_machines.size() != n)
				Normal_machines.add(rand.nextInt(Totalmachines));
			for (int i = 0; i < Totalmachines; i++) {
				if (Normal_machines.contains(i)) {
					machines.get(i).sendMessage(id, phase, round_num, decision);
				} else {
					machines.get(i).sendMessage(id, phase, round_num, rand.nextInt(2));
				}
			}
		}
	}

	@Override
	public void sendMessage(int sourceId, int phaseNum, int roundNum, int decision) {
		if (roundNum == 0) {
			round_num = 1;
			if (state) {
				for (Machine m : machines)
					m.sendMessage(id, phase, round_num, decision);
			} else {
				int n = rand.nextInt(2);
				int decision2 = rand.nextInt(2);
				if (n == 0) {
					for (Machine m : machines)
						m.sendMessage(id, phase, round_num, decision2);
				}
			}
		} else if (roundNum == 1 && round_num != 2) {
			if (decision == 0)
				left_1++;
			else
				right_1++;
			if (left_1 + right_1 >= (2 * faulty + 1)) {
				int decision2 = (left_1 >= faulty + 1) ? 0 : 1;
				round_num = 2;
				if (state) {
					for (Machine m : machines)
						m.sendMessage(id, phase, round_num, decision2);
				} else {
					int n = rand.nextInt(2);
					int decision3 = rand.nextInt(2);
					if (n == 0) {
						for (Machine m : machines)
							m.sendMessage(id, phase, round_num, decision3);
					}
				}
			}
		} else if (roundNum == 2 && round_num != -1) {
			if (decision == 0)
				left_2++;
			else
				right_2++;
			if (left_2 >= 2 * faulty + 1) {
				if (dir.getY() == 0)
					dir.setLoc(0, dir.getX());
				else
					dir.setLoc(dir.getY() * -1, 0);
				move();
				left_1 = left_2 = right_1 = right_2 = 0;
				round_num = -1;
			} else {
				if (dir.getY() == 0)
					dir.setLoc(0, dir.getX() * -1);
				else
					dir.setLoc(dir.getY(), 0);
				move();
				left_1 = left_2 = right_1 = right_2 = 0;
				round_num = -1;
			}
			if (right_2 > 0)
				;
		}
	}

	@Override
	public void move() {
		pos.setLoc(pos.getX() + dir.getX() * step, pos.getY() + dir.getY() * step);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Location getPosition() {

		return new Location(pos.getX(), pos.getY());
	}

}
