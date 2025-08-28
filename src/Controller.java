import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Character;

public class Controller implements KeyListener {
	private static boolean KeyAPressed = false;
	private static boolean KeySPressed = false;
	private static boolean KeyDPressed = false;
	private static boolean KeyWPressed = false;
	private static boolean KeySpacePressed = false;
	
	private static boolean KeyXPressed = false;
	private static boolean KeyCPressed = false;
	private static boolean KeyVPressed = false;

	private static boolean KeyJPressed = false;
	private static boolean KeyKPressed = false;
	private static boolean KeyLPressed = false;
	
	private static boolean KeyLeftBracketPressed = false;
	private static boolean KeyRightBracketPressed = false;
	
	
	private static boolean KeyRPressed = false;
	private static boolean KeyMPressed = false;
	
	
	private static final Controller instance = new Controller();
	   
	public Controller() {}
	 
	public static Controller getInstance() {
		return instance;
	}
	   
	@Override
	// Key pressed , will keep triggering 
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) { 
		switch (e.getKeyChar()) {
			case 'a':setKeyAPressed(true);break;  
			case 's':setKeySPressed(true);break;
			case 'w':setKeyWPressed(true);break;
			case 'd':setKeyDPressed(true);break;
			case ' ':setKeySpacePressed(true);break;   
			case 'x':setKeyXPressed(true);break;
			case 'c':setKeyCPressed(true);break;
			case 'v':setKeyVPressed(true);break;
			case 'j':setKeyJPressed(true);break;
			case 'k':setKeyKPressed(true);break;
			case 'l':setKeyLPressed(true);break;
			case '[':setKeyLeftBracketPressed(true);break;
			case ']':setKeyRightBracketPressed(true);break;
			case 'r':setKeyRPressed(true);break;
			case 'm':setKeyMPressed(true);break;
		    default:
		        break;
		}  
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) { 
		switch (e.getKeyChar()) {
			case 'a':setKeyAPressed(false);break;  
			case 's':setKeySPressed(false);break;
			case 'w':setKeyWPressed(false);break;
			case 'd':setKeyDPressed(false);break;
			case ' ':setKeySpacePressed(false);break; 
			case 'x':setKeyXPressed(false);break;
			case 'c':setKeyCPressed(false);break;
			case 'v':setKeyVPressed(false);break;
			case 'j':setKeyJPressed(false);break;
			case 'k':setKeyKPressed(false);break;
			case 'l':setKeyLPressed(false);break;
			case '[':setKeyLeftBracketPressed(false);break;
			case ']':setKeyRightBracketPressed(false);break;
			case 'r':setKeyRPressed(false);break;
			case 'm':setKeyMPressed(false);break;
		    default:
		    	//System.out.println("Controller test:  Unknown key pressed");
		        break;
		}  
		 //upper case 
	
	}

	public boolean isKeyAPressed() {
		return KeyAPressed;
	}

	public void setKeyAPressed(boolean keyAPressed) {
		KeyAPressed = keyAPressed;
	}

	public boolean isKeySPressed() {
		return KeySPressed;
	}

	public void setKeySPressed(boolean keySPressed) {
		KeySPressed = keySPressed;
	}

	public boolean isKeyDPressed() {
		return KeyDPressed;
	}

	public void setKeyDPressed(boolean keyDPressed) {
		KeyDPressed = keyDPressed;
	}

	public boolean isKeyWPressed() {
		return KeyWPressed;
	}

	public void setKeyWPressed(boolean keyWPressed) {
		KeyWPressed = keyWPressed;
	}

	public boolean isKeySpacePressed() {
		return KeySpacePressed;
	}

	public void setKeySpacePressed(boolean keySpacePressed) {
		KeySpacePressed = keySpacePressed;
	} 	 
	
	public void setKeyXPressed(boolean keyXPressed) {
		KeyXPressed = keyXPressed;
	}
	
	public boolean isKeyXPressed() {
		return KeyXPressed;
	}
	
	public void setKeyCPressed(boolean keyCPressed) {
		KeyCPressed = keyCPressed;
	}
	
	public boolean isKeyCPressed() {
		return KeyCPressed;
	}
	
	public void setKeyVPressed(boolean keyVPressed) {
		KeyVPressed = keyVPressed;
	}
	
	public boolean isKeyVPressed() {
		return KeyVPressed;
	}
	
	public void setKeyJPressed(boolean keyJPressed) {
		KeyJPressed = keyJPressed;
	}
	
	public boolean isKeyJPressed() {
		return KeyJPressed;
	}
	
	public void setKeyKPressed(boolean keyKPressed) {
		KeyKPressed = keyKPressed;
	}
	
	public boolean isKeyKPressed() {
		return KeyKPressed;
	}
	
	public void setKeyLPressed(boolean keyLPressed) {
		KeyLPressed = keyLPressed;
	}
	
	public boolean isKeyLPressed() {
		return KeyLPressed;
	}
	
	public void setKeyLeftBracketPressed(boolean keyLeftBracketPressed) {
		KeyLeftBracketPressed = keyLeftBracketPressed;
	}
	public boolean isKeyLeftBracketPressed() {
		return KeyLeftBracketPressed;
	}
	
	public void setKeyRightBracketPressed(boolean keyRightBracketPressed) {
		KeyRightBracketPressed = keyRightBracketPressed;
	}
	public boolean isKeyRightBracketPressed() {
		return KeyRightBracketPressed;
	}
	
	public void setKeyRPressed(boolean keyRPressed) {
		KeyRPressed = keyRPressed;
	}
	public boolean isKeyRPressed() {
		return KeyRPressed;
	}
	
	public void setKeyMPressed(boolean keyMPressed) {
		KeyMPressed = keyMPressed;
	}
	public boolean isKeyMPressed() {
		return KeyMPressed;
	}
}