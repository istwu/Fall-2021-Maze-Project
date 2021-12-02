package edu.wm.cs.cs301.isabellawu.gui;

import edu.wm.cs.cs301.isabellawu.generation.CardinalDirection;

/**
 * The responsibility of the UnreliableSensor is to detect the closest wall in a given direction,
 * and to repair itself in a separate background thread when it fails.
 * It collaborates with the Maze and Floorplan to get the measure the distances toward obstacles.
 * (generally has same responsibilities and collaborators as ReliableSensor)
 * 
 * @author Isabella Wu
 */
public class UnreliableSensor extends ReliableSensor implements Runnable {
	
	private boolean operational;
	private boolean interrupted;
	private Thread thread;
	
	/**
	 * Constructor instantiates variables from superclass, as well as a boolean
	 * to determine whether or not the sensor is operational.
	 */
	public UnreliableSensor() {
		super();
		operational = true;
		interrupted = false;
	}
	
	/**
	 * Returns true if the sensor is operational, false otherwise. Used for testing.
	 * @return sensor operation status
	 */
	protected boolean isOperational() {
		return operational;
	}
	
	/**
	 * Sets the sensor's operation status from outside. Used for testing.
	 * @param o sensor operation status
	 */
	protected void setOperational(boolean o) {
		operational = o;
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
	 * @throws Exception with message 
	 * SensorFailure if the sensor is currently not operational
	 */
	@Override
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply) {
		if(!operational) {
			throw new UnsupportedOperationException("SensorFailure: " + direction);
		}
		return super.distanceToObstacle(currentPosition, currentDirection, powersupply);
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
	 * @param meanTimeBetweenFailures is the mean time in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time in seconds, must be greater than zero
	 */
	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		thread = new Thread(this);
		thread.start();
		interrupted = false;
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
	 * @throws UnsupportedOperationException if there is no running 
	 * failure and repair process
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		if(thread.isInterrupted()) {
			throw new UnsupportedOperationException("No running failure and repair process.");
		}
		interrupted = true;		
	}
	
	/**
	 * Thread run method that is called to automate the intervals with which 
	 * the sensor fails and repairs. 
	 */
	@Override
	public void run() {
		while(!interrupted) {
			try {
				operational = true;
				Thread.sleep(4000);
				operational = false;
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				thread.interrupt();
				operational = true;
			}
		}
		thread.interrupt();
		operational = true;
	}
	
}
