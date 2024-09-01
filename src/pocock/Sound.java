package pocock;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

//only mp3
public class Sound{
private Media imageSound;
private MediaPlayer sound;
	public Sound (String fileLocation) {
		imageSound = new Media (new File (getClass().getResource(fileLocation).getPath()).toURI().toString());
		sound = new MediaPlayer(imageSound);
	}
	//use this to access the methods in the media player class
	public MediaPlayer sound() {
		return sound;
	}
	/*
	 * cool methods such as:
	 * 
	 * -setAutoPlay(boolean)
	 *  -plays the sound immediately, maybe repeats?
	 * -play()
	 *  - plays the sound once
	 *  -setVolume()
	 *  -stop()
	 */
}
