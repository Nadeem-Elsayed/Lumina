package pocock;

import javafx.scene.image.Image;

public class Resources {

	//ui
	public static final Image TITLE_BACKGROUND = new Image("file:images/homeScreen.png");
	public static final Image SKULL = new Image("file:images/skull.png");

	//Player Images
	public static final Image PLAYER = new Image("file:images/skippySprite.png");
	public static final Image[] CURSOR = {
			new Image("file:images/flame1.png"),
			new Image("file:images/flame2.png"),
			new Image("file:images/flame3.png"),
			new Image("file:images/flame4.png"),
			new Image("file:images/flame5.png"),
	};
	public static final Image SKELETON = new Image("file:images/corpse.png");

	//backgrounds
	public static final Image BACKGROUND1 = new Image ("file:images/level1Background.png");
	public static final Image BACKGROUND2 = new Image ("file:images/level2Background.png");
	public static final Image BACKGROUND3 = new Image ("file:images/level4Back.png");
	public static final Image BACKGROUND4 = new Image ("file:images/level3Back.png");
	
	//enemy Sprites
	public static final Image BAT = new Image("file:images/batSprite.png");
	public static final Image RAT = new Image("file:images/ratSprite.png");
	public static final Image BOSS = new Image("file:images/slimeMonsterGloop.png");

	//tiles Images
	public static final Image PLATFORM = new Image("file:images/floor.png");
	public static final Image LEFT_PLATFORM = new Image("file:images/leftFloor.png");
	public static final Image RIGHT_PLATFORM = new Image("file:images/rightFloor.png");
	public static final Image BOTTOM_PLATFORM = new Image("file:images/bottomFloor.png");
	public static final Image TOP_PLATFORM = new Image("file:images/topFloor.png");
	public static final Image SPIKES = new Image("file:images/spikes.png");
	public static final Image BRAZIER = new Image("file:images/braziersSprite.png");
	public static final Image HAZE = new Image("file:images/haze.png");
	public static final Image EYES_IN_DARK = new Image("file:images/rocksInDark.png");
	public static final Image ROCKS_IN_DARK = new Image("file:images/eyesInDark.png");

	//Sounds
	public static final Sound RAT_BAT = new Sound("BatOrRatSound.mp3");
	public static final Sound BACKGROUND_MUSIC = new Sound ("gameSong.mp3");
	public static final Sound BRAZIERON = new Sound ("BrazierOn.mp3");
	public static final Sound CALM_SONG = new Sound ("harp.mp3");

	//fonts
}
