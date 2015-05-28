import java.util.ArrayDeque;


public class Properties {
	
	private ArrayDeque<Point> pathSoFar;
	private Point start;
	private Point end;
	private byte[][] passages;
	private boolean[][] visited;
	private int labyrinthWidth;
	private int labyrinthHeigth;
	
	public Properties(ArrayDeque<Point> pathSoFar, Point start, Point end, byte[][] passages, boolean[][] visited, int width, int height) {
		setPathSoFar(pathSoFar);
		setStart(start);
		setEnd(end);
		setPassages(passages);
		setVisited(visited);
		setLabyrinthHeigth(height);
		setLabyrinthWidth(width);
	}
	
	public ArrayDeque<Point> getPathSoFar() {
		return pathSoFar;
	}
	public void setPathSoFar(ArrayDeque<Point> pathSoFar) {
		this.pathSoFar = pathSoFar;
	}
	public Point getStart() {
		return start;
	}
	public void setStart(Point start) {
		this.start = start;
	}
	public Point getEnd() {
		return end;
	}
	public void setEnd(Point end) {
		this.end = end;
	}
	public byte[][] getPassages() {
		return passages;
	}
	public void setPassages(byte[][] passages) {
		this.passages = passages;
	}
	public boolean[][] getVisited() {
		return visited;
	}
	public void setVisited(boolean[][] visited) {
		this.visited = visited;
	}
	public int getLabyrinthWidth() {
		return labyrinthWidth;
	}
	public void setLabyrinthWidth(int labyrinthWidth) {
		this.labyrinthWidth = labyrinthWidth;
	}
	public int getLabyrinthHeigth() {
		return labyrinthHeigth;
	}
	public void setLabyrinthHeigth(int labyrinthHeigth) {
		this.labyrinthHeigth = labyrinthHeigth;
	}

}
