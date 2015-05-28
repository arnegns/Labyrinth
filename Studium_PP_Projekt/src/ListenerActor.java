import java.util.ArrayDeque;

import javax.swing.JFrame;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.IndirectActorProducer;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ListenerActor extends UntypedActor {
	
	private long startTime;
	private long endTime;
	
	private final Labyrinth labyrinth;
	private final byte[][] passages;
	private final Point start;
	private final Point end;
	private Point[] solution;
	
	final public JFrame frame;
	
	public void preStart() {
//		final ArrayDeque<Point> pathSoFar = new ArrayDeque<Point>();
//		//pathSoFar.push(start);
//		final boolean[][] visited = new boolean[passages[0].length][passages[1].length];
		startTime = System.currentTimeMillis();
//		ActorRef master = getContext().actorOf(
//				Props.create(MasterActorSteffen.class, pathSoFar, start, end, passages, visited, getSelf()));
//				//master.tell(this, getSelf());
	}
	
	public void onReceive(Object message) {
		if (message instanceof Integer) {
						
			getContext().system().shutdown();
		} else if (message instanceof String) {
			System.out.println("Danke");
		} else if(message instanceof Point[]) {
			getContext().system().shutdown();
			endTime = System.currentTimeMillis();
			
			solution = (Point[]) message;
			
			show();
			System.out.println("Computed sequential solution of length " + solution.length + " to labyrinth of size " + 
					passages[0].length + "x" + passages[1].length + " in " + (endTime - startTime) + "ms.");
			
//			if (labyrinth.smallEnoughToDisplay()) {
//				labyrinth.displaySolution(frame);
//			    labyrinth.printSolution();
//			}

			if (checkSolution())
				System.out.println("Solution correct :-)"); 
			else
				System.out.println("Solution incorrect :-(");
			
		}
		else {
			unhandled(message);			
		}
	}
	
	protected boolean checkSolution() {
		Point from = solution[0];
		if (!from.equals(start)) {
			System.out.println("checkSolution fails because the first cell is" + from + ", but not  " + start);
			return false;
		}

		for (int i = 1; i < solution.length; ++i) {
			Point to = solution[i];
			if (!hasPassage(from, to)) {
				System.out.println("checkSolution fails because there is no passage from " + from + " to " + to);
				return false;
			}
			from = to;
		}
		if (!from.equals(end)) {
			System.out.println("checkSolution fails because the last cell is" + from + ", but not  " + end);
			return false;
		}
		return true;
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
		return 0 <= p.getX() && p.getX() < passages[0].length && 
			   0 <= p.getY() && p.getY() < passages[1].length;
	}
	
	ListenerActor(Labyrinth labyrinth, byte[][] passages, Point start, Point end, Point[] solution, JFrame frame) {
		this.labyrinth = labyrinth;
		this.passages = passages;
		this.start = start;
		this.end = end;
		this.solution = solution;
		this.frame = frame;
	}
	
	private void show() {
		System.out.println();
		System.out.println("----------Zeige Ergebnis----------");
		System.out.println("Start: " + start);
		System.out.println("Ende: " + end);
		System.out.println("Länge von pathsoFar: " + solution.length);
		int i = 1;
		for (Point p : solution) {
			System.out.println(i+". Element: " + p);
			i++;
		}
		System.out.println("----------Zeige Ergebnis Ende----------");
		System.out.println();
	}
}