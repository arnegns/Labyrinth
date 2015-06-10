import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Arrays;


public final class CheckMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public final Point start;
	public final Point end;
	public final byte[][] passages;
	public final ArrayDeque<Point> pathSoFar;
	public final boolean[][] visited;
	
	public CheckMessage(Point start, Point end, byte[][] passages, ArrayDeque<Point> pathSoFar, boolean[][] visited) {
		this.start = start;
		this.end = end;
		this.passages = passages;
		this.pathSoFar = pathSoFar;
		this.visited = visited;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + Arrays.hashCode(passages);
		result = prime * result
				+ ((pathSoFar == null) ? 0 : pathSoFar.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + Arrays.hashCode(visited);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CheckMessage other = (CheckMessage) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (!Arrays.deepEquals(passages, other.passages))
			return false;
		if (pathSoFar == null) {
			if (other.pathSoFar != null)
				return false;
		} else if (!pathSoFar.equals(other.pathSoFar))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (!Arrays.deepEquals(visited, other.visited))
			return false;
		return true;
	}
}
