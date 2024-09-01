/**
 * 
 */
package pocock;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * @author mark
 *
 */
public class Platform extends Sprite {

	/**
	 * @param graphic
	 * @param hitBox
	 */
	public Platform(Node graphic, Rectangle hitBox) {
		super(graphic, hitBox);
	}

	@Override
	/*
	 * basic platforms don't move ... might do some animating here though?
	 */
	public void update(double time) {
		
	}

}
