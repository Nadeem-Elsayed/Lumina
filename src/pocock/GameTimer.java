package pocock;

import javafx.animation.AnimationTimer;

public class GameTimer extends AnimationTimer {
	
	private long previousTime = -1;
	private GameTick myHandler;

	/**
	 * Create a default GameTimer.  The program will still need to define the GameTick handler.
	 */
	public GameTimer() { }
	
	/**
	 * Create a GameTimer with the given handler.  A handler is an object that implements the GameTick interface.
	 * Alternatively, a lambda expression can be used to indicate the method to be executed on each update.
	 * 
	 * @param handler Object implementing GameTick interface
	 */
	public GameTimer(GameTick handler) {
		myHandler = handler;		
	}
	
	/**
	 * Set the object used to handle game ticks
	 * @param handler Object implementing GameTick interface
	 */
	public void setOnTick(GameTick handler) {
		myHandler = handler;
	}
	
	/**
	 * Process timer events.  When a TimerEvent is triggered for this 
	 * AnimationTimer, the game timer will 
	 * - calculate the elapsed time since the last timer event
	 * - call the gameTick(elapsedTime) method of the handler
	 * 
	 * @param currentTime system time in nanoseconds when the event is triggered
	 */
	@Override
	public void handle(long currentTime) {
		//calculate the elapsed time
		double elapsedTime = (currentTime - previousTime) / 1000000000.0;
		//call the handling class' gameTick event
		if (myHandler != null) myHandler.gameTick(elapsedTime);		
		//save the current time
		previousTime = currentTime;
	}
	
	/**
	 * Starts the game timer
	 */
	public void start() {
		previousTime = System.nanoTime();
		super.start();
	}
	
	/**
	 * Stops the game timer
	 */
	public void stop() {
		previousTime = -1;
		super.stop();
	}
	
	/**
	 * Tells if the game timer is currently running
	 * @return true if the gameTimer is running, false otherwise.
	 */
	public boolean isPaused() { return previousTime == -1; }
}