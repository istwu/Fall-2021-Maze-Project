package edu.wm.cs.cs301.isabellawu.gui;

import java.util.ArrayList;

import edu.wm.cs.cs301.isabellawu.generation.CardinalDirection;
import edu.wm.cs.cs301.isabellawu.generation.Maze;
import edu.wm.cs.cs301.isabellawu.generation.Wallboard;
import edu.wm.cs.cs301.isabellawu.gui.Robot.Turn;

/**
 * The responsibility of the Wizard is to solve the maze by continuously finding the adjacent cells 
 * to the robot closest to the exit and moving the robot one step at a time until it reaches the exit position. 
 * It will also jump over walls in situations where it is most energy efficient to do so.
 * It collaborates with the Maze object to get distance information and the ReliableRobot to give instructions.
 * 
 * @author Isabella Wu
 */
public class Wizard implements RobotDriver {

	protected Robot robot;
	protected Maze maze;
	protected float energyConsumption;
	protected int pathLength;
	protected int[][] mazedists;
	
	/**
	 * Default constructor, instantiates counters used to track energy and cells traveled.
	 */
	public Wizard() {
		energyConsumption = 0;
		pathLength = 0;
	}

	/**
	 * Assigns a robot platform to the driver. 
	 * The driver uses a robot to perform, this method provides it with this necessary information.
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		robot = r;
	}

	/**
	 * Provides the robot driver with the maze information.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param maze represents the maze, must be non-null and a fully functional maze object.
	 */
	@Override
	public void setMaze(Maze maze) {
		this.maze = maze;
		mazedists = maze.getMazedists().getAllDistanceValues();
	}

	/**
	 * Drives the robot towards the exit following
	 * its solution strategy and given the exit exists and  
	 * given the robot's energy supply lasts long enough. 
	 * When the robot reached the exit position and its forward
	 * direction points to the exit the search terminates and 
	 * the method returns true.
	 * If the robot failed due to lack of energy or crashed, the method
	 * throws an exception.
	 * If the method determines that it is not capable of finding the
	 * exit it returns false, for instance, if it determines it runs
	 * in a cycle and can't resolve this.
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		ArrayList<int[]> visited = new ArrayList<>();
		
		while(!robot.getCurrentPosition().equals(maze.getExitPosition())) {
			if(robot.hasStopped()) {
				throw new Exception("Robot has stopped.");
			}
			boolean driving = drive1Step2Exit();
			if(!driving) {	// if exit has been reached
				robot.move(1);
				energyConsumption += robot.getEnergyForStepForward();
				pathLength += 1;
				return true;
			}
			if(visited.contains(robot.getCurrentPosition())) {
				return false;
			}
			visited.add(robot.getCurrentPosition());
		}
		return false;
	}

	/**
	 * Drives the robot one step towards the exit following
	 * its solution strategy and given the exists and 
	 * given the robot's energy supply lasts long enough.
	 * It returns true if the driver successfully moved
	 * the robot from its current location to an adjacent
	 * location.
	 * At the exit position, it rotates the robot 
	 * such that if faces the exit in its forward direction
	 * and returns false. 
	 * If the robot failed due to lack of energy or crashed, the method
	 * throws an exception. 
	 * @return true if it moved the robot to an adjacent cell, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		assert !robot.hasStopped();
		
		if(robot.getBatteryLevel() < robot.getEnergyForStepForward()) {
			throw new Exception("Not enough battery to move 1 step.");
		}
		
		if(rotateOnExit()) {
			return false;
		}

		// jump has been implemented as a bonus for p4
		// commented out to preserve functionality of tests written before jump was implemented
//		if(canJump()) {
//			return true;
//		}
		
		int[] current_position = robot.getCurrentPosition();
		CardinalDirection current_direction = robot.getCurrentDirection();
		int[] step = maze.getNeighborCloserToExit(current_position[0], current_position[1]);
		CardinalDirection step_direction = robot.getCurrentDirection();
		
		if(step[0] < current_position[0]) {
			step_direction = CardinalDirection.West;
		}
		else if(step[0] > current_position[0]) {
			step_direction = CardinalDirection.East;
		}
		else if(step[1] < current_position[1]) {
			step_direction = CardinalDirection.North;
		}
		else if(step[1] > current_position[1]){
			step_direction = CardinalDirection.South;
		}
		
		if(step_direction != current_direction) {
			mostEfficientRotation(step_direction);
		}
		robot.move(1);
		energyConsumption += robot.getEnergyForStepForward();
		pathLength += 1;
		return true;
	}
	
	/**
	 * Checks if the robot is on the exit position. If it is, rotates the 
	 * robot to face the outside and returns true. Otherwise, returns false.
	 * @return true if robot is at exit position, false otherwise
	 * @throws Exception thrown if robot's current position is out of bounds
	 */
	private boolean rotateOnExit() throws Exception {
		int[] exit_position = maze.getExitPosition();
		int[] current_position = robot.getCurrentPosition();
		CardinalDirection current_direction = robot.getCurrentDirection();
		
		// if current position is exit position, rotate to face the outside and return false
		if(current_position[0] == exit_position[0] && current_position[1] == exit_position[1]) {
			if(current_position[0] == 0 && !maze.hasWall(current_position[0], current_position[1], CardinalDirection.West) 
					&& current_direction != CardinalDirection.West) {
				mostEfficientRotation(CardinalDirection.West);
			}
			else if(current_position[0] == maze.getWidth()-1 && !maze.hasWall(current_position[0], current_position[1], CardinalDirection.East) 
					&& current_direction != CardinalDirection.East) {
				mostEfficientRotation(CardinalDirection.East);
			}
			else if(current_position[1] == 0 && !maze.hasWall(current_position[0], current_position[1], CardinalDirection.North) 
					&& current_direction != CardinalDirection.North) {
				mostEfficientRotation(CardinalDirection.North);
			}
			else if(current_position[1] == maze.getHeight()-1 && !maze.hasWall(current_position[0], current_position[1], CardinalDirection.South) 
					&& current_direction != CardinalDirection.South) {
				mostEfficientRotation(CardinalDirection.South);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Determines whether or not the robot should jump over a wall. If so, performs
	 * the jump and returns true. Else, returns false.
	 * @return whether or not the robot has jumped over a wall
	 * @throws Exception if robot's current position is out of bounds
	 */
	private boolean canJump() throws Exception {
		int[] current_position = robot.getCurrentPosition();
		CardinalDirection current_direction = robot.getCurrentDirection();
		int[] jump = robot.getCurrentPosition();
		CardinalDirection jump_direction = robot.getCurrentDirection();
		
		// find adjacent space (regardless of walls) closest to exit
		int[] N = new int[] {current_position[0], current_position[1] - 1};
		int[] S = new int[] {current_position[0], current_position[1] + 1};
		int[] E = new int[] {current_position[0] + 1, current_position[1]};
		int[] W = new int[] {current_position[0] - 1, current_position[1]};
		
		if(W[0] >= 0 && W[0] < maze.getWidth()) {
			if(mazedists[W[0]][W[1]] < mazedists[jump[0]][jump[1]]) {
				jump = W;
				jump_direction = CardinalDirection.West;
			}
		}
		if(E[0] >= 0 && E[0] < maze.getWidth()) {
			if(mazedists[E[0]][E[1]] < mazedists[jump[0]][jump[1]]) {
				jump = E;
				jump_direction = CardinalDirection.East;
			}
		}
		if(N[1] >= 0 && N[1] < maze.getHeight()) {
			if(mazedists[N[0]][N[1]] < mazedists[jump[0]][jump[1]]) {
				jump = N;
				jump_direction = CardinalDirection.North;
			}
		}
		if(S[1] >= 0 && S[1] < maze.getHeight()) {
			if(mazedists[S[0]][S[1]] < mazedists[jump[0]][jump[1]]) {
				jump = S;
				jump_direction = CardinalDirection.South;
			}
		}
		
		// if there is no wall to jump, or if the wall to jump is a border wall
		if(!maze.getFloorplan().hasWall(current_position[0], current_position[1], jump_direction) || 
				maze.getFloorplan().isPartOfBorder(new Wallboard(current_position[0], current_position[1], jump_direction))) {
			return false;
		}
		else if(mazedists[jump[0]][jump[1]] > mazedists[current_position[0]][current_position[1]] - 7) {
			return false;
		}
		else {
			if(jump_direction != current_direction) {
				mostEfficientRotation(jump_direction);
			}
			robot.jump();
			energyConsumption += 40;
			pathLength += 1;
			return true;
		}
	}
	
	
	/**
	 * Given the desired cardinal direction, finds and performs the rotation that
	 * costs the robot the least amount of energy, then stores the energy used to rotate 
	 * into energy_used.
	 * Does nothing if the desired direction and the robot's current direction are the same.
	 * @param desiredDirection	the intended direction for the robot after rotating
	 */
	private void mostEfficientRotation(CardinalDirection desiredDirection) {
		if(desiredDirection != robot.getCurrentDirection()) {
			if(desiredDirection == robot.getCurrentDirection().oppositeDirection() && robot.getBatteryLevel() >= 6) {
				robot.rotate(Turn.AROUND);
				energyConsumption += robot.getEnergyForFullRotation()/2;
			}
			else if(desiredDirection == robot.getCurrentDirection().rotateClockwise() && robot.getBatteryLevel() >= 3) {
				robot.rotate(Turn.LEFT);
				energyConsumption += robot.getEnergyForFullRotation()/4;
			}
			else if(desiredDirection == robot.getCurrentDirection().rotateClockwise().rotateClockwise().rotateClockwise() && robot.getBatteryLevel() >= 3) {
				robot.rotate(Turn.RIGHT);
				energyConsumption += robot.getEnergyForFullRotation()/4;
			}
		}
	}

	/**
	 * Returns the total energy consumption of the journey, i.e.,
	 * the difference between the robot's initial energy level at
	 * the starting position and its energy level at the exit position. 
	 * This is used as a measure of efficiency for a robot driver.
	 * @return the total energy consumption of the journey
	 */
	@Override
	public float getEnergyConsumption() {
		return energyConsumption;
	}

	/**
	 * Returns the total length of the journey in number of cells traversed. 
	 * Being at the initial position counts as 0. 
	 * This is used as a measure of efficiency for a robot driver.
	 * @return the total length of the journey in number of cells traversed
	 */
	@Override
	public int getPathLength() {
		return pathLength;
	}

}
