package pocock;

public interface GameTick {
	
	/**
	 * Code to be executed for each tick of game time
	 * 
	 * @param elapsedTime amount of time that has passed since the previous game tick in seconds
	 */
	public void gameTick(double elapsedTime);
}
