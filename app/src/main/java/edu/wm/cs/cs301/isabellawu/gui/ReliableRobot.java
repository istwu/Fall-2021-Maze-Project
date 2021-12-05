package edu.wm.cs.cs301.isabellawu.gui;

import edu.wm.cs.cs301.isabellawu.generation.CardinalDirection;
import edu.wm.cs.cs301.isabellawu.gui.Constants.UserInput;

/**
 * The responsibilities of the ReliableRobot are to perform actions (turning, moving forward, jumping walls), 
 * to use sensors (to detect walls, rooms, and the exit position), and to keep track of its battery level and 
 * the distance traveled. It also records whether or not it has stopped/ceased to function.
 * It collaborates with the Controller to move and the RobotDriver to receive instructions.
 * 
 * @author Isabella Wu
 */
public class ReliableRobot implements Robot {
	
//	protected Controller controller;
	protected PlayAnimationActivity activity;
	protected DistanceSensor sensor_left;
	protected DistanceSensor sensor_right;
	protected DistanceSensor sensor_forward;
	protected DistanceSensor sensor_backward;
	protected float[] battery;
	protected int odometer;
	protected boolean crashed;
	
	/**
	 * Default constructor, instantiates sensors and counters used to track battery 
	 * level and  distance traveled. Also instantiates a boolean to record whether 
	 * or not the robot has crashed.
	 */
	public ReliableRobot() {
		battery = new float[]{3500};
		odometer = 0;
		crashed = false;
	}

	/**
	 * Provides the robot with a reference to the controller to cooperate with.
	 * The robot memorizes the controller such that this method is most likely called only once
	 * and for initialization purposes. The controller serves as the main source of information
	 * for the robot about the current position, the presence of walls, the reaching of an exit.
	 * The controller is assumed to be in the playing state.
	 * @param a is the communication partner for robot
	 */
//	@Override
//	public void setController(Controller controller) {
//		assert controller != null;
//		assert controller.currentState.equals(controller.states[2]);
//		assert controller.getMazeConfiguration() != null;
//
//		this.controller = controller;
//	}

	public void setActivity(PlayAnimationActivity a) {
//		assert controller != null;
//		assert controller.currentState.equals(controller.states[2]);
		assert a.getMazeConfiguration() != null;

		activity = a;
	}
	
	/**
	 * Adds a distance sensor to the robot such that it measures in the given direction.
	 * This method is used when a robot is initially configured to get ready for operation.
	 * The point of view is that one mounts a sensor on the robot such that the robot
	 * can measure distances to obstacles or walls in that particular direction.
	 * For example, if one mounts a sensor in the forward direction, the robot can tell
	 * with the distance to a wall for its current forward direction, more technically,
	 * a method call distanceToObstacle(FORWARD) will return a corresponding distance.
	 * So a robot with a left and forward sensor will internally have 2 DistanceSensor
	 * objects at its disposal to calculate distances, one for the forward, one for the
	 * left direction.
	 * A robot can have at most four sensors in total, and at most one for any direction.
	 * If a robot already has a sensor for the given mounted direction, adding another
	 * sensor will replace/overwrite the current one for that direction with the new one.
	 * @param sensor is the distance sensor to be added
	 * @param mountedDirection is the direction that it points to relative to the robot's forward direction
	 */
	@Override
	public void addDistanceSensor(DistanceSensor sensor, Direction mountedDirection) {
		switch (mountedDirection) {
		case LEFT: sensor_left = sensor;
			break;
		case RIGHT: sensor_right = sensor;
			break;
		case FORWARD: sensor_forward = sensor;
			break;
		case BACKWARD: sensor_backward = sensor;
			break;
		}
	}

	/**
	 * Provides the current position as (x,y) coordinates for 
	 * the maze as an array of length 2 with [x,y].
	 * @return array of length 2, x = array[0], y = array[1]
	 * and ({@code 0 <= x < width, 0 <= y < height}) of the maze
	 * @throws Exception if position is outside of the maze
	 */
	@Override
	public int[] getCurrentPosition() throws Exception {
		int[] currentPosition = activity.getCurrentPosition();
		if(currentPosition[0] < 0 || currentPosition[0] >= activity.getMazeConfiguration().getWidth() ||
				currentPosition[1] < 0 || currentPosition[1] >= activity.getMazeConfiguration().getHeight()) {
			throw new Exception("Current position outside of maze.");
		}
		else {
			return currentPosition;
		}
	}

	/**
	 * Provides the robot's current direction.
	 * @return cardinal direction is the robot's current direction in absolute terms
	 */	
	@Override
	public CardinalDirection getCurrentDirection() {
		return activity.getCurrentDirection();
	}

	/**
	 * Returns the current battery level.
	 * The robot has a given battery level (energy level) 
	 * that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call 
	 * for sensor distance2Obstacle may use less energy than a move forward operation.
	 * If battery {@code level <= 0} then robot stops to function and hasStopped() is true.
	 * @return current battery level, {@code level > 0} if operational. 
	 */
	@Override
	public float getBatteryLevel() {
		return battery[0];
	}

	/**
	 * Sets the current battery level.
	 * The robot has a given battery level (energy level) 
	 * that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call 
	 * for distance2Obstacle may use less energy than a move forward operation.
	 * If battery {@code level <= 0} then robot stops to function and hasStopped() is true.
	 * @param level is the current battery level
	 */
	@Override
	public void setBatteryLevel(float level) {
		assert level >= 0;
		battery[0] = level;
	}
	
	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption. 
	 * @return energy for a full rotation
	 */
	@Override
	public float getEnergyForFullRotation() {
		return 12;
	}

	/**
	 * Gives the energy consumption for moving forward for a distance of 1 step.
	 * For simplicity, we assume that this equals the energy necessary 
	 * to move 1 step and that for moving a distance of n steps 
	 * takes n times the energy for a single step.
	 * @return energy for a single step forward
	 */
	@Override
	public float getEnergyForStepForward() {
		return 6;
	}

	/** 
	 * Gets the distance traveled by the robot.
	 * The robot has an odometer that calculates the distance the robot has moved.
	 * Whenever the robot moves forward, the distance 
	 * that it moves is added to the odometer counter.
	 * The odometer reading gives the path length if its setting is 0 at the start of the game.
	 * The counter can be reset to 0 with resetOdomoter().
	 * @return the distance traveled measured in single-cell steps forward
	 */
	@Override
	public int getOdometerReading() {
		return odometer;
	}

	/** 
     * Resets the odometer counter to zero.
     * The robot has an odometer that calculates the distance the robot has moved.
     * Whenever the robot moves forward, the distance 
     * that it moves is added to the odometer counter.
     * The odometer reading gives the path length if its setting is 0 at the start of the game.
     */
	@Override
	public void resetOdometer() {
		odometer = 0;
	}

	/**
	 * Turn robot on the spot for amount of degrees. 
	 * If robot runs out of energy, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * @param turn is the direction to turn and relative to current forward direction. 
	 */
	@Override
	public void rotate(Turn turn) {
		if(battery[0] < 3) {
			crashed = true;		// forces robot to stop
		}
		
		if(battery[0] >= 3 && !hasStopped()) {
			switch(turn) {
			case LEFT: {
				battery[0] -= getEnergyForFullRotation()/4;
				activity.keyDown(UserInput.LEFT, 0);
				break;
			}
			case RIGHT: {
				battery[0] -= getEnergyForFullRotation()/4;
				activity.keyDown(UserInput.RIGHT, 0);
				break;
			}
			case AROUND: {
				if(battery[0] >= getEnergyForFullRotation()/2) {
					battery[0] -= getEnergyForFullRotation()/2;
					activity.keyDown(UserInput.LEFT, 0);
					activity.keyDown(UserInput.LEFT, 0);
				}
				break;
			}
			}
		}
	}

	/**
	 * Moves robot forward a given number of steps. A step matches a single cell.
	 * If the robot runs out of energy somewhere on its way, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * If the robot hits an obstacle like a wall, it remains at the position in front 
	 * of the obstacle and also hasStopped() == true as this is not supposed to happen.
	 * This is also helpful to recognize if the robot implementation and the actual maze
	 * do not share a consistent view on where walls are and where not.
	 * @param distance is the number of cells to move in the robot's current forward direction
	 */
	@Override
	public void move(int distance) {
		assert distance > 0;
		
		for(int i = 0; i < distance; i++) {
			// if robot tries to move into a wall w/o jumping, it crashes
			if(activity.getMazeConfiguration().hasWall(activity.getCurrentPosition()[0], activity.getCurrentPosition()[1], getCurrentDirection())) {
				crashed = true;
			}
			if(battery[0] < getEnergyForStepForward()) {
				crashed = true;
			}
			if(battery[0] >= 6 && !hasStopped()) {
				battery[0] -= getEnergyForStepForward();
				activity.keyDown(UserInput.UP, 0);
				odometer += 1;
			}
		}
	}

	/**
	 * Makes robot move in a forward direction even if there is a wall
	 * in front of it. In this sense, the robot jumps over the wall
	 * if necessary. The distance is always 1 step and the direction
	 * is always forward.
	 * If the robot runs out of energy somewhere on its way, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level.
	 * If the robot tries to jump over an exterior wall and
	 * would land outside of the maze that way,  
	 * it remains at its current location and direction,
	 * hasStopped() == true as this is not supposed to happen.
	 */
	@Override
	public void jump() {
		// check if the wall to jump is an exterior wall
		if(activity.getCurrentPosition()[0] == 0 && getCurrentDirection() == CardinalDirection.West) {
			crashed = true;
		}
		else if(activity.getCurrentPosition()[0] == activity.getMazeConfiguration().getWidth()-1 && getCurrentDirection() == CardinalDirection.East) {
			crashed = true;
		}
		else if(activity.getCurrentPosition()[1] == 0 && getCurrentDirection() == CardinalDirection.North) {
			crashed = true;
		}
		else if(activity.getCurrentPosition()[1] == activity.getMazeConfiguration().getHeight()-1 && getCurrentDirection() == CardinalDirection.South) {
			crashed = true;
		}
		
		if(battery[0] >= 40 && !hasStopped()) {
			if(activity.getMazeConfiguration().hasWall(activity.getCurrentPosition()[0], activity.getCurrentPosition()[1], getCurrentDirection())) {
				activity.keyDown(UserInput.JUMP, 0);
			}
			else {
				activity.keyDown(UserInput.UP, 0);
			}
			battery[0] -= 40;
			odometer += 1;
		}
	}

	/**
	 * Tells if the current position is right at the exit but still inside the maze. 
	 * The exit can be in any direction. It is not guaranteed that 
	 * the robot is facing the exit in a forward direction.
	 * @return true if robot is at the exit, false otherwise
	 */
	@Override
	public boolean isAtExit() {
		if(activity.getCurrentPosition()[0] == activity.getMazeConfiguration().getExitPosition()[0] &&
				activity.getCurrentPosition()[1] == activity.getMazeConfiguration().getExitPosition()[1]) {
			return true;
		}
		return false;
	}

	/**
	 * Tells if current position is inside a room. 
	 * @return true if robot is inside a room, false otherwise
	 */	
	@Override
	public boolean isInsideRoom() {
		if(activity.getMazeConfiguration().isInRoom(activity.getCurrentPosition()[0], activity.getCurrentPosition()[1])) {
			return true;
		}
		return false;
	}

	/**
	 * Tells if the robot has stopped for reasons like lack of energy, 
	 * hitting an obstacle, etc.
	 * Once a robot is has stopped, it does not rotate or 
	 * move anymore.
	 * @return true if the robot has stopped, false otherwise
	 */
	@Override
	public boolean hasStopped() {
		if(battery[0] <= 0) {
			return true;
		}
		if(crashed) {
			return true;
		}	
		return false;
	}

	/**
	 * Tells the distance to an obstacle (a wall) 
	 * in the given direction.
	 * The direction is relative to the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle, 
	 * e.g. 0 if the current cell has a wallboard in this direction, 
	 * 1 if it is one step forward before directly facing a wallboard,
	 * Integer.MaxValue if one looks through the exit into eternity.
	 * The robot uses its internal DistanceSensor objects for this and
	 * delegates the computation to the DistanceSensor which need
	 * to be installed by calling the addDistanceSensor() when configuring
	 * the robot.
	 * @param direction specifies the direction of interest
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise
	 */
	@Override
	public int distanceToObstacle(Direction direction) {		
		int dist = Integer.MAX_VALUE;
		switch(direction) {
		case LEFT: {
			assert sensor_left != null;
			try {
				battery[0] -= sensor_left.getEnergyConsumptionForSensing();
				dist = sensor_left.distanceToObstacle(activity.getCurrentPosition(), getCurrentDirection(), battery);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case RIGHT: {
			assert sensor_right != null;
			try {
				battery[0] -= sensor_right.getEnergyConsumptionForSensing();
				dist = sensor_right.distanceToObstacle(activity.getCurrentPosition(), getCurrentDirection(), battery);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case FORWARD: {
			assert sensor_forward != null;
			try {
				battery[0] -= sensor_forward.getEnergyConsumptionForSensing();
				dist = sensor_forward.distanceToObstacle(activity.getCurrentPosition(), getCurrentDirection(), battery);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case BACKWARD: {
			assert sensor_backward != null;
			try {
				battery[0] -= sensor_backward.getEnergyConsumptionForSensing();
				dist = sensor_backward.distanceToObstacle(activity.getCurrentPosition(), getCurrentDirection(), battery);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		}
		
		return dist;
	}

	/**
	 * Tells if a sensor can identify the exit in the given direction relative to 
	 * the robot's current forward direction from the current position.
	 * It is a convenience method is based on the distanceToObstacle() method and transforms
	 * its result into a boolean indicator.
	 * @param direction is the direction of the sensor
	 * @return true if the exit of the maze is visible in a straight line of sight
	 */
	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) {
		if(distanceToObstacle(direction) == Integer.MAX_VALUE) {
			return true;
		}
		return false;
	}
	
	/**
	 * P4 method, not pseudocoded/implemented
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * P4 method, not pseudocoded/implemented
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
