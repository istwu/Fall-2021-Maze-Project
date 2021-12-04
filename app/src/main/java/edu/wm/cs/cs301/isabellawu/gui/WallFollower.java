package edu.wm.cs.cs301.isabellawu.gui;

import edu.wm.cs.cs301.isabellawu.generation.CardinalDirection;
import edu.wm.cs.cs301.isabellawu.gui.Robot.Direction;
import edu.wm.cs.cs301.isabellawu.gui.Robot.Turn;

/**
 * The responsibility of the WallFollower is to solve the maze by following the wall on the robot's left until it reaches the exit. 
 * If there is no wall on the robot's left side, it rotates left and moves forward. If there is a wall, it checks if there is
 * a wall in front. If there is no wall in front, it moves forward. Otherwise, it checks the right wall. If there is no wall to the
 * right, it rotates right and moves forward. If there is, it turns around and moves one step back the way it came.
 * If the robot is in a hallway, it continues along the left side wall.
 * If the robot is in a room, it will find and follow the closest wall on its left until it reaches the opposite opening.
 * If the robot is at or in range of the exit position, it turns to face the outside and moves forward out of the maze, 
 * finishing the game.
 * If one sensor fails, it rotates the robot so a working sensor is in position, uses that sensor to measure the distance, 
 * then rotates back to the previous orientation.
 * If all sensors fail, it waits for a sensor to be repaired, then uses that sensor as described above.
 * The WallFollower collaborates with the UnreliableRobot to give instructions.
 * 
 * @author Isabella Wu
 */
public class WallFollower extends Wizard implements RobotDriver {

	/**
	 * Default constructor, instantiates counters used to track energy and cells traveled.
	 */
	public WallFollower() {
		super();
	}

	/**
	 * Drives the robot one step towards the exit using
	 * the wall-following solution strategy and given the exists and 
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
		
		// check if there is a wall to the left
		int left_dist = -1;
		try {					// check left sensor
			left_dist = robot.distanceToObstacle(Direction.LEFT);
			energyConsumption += 1;
		} catch(Exception e) {	// if left sensor is not operational, find replacement
			left_dist = useActiveSensor(Direction.LEFT);
		}
		if(left_dist != 0) {		// if there is no wall to the left, rotate left, move fwd
			robot.rotate(Turn.LEFT);
			energyConsumption += 3;
			robot.move(1);
			energyConsumption += 6;
			pathLength += 1;
			return true;
		}
		else {				// if there is a wall to the left
			// check if there is a wall in front
			int front_dist = -1;
			try {
				front_dist = robot.distanceToObstacle(Direction.FORWARD);
				energyConsumption += 1;
			} catch(Exception e) {	// if front sensor is not operational, find replacement
				front_dist = useActiveSensor(Direction.FORWARD);
			}
			if(front_dist != 0) {	// if there is no wall in front, move fwd
				robot.move(1);
				energyConsumption += 6;
				pathLength += 1;
				return true;
			}
			else {
				// check if there is a wall to the right
				int right_dist = -1;
				try {
					right_dist = robot.distanceToObstacle(Direction.RIGHT);
					energyConsumption += 1;
				}
				catch(Exception e) {	// if right sensor is not operational, find replamcement
					right_dist = useActiveSensor(Direction.RIGHT);
				}
				if(right_dist != 0) {	// if there is no wall to the right, rotate right, move fwd
					robot.rotate(Turn.RIGHT);
					energyConsumption += 3;
					robot.move(1);
					energyConsumption += 6;
					pathLength += 1;
					return true;
				}
				else {		// if there are walls on all sides of the robot, turn around and move back
					robot.rotate(Turn.AROUND);
					energyConsumption += 6;
					robot.move(1);
					energyConsumption += 6;
					pathLength += 1;
					return true;
				}
			}
		}
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
	 * Given a desired direction in which to sense obstacles, finds an operational sensor,
	 * rotates and senses in that direction, reverses the rotation, and returns the
	 * distance found. If no sensors are operational, waits for 2 seconds for the initial
	 * sensor to come online, then returns the distance from that sensor.
	 * @param desired_direction the direction in which to sense for obstacles
	 */
	private int useActiveSensor(Direction desired_direction) {
		// assumes that sensor in desired direction is not operational
		int dist = -1;
		if(desired_direction == Direction.LEFT) {
			try {
				// check first nearest sensor
				// if functional, rotate, use sensor, and rotate back
				robot.distanceToObstacle(Direction.FORWARD);	
				energyConsumption += 1;
				robot.rotate(Turn.LEFT);
				energyConsumption += 3;
				dist = robot.distanceToObstacle(Direction.FORWARD);
				energyConsumption += 1;
				robot.rotate(Turn.RIGHT);
				energyConsumption += 3;
				return dist;
			} catch(Exception e) {	// first nearest sensor not functional
				try {
					// check second nearest sensor
					// if functional, rotate, use sensor, and rotate back
					robot.distanceToObstacle(Direction.BACKWARD);
					energyConsumption += 1;
					robot.rotate(Turn.RIGHT);
					energyConsumption += 3;
					dist = robot.distanceToObstacle(Direction.BACKWARD);
					energyConsumption += 1;
					robot.rotate(Turn.LEFT);
					energyConsumption += 3;
					return dist;
				} catch(Exception f) {	// second nearest sensor not functional
					try {
						// check farthest sensor
						// if functional, rotate, use sensor, and rotate back
						robot.distanceToObstacle(Direction.RIGHT);
						energyConsumption += 1;
						robot.rotate(Turn.AROUND);
						energyConsumption += 6;
						dist = robot.distanceToObstacle(Direction.RIGHT);
						energyConsumption += 1;
						robot.rotate(Turn.AROUND);
						energyConsumption += 6;
						return dist;
					} catch(Exception g) {
						try {	// wait for sensor in desired direction to come back online, then use
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						dist = robot.distanceToObstacle(desired_direction);
						energyConsumption += 1;
						return dist;
					}
				}
			}
		}
		
		else if(desired_direction == Direction.FORWARD) {
			try {
				robot.distanceToObstacle(Direction.RIGHT);
				energyConsumption += 1;
				robot.rotate(Turn.LEFT);
				energyConsumption += 3;
				dist = robot.distanceToObstacle(Direction.RIGHT);
				energyConsumption += 1;
				robot.rotate(Turn.RIGHT);
				energyConsumption += 3;
				return dist;
			} catch(Exception e) {
				try {
					robot.distanceToObstacle(Direction.LEFT);
					energyConsumption += 1;
					robot.rotate(Turn.RIGHT);
					energyConsumption += 3;
					dist = robot.distanceToObstacle(Direction.LEFT);
					energyConsumption += 1;
					robot.rotate(Turn.LEFT);
					energyConsumption += 3;
					return dist;
				} catch(Exception f) {
					try {
						robot.distanceToObstacle(Direction.BACKWARD);
						energyConsumption += 1;
						robot.rotate(Turn.AROUND);
						energyConsumption += 6;
						dist = robot.distanceToObstacle(Direction.BACKWARD);
						energyConsumption += 1;
						robot.rotate(Turn.AROUND);
						energyConsumption += 6;
						return dist;
					} catch(Exception g) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						dist = robot.distanceToObstacle(desired_direction);
						energyConsumption += 1;
						return dist;
					}
				}
			}
		}
		
		else if(desired_direction == Direction.RIGHT) {
			try {
				robot.distanceToObstacle(Direction.BACKWARD);
				energyConsumption += 1;
				robot.rotate(Turn.LEFT);
				energyConsumption += 3;
				dist = robot.distanceToObstacle(Direction.BACKWARD);
				energyConsumption += 1;
				robot.rotate(Turn.RIGHT);
				energyConsumption += 3;
				return dist;
			} catch(Exception e) {
				try {
					robot.distanceToObstacle(Direction.FORWARD);
					energyConsumption += 1;
					robot.rotate(Turn.RIGHT);
					energyConsumption += 3;
					dist = robot.distanceToObstacle(Direction.FORWARD);
					energyConsumption += 1;
					robot.rotate(Turn.LEFT);
					energyConsumption += 3;
					return dist;
				} catch(Exception f) {
					try {
						robot.distanceToObstacle(Direction.LEFT);
						energyConsumption += 1;
						robot.rotate(Turn.AROUND);
						energyConsumption += 6;
						dist = robot.distanceToObstacle(Direction.LEFT);
						energyConsumption += 1;
						robot.rotate(Turn.AROUND);
						energyConsumption += 6;
						return dist;
					} catch(Exception g) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						dist = robot.distanceToObstacle(desired_direction);
						energyConsumption += 1;
						return dist;
					}
				}
			}
		}
		
		return dist;
	}
	
}
