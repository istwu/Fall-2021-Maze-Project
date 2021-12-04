package edu.wm.cs.cs301.isabellawu.gui;

import android.content.Context;

/**
 * The responsibilities of the UnreliableRobot are to perform actions (turning, moving forward, jumping walls),
 * to use unreliable sensors (to detect walls, rooms, and the exit position), and to keep track of its battery 
 * level and the distance traveled.
 * It collaborates with the Controller to move and the WallFollower to receive instructions.
 * 
 * @author Isabella Wu
 */
public class UnreliableRobot extends ReliableRobot {
	
	/**
	 * Default constructor, instantiates instance variables from ReliableRobot superclass.
	 */
	public UnreliableRobot() {
		super();
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
	 * in a straight line of sight, Integer.MAX_VALUE otherwise. If 
	 * the sensor is not operational, instead returns -1.
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 * or the sensor exists but is currently not operational
	 */
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {		
		int dist = Integer.MAX_VALUE;
		switch(direction) {
		case LEFT: {
			assert sensor_left != null;
			try {	// check if sensor is operational, if so deplete battery and return the distance
				dist = sensor_left.distanceToObstacle(activity.getCurrentPosition(), getCurrentDirection(), battery);
				battery[0] -= sensor_left.getEnergyConsumptionForSensing();
				return dist;
			} catch (Exception e) {		// if sensor is not in operation, throw exception
				throw new UnsupportedOperationException(direction + " sensor is not operational.");
			}
		}
		case FORWARD: {
			assert sensor_forward != null;
			try {
				dist = sensor_forward.distanceToObstacle(activity.getCurrentPosition(), getCurrentDirection(), battery);
				battery[0] -= sensor_forward.getEnergyConsumptionForSensing();
				return dist;
			} catch (Exception e) {
				throw new UnsupportedOperationException(direction + " sensor is not operational.");
			}
		}
		case BACKWARD: {
			assert sensor_backward != null;
			try {
				dist = sensor_backward.distanceToObstacle(activity.getCurrentPosition(), getCurrentDirection(), battery);
				battery[0] -= sensor_backward.getEnergyConsumptionForSensing();
				return dist;
			} catch (Exception e) {
				throw new UnsupportedOperationException(direction + " sensor is not operational.");
			}
		}
		case RIGHT: {
			assert sensor_right != null;
			try {
				dist = sensor_right.distanceToObstacle(activity.getCurrentPosition(), getCurrentDirection(), battery);
				battery[0] -= sensor_right.getEnergyConsumptionForSensing();
				return dist;
			} catch (Exception e) {
				throw new UnsupportedOperationException(direction + " sensor is not operational.");
			}
		}
		}
		return dist;
	}

	/**
	 * Method starts a concurrent, independent failure and repair
	 * process that makes the sensor fail and repair itself.
	 * This creates alternating time periods of up time and down time.
	 * Up time: The duration of a time period when the sensor is in 
	 * operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeBetweenFailures.
	 * Down time: The duration of a time period when the sensor is in repair
	 * and not operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeToRepair.
	 * 
	 * @param direction the direction the sensor is mounted on the robot
	 * @param meanTimeBetweenFailures is the mean time in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time in seconds, must be greater than zero
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair) {
		switch(direction) {
		case LEFT: {
			sensor_left.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		}
		case RIGHT: {
			sensor_right.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		}
		case FORWARD: {
			sensor_forward.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		}
		case BACKWARD: {
			sensor_backward.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		}
		}
	}

	/**
	 * This method stops a failure and repair process and
	 * leaves the sensor in an operational state.
	 * 
	 * It is complementary to starting a 
	 * failure and repair process. 
	 * 
	 * Intended use: If called after starting a process, this method
	 * will stop the process as soon as the sensor is operational.
	 * 
	 * If called with no running failure and repair process, 
	 * the method will return an UnsupportedOperationException.
	 * 
	 * This an optional operation. If not implemented, the method
	 * throws an UnsupportedOperationException.
	 * 
	 * @param direction the direction the sensor is mounted on the robot
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) {
		switch(direction) {
		case LEFT: {
			try {
				sensor_left.stopFailureAndRepairProcess();
			} catch (Exception e) {
				throw new UnsupportedOperationException("No running failure and repair process.");
			}
			break;
		}
		case RIGHT: {
			try {
				sensor_right.stopFailureAndRepairProcess();
			} catch (Exception e) {
				throw new UnsupportedOperationException("No running failure and repair process.");
			}			
			break;
		}
		case FORWARD: {
			try {
				sensor_forward.stopFailureAndRepairProcess();
			} catch (Exception e) {
				throw new UnsupportedOperationException("No running failure and repair process.");
			}			
			break;
		}
		case BACKWARD: {
			try {
				sensor_backward.stopFailureAndRepairProcess();
			} catch (Exception e) {
				throw new UnsupportedOperationException("No running failure and repair process.");
			}			
			break;
		}
		}
	}

}
