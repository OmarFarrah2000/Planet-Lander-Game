import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Asteroid extends GameObject{ //Class defining the properties/methods of an asteroid

	private double speed = 2; //Asteroid speed
	
	public Asteroid(String imageName, GraphicsContext gc, double x, double y, IntersectShape shape) {
		super(gc, x, y, 25, 25, shape); //pass relevant arguments to constructor in super class 
		img = new Image("resources/"+imageName); //set image
		update();
	}
	
	@Override //Move the asteroid on update
	public void update() {
		super.update();
		if(y < 70 || y > 455) { //if touching top/bottom of game area invert speed
			speed = -speed;
		}
		y += speed;
	}
	
	//Change speed of asteroid
	public void setSpeed(double speed) {
		this.speed = speed;
	}

}
