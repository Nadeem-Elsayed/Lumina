/**
 * 
 */
package pocock;

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;


/**
 * @author mark
 * most of the code is by Rosemarie I think
 */
public class Player extends Sprite {
	private final double TIME_BETWEEN_RUNNING_FRAMES = 0.08;
	private final double TIME_BETWEEN_IDLE_FRAMES = 0.25;

	private ImageView graphic;

	private double nextFrameTime = TIME_BETWEEN_IDLE_FRAMES;
	private int currFrame = 0;

	private double xAcc;
	private double yAcc;

	private Rectangle2D[] runningFrames = {
			new Rectangle2D(15, 9, 76, 84),
			new Rectangle2D(119, 9, 76, 84),
			new Rectangle2D(217, 9, 76, 84),
			new Rectangle2D(318, 9, 76, 84),
			new Rectangle2D(419, 9, 76, 84),
			new Rectangle2D(521, 9, 76, 84),
			new Rectangle2D(626, 9, 76, 84),
			new Rectangle2D(728, 9, 76, 84)
	};

	private Rectangle2D[] idleFrames = {
			new Rectangle2D(15, 108, 76, 88),
			new Rectangle2D(105, 108, 76, 88),
			new Rectangle2D(195, 108, 76, 88),
			new Rectangle2D(285, 108, 76, 88)
	};

	public Player(ImageView source, Rectangle hitbox) {
		super(source, hitbox);
		this.graphic = source;
		this.graphic.relocate(-9, 0);
		//Set initial position
		graphic.setViewport(idleFrames[0]);

	}

	@Override
	public void update(double time) {
		
		//player slows down if they're moving
		xVelocity *= 0.7;
		
		xAcc = 0;
		yAcc = 0;

		//player speed is based on movement of mouse
		//move left
		if (this.boundary().getCenterX() -FinalLumina.getMouseX() +FinalLumina.scrollLevel.getX()>40) {;
			xAcc = -90;
		}
		//move right
		if (this.boundary().getCenterX() -FinalLumina.getMouseX() +FinalLumina.scrollLevel.getX()<-40) {
			xAcc = 90;
		}
		if (FinalLumina.intersectsFrom == 3) {//standing on floor
			yAcc = 0;
			yVelocity = 0;
		}
		
		//different jump heights
		if (FinalLumina.intersectsFrom == 3 && FinalLumina.wantsToJump==true && this.boundary().getCenterY() -FinalLumina.getMouseY()>=100) {
			yVelocity = -900;
		}
		else if (FinalLumina.intersectsFrom == 3 && FinalLumina.wantsToJump==true && this.boundary().getCenterY() -FinalLumina.getMouseY()<100) {
			yVelocity = -800;
		}
		FinalLumina.wantsToJump=false;
		
		if (FinalLumina.intersectsFrom!=3) {
			yAcc = 30;
		}
		
		xVelocity += xAcc;
		yVelocity += yAcc;

		//Apply a maximum to horizontal velocity
		if (xVelocity < -480) xVelocity = -480;
		if (xVelocity > 480) xVelocity = 480;
		
		//change the x and y positions
		x.set(x.get() + xVelocity * time);
		y.set(y.get() + yVelocity * time);		

		// if facing right or left reverse image
		if (this.boundary().getCenterX() -FinalLumina.getMouseX() +FinalLumina.scrollLevel.getX()>0) {
			graphic.setScaleX(-1);
			
		}
		if (this.boundary().getCenterX() -FinalLumina.getMouseX() +FinalLumina.scrollLevel.getX()<0) {
			graphic.setScaleX(1);
		}

		//animation, idle and moving
		if (nextFrameTime < 0) {
			currFrame++;
			if(xAcc != 0) {
				if(currFrame >= runningFrames.length) currFrame = 0;
				graphic.setViewport(runningFrames[currFrame]);
				nextFrameTime = TIME_BETWEEN_RUNNING_FRAMES;
			}
			else {
				if (currFrame >= idleFrames.length) currFrame = 0;
				graphic.setViewport(idleFrames[currFrame]);
				nextFrameTime = TIME_BETWEEN_IDLE_FRAMES;
			}
		}
		else {
			nextFrameTime -= time;
		}	
	}
}

