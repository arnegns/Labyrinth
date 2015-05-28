import java.util.ArrayDeque;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;


public class Master extends UntypedActor{

	private final ArrayDeque<Point> pathSoFar;
	private Point start;
	private Point end;
	private final byte[][] passages;
	private final boolean[][] visited;
	private final int labyrinthWidth;
	private final int labyrinthHeigth;
	private final ActorRef listener;

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof String) {
			solve();
		}
		else {
			unhandled(message);
		}

	}
	public Master(Properties prop, ActorRef listener) {
		pathSoFar = prop.getPathSoFar();
		start = prop.getStart();
		end = prop.getEnd();
		passages = prop.getPassages();
		visited = prop.getVisited();
		labyrinthHeigth = prop.getLabyrinthHeigth();
		labyrinthWidth = prop.getLabyrinthWidth();
		this.listener = listener;				
	}

	public void solve(){
		//show();
		boolean isbreak = false;
		Point current = start;
		

		while (!current.equals(end)) {
			Point next = null;
			visit(current);
			pathSoFar.addLast(current);

			// Use first random unvisited neighbor as next cell, push others on the backtrack stack: 
			Direction[] dirs = Direction.values();
			for (Direction directionToNeighbor: dirs) {
				Point neighbor = current.getNeighbor(directionToNeighbor);
				if (hasPassage(current, neighbor) && !visitedBefore(neighbor)) {
					if (next == null){ // 1st unvisited neighbor
						next = neighbor;
					}
					else {
						ActorRef worker = getContext().actorOf(
								Props.create(Master.class,toProp(neighbor),listener));
						worker.tell("solve",getSelf());
					}
				}
			}
			// Advance to next cell, if any:
			if (next != null) {
				current = next;
			} else { 

				//System.out.println("Stop MasterActorSteffen");				
				isbreak = true;
				getContext().stop(getSelf());
				break;
			}
		}
		//show();
		if(!isbreak) {
			pathSoFar.addLast(current);
			Point[] result = pathSoFar.toArray(new Point[0]); 
			listener.tell(result , getSelf());
			getContext().stop(getSelf());
		}
	}

	private void visit(Point p) {
		// DEBUG System.out.println("Visiting " + p);
		visited [p.getX()][p.getY()] = true;
	}

	private boolean visitedBefore(Point p) {
		boolean result = visited[p.getX()][p.getY()];
		/*//DEBUG
		if (result)
			System.out.println("Node " + p + " already visited.");*/
		return result;
	}

	private boolean hasPassage(Point from, Point to) {
		if (!contains(from) ||  !contains(to)) {
			return false;
		}
		if (from.getNeighbor(Direction.N).equals(to))
			return (passages[from.getX()][from.getY()] & Direction.N.bit) != 0;
		if (from.getNeighbor(Direction.S).equals(to))
			return (passages[from.getX()][from.getY()] & Direction.S.bit) != 0;
		if (from.getNeighbor(Direction.E).equals(to))
			return (passages[from.getX()][from.getY()] & Direction.E.bit) != 0;
		if (from.getNeighbor(Direction.W).equals(to))
			return (passages[from.getX()][from.getY()] & Direction.W.bit) != 0;
		return false;  // To suppress warning about undefined return value
	}

	private boolean contains(Point p) {
		return 0 <= p.getX() && p.getX() < labyrinthWidth && 
				0 <= p.getY() && p.getY() < labyrinthHeigth;
	}

	private void show() {
		System.out.println();
		System.out.println("----------PreStart Actor wurde erzeugt----------");
		System.out.println("Start: " + start);
		System.out.println("Ende: " + end);
		System.out.println("Länge von pathsoFar: " + pathSoFar.size());
		int i = 1;
		for (Point p : pathSoFar) {
			System.out.println(i+". Element: " + p);
			i++;
		}
		System.out.println("----------Show Ende----------");
		System.out.println();
	}

	public Properties toProp(Point current) {
		return new Properties(pathSoFar.clone(), current, end, passages.clone(), visited.clone(), labyrinthWidth, labyrinthHeigth);
	}

}
