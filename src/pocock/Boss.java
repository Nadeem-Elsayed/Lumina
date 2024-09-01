package pocock;

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;

/**
 * @author Rosemarie
 **/
public class Boss extends Sprite {

	private final double TIME_BETWEEN_FRAMES = 0.2;

	private ImageView graphic;

	private double nextFrameTime = TIME_BETWEEN_FRAMES;
	private int currFrame = 0;


	private Rectangle2D[] frames = {
			new Rectangle2D(0,0,309,540),
			new Rectangle2D(309,0,309,540),
			new Rectangle2D(618,0,309,540),
			new Rectangle2D(927,0,309,540),
			new Rectangle2D(1236,0,309,540),
			new Rectangle2D(1545,0,309,540)
	};

	public Boss(ImageView source, Rectangle hitbox) {
		super(source, hitbox);
		this.graphic = source;		
		
		xVelocity = 250;
	}

	@Override
	public void update(double time) {
		//Movement
		x.set(x.get() + xVelocity * time);

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
