package pocock;

import javafx.scene.shape.Rectangle; 
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public abstract class Sprite extends Group{
	public final static boolean SHOW_HIT_BOXES = false;
	
	/**
	 *Graphic property for the sprite - could be an image view or shape object
	 *Unless otherwise deliberately changed the graphic will be represented by a 
	 *rectangle with top left corner at (0,0) in the group
	 */
	private Node graphic;
	
	/**
	 * hitbox represents a rectangle within the bounds of the graphic that is accepted as "touching" the sprite.
	 * which may or may not include all pixels of the graphic
	 */
	private Rectangle hitbox;
	
	/**
	 * Location of the Sprite.  Using properties so that the transformation can be bound to the location automatically
	 * updating the position on screen as the location changes. 
	 */
	protected DoubleProperty x = new SimpleDoubleProperty(0.0);
	/**
	 * Location of the Sprite.  Using properties so that the transformation can be bound to the location automatically
	 * updating the position on screen as the location changes. 
	 */
	protected DoubleProperty y = new SimpleDoubleProperty(0.0);
	
	/**
	 * translation transformation to place the sprite on screen
	 */
	private Translate loc = new Translate();

	/**
	 * horizontal velocity
	 */
	protected double xVelocity = 0;
	
	/**
	 * vertical velocity
	 */
	protected double yVelocity = 0;
	
	protected double xAcc = 0;
	protected double yAcc = 0;
	/**
	 * Sprite have a rectangular hitbox and have an ImageView
	 * Constructor creates a square for the hitbox 
	 */
	public Sprite (Node graphic, Rectangle hitbox) {
		super();
		this.graphic = graphic;
		this.hitbox = hitbox;
		this.hitbox.setFill(null);
		this.hitbox.setStroke(Color.GREEN);
		this.hitbox.setVisible(SHOW_HIT_BOXES);

		//Add the imageview and hit box to the group
		this.getChildren().addAll(this.graphic, this.hitbox);

		loc.xProperty().bind(x);
		loc.yProperty().bind(y);
		this.getTransforms().add(loc);
	}

	/*
	 * used for hitBox I think
	 */
	public Bounds boundary(){
		Transform parentTransform = this.getLocalToParentTransform();		
		return parentTransform.transform(hitbox.getBoundsInLocal());
	}
	/*
	 * checks if the Sprites intersect
	 */
	public boolean intersects(Sprite other){
		return this.boundary().intersects(other.boundary()); 
	}
	/*
	 * return hit box
	 */
	public Rectangle getHitbox() {
		return hitbox;
	}
	/*
	 * return the graphic
	 */
	public Node graphic() {
		return graphic;
	}
	/*
	 * set the velocity using a vector pretty much
	 */
	public void setVelocity(Point2D velocity) {
		this.xVelocity = velocity.getX();
		this.yVelocity = velocity.getY();
	}
	/*
	 * set the velocity using two values
	 */
	public void setVelocity(double x, double y) {
		this.xVelocity = x;
		this.yVelocity = y;		
	}
	/**
	 * @return the xVelocity
	 */
	public double getxVelocity() {
		return xVelocity;
	}

	/**
	 * @param xVelocity the xVelocity to set
	 */
	public void setxVelocity(double xVelocity) {
		this.xVelocity = xVelocity;
	}

	/**
	 * @return the yVelocity
	 */
	public double getyVelocity() {
		return yVelocity;
	}

	/**
	 * @param yVelocity the yVelocity to set
	 */
	public void setyVelocity(double yVelocity) {
		this.yVelocity = yVelocity;
	}
	public void setYAcc (double y) {
		yAcc = y;
	}
	public void setXAcc(double x) {
		xAcc = x;
	}
	public double getYAcc (double y) {
		return yAcc;
	}
	public double getXAcc(double x) {
		return xAcc;
	}
	
	/**
	 * @return x location
	 */
	public double getX() { return x.get(); }
	
	/** 
	 * @return y location
	 */
	public double getY() { return y.get(); }
	
	/**
	 * @param x new x location
	 */
	public void setX(double x) { this.x.set(x); }

	/**
	 * @param y new y location
	 */
	public void setY(double y) { this.y.set(y); }

	/**
	 * Relocate to the give co-ordinate
	 * @param x
	 * @param y
	 */
	public void relocate(double x, double y) {
		this.x.set(x);
		this.y.set(y);
	}
	
	/**
	 * To be implemented by child classes
	 * Update the sprite given the amount of elapsed time.
	 * 
	 * @param time
	 */
	public abstract void update(double time);
	
}
