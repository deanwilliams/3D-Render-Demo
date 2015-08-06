import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Scene {

	private List<Triangle> tris;

	public Scene() {
		tris = new ArrayList<>();

		tris.add(new Triangle(new Vertex(100, 100, 100), new Vertex(-100, -100,
				100), new Vertex(-100, 100, -100), Color.WHITE));
		tris.add(new Triangle(new Vertex(100, 100, 100), new Vertex(-100, -100,
				100), new Vertex(100, -100, -100), Color.RED));
		tris.add(new Triangle(new Vertex(-100, 100, -100), new Vertex(100,
				-100, -100), new Vertex(100, 100, 100), Color.GREEN));
		tris.add(new Triangle(new Vertex(-100, 100, -100), new Vertex(100,
				-100, -100), new Vertex(-100, -100, 100), Color.BLUE));
	}

	public List<Triangle> getTris() {
		return tris;
	}

}
