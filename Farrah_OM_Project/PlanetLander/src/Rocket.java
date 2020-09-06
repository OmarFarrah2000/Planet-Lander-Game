import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Rocket extends GameObject{ //Singleton class for the Rocket Game Object - Can only be one of this object

	private static Rocket instance = null; //static field to hold instance of this class
	
	private Rocket(GraphicsContext gc, double x, double y, IntersectShape shape) { //Private constructor (can only be called by getInstance).
		super(gc, x, y, 50, 30, shape); //create rocket object
		img = new Image("resources/rocketleft.png");
	}
	
	public static Rocket getInstance(GraphicsContext gc, double x, double y, IntersectShape shape) {
		if(instance == null) { //If no instance of class create one and assign to static field
			instance = new Rocket(gc, x, y, shape);
		}
		return instance; //if one exists return static field
	}

	
	//Methods to move the rocket in every direction
	public void moveLeft() { //Move rocket left
		setSize(50, 30); //update object size
		img = new Image("resources/rocketleft.png"); //update object image
		if(x > 0) { //update objects position on screen
			x -= 2.3;
		}
	}
	
	public void moveRight() { //Move rocket right
		setSize(50, 30);
		img = new Image("resources/rocketright.png");
		if(x < 1450) {
			x += 2.3;
		}
	}
	
	public void moveUp() { //Move rocket up
		setSize(30, 50);
		img = new Image("resources/rocketup.png");
		if(y > 50) {
			y -= 1.5;
		}
	}
	
	public void moveDown() { //Move rocket down
		setSize(30, 50);
		img = new Image("resources/rocketdown.png");
		if(y < 450) {
			y += 1.5;
		}
	}
	
	//Resets rocket position (to left-hand side of screen) - Round reset
	public void resetPosition() {
		x = 1400;
		y = 100;
		moveLeft();
	}
	
	
}
