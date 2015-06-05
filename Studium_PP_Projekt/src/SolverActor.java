
import java.util.ArrayDeque;

import akka.actor.UntypedActor;


public class SolverActor extends UntypedActor {
	
	private final Point start;
	private final Point end;
	private final byte[][] passages;
	private final boolean[][] visited;
	private PathNode<Point> pathSoFar;
	private final int labyrinthWidth;
	private final int labyrinthHeigth;
	
	public SolverActor(Point start, Point end, byte[][] passages, boolean[][] visited, PathNode<Point> pathSoFar) {
		this.start = start;
		this.end = end;
		this.passages = passages;
		this.visited = visited;
		this.pathSoFar = pathSoFar;
		this.labyrinthWidth = passages[0].length;
		this.labyrinthHeigth = passages[1].length;
	}
	
	public void preStart() {		
		solve();
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof AbortMessage) {
			getContext().stop(getSelf());
		} else {
			unhandled(msg);
		}
	}
	
	public void solve() {
		boolean isbreak = false;
		Point current = start;

		PathNode<Point> newPath = new PathNode<Point>();
		while (!current.equals(end)) {
			Point next = null;

			visit(current);
			pathSoFar = pathSoFar.addWay(current);

			// Use first random unvisited neighbor as next cell, push others on the backtrack stack: 
			Direction[] dirs = Direction.values();
			for (Direction directionToNeighbor: dirs) {
				Point neighbor = current.getNeighbor(directionToNeighbor);
				if (hasPassage(current, neighbor) && !visitedBefore(neighbor)) {
					if (next == null) { // 1st unvisited neighbor
						next = neighbor;
					} else {
						getContext().parent().tell(new CheckMessage(
								neighbor, 
								end, 
								passages, 
								pathSoFar, 
								visited.clone()), 
								getSelf());
					}
				}
			}
			// Advance to next cell, if any:
			if (next != null) {
				current = next;
			} else { 				
				isbreak = true;
				getSelf().tell(new AbortMessage(), getSelf());
				break;
			}
		}

		if(!isbreak) {
			pathSoFar = pathSoFar.addWay(current);
			getContext().parent().tell(new ResultMessage(pathSoFar.getPath()), getSelf());
			getSelf().tell(new AbortMessage(), getSelf());
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
	
	
}
