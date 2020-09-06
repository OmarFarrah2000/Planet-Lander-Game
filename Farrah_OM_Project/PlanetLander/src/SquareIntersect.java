import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class SquareIntersect implements IntersectShape{//Defines SquareIntersect class
	//concrete implementation of interface method
	@Override
	public Shape drawIntersectShape(double width, double height, double x, double y) {
		return new Rectangle(x,y,width,height); //returns square of correct size/position for the object it mimics
	}

}
