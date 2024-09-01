/**
 * 
 */
package pocock;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/*
 *
 */
public class Spike extends Sprite {

	/**
	 * @param graphic
	 * @param hitBox
	 */
	public Spike(Node graphic, Rectangle hitBox) {
		super(graphic, hitBox);
	}
	
	@Override
	public void update(double time) {
		//spikes don't move, I doubt you will animate something here
	}

}