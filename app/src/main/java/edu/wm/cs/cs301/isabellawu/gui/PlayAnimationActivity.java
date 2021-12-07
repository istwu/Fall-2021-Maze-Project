package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;

import edu.wm.cs.cs301.isabellawu.R;
import edu.wm.cs.cs301.isabellawu.generation.CardinalDirection;
import edu.wm.cs.cs301.isabellawu.generation.Distance;
import edu.wm.cs.cs301.isabellawu.generation.Floorplan;
import edu.wm.cs.cs301.isabellawu.generation.Maze;
import edu.wm.cs.cs301.isabellawu.generation.Order;

/**
 * @author Isabella Wu
 */
public class PlayAnimationActivity extends AppCompatActivity {

    private int config;

    private int path;
    private int shortest_path;
    private int energy_used;
    private int losing;
    private int speed;
    private boolean gameIsRunning;
    private boolean paused;
    private boolean interrupted;
    private ProgressBar energyBar;

    private Thread animationThread;
    private FirstPersonView firstPersonView;
    private Map mapView;
    private MazePanel panel;
    private Maze mazeConfig;
    private UnreliableRobot robot;
    private Wizard driver;
    private ArrayList<int[]> visited;
    private HashMap<Robot.Direction, ImageView> sensorMap;
    private Thread[] sensorThreads;

    private boolean started;
    private boolean showMaze;           // toggle switch to show overall maze on screen
    private boolean showSolution;       // toggle switch to show solution in overall maze on screen
    private boolean mapMode; // true: display map of maze, false: do not display map of maze
    // mapMode is toggled by user keyboard input, causes a call to drawMap during play mode

    // current position and direction with regard to MazeConfiguration
    int px, py ; // current position on maze grid (x,y)
    int dx, dy;  // current direction

    int angle; // current viewing angle, east == 0 degrees
    int walkStep; // counter for intermediate steps within a single step forward or backward
    Floorplan seenCells; // a matrix with cells to memorize which cells are visible from the current point of view
    // the FirstPersonView obtains this information and the Map uses it for highlighting currently visible walls on the map
    private CompassRose cr; // compass rose to show current direction

    private static final String TAG = "PlayAnimationActivity";

    /**
     * Displays the maze in the center of the screen.
     * Instantiates one ToggleButton to turn on and off the map,
     * the maze solution, and the maze walls, creates a SeekBar to
     * zoom in and out of the maze, and displays a ProgressBar to
     * show the robot's remaining energy. Also instantiates a
     * button to pause/play the animation, and a SeekBar to
     * adjust the animation speed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);

        if(AMazeActivity.musicPlayer == null) {
            AMazeActivity.musicPlayer = MediaPlayer.create(PlayAnimationActivity.this, R.raw.kahoot_countdown);
            AMazeActivity.musicPlayer.start();
            AMazeActivity.musicPlayer.setLooping(true);
        }

        Bundle extras = getIntent().getExtras();
        config = extras.getInt("config");

        ToggleButton toggleMap = findViewById(R.id.toggleMapButton_auto);
        toggleMap.setChecked(true);
        toggleMap.setOnClickListener(view -> {
            if(toggleMap.isChecked()) {
                keyDown(Constants.UserInput.TOGGLELOCALMAP, 0);
                keyDown(Constants.UserInput.TOGGLESOLUTION, 0);
                keyDown(Constants.UserInput.TOGGLEFULLMAP, 0);
                Log.v(TAG, "Map on");
            }
            else {
                keyDown(Constants.UserInput.TOGGLELOCALMAP, 0);
                keyDown(Constants.UserInput.TOGGLESOLUTION, 0);
                keyDown(Constants.UserInput.TOGGLEFULLMAP, 0);
                Log.v(TAG, "Map off");
            }
        });

        Button zoomOutButton = findViewById(R.id.zoomOutButton_auto);
        zoomOutButton.setOnClickListener(view -> {
            keyDown(Constants.UserInput.ZOOMOUT, 0);
            keyDown(Constants.UserInput.ZOOMOUT, 0);
            keyDown(Constants.UserInput.ZOOMOUT, 0);
            Log.v(TAG, "Zooming out");
        });
        Button zoomInButton = findViewById(R.id.zoomInButton_auto);
        zoomInButton.setOnClickListener(view -> {
            keyDown(Constants.UserInput.ZOOMIN, 0);
            keyDown(Constants.UserInput.ZOOMIN, 0);
            keyDown(Constants.UserInput.ZOOMIN, 0);
            Log.v(TAG, "Zooming in");
        });

        // change the color of these depending on sensor status
        ImageView sensor_forward = findViewById(R.id.sensor_forward);
        ImageView sensor_left = findViewById(R.id.sensor_left);
        ImageView sensor_right = findViewById(R.id.sensor_right);
        ImageView sensor_backward = findViewById(R.id.sensor_backward);
        sensorMap = new HashMap<>();
        sensorMap.put(Robot.Direction.FORWARD, sensor_forward);
        sensorMap.put(Robot.Direction.LEFT, sensor_left);
        sensorMap.put(Robot.Direction.RIGHT, sensor_right);
        sensorMap.put(Robot.Direction.BACKWARD, sensor_backward);

        // change this based on remaining energy
        energy_used = 0;
        energyBar = findViewById(R.id.energyBar);
        energyBar.setProgress(3500);

        paused = false;
        ImageButton pauseplay = findViewById(R.id.pause_play);
        pauseplay.setOnClickListener(view -> {
            if(!paused) {
                paused = true;
                interrupted = true;
                pauseplay.setImageResource(R.drawable.mr_media_play_light);
                animationThread.interrupt();
                Log.v(TAG, "Pausing animation");
            }
            else {
                paused = false;
                interrupted = false;
                pauseplay.setImageResource(R.drawable.mr_media_pause_light);
                try {
                    drive2Exit();
                } catch (Exception e) {
                    if(robot.noEnergy()) {
                        losing = 1;
                    }
                    else if(robot.crashed()) {
                        losing = 0;
                    }
                    else if(robot.jumpedBorder()) {
                        losing = 2;
                    }
                    go2losing();
                }
                Log.v(TAG, "Playing animation");
            }
        });

        SeekBar animationSpeed = findViewById(R.id.speedSeekBar);
        speed = 5;
        animationSpeed.setProgress(speed);
        animationSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Sets the speed variable to the value from the SeekBar.
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speed = i;
            }

            /**
             * Sends a Logcat message to inform user that the SeekBar is
             * receiving their input.
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.v(TAG, "Changing speed");
            }

            /**
             * Sends a Logcat message to inform user that the SeekBar stopped
             * receiving their input.
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(TAG, "Speed set to " + speed);
            }
        });

        gameIsRunning = true;
        mazeConfig = GeneratingActivity.maze;
        panel = findViewById(R.id.mazePanel_auto);
        started = false;
        path = 0;
        shortest_path = mazeConfig.getMazedists().getDistanceValue(mazeConfig.getStartingPosition()[0],mazeConfig.getStartingPosition()[1]);
        // instantiate driver and robot/sensors
        robot = new UnreliableRobot();
        robot.setActivity(this);
        robot.setBatteryLevel(3500);
        robot.resetOdometer();
        switch(extras.getInt("driver")) {    // 2 = wizard, 3 = wallfollower
            case 2: {
                driver = new Wizard();
                break;
            }
            case 3: {
                driver = new WallFollower();
                break;
            }
        }
        driver.setMaze(mazeConfig);
        driver.setRobot(robot);

//        Premium has 4 reliable sensors, mediocre has reliable front & back sensors,
//        unreliable left and right sensors, Soso has reliable left & right sensors, unreliable front
//        and back sensors, Shaky has 4 unreliable sensors.
        // 0 = unreliable
        // 1 = reliable
        // string order: front left right back
        switch(config) {    // 1 = premium, 2 = mediocre, 3 = soso, 4 = shaky
            case 1: {
                setSensors(robot, "1111");
                break;
            }
            case 2: {
                setSensors(robot, "1001");
                break;
            }
            case 3: {
                setSensors(robot, "0110");
                break;
            }
            case 4: {
                setSensors(robot, "0000");
                break;
            }
        }

        start(panel);

    }

    /**
     * Passes the seed, skill, perfect, and generation variables back
     * to AMazeActivity to be saved, then closes the activity, returning
     * the user back to the title screen.
     */
    @Override
    public void onBackPressed() {
        Log.v(TAG, "Returning to title screen");
        AMazeActivity.musicPlayer.release();
        AMazeActivity.musicPlayer = null;
        gameIsRunning = false;
        for(Robot.Direction d : Robot.Direction.values()) {
            try {
                robot.stopFailureAndRepairProcess(d);
            } catch (Exception e0) {

            }
        }
        if(animationThread != null) {
            animationThread.interrupt();
        }
        if(sensorThreads != null) {
            for (Thread sensorThread : sensorThreads) {
                if (sensorThread != null) {
                    sensorThread.interrupt();
                }
            }
        }
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        this.finish();
    }

    // WINNING/LOSING METHODS

    /**
     * Takes the user to the winning screen, passing in the user's
     * path length and the solution path length through an intent.
     */
    public void go2winning() {
        Log.v(TAG, "Moving to winning screen");
        if(AMazeActivity.musicPlayer != null) {
            AMazeActivity.musicPlayer.release();
            AMazeActivity.musicPlayer = null;
        }
        gameIsRunning = false;
        for(Robot.Direction d : Robot.Direction.values()) {
            try {
                robot.stopFailureAndRepairProcess(d);
            } catch (Exception e0) {

            }
        }
        if(sensorThreads != null) {
            for (Thread sensorThread : sensorThreads) {
                if(sensorThread != null) {
                    sensorThread.interrupt();
                }
            }
        }
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("path", path);
        intent.putExtra("shortest path", shortest_path);
        intent.putExtra("energy used", energy_used);
        startActivity(intent);
    }

    /**
     * Takes the user to the losing screen, passing in the user's
     * path length and the solution path length through an intent.
     */
    public void go2losing() {
        Log.v(TAG, "Moving to losing screen");
        if(AMazeActivity.musicPlayer != null) {
            AMazeActivity.musicPlayer.release();
            AMazeActivity.musicPlayer = null;
        }
        gameIsRunning = false;
        for(Robot.Direction d : Robot.Direction.values()) {
            try {
                robot.stopFailureAndRepairProcess(d);
            } catch (Exception e0) {

            }
        }
        if(sensorThreads != null) {
            for (Thread sensorThread : sensorThreads) {
                if(sensorThread != null) {
                    sensorThread.interrupt();
                }
            }
        }

        // need to pass in steps, energy, reason for loss
        Intent intent = new Intent(this, LosingActivity.class);
        intent.putExtra("path", path);
        intent.putExtra("shortest path", shortest_path);
        intent.putExtra("energy used", energy_used);
        // 0 = crashed into wall
        // 1 = ran out of energy
        // 2 = jumped over border wall
        // 3 = stuck in loop
        intent.putExtra("losing", losing);
        startActivity(intent);
    }

    // STATEPLAYING METHODS

    public void setMazeConfiguration(Maze config) {
        mazeConfig = config;
    }

    /**
     * Sets the four directional sensors of the robot based on the given
     * configuration.
     * @param robot the robot to which the method attaches the sensors
     * @param r the string of 0s and 1s representing the configuration of
     * the robot's sensors.
     */
    public void setSensors(Robot robot, String r) {
        sensorThreads = new Thread[4];
        for(int i = 0; i < r.length(); i++) {
            Robot.Direction dir = null;
            switch(i) {
                case 0: {
                    dir = Robot.Direction.FORWARD;
                    break;
                }
                case 1: {
                    dir = Robot.Direction.LEFT;
                    break;
                }
                case 2: {
                    dir = Robot.Direction.RIGHT;
                    break;
                }
                case 3: {
                    dir = Robot.Direction.BACKWARD;
                    break;
                }
            }

            if(r.charAt(i) == '0') {
                UnreliableSensor sensor = new UnreliableSensor();
                sensor.setMaze(mazeConfig);
                sensor.setSensorDirection(dir);
                robot.addDistanceSensor(sensor, dir);
                sensor.startFailureAndRepairProcess(4, 2);
                try {
                    Thread.sleep(1333);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                UnreliableSensor finalSensor = sensor;
                Robot.Direction finalDir = dir;
                sensorThreads[i] = new Thread(() -> {
                    while (gameIsRunning) {
                        if(finalSensor.isOperational()) {
                            sensorMap.get(finalDir).setColorFilter(Color.parseColor("#00A300")); // green
                        }
                        else {
                            sensorMap.get(finalDir).setColorFilter(Color.parseColor("#D10000")); // red
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                sensorThreads[i].start();

            }
            else if(r.charAt(i) == '1') {
                ReliableSensor sensor = new ReliableSensor();
                sensor.setMaze(mazeConfig);
                sensor.setSensorDirection(dir);
                robot.addDistanceSensor(sensor, dir);
                sensorMap.get(dir).setColorFilter(Color.parseColor("#00A300")); // green
            }
        }
    }

    /**
     * Start the actual game play by showing the playing screen.
     * If the panel is null, all drawing operations are skipped.
     * This mode of operation is useful for testing purposes,
     * i.e., a dryrun of the game without the graphics part.
     * @param panel is part of the UI and visible on the screen, needed for drawing
     */
    public void start(MazePanel panel) {
        // starting the game will have a delay before robot starts...
        // ...solving the maze b/c instantiating sensors has a delay (1.3s)

        started = true;
        // keep the reference to the panel for drawing
        this.panel = panel;
        //
        // adjust internal state of maze model
        // visibility settings
        showMaze = true;
        showSolution = true;
        mapMode = true;
        // init data structure for visible walls
        seenCells = new Floorplan(mazeConfig.getWidth()+1,mazeConfig.getHeight()+1) ;
        // set the current position and direction consistently with the viewing direction
        setPositionDirectionViewingDirection();
        walkStep = 0; // counts incremental steps during move/rotate operation

        // configure compass rose
        cr = new CompassRose();
        cr.setPositionAndSize(Constants.VIEW_WIDTH/2,
                (int)(0.1*Constants.VIEW_HEIGHT),80);   // original size: 35

        if (panel != null) {
            startDrawer();
        }
        else {
            // else: dry-run without graphics, most likely for testing purposes
            printWarning();
        }
        draw();

        // PLAY ANIMATION
        interrupted = false;
        visited = new ArrayList<>();
        drive2Exit();
    }

    private void drive2Exit() {
        Runnable drive = () -> {
            int[] currentPosition = null;
            try {
                currentPosition = robot.getCurrentPosition();
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (!currentPosition.equals(mazeConfig.getExitPosition())) {
                if(interrupted) {
                    break;
                }
                try {
                    currentPosition = robot.getCurrentPosition();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (robot.hasStopped()) {
                    if(robot.noEnergy()) {
                        losing = 1;
                    }
                    else if(robot.crashed()) {
                        losing = 0;
                    }
                    else if(robot.jumpedBorder()) {
                        losing = 2;
                    }
                    go2losing();
                    break;
                }
                boolean driving = true;
                try {
                    driving = driver.drive1Step2Exit();
                } catch (Exception e) {
                    losing = 1;     // usually ran out of energy by default
                    if(robot.noEnergy()) {
                        losing = 1;
                    }
                    else if(robot.crashed()) {
                        losing = 0;
                    }
                    else if(robot.jumpedBorder()) {
                        losing = 2;
                    }
                    go2losing();
                    e.printStackTrace();
                    break;
                }
                energy_used = (int) driver.getEnergyConsumption();
                energyBar.setProgress(3500 - energy_used);
                path = driver.getPathLength();
                try {
                    Thread.sleep(1000 / speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!driving) {    // if exit has been reached
                    energy_used += robot.getEnergyForStepForward();
                    path += 1;
                    robot.move(1);
                    go2winning();
                    break;
                }
                if (visited.contains(currentPosition)) {
                    losing = 3;
                    go2losing();
                    break;
                }
                visited.add(currentPosition);
            }
            return;
        };
        animationThread = new Thread(drive);
        animationThread.start();
    }

    /**
     * Initializes the drawer for the first person view
     * and the map view and then draws the initial screen
     * for this state.
     */
    protected void startDrawer() {
        firstPersonView = new FirstPersonView(Constants.VIEW_WIDTH,
                Constants.VIEW_HEIGHT, Constants.MAP_UNIT,
                Constants.STEP_SIZE, seenCells, mazeConfig.getRootnode()) ;
        mapView = new Map(seenCells, 50, mazeConfig) ;
        // draw the initial screen for this state
        draw();
    }
    /**
     * Internal method to set the current position, the direction
     * and the viewing direction to values consistent with the
     * given maze.
     */
    private void setPositionDirectionViewingDirection() {
        // obtain starting position
        int[] start = mazeConfig.getStartingPosition() ;
        setCurrentPosition(start[0],start[1]) ;
        // set current view direction and angle
        angle = 0; // angle matches with east direction,
        // hidden consistency constraint!
        setDirectionToMatchCurrentAngle();
        // initial direction is east, check this for sanity:
        assert(dx == 1);
        assert(dy == 0);
    }


    /**
     * Method incorporates all reactions to keyboard input in original code,
     * The simple key listener calls this method to communicate input.
     * Method requires {@link #start(MazePanel) start} to be
     * called before.
     * @param key provides the feature the user selected
     * @param value is not used, exists only for consistency across State classes
     * @return false if not started yet otherwise true
     */
    public boolean keyDown(Constants.UserInput key, int value) {
        if (!started)
            return false;

        // react to input for directions and interrupt signal (ESCAPE key)
        // react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
        // react to input to display solution (on/off toggle switch)
        // react to input to increase/reduce map scale
        switch (key) {
            case UP: // move forward
                walk(1);
                // check termination, did we leave the maze?
                if (isOutside(px,py)) {
                    go2winning();
                }
                break;
            case LEFT: // turn left
                rotate(1);
                break;
            case RIGHT: // turn right
                rotate(-1);
                break;
            case DOWN: // move backward
                walk(-1);
                // check termination, did we leave the maze?
                if (isOutside(px,py)) {
                    go2winning();
                }
                break;
            case JUMP: // make a step forward even through a wall
                // go to position if within maze
                if (mazeConfig.isValidPosition(px + dx, py + dy)) {
                    setCurrentPosition(px + dx, py + dy) ;
                    draw() ;
                }
                break;
            case TOGGLELOCALMAP: // show local information: current position and visible walls
                // precondition for showMaze and showSolution to be effective
                // acts as a toggle switch
                mapMode = !mapMode;
                draw() ;
                break;
            case TOGGLEFULLMAP: // show the whole maze
                // acts as a toggle switch
                showMaze = !showMaze;
                draw() ;
                break;
            case TOGGLESOLUTION: // show the solution as a yellow line towards the exit
                // acts as a toggle switch
                showSolution = !showSolution;
                draw() ;
                break;
            case ZOOMIN: // zoom into map
                mapView.incrementMapScale();
                draw() ;
                break ;
            case ZOOMOUT: // zoom out of map
                mapView.decrementMapScale();
                draw() ;
                break ;
        } // end of internal switch statement for playing state
        return true;
    }
    /**
     * Draws the current content on panel to show it on screen.
     */
    protected void draw() {
        if (panel == null) {
            printWarning();
            return;
        }
        // draw the first person view and the map view if wanted
        firstPersonView.draw(panel, px, py, walkStep, angle,
                getPercentageForDistanceToExit()) ;
        if (isInMapMode()) {
            mapView.draw(panel, px, py, angle, walkStep,
                    isInShowMazeMode(),isInShowSolutionMode()) ;
        }
        // update the screen with the buffer graphics
        panel.commit() ;
    }
    /**
     * Calculates a distance to exit as a percentage.
     * 1.0 is for the starting position as this is the maximal
     * distance possible.
     * @return a value between 0.0 and 1.0, the smaller the closer
     */
    float getPercentageForDistanceToExit() {
        return mazeConfig.getDistanceToExit(px, py) /
                ((float) mazeConfig.getMazedists().getMaxDistance());
    }
    /**
     * Prints the warning about a missing panel only once
     */
    boolean printedWarning = false;
    protected void printWarning() {
        if (printedWarning)
            return;
        System.out.println("StatePlaying.start: warning: no panel, dry-run game without graphics!");
        printedWarning = true;
    }
    ////////////////////////////// set methods ///////////////////////////////////////////////////////////////
    ////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
    protected void setCurrentPosition(int x, int y) {
        px = x ;
        py = y ;
    }
    private void setCurrentDirection(int x, int y) {
        dx = x ;
        dy = y ;
    }
    /**
     * Sets fields dx and dy to be consistent with
     * current setting of field angle.
     */
    private void setDirectionToMatchCurrentAngle() {
        setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
    }

    ////////////////////////////// get methods ///////////////////////////////////////////////////////////////
    protected int[] getCurrentPosition() {
        int[] result = new int[2];
        result[0] = px;
        result[1] = py;
        return result;
    }
    protected CardinalDirection getCurrentDirection() {
        return CardinalDirection.getDirection(dx, dy);
    }
    boolean isInMapMode() {
        return mapMode ;
    }
    boolean isInShowMazeMode() {
        return showMaze ;
    }
    boolean isInShowSolutionMode() {
        return showSolution ;
    }
    public Maze getMazeConfiguration() {
        return mazeConfig ;
    }
    //////////////////////// Methods for move and rotate operations ///////////////
    final double radify(int x) {
        return x*Math.PI/180;
    }
    /**
     * Helper method for walk()
     * @param dir is the direction of interest
     * @return true if there is no wall in this direction
     */
    protected boolean checkMove(int dir) {
        CardinalDirection cd = null;
        switch (dir) {
            case 1: // forward
                cd = getCurrentDirection();
                break;
            case -1: // backward
                cd = getCurrentDirection().oppositeDirection();
                break;
            default:
                throw new RuntimeException("Unexpected direction value: " + dir);
        }
        return !mazeConfig.hasWall(px, py, cd);
    }
    /**
     * Draws and waits. Used to obtain a smooth appearance for rotate and move operations
     */
    private void slowedDownRedraw() {
        draw() ;
        try {
            Thread.sleep(25);
        } catch (Exception e) {
            // may happen if thread is interrupted
            // no reason to do anything about it, ignore exception
        }
    }

    /**
     * Performs a rotation with 4 intermediate views,
     * updates the screen and the internal direction
     * @param dir for current direction, values are either 1 or -1
     */
    private synchronized void rotate(int dir) {
        final int originalAngle = angle;
        final int steps = 4;

        for (int i = 0; i != steps; i++) {
            // add 1/4 of 90 degrees per step
            // if dir is -1 then subtract instead of addition
            angle = originalAngle + dir*(90*(i+1))/steps;
            angle = (angle+1800) % 360;
            // draw method is called and uses angle field for direction
            // information.
            slowedDownRedraw();
        }
        // update maze direction only after intermediate steps are done
        // because choice of direction values are more limited.
        setDirectionToMatchCurrentAngle();
        //logPosition(); // debugging
        drawHintIfNecessary();
    }

    /**
     * Moves in the given direction with 4 intermediate steps,
     * updates the screen and the internal position
     * @param dir, only possible values are 1 (forward) and -1 (backward)
     */
    private synchronized void walk(int dir) {
        // check if there is a wall in the way
        if (!checkMove(dir))
            return;
        // walkStep is a parameter of FirstPersonDrawer.draw()
        // it is used there for scaling steps
        // so walkStep is implicitly used in slowedDownRedraw
        // which triggers the draw operation in
        // FirstPersonDrawer and MapDrawer
        for (int step = 0; step != 4; step++) {
            walkStep += dir;
            slowedDownRedraw();
        }
        setCurrentPosition(px + dir*dx, py + dir*dy) ;
        walkStep = 0; // reset counter for next time
        //logPosition(); // debugging
        drawHintIfNecessary();
    }

    /**
     * Checks if the given position is outside the maze
     * @param x coordinate of position
     * @param y coordinate of position
     * @return true if position is outside, false otherwise
     */
    private boolean isOutside(int x, int y) {
        return !mazeConfig.isValidPosition(x, y) ;
    }
    /**
     * Draw a visual cue to help the user unless the
     * map is on display anyway.
     * This is the map if current position faces a dead end
     * otherwise it is a compass rose.
     */
    private void drawHintIfNecessary() {
        if (isInMapMode())
            return; // no need for help
        // in testing environments, there is sometimes no panel to draw on
        // or the panel is unable to deliver a graphics object
        // check this and quietly move on if drawing is impossible
        if ((panel == null || panel.getBufferGraphics() == null)) {
            printWarning();
            return;
        }
        // if current position faces a dead end, show map with solution
        // for guidance
        if (isFacingDeadEnd()) {
            //System.out.println("Facing deadend, help by showing solution");
            mapView.draw(panel, px, py, angle, walkStep, true, true) ;
        }
        else {
            // draw compass rose
            cr.setCurrentDirection(getCurrentDirection());
//    		cr.paintComponent(panel.getBufferGraphics());
            cr.paintComponent(panel);
        }
        panel.commit();
    }
    /**
     * Checks if the current position and direction
     * faces a dead end
     * @return true if at the current position there is
     * a wall to the left, right and front, false otherwise
     */
    private boolean isFacingDeadEnd() {
        return (!isOutside(px,py) &&
                mazeConfig.hasWall(px, py, getCurrentDirection()) &&
                mazeConfig.hasWall(px, py, getCurrentDirection().oppositeDirection().rotateClockwise()) &&
                mazeConfig.hasWall(px, py, getCurrentDirection().rotateClockwise()));
    }
}
