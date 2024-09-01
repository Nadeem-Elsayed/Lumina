package pocock;

import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;

public class Corpse extends Sprite {

	//Create a new corpse object
    public Corpse(ImageView source, Rectangle hitbox) {
        super(source, hitbox);
    }

	@Override
	/*
	 * no real motion or anything for a corpse
	 */
	public void update(double time) {
		
	}

}
