import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

public abstract class GameObject { //Game object super class holding shared properties of a game object.
	protected Image img; 
	protected double x, y; //Stores position on canvas
	protected double sizeX, sizeY; //Stores size of object
	protected GraphicsContext gc; 
	protected IntersectShape shape; //reference to the type of shape the object will draw (SquareIntersect, CircleIntersect)
	
	//populates object fields
	public GameObject(GraphicsContext gc, double x, double y, double sizeX, double sizeY, IntersectShape shape) {
		this.gc = gc;
		this.x = x;
		this.y = y;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.shape = shape;
	}
	
	public void update() { //If image set then update on canvas
		if(img != null) {
			gc.drawImage(img, x, y, sizeX ,sizeY);
		}
	}
	
	//Return object width
	public double getSizeX() {
		return sizeX;
	}
	
	//Return object height
	public double getSizeY() {
		return sizeY;
	}
	
	//method to change the size of the object (mainly for spaceship when moving around)
	public void setSize(double sizeX, double sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	//return intersect shape for object
	public Shape getIntersectShape() {
		return shape.drawIntersectShape(sizeX, sizeY, x, y);
	}
}
