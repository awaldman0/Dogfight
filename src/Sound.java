

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    public static AudioInputStream explosion;
    //explosion sound from https://samplefocus.com/samples/8-bit-explosion
    public static AudioInputStream playerHit;
    //hit sound from https://samplefocus.com/samples/8-bit-kit-snare-8-quirky-8-bit-kit
    public static AudioInputStream playerLose;
    //lose sound from https://samplefocus.com/samples/8-bit-down-sweep
    public static AudioInputStream startSound;
    //startsound from https://samplefocus.com/samples/dubstep-color-bass-8-bit-rise-effect

    public static void playStartSound() {  
		try { 
	        AudioInputStream startSound = AudioSystem.getAudioInputStream(Sound.class.getClassLoader().getResource("rise.wav"));
	        
	        Clip clip = AudioSystem.getClip();
	        clip.open(startSound);
	        clip.start();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
		
	}
	
    public static void playExplosionSound() {  
		try { 
	        AudioInputStream explosion = AudioSystem.getAudioInputStream(Sound.class.getClassLoader().getResource("explosion.wav"));
	        
	        Clip clip = AudioSystem.getClip();
	        clip.open(explosion);
	        clip.start();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
		
	}
	
	public static void playHitSound() {  
		try { 
	        AudioInputStream playerHit = AudioSystem.getAudioInputStream(Sound.class.getClassLoader().getResource("hit.wav"));
	        
	        Clip clip = AudioSystem.getClip();
	        clip.open(playerHit);
	        clip.start();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
		
	}
	
	public static void playLoseSound() {  
		try { 
	        AudioInputStream playerLose = AudioSystem.getAudioInputStream(Sound.class.getClassLoader().getResource("sweep.wav"));
	        
	        Clip clip = AudioSystem.getClip();
	        clip.open(playerLose);
	        clip.start();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
		
	}
}