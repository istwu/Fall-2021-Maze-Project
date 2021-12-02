package edu.wm.cs.cs301.isabellawu.gui;

import edu.wm.cs.cs301.isabellawu.generation.CardinalDirection;
import edu.wm.cs.cs301.isabellawu.generation.Maze;
import edu.wm.cs.cs301.isabellawu.gui.Robot.Direction;

/**
 * The responsibility of the ReliableSensor is to use energy to detect the distance to a wall 
 * in a given direction. 
 * It collaborates with the Maze and Floorplan to measure the distances toward obstacles.
 * 
 * @author Isabella Wu
 */
public class ReliableSensor implements DistanceSensor {
	
	protected Direction direction;
	protected Maze maze;
	
	/**
	 * Empty constructor; instance variables are set from outside.
	 */
	public ReliableSensor() {
		
	}

	/**
	 * Tells the distance to an obstacle (a wallboard) that the sensor
	 * measures. The sensor is assumed to be mounted in a particular
	 * direction relative to the forward direction of the robot.
	 * Distance is measured in the number of cells towards that obstacle, 
	 * e.g. 0 if the current cell has a wallboard in this direction, 
	 * 1 if it is one step in this direction before directly facing a wallboard,
	 * Integer.MaxValue if one looks through the exit into eternity.
	 * 
	 * This method requires that the sensor has been given a reference
	 * to the current maze and a mountedDirection by calling 
	 * the corresponding set methods with a parameterized constructor.
	 * 
	 * @param currentPosition is the current location as (x,y) coordinates
	 * @param currentDirection specifies the direction of the robot
	 * @param powersupply is an array of length 1, whose content is modified 
	 * to account for the power consumption for sensing
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise.
	 */
	@Override
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply) {	
		assert currentPosition != null;
		assert currentDirection != null;
		assert powersupply != null;
		assert currentPosition[0] >= 0 && currentPosition[0] < maze.getWidth();
		assert currentPosition[1] >= 0 && currentPosition[1] < maze.getHeight();
		assert powersupply[0] >= 1;
		
		int dist = 0;
		int[] step = new int[] {currentPosition[0], currentPosition[1]};
		CardinalDirection sensingDirection = getSensingDirection(currentDirection);
		
		while(!maze.hasWall(step[0], step[1], sensingDirection)) {
			switch(sensingDirection) {
			case North: {
				step[1] -= 1;
				break;
			}
			case South: {
				step[1] += 1;
				break;
			}
			case East: {
				step[0] += 1;
				break;
			}
			case West: {
				step[0] -= 1;
				break;
			}
			}
			dist += 1;
			
			if(step[0] < 0 || step[0] >= maze.getWidth() || step[1] < 0 || step[1] >= maze.getHeight()) {
				return Integer.MAX_VALUE;
			}
		}
		
		return dist;
	}

	/**
	 * Provides the maze information that is necessary to make
	 * a DistanceSensor able to calculate distances.
	 * @param maze the maze for this game
	 */
	@Override
	public void setMaze(Maze maze) {
		assert maze != null;
		assert maze.getFloorplan() != null;
		this.maze = maze;
	}

	/**
	 * Provides the angle, the relative direction at which this 
	 * sensor is mounted on the robot.
	 * If the direction is left, then the sensor is pointing
	 * towards the left hand side of the robot at a 90 degree
	 * angle from the forward direction. 
	 * @param mountedDirection is the sensor's relative direction
	 */
	@Override
	public void setSensorDirection(Direction mountedDirection) {
		assert mountedDirection != null;
		direction = mountedDirection;
	}

	/**
	 * Returns the amount of energy this sensor uses for 
	 * calculating the distance to an obstacle exactly once.
	 * This amount is a fixed constant for a sensor.
	 * @return the amount of energy used for using the sensor once
	 */
	@Override
	public float getEnergyConsumptionForSensing() {
		return 1;
	}
	
	/**
	 * Finds the cardinal direction of the sensor based on the the direction
	 * in which the robot is facing.
	 * @return the cardinal direction of the sensor relative to the maze
	 */
	private CardinalDirection getSensingDirection(CardinalDirection robotDirection) {
		CardinalDirection sensingDirection = robotDirection;
		switch(robotDirection) {
		case North: {
			switch(direction) {
			case FORWARD: sensingDirection = CardinalDirection.North;
				break;
			case RIGHT: sensingDirection = CardinalDirection.West;
				break;
			case BACKWARD: sensingDirection = CardinalDirection.South;
				break;
			case LEFT: sensingDirection = CardinalDirection.East;
				break;
			}
			break;
		}
		case East: {
			switch(direction) {
			case FORWARD: sensingDirection = CardinalDirection.East;
				break;
			case RIGHT: sensingDirection = CardinalDirection.North;
				break;
			case BACKWARD: sensingDirection = CardinalDirection.West;
				break;
			case LEFT: sensingDirection = CardinalDirection.South;
				break;
			}
			break;
		}
		case South: {
			switch(direction) {
			case FORWARD: sensingDirection = CardinalDirection.South;
				break;
			case RIGHT: sensingDirection = CardinalDirection.East;
				break;
			case BACKWARD: sensingDirection = CardinalDirection.North;
				break;
			case LEFT: sensingDirection = CardinalDirection.West;
				break;
			}
			break;
		}
		case West: {
			switch(direction) {
			case FORWARD: sensingDirection = CardinalDirection.West;
				break;
			case RIGHT: sensingDirection = CardinalDirection.South;
				break;
			case BACKWARD: sensingDirection = CardinalDirection.East;
				break;
			case LEFT: sensingDirection = CardinalDirection.North;
				break;
			}
			break;
		}
		}
		
		return sensingDirection;
	}
	
	/**
	 * P4 method, not pseudocoded/implemented
	 */
	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * P4 method, not pseudocoded/implemented
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
