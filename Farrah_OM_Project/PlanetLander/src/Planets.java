import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

public class Planets extends GameObject{
	private String imageName;

	//Constructor creates planet object, passing valid arguments to super class
	public Planets(String imageName, GraphicsContext gc, double x, double y, double sizeX, double sizeY, IntersectShape shape) {
		super(gc, x, y, sizeX, sizeY, shape);
		img = new Image("resources/"+imageName);
		this.imageName = imageName;
		update();
	}
	
	//Method that returns the name of the planet (determining good/bad planets)
	public String getPlanetImage() {
		return imageName;
	}
	
	//Overrides method in GameObject super class (for saturn object)
	@Override
	public Shape getIntersectShape() {
		if(imageName.equals("saturn.png")) { //if object is saturn use different object size
			return shape.drawIntersectShape(120, sizeY, x+40, y);
		}else { //otherwise call gameObject getIntersectShape
			return super.getIntersectShape(); 
		}
		
	}

}
