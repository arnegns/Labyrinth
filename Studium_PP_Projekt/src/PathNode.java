import java.util.ArrayDeque;

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
    
	public final ArrayDeque<T> getPath() {
    	PathNode<T> next = this;
    	ArrayDeque<T> holePath = new ArrayDeque<T>();
    	
    	while(next.before != null) {
    		holePath.addFirst((T) next.way);
    		next = next.before;
    	}

		return holePath;
    }
	
	public String toString() {
		return way.toString();
	}
}