import java.util.ArrayDeque;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.dsl.Inbox.Get;

public class MasterActor extends UntypedActor {

	private final ArrayDeque<Point> pathSoFar;
	private Point start;
	private Point end;
	private final byte[][] passages;
	private final boolean[][] visited;
	private final int labyrinthWidth;
	private final int labyrinthHeigth;

	@Override
	public void onReceive(Object message) throws Exception {
		if ( message instanceof ListenerActor ) {
			ActorRef reporter = getSender();
			reporter.tell(new Integer(2), getSelf());
		} else {
			unhandled(message);
		}
	}

	public void preStart() {
		Point current = start;

		while (!current.equals(end)) {
			Point next = null;
			visit(current);

			// Use first random unvisited neighbor as next cell, push others on the backtrack stack: 
			Direction[] dirs = Direction.values();
			for (Direction directionToNeighbor: dirs) {
				Point neighbor = current.getNeighbor(directionToNeighbor);
				if (hasPassage(current, neighbor) && !visitedBefore(neighbor)) {
					if (next == null) // 1st unvisited neighbor
						next = neighbor;
					else {
						// 2nd or higher unvisited neighbor: start a new actor to continue solving
						//backtrackStack.push(new PointAndDirection(neighbor, directionToNeighbor.opposite));
						
						final ActorRef newSolver = getContext().actorOf(
								Props.create(MasterActor.class, pathSoFar, neighbor, end, passages, visited));
					}
				}
			}
			// Advance to next cell, if any:
			if (next != null) {
				// DEBUG System.out.println("Advancing from " + current + " to " + next);
				pathSoFar.addLast(current);
				current = next;
			} else { 
				// current has no unvisited neighbor: Backtrack, if possible
//				if (backtrackStack.isEmpty())
//					return null; // No more backtracking avaible: No solution exists

				// Backtrack: Continue with cell saved at latest branching point:
				PointAndDirection pd = null;//backtrackStack.pop();
				current = pd.getPoint();
				Point branchingPoint = current.getNeighbor(pd.getDirectionToBranchingPoint());
				// DEBUG System.out.println("Backtracking to " +  branchingPoint);
				// Remove the dead end from the top of pathSoFar, i.e. all cells after branchingPoint:
				while (!pathSoFar.peekLast().equals(branchingPoint)) {
					// DEBUG System.out.println("    Going back before " + pathSoFar.peekLast());
					pathSoFar.removeLast();
				}
			}
		}
		pathSoFar.addLast(current);
	}

	public MasterActor(ArrayDeque<Point> pathSoFar, Point start, Point end, byte[][] passages, boolean[][] visited) {
		this.pathSoFar = pathSoFar;
		this.start = start;
		this.end = end;
		this.passages = passages;
		this.labyrinthWidth = passages[0].length;
		this.labyrinthHeigth = passages[1].length;
		this.visited = visited;
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
