import javafx.scene.canvas.GraphicsContext;

public class ObjectFactory implements Factory{ //Defines factory and provides concrete implementation of interface methods

	private GraphicsContext gc;
	//Constructor populates graphicscontext field 
	public ObjectFactory(GraphicsContext gc) {
		this.gc = gc; //canvas to draw objects onto
	}
	
	@Override //Concrete implementation of Factory interface method
	public GameObject createProduct(String imageName, double x, double y, double sizeX, double sizeY, IntersectShape shape) {
		if(imageName.equals("asteroid.png")) { //creates asteroid/planet based on arguments passed
			return new Asteroid(imageName, gc, x, y, shape);
		} else {
			return new Planets(imageName, gc, x, y, sizeX, sizeY, shape);
		}
	}

}
