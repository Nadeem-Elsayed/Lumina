package pocock;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;

/**
 * @author Rose
 *
 */
public class Bat extends Sprite {
	private final double TIME_BETWEEN_FRAMES = 0.2;

	private ImageView graphic;
	private Rectangle hitbox;

	private double patrolStart;
	private double patrolEnd;
	
	private double nextFrameTime = TIME_BETWEEN_FRAMES;
	private int currFrame = 0;

	//Frames for frame animation
	private Rectangle2D[] frames = {
			new Rectangle2D(0,0,86,47),
			new Rectangle2D(87,0,176,50)
	};

	public Bat(ImageView source, Rectangle hitbox) {
		super(source, hitbox);
		this.graphic = source;
		this.hitbox = hitbox;
		//Set initial position
		graphic.setViewport(frames[0]);
	}

	@Override
	/*
	 * updates a bat's movement
	 */
	public void update(double time) {
		
		//Movement by Nadeem
		if (y.get()<=patrolStart) {
			yVelocity=60;
		}
		if (y.get()+hitbox.getHeight()>=patrolEnd) {
			yVelocity = -60;
		}	

		y.set(y.get() + yVelocity * time);


		//Frame animation by Rosemarie because she is amazing
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
	public void setPatrolStartY(double x) {
		patrolStart = x;
	}
	public void setPatrolEndY(double x) {
		patrolEnd = x;
	}

}