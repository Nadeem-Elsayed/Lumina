package pocock;


import javafx.geometry.Rectangle2D;

import javafx.scene.shape.Rectangle;

import javafx.scene.image.ImageView;


/**
 * @author Rose
 *
 */

public class Rat extends Sprite {
	private final double TIME_BETWEEN_FRAMES = 0.2;

	private ImageView graphic;
	private Rectangle hitbox;

	private double patrolStart;
	private double patrolEnd;

	private double nextFrameTime = TIME_BETWEEN_FRAMES;
	private int currFrame = 0;


	//Frames for frame animation
	private Rectangle2D[] frames = {
			new Rectangle2D(15,0,27,39),
			new Rectangle2D(42,0,45,39),
			new Rectangle2D(87,0,57,39),
			new Rectangle2D(147,0,54,39),
			new Rectangle2D(201,0,57,39)
	};


	public Rat(ImageView source, Rectangle hitbox) {
		super(source, hitbox);
		this.graphic = source;
		this.hitbox = hitbox;
		//Set initial position
		graphic.relocate(0,16);
	}

	@Override
	public void update(double time) {
		//Movement
		if (x.get()<=patrolStart) {
			graphic.setScaleX(-1);
			xVelocity=40;
		} else if (x.get()+hitbox.getWidth()>=patrolEnd) {
			graphic.setScaleX(1);
			xVelocity = -40;
		}	

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
	public void setPatrolStartX(double x) {
		patrolStart = x;
	}
	public void setPatrolEndX(double x) {
		patrolEnd = x;
	}


}