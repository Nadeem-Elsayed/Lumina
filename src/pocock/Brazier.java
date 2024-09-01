package pocock;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class Brazier extends Sprite {
	private boolean isLit = false;

	private ImageView graphic;

	private final double TIME_BETWEEN_FRAMES = 0.2;
	private double nextFrameTime = TIME_BETWEEN_FRAMES;
	private int currFrame = 0;


	private Rectangle2D unlitFrame = new Rectangle2D(483, 0, 93, 54); 

	private Rectangle2D[] frames = {
			new Rectangle2D(3, 0, 93, 54),
			new Rectangle2D(99, 0, 93, 54),
			new Rectangle2D(195, 0, 93, 54),
			new Rectangle2D(291, 0, 93, 54),
			new Rectangle2D(387, 0, 93, 54)
	};

	public Brazier(ImageView source, Rectangle hitbox) {
		super(source, hitbox);
		this.graphic = source;
		graphic.setViewport(unlitFrame);
		
	}

	public void update(double time) {
		if (isLit) {
			//Frame animation
			if (nextFrameTime < 0) {
				currFrame++;
				if(currFrame >= frames.length) currFrame = 0;
				graphic.setViewport(frames[currFrame]);
				nextFrameTime = TIME_BETWEEN_FRAMES;
			}
			else {
				nextFrameTime -= time;
			}
		}
		}

	public void light() {
		isLit = true;
	}
	public boolean isLit() {
		return isLit;
	}

}
