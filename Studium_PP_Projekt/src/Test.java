import java.util.List;
import java.util.ListIterator;


public class Test {

	public static void main(String[] args) {
		Point b = new Point(1,2);
		PathNode<Point> test = new PathNode<Point>(new Point(0,0));
		PathNode<Point> test2 = test.addWay(new Point(1,1));
		PathNode<Point> test3 = test2.addWay(new Point(2,2));
		List<Point> holePath = test3.getPath();

		Point[] myPoints = holePath.toArray(new Point[0]);
		System.out.println(myPoints);

		ListIterator<Point> li = holePath.listIterator();
		while(li.hasNext()) {
			System.out.println(li.next());
		}
	}
}
