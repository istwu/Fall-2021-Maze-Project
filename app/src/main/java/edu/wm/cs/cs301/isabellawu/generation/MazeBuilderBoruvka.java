package edu.wm.cs.cs301.isabellawu.generation;

//import java.awt.Point;
import android.graphics.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * MazeBuilderBoruvka generates a maze onto a Floorplan object using Boruvka's algorithm for building minimum spanning trees. 
 * It takes an order from the MazeFactory and builds the maze according to the order's specifications.
 * 
 * The maze generation uses Boruvka's algorithm in that the floorplan starts with all wallboards (edges) up, as well as all cells in the maze 
 * as separate components (vertices). Components are made up of cells in the maze, and are considered connected components of the MST representing the maze.
 * Each wallboard has a randomly generated weight, with no duplicate weights. It goes through each component, tears down the wallboard with the smallest weight, 
 * then merges with the neighboring component, continuing until there is only one component remaining.
 * 
 * @author Isabella Wu
 */
public class MazeBuilderBoruvka extends MazeBuilder {
	
	/**
	 * ArrayList used to keep track of the connected cells of the maze.
	 */
	private ArrayList<ArrayList<Point>> components;
	/**
	 * HashMap used to assign randomized weights to each wallboard.
	 */
	private HashMap<Wallboard, Integer> weights;
	
	/**
	 * Constructor instantiates values from MazeBuilder superclass, as well as an ArrayList to keep track of component ArrayLists 
	 * and a HashMap for mapping wallboards to randomized weights.
	 */
	public MazeBuilderBoruvka() {
		super();
		components = new ArrayList<ArrayList<Point>>();
		weights = new HashMap<>();
		System.out.println("MazeBuilderBoruvka uses Boruvka's algorithm to generate maze.");
	}

	/**
	 * Getter method used for testing. Accesses the floorplan.
	 * 
	 * @return 	the builder's floorplan object
	 */
	public Floorplan getFloorplan() {	// for testing
		return floorplan;
	}
	
	/**
	 * Adds weights from 0 to 4*maze width*maze height to an ArrayList and randomizes their order by shuffling. (4*width*height is the maximum number 
	 * of walls in the maze.) Then assigns each wallboard and its corresponding wallboard from the neighboring cell to a weight from the ArrayList.
	 */
	protected void generateWeights() {
		// loop should only add inner (removable) walls to arraylist
		Wallboard temp = new Wallboard(-1, -1, CardinalDirection.North);
		ArrayList<Integer> random_weights = new ArrayList<>();
		int weight = 0;
		for(int i = 0; i < 4*width*height; i++) {	// w*h*4 - 1 = total # of wallboards in maze (incl. borders)
			random_weights.add(weight);
			weight++;
		}
		Collections.shuffle(random_weights);	// randomize weights
		
		int pointer = 0;
		for(int w = 0; w < width; w++) {
		    for(int h = 0; h < height; h++) {
		    	// add same wall twice from both sides w/ same weight
		    	for(CardinalDirection cd : CardinalDirection.values()) {
		    		temp.setLocationDirection(w, h, cd);
		    		if(floorplan.canTearDown(temp)) {
		    			weights.put(new Wallboard(w, h, cd), random_weights.get(pointer));
						switch (cd) {
							case North: {
								weights.put(new Wallboard(temp.getNeighborX(), temp.getNeighborY(), CardinalDirection.South), 
										random_weights.get(pointer));
								break;
							}
							case South: {
								weights.put(new Wallboard(temp.getNeighborX(), temp.getNeighborY(), CardinalDirection.North), 
										random_weights.get(pointer));
								break;
							}
							case East: {
								weights.put(new Wallboard(temp.getNeighborX(), temp.getNeighborY(), CardinalDirection.West), 
										random_weights.get(pointer));
								break;
							}
							case West: {
								weights.put(new Wallboard(temp.getNeighborX(), temp.getNeighborY(), CardinalDirection.East), 
										random_weights.get(pointer));
								break;
							}
						}
					pointer++;
		    		}
		    	}

		    }
		}
	}
	
	/**
	 * Searches through each point in the component passed and finds the wallboard with the cheapest weight out of all the points. 
	 * The wallboard must not be marked as a border, and the neighboring point must not be a part of the component.
	 * Returns the cheapest wallboard if it exists, null otherwise.
	 * 
	 * @param component		ArrayList of points to search
	 * @return key			Wallboard with cheapest weight
	 */
	private Wallboard findCheapestNeighboringWeight(ArrayList<Point> component) {
		int min_weight = Integer.MAX_VALUE;
		int x = -1;
		int y = -1;
		CardinalDirection dir = null;
		Wallboard temp = new Wallboard(-1, -1, CardinalDirection.North);
		
		for(Point point : component) {
			for(CardinalDirection cd : CardinalDirection.values()) {
				temp.setLocationDirection((int)point.getX(), (int)point.getY(), cd);
				Point neighbor = new Point(temp.getNeighborX(), temp.getNeighborY());
				if(floorplan.canTearDown(temp) && !component.contains(neighbor)) {
					int current_weight = getEdgeWeight(temp);
					if(current_weight < min_weight) {
						x = (int)point.getX();
						y = (int)point.getY();
						dir = cd;
						min_weight = current_weight;
					}
				}
			}	
		}
		
		for(Wallboard key : weights.keySet()) {
			if(key.getX() == x && key.getY() == y && key.getDirection() == dir) {
				return key;
			}
		}
		return null;
	}
	
	/**
	 * Searches the weights HashMap for the wallboard object, and returns the associated weight. If the wallboard is not in the HashMap,
	 * returns the max value integer.
	 * 
	 * @param wallboard 	a Wallboard object
	 * @return 				the weight of 
	 */
	public int getEdgeWeight(Wallboard wallboard) {
		int x = wallboard.getX();
		int y = wallboard.getY();
		CardinalDirection dir = wallboard.getDirection();
		for(Wallboard key : weights.keySet()) {
			if(key.getX() == x && key.getY() == y && key.getDirection() == dir) {
				return weights.get(key);
			}
		}
		return Integer.MAX_VALUE;
	}
	
	
	/**
	 * Generates a path in the floorplan using Boruvka's algorithm. Vertices and edges are analogous to cells and walls. 
	 * First adds each cell in the maze as an individual component, then loops through the list of components, finds their 
	 * cheapest weighted wallboards, and merges with the neighboring components by deleting the wallboards until only one component remains.
	 */
	@Override 
	protected void generatePathways() {
		// generate random weights for each wallboard (no duplicates)
		generateWeights();
		
		// add individual cells as new components
		for(int w = 0; w < width; w++) {
			for(int h = 0; h < height; h++) {
				ArrayList<Point> temp = new ArrayList<>();
				temp.add(new Point(w, h));
				components.add(temp);
			}
		}
		
		// Boruvka - iterate through components, merge until one remains
		while(components.size() > 1) {		
			// get current component
			ArrayList<Point> current_component = components.remove(0);	// list of coords in component
			
			// iterate through cells in component to find wall w/ cheapest weight
			Wallboard key = findCheapestNeighboringWeight(current_component);
			
			if(key != null) {
				if(floorplan.canTearDown(key)) {
					Point neighbor = new Point(key.getNeighborX(), key.getNeighborY());
					ArrayList<Point> to_merge = current_component;
					// search list of components for component w/ neighbor point
					for(ArrayList<Point> c : components) {
						if(c.contains(neighbor)) {
							to_merge = c;
						}
					}
					current_component.addAll(to_merge);
					components.remove(to_merge);
					floorplan.deleteWallboard(key);
				}
				
			}
			components.add(current_component);
		}
	}
	
}