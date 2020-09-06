import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class CircleIntersect implements IntersectShape{ //Defines CircleIntersect class
	//concrete implementation of interface method
	@Override
	public Shape drawIntersectShape(double width, double height, double x, double y) {
		return new Circle(x+(width/2), y+(height/2), width/2); //returns circle of correct size/position for the object it mimics
	}

}
