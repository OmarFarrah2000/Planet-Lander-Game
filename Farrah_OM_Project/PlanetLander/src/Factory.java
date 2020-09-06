
public interface Factory { //interface defining abstract method for creating objects
	GameObject createProduct(String imageName, double x, double y, double sizeX, double sizeY, IntersectShape shape);
}
