import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class Test {

	public static void main(String[] args) {
		PathNode<Point> test = new PathNode<Point>(new Point(0,0));
		PathNode<Point> test2 = test.addWay(new Point(1,1));
		PathNode<Point> test3 = test2.addWay(new Point(2,2));
		ArrayDeque<Point> holePath = test3.getPath();

		Iterator<Point> li = holePath.iterator();
		while(li.hasNext()) {
			System.out.println(li.next());
		}
	}
}
