import java.util.ArrayList;
import java.util.Collections;


public final class PathNode<T> {

    public T way;
    public PathNode<T> before = null;

    public PathNode(T way) {
        this.way = way;
    }
    
    public PathNode() {
    	
    }

    public PathNode<T> addWay(T way) {
    	PathNode<T> newWay = new PathNode<T>(way);
    	newWay.before = this;
        return newWay;
    }

	public final ArrayList<T> getPath() {
    	PathNode<T> next = this;
    	ArrayList<T> holePath = new ArrayList<T>();
    	
    	while(next.before != null) {
    		holePath.add((T) next.way);
    		next = next.before;
    	}
    	holePath.add((T) next.way);
    	Collections.reverse(holePath);
		return holePath;
    }
	
	public String toString() {
		return way.toString();
	}
}