package pocock;

import java.util.HashSet;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;

public class FinalLumina extends Application {

	//I made the player static because there is only one of them, and it helps with the mouse jump
	static Sprite player = new Player(new ImageView(Resources.PLAYER), new Rectangle(0, 0, 38, 84));

	//start at level 1
	private int currentLevel = 1;

	//spawn point, can change
	private double spawnX = 100;
	private double spawnY = 350;

	//This is a list of array lists of the sprites (lol list of array lists)
	ArrayList<Sprite> platforms = new ArrayList<Sprite>();
	ArrayList<Sprite> enemies = new ArrayList<Sprite>();
	ArrayList<Sprite> spikes = new ArrayList<Sprite>();
	ArrayList<Brazier> braziers = new ArrayList<Brazier>();
	ArrayList<Sprite> levelTransitions = new ArrayList<Sprite>();

	//where all the keyboard input is stored
	private static HashSet<KeyCode> keyboard = new HashSet<KeyCode>();

	private Group ui = new Group();//for lights only
	private Group graphics = new Group();//for sprites and other doodads
	private Group DeathScreen = DeathScreen();//R.I.P
	private Group StartScreen = startScreen();//main menu
	private Group instructions;//all the instructions floating in the air on level one

	public static int intersectsFrom;//where the player intersects a platform from

	//constants for where the player intersects from
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int ABOVE = 3;
	public static final int BELOW = 4;

	//used for the scrolling effect
	static Translate scrollLevel = new Translate();
	private int levelWidth;//varies by level
	private int levelHeight; //varies by level

	//used to find the mouse location
	Translate mouseLocation = new Translate();

	//timer for the application
	GameTimer timer = new GameTimer (elapsedTime -> gameUpdate(elapsedTime));

	//deathCount elements
	private int totalDeathCount = 0;
	private int deathCount = 0;
	Label skullLabel = new Label(String.valueOf(deathCount));
	ImageView skull = new ImageView(Resources.SKULL);

	//load image of Skippy corpse
	private Sprite [] deadGuys = new Sprite[10];

	//add ui to the root if you want to see the light stuff, remove if you dont wanna see cause you're a loser
	Group root = new Group(graphics, ui, skull, skullLabel);
	Scene scene = new Scene(root, 960, 540, Color.BLACK);

	//light
	Rectangle shade;
	Shape dark;
	Shape dim;

	//--------Cursor, this stuff is used for animating the cursor
	private final double TIME_BETWEEN_FRAMES = 0.2;
	private double nextFrameTime = TIME_BETWEEN_FRAMES;
	private int currFrame = 0;

	//Mouse location
	public static DoubleProperty mouseX = new SimpleDoubleProperty();
	static public DoubleProperty mouseY = new SimpleDoubleProperty();

	//-----Game States
	public static final int START_SCREEN = 0;
	public static final int PLAYING_GAME = 1;
	public static final int END_SCREEN = 2;

	//Boss variables
	Boss currentBoss;
	boolean bossFight = false;

	//used for intersection and jumping
	static boolean intersects = true;
	static boolean wantsToJump;

	//all the images for the background
	ImageView background1 = new ImageView(Resources.BACKGROUND1);
	ImageView background2 = new ImageView(Resources.BACKGROUND2);
	ImageView background3 = new ImageView(Resources.BACKGROUND3);
	ImageView background4 = new ImageView(Resources.BACKGROUND4);

	//the instructions on level 1
	Label instructJump;
	Label instructAvoid;
	Label instructGoal;

	/*
	 * main
	 */
	public static void main(String[] args) {
		launch();
	}

	/*
	 *Returns a group
	 *this gives us the title screen 
	 */
	public Group startScreen() {
		//background image
		ImageView background = new ImageView(Resources.TITLE_BACKGROUND);

		//the button used to start the game
		Button start = new Button ("Start");
		start.relocate(200, 400);
		start.setPrefSize(300, 150);
		start.setFont(Font.loadFont("file:images/Deutsch.ttf", 50));
		start.setBackground(null);
		start.setOnAction(e -> {
			//this sets where you spawn in when game is started
			spawnX = 100;
			spawnY = 350;
			respawn();
		});

		//color change if the mouse enters the button
		start.setOnMouseEntered(e -> {start.setTextFill(Color.rgb(251, 170, 65));});
		start.setOnMouseExited(e -> {start.setTextFill(Color.rgb(25,23,36));});

		//returns the group
		return new Group(background, start);
	}

	/*
	 * This is the code for the death screen
	 */
	public Group DeathScreen() {

		//this is the background 
		Rectangle redOverlay = new Rectangle (960, 540, Color.rgb(25,23,36));
		Label dead = new Label ("YOU DIED");
		dead.relocate(220, 30);
		dead.setTextFill(Color.WHITE);

		dead.setFont(Font.loadFont("file:images/Deutsch.ttf", 100));

		//this is the code for the button
		Button respawn = new Button ("respawn");
		respawn.relocate(310, 180);
		respawn.setTextFill(Color.BLACK);
		respawn.setPrefSize(300, 100);
		respawn.setFont(Font.loadFont("file:images/Deutsch.ttf", 45));
		respawn.setOnAction(e -> {
			//this sends you to your last respawn point
			respawn();
		});

		//this button will send you back to the home screen
		Button quit = new Button ("quit");
		quit.relocate(310, 330);
		quit.setTextFill(Color.BLACK);
		quit.setPrefSize(300, 100);
		quit.setFont(Font.loadFont("file:images/Deutsch.ttf", 45));
		quit.setOnAction(e -> {
			scene.setRoot(startScreen());

			//ADDITION: update death count on quit
			deathCount = 0;
			skullLabel.setText(String.valueOf(deathCount));
			//send player back to level 1
			currentLevel = 1;
		});

		Group screen = new Group(redOverlay, dead, respawn, quit);
		return screen;
	}

	/**
	 * Events to occur when the player dies
	 * No more playing for you
	 */
	public void die() {
		//ADDITION by Mark: add a dead Skippy corpse where Skippy died 
		deadGuys [deathCount%10] = new Corpse(new ImageView(Resources.SKELETON), new Rectangle(0, 0, 0, 0));
		deadGuys[deathCount%10].setX(player.getX()+10);
		deadGuys[deathCount%10].setY(player.getY()+27);

		//ADDITION: add to the death count and show on the skull icon
		deathCount++;
		totalDeathCount++;
		skullLabel.setText(String.valueOf(totalDeathCount));

		//no more playing for you
		timer.stop();

		//R.I.P, go to the death screen
		scene.setRoot(DeathScreen);

		//reset cursor to normal arrow
		scene.setCursor(Cursor.DEFAULT);
	}

	/*
	 * Respawn the player after death
	 */
	public void respawn() {
		graphics.getChildren().clear();

		//this adds in the graphics for level one
		if (currentLevel == 1) {
			graphics.getChildren().addAll(background1, instructions, player, loadLevel(currentLevel));
		}
		//this adds in the graphics for level two
		if (currentLevel ==2) {
			graphics.getChildren().addAll(background2, player, loadLevel(currentLevel));
		}
		//this adds in the graphics for level three
		if (currentLevel ==3) {
			background3.relocate(background3.getX()-192, background3.getY());
			graphics.getChildren().addAll(background3, player, loadLevel(currentLevel));
		}
		if (currentLevel ==4) {
			background4.relocate(background4.getX()-192, background4.getY());
			graphics.getChildren().addAll(background4, player, loadLevel(currentLevel));

			//boss starts a little bit after the spawn point, when you respawn you are safe
			bossFight = false;
		}

		//add the dead guys to the graphics
		for (int i = 0; i < deathCount; i++) {
			if (i == 10) break;
			graphics.getChildren().add(deadGuys[i]);
		}

		//change screen and 
		scene.setRoot(root);
		player.relocate(spawnX, spawnY);
		timer.start();
	}

	/*
	 * what the timer repeats
	 */
	private void gameUpdate(double elapsedTime) {

		//-------------update all sprites
		//check if player intersects stuff
		floorCheck();
		outOfBoundsCheck();
		spikeCheck();
		enemyCheck();
		endOfLevelCheck();
		//Check if Brazier is close to player and remove darkness

		//Random chance to make a rat noise
		if (Math.random()*5000<2) {
			Resources.RAT_BAT.sound().setCycleCount(Integer.MAX_VALUE);
			Resources.RAT_BAT.sound().play();

			//end media, I don't want the effect to keep playing
			Resources.RAT_BAT.sound().setOnEndOfMedia(new Runnable() {
				public void run() {
					Resources.RAT_BAT.sound().seek(Duration.ZERO);
					Resources.RAT_BAT.sound().pause();
				}
			});
		}

		//all the stuff the player goes through in terms of movement
		player.update(elapsedTime);	

		//update enemies
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).update(elapsedTime);
		}

		//update braziers
		for (int i = 0; i < braziers.size(); i++) {

			//if the player comes in contact with the brazier it will be lit
			Brazier brazier = braziers.get(i);
			if (player.intersects(braziers.get(i))) {
				brazierLight(i, brazier);
			}

			//for light changing
			if (brazier.isLit()==true) {
				if (Math.abs(brazier.getX()-player.getX())<=960) {
					dark.setOpacity(Math.abs(brazier.getX()-player.getX())/960);
					dim.setOpacity(Math.abs(brazier.getX()-player.getX())/1920);
				} 
			}
			braziers.get(i).update(elapsedTime);
		}
		//closer you get to the boss, the less you see
		if (bossFight) {
			dark.setOpacity((960-(player.getX()-currentBoss.getX()))/960);
			dim.setOpacity(0.3);
		}

		//update the scrolling transform
		if (bossFight) {
			//Automatic scroll for chase boss fight
			scrollLevel.setX(-currentBoss.getX());
		}
		else {
			//update the scrolling transform for game length
			if (player.getX() < 480) {
				scrollLevel.setX(0);
			}
			else if (player.getX() < levelWidth - 480) {
				scrollLevel.setX(480 - player.getX());
			}
			else {
				scrollLevel.setX(960 - levelWidth);
			}
		}

		//update the scrolling transform for game width
		if (player.getY() < 270) {
			scrollLevel.setY(0);
		}
		else if (player.getY() < levelHeight - 270) {
			scrollLevel.setY(270 - player.getY());
		}
		else {
			scrollLevel.setY(540 - levelHeight);
		}

		//Fix the animated cursor speed
		if (nextFrameTime < 0) {
			currFrame++;
			if(currFrame >= Resources.CURSOR.length) currFrame = 0;
			scene.setCursor(new ImageCursor(Resources.CURSOR[currFrame]));
			nextFrameTime = TIME_BETWEEN_FRAMES;

		}
		else {
			nextFrameTime -= elapsedTime;
		}
	}

	/**
	 * Checks the current key map to see if a key is pressed
	 * @param code for the key to check
	 * @return true if the key is currently recorded as pressed
	 */
	public static boolean keyPressed(KeyCode code) {
		return keyboard.contains(code);
	}

	/*
	 * detects jump input
	 */
	public void mouseClick(MouseEvent m) {
		if (m.getButton() == MouseButton.PRIMARY) {

			//if the player lefts clicks he jumps
			wantsToJump = true;
		} 

		//if they don't click, don't jump
		else {
			wantsToJump = false;
		}
	}

	/*
	 * This runs the game code
	 */
	public void start(Stage stage) {
		//ADDITION by Mark: setting the skull icon
		skull.relocate(30, 30);
		skullLabel.setFont(Font.loadFont("file:images/Deutsch.ttf", 25));
		skullLabel.setTextFill(Color.WHITE);
		skullLabel.relocate(97, 90);

		//starts the level one code
		if (currentLevel ==1) {
			Resources.BACKGROUND_MUSIC.sound().setCycleCount(Integer.MAX_VALUE);
			Resources.BACKGROUND_MUSIC.sound().setAutoPlay(true);
		}

		//plays the back ground music
		else {
			Resources.BACKGROUND_MUSIC.sound().stop();
			Resources.CALM_SONG.sound().setCycleCount(Integer.MAX_VALUE);
			Resources.CALM_SONG.sound().setAutoPlay(true);
		}

		//the stage is not resizeable
		stage.setResizable(false);

		//for now, hard coding some location data
		stage.setTitle("Lumina");
		player.relocate(80, 402);

		//used for location of the light
		mouseLocation.xProperty().bind(mouseX.add(-960));
		mouseLocation.yProperty().bind(mouseY.add(-540));

		//create a light shade
		shade = new Rectangle(0, 0, 1920, 1080);
		Circle small = new Circle(150);
		Circle large = new Circle(250);
		small.setCenterX(960);
		small.setCenterY(540);
		large.setCenterX(960);
		large.setCenterY(540);

		dark = Shape.subtract(shade, large);//big black rectangle

		//the slightly lit area around the light
		dim = Shape.subtract(large, small);
		dim.setOpacity(0.5);

		//set Graphics and lights for level
		ui.getChildren().addAll(dark, dim);
		ui.getTransforms().add(mouseLocation);

		//inserts the the text found in level one
		instructJump = new Label ("Move with mouse, click to jump");
		instructJump.setFont(Font.loadFont("file:images/Deutsch.ttf", 50));
		instructJump.setTextFill(Color.rgb(251, 170, 65));
		instructJump.relocate(192, 216);

		instructAvoid = new Label ("Avoid rats, bats, and spikes");
		instructAvoid.setFont(Font.loadFont("file:images/Deutsch.ttf", 50));
		instructAvoid.setTextFill(Color.rgb(251, 170, 65));
		instructAvoid.relocate(1500, 216);

		instructGoal = new Label ("Get to the end! braziers are checkpoints");
		instructGoal.setFont(Font.loadFont("file:images/Deutsch.ttf", 50));
		instructGoal.setTextFill(Color.rgb(251, 170, 65));
		instructGoal.relocate(2500, 216);

		//this generates the in level text
		instructions = new Group(instructJump, instructAvoid, instructGoal);
		graphics.getChildren().addAll(background1, instructions, player, loadLevel(currentLevel));
		graphics.getTransforms().add(scrollLevel);

		//make sure that mouse events are caught by the scene, not this group
		graphics.setMouseTransparent(false);

		//add and remove keyboard input from HashSet
		scene.setOnKeyPressed(key -> keyboard.add(key.getCode()));
		scene.setOnKeyReleased(key -> keyboard.remove(key.getCode()));

		//get mouse location, find out if mouse button was clicked
		scene.setOnMouseMoved(mouseEvent -> mouseLocation(mouseEvent));
		scene.setOnMouseDragged(mouseEvent -> mouseLocation(mouseEvent));
		scene.setOnMousePressed(mouseEvent -> mouseClick(mouseEvent));

		//timer
		scene.setRoot(StartScreen);

		stage.setScene(scene);
		stage.show();
	}

	/*
	 * retrieves location of mouse
	 */
	public void mouseLocation(MouseEvent m) {
		mouseX.set(m.getX());
		mouseY.set(m.getY());
	}

	/*
	 * returns x location of mouse
	 */
	public static double getMouseX() {
		return mouseX.get();
	}

	/*
	 * returns y location of mouse
	 */
	public static double getMouseY() {
		return mouseY.get();
	}

	/*
	 * checks for collision with floor
	 */
	public void floorCheck() {
		for(int i = 0; i < platforms.size(); i++) {
			Sprite floor = platforms.get(i);
			if (player.intersects(floor)) {
				intersects = true;
				intersectsFrom =collisionFrom(floor);

				if (intersectsFrom==LEFT) {
					//correct x position, y stays the same
					player.relocate(floor.boundary().getMinX()-player.boundary().getWidth()-2, player.getY());
					player.setxVelocity(-player.getxVelocity()-20);
				}
				else if (intersectsFrom==RIGHT) {
					//correct x position, y stays the same
					player.relocate(floor.boundary().getMaxX()+2, player.getY());
					player.setxVelocity(-player.getxVelocity()-20);
				}
				else if (intersectsFrom==ABOVE) { 
					//correct y position, x stays the same
					player.relocate(player.getX(), floor.boundary().getMinY()-player.boundary().getHeight()+2);
					player.setyVelocity(-player.getyVelocity());
				}
				else if (intersectsFrom==BELOW ) {					
					//correct y position, x stays the same
					player.relocate(player.getX(), floor.boundary().getMaxY()+5);
					player.setyVelocity(0);
				}
				return;
			} 
			else {
				intersectsFrom = 0;//doesn't intersect
				intersects = false;
			}
		}
	}

	/*
	 * Checks if the player is out of bounds
	 * The player dies when they fall through the floor
	 */
	public void outOfBoundsCheck() {
		if (player.y.get() >= levelHeight) {
			player.relocate(player.getX(), levelHeight - 54);
			player.yAcc = 0;
		}

		if (player.y.get() <= 0) {
			player.relocate(player.getX(), 54);
			player.yAcc = 0;
		}

		if (player.y.get() >= levelHeight - 54 && player.x.get() <= 96) {
			player.relocate(96, 54);
		}
	}

	/*
	 * checks from which direction collision happens
	 */
	public int collisionFrom(Sprite collider) {
		Bounds playerBounds = player.boundary();
		Bounds colliderBounds = collider.boundary();

		if (playerBounds.getMaxX()>=colliderBounds.getMinX()
				&& playerBounds.getMinX() < colliderBounds.getMinX()
				&& playerBounds.getMaxY()-40 >= colliderBounds.getMinY()//not above
				&& playerBounds.getMinY()+20 <= colliderBounds.getMaxY()//not below
				) {
			return LEFT; 
		}
		if (playerBounds.getMinX() <= colliderBounds.getMaxX()
				&& playerBounds.getMaxX() > colliderBounds.getMaxX()
				&& playerBounds.getMaxY()-40>=colliderBounds.getMinY()//not above
				&& playerBounds.getMinY()+20<=colliderBounds.getMaxY()//not below
				) {
			return RIGHT;
		}
		if (playerBounds.getMinY()+playerBounds.getHeight()>colliderBounds.getMinY()
				&& playerBounds.getMaxX()>=colliderBounds.getMinX()//not to left
				&& playerBounds.getMinX()<=colliderBounds.getMaxX()//not to right
				&& playerBounds.getMaxY()<colliderBounds.getMaxY()
				) {
			return ABOVE;
		}
		if (colliderBounds.getMinY()+colliderBounds.getHeight()>playerBounds.getMinY()
				&& playerBounds.getMaxX()>=colliderBounds.getMinX()//not to left
				&& playerBounds.getMinX()<=colliderBounds.getMaxX()//not to right
				&& playerBounds.getMaxY()>=colliderBounds.getMinY()
				) {
			return BELOW;
		}
		return 0;
	}

	/**
	 * Check if player is touching a spike
	 */
	public void spikeCheck() {
		for (int i =0; i<spikes.size(); i++) {
			Sprite spike = spikes.get(i);
			if (player.intersects(spike)) {
				die();
				// show death screen and possibly respawn
			}
		}
	}

	/**
	 * Check if player is touching an enemy
	 */
	public void enemyCheck() {
		for (int i =0; i<enemies.size(); i++) {
			Sprite enemy = enemies.get(i);
			if (player.intersects(enemy)) {
				die();
			}
		}
	}

	/*
	 * Check for levels and boss events
	 */
	public void endOfLevelCheck() {
		if (currentLevel ==1 && player.x.get()>=levelWidth-90) {
			graphics.getChildren().clear();
			graphics.getChildren().addAll(background2, loadLevel(2), player);
			player.relocate(100, 400);
			currentLevel++;
			Resources.BACKGROUND_MUSIC.sound().stop();
			Resources.CALM_SONG.sound().setCycleCount(Integer.MAX_VALUE);
			Resources.CALM_SONG.sound().setAutoPlay(true);
			Resources.CALM_SONG.sound().play();

			//clear dead guys
			deathCount = 0;
			clearDeadPeople();

		} else if (currentLevel ==2 && player.x.get()>=levelWidth-90) {
			graphics.getChildren().clear();
			//     background3.relocate(background3.getX()-192, background3.getY());
			graphics.getChildren().addAll(background3, loadLevel(3), player);
			player.relocate(100, 400);
			currentLevel++;
			deathCount = 0;
			clearDeadPeople();

		} else if (currentLevel == 3 && player.x.get()>=levelWidth-90) {
			graphics.getChildren().clear();
			//   background4.relocate(background4.getX()-192, background4.getY());
			graphics.getChildren().addAll(background4, loadLevel(4), player);
			player.relocate(100, 400);
			currentLevel++;
			deathCount = 0;
			clearDeadPeople();
		}
		else if (currentLevel == 4 && player.x.get()>611 && !bossFight) {
			//Initiate Boss Fight
			bossFight = true;
			currentBoss.relocate(0, 0);
		} 
		else if (currentLevel == 4 && player.x.get()>=14496) {
			graphics.getChildren().clear();
			timer.stop();
			player.relocate(100, 400);
			currentLevel=1;
			scene.setRoot(StartScreen);
			deathCount = 0;
			clearDeadPeople();
		}
	}

	/*
	 * Light the braziers
	 */
	public void brazierLight(int i, Brazier brazier) {
		if (brazier.isLit()==false) {
			Resources.BRAZIERON.sound().setCycleCount(Integer.MAX_VALUE);
			Resources.BRAZIERON.sound().play();
			Resources.BRAZIERON.sound().setOnEndOfMedia(new Runnable() {
				public void run() {
					Resources.BRAZIERON.sound().seek(Duration.ZERO);
					Resources.BRAZIERON.sound().pause();

				}
			});
		}
		brazier.light();
		spawnX = brazier.getX();
		spawnY = brazier.getY();
	}

	/*
	 * clears the dead people from the array
	 * this is implemented in the endOfLevelCheck
	 */
	public void clearDeadPeople() {
		for (int i = 0; i<deadGuys.length; i++) {
			deadGuys[i] = null;
		}
	}

	/**
	 * Load a level based on level data
	 * @param level
	 * @return group with all level objects
	 */
	public Group loadLevel(int level) {
		Group levelPlatforms = new Group();
		//grab level data array for the given level
		String[] levelData = LevelData.getLevel(level);

		//record the width of the level
		levelWidth = 96 * levelData[0].length();
		levelHeight = 54 * levelData[0].length();

		//clear any old level
		platforms.clear();
		enemies.clear();
		spikes.clear();
		braziers.clear();

		//create new platforms based on the level data
		for(int row = 0; row < levelData.length; row++) {
			for (int col = 0; col < levelData[row].length(); col++) {
				switch(levelData[row].charAt(col)) {
				case '0' :
					break;
					//Platform
				case '1' :
					Sprite floor = new Platform(new ImageView(Resources.PLATFORM), new Rectangle(0, 0, 96, 54));
					floor.relocate(96 * col, 54 * row);
					platforms.add(floor);
					levelPlatforms.getChildren().add(floor);
					break;
					//Left end piece 
				case '2':
					Sprite leftFloor = new Platform(new ImageView(Resources.LEFT_PLATFORM), new Rectangle(0,0,96,54));
					leftFloor.relocate(96 * col, 54 * row);
					platforms.add(leftFloor);
					levelPlatforms.getChildren().add(leftFloor);
					break;
					//Right end piece
				case '3':
					Sprite rightFloor = new Platform(new ImageView(Resources.RIGHT_PLATFORM), new Rectangle(0,0,96,54));
					rightFloor.relocate(96 * col, 54 * row);
					platforms.add(rightFloor);
					levelPlatforms.getChildren().add(rightFloor);
					break;
					//Bottom end piece
				case '4':
					Sprite bottomFloor = new Platform(new ImageView(Resources.BOTTOM_PLATFORM), new Rectangle(0,0,96,54));
					bottomFloor.relocate(96 * col, 54 * row);
					platforms.add(bottomFloor);
					levelPlatforms.getChildren().add(bottomFloor);
					break;
					//Top end piece
				case '5':
					Sprite topFloor = new Platform(new ImageView(Resources.TOP_PLATFORM), new Rectangle(0,0,96,54));
					topFloor.relocate(96 * col, 54 * row);
					platforms.add(topFloor);
					levelPlatforms.getChildren().add(topFloor);
					break;
					//Spike
				case '6':
					Sprite spike = new Platform(new ImageView(Resources.SPIKES), new Rectangle(0, 34, 96, 20));
					spike.relocate(96 * col, 54 * row);
					spikes.add(spike);
					levelPlatforms.getChildren().add(spike);
					break;
					//Bats
				case '7':
					Bat bat = new Bat(new ImageView(Resources.BAT), new Rectangle(0, 0, 84, 44));
					bat.relocate(96 * col, 54 * row);
					bat.setPatrolStartY(54*row);
					bat.setPatrolEndY(54*row + 3*96);
					enemies.add(bat);
					levelPlatforms.getChildren().add(bat);
					break;
					//Rats
				case '8':
					Rat rat = new Rat(new ImageView(Resources.RAT), new Rectangle(0,30,45,20));
					rat.relocate(96 *col + Math.random()*96*2, 54 * row);
					rat.setPatrolStartX(96*col);
					rat.setPatrolEndX(96*col + 2*96);
					rat.setxVelocity(40);
					rat.graphic().setScaleX(-1);
					enemies.add(rat);
					levelPlatforms.getChildren().add(rat);
					break;
					//Braziers
				case '9':
					Brazier brazier = new Brazier(new ImageView(Resources.BRAZIER), new Rectangle(0,0,96,54));
					brazier.relocate(96 * col, 54 * row);
					braziers.add(brazier);
					levelPlatforms.getChildren().add(brazier);
					break;
					//Haze
				case 'a':
					Sprite haze = new Platform(new ImageView(Resources.HAZE), new Rectangle(0,0,96,54));
					haze.relocate(96 * col, 54 * row);
					levelTransitions.add(haze);
					levelPlatforms.getChildren().add(haze);
					break; 
					//'B' is for Boss
				case 'b':
					currentBoss = new Boss(new ImageView(Resources.BOSS), new Rectangle(0,0,309,504));
					currentBoss.relocate(96 * col, 54 * row);
					enemies.add(currentBoss);
					levelPlatforms.getChildren().add(currentBoss);
					break;
					//Eyes in dark
				case 'c':
					break;
					//Rocks in dark
				case 'd':
					break;
				}
			}
		}
		return new Group (levelPlatforms);		
	}
}