import java.util.ArrayDeque;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.dsl.Inbox.Get;

public class MasterActor extends UntypedActor {

	ArrayDeque<Point> pathSoFar = new ArrayDeque<Point>();
	Point start;
	Point end;
	byte[][] passages = null;
	final boolean[][] visited = null;
	
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
					else // 2nd or higher unvisited neighbor: Save neighbor as starting cell for a later backtracking
						backtrackStack.push(new PointAndDirection(neighbor, directionToNeighbor.opposite));
				}
			}
			// Advance to next cell, if any:
			if (next != null) {
				// DEBUG System.out.println("Advancing from " + current + " to " + next);
				pathSoFar.addLast(current);
				current = next;
			} else { 
				// current has no unvisited neighbor: Backtrack, if possible
				if (backtrackStack.isEmpty())
					return null; // No more backtracking avaible: No solution exists

				// Backtrack: Continue with cell saved at latest branching point:
				PointAndDirection pd = backtrackStack.pop();
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
	
	private void visit(Point p) {
		// DEBUG System.out.println("Visiting " + p);
		visited [p.getX()][p.getY()] = true;
	}
	
	public MasterActor(ArrayDeque<Point> psf, Point start, Point end, byte[][] passages) {
		this.pathSoFar = psf;
		this.start = start;
		this.end = end;
		this.passages = passages;
	}
}
