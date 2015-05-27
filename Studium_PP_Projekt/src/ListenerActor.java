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
	private final Point[] solution;
	
	final public JFrame frame;
	
	public void preStart() {
		final ArrayDeque<Point> pathSoFar = new ArrayDeque<Point>();
		final boolean[][] visited = new boolean[passages[0].length][passages[1].length];
		startTime = System.currentTimeMillis();
		ActorRef master = getContext().actorOf(
				Props.create(MasterActor.class, pathSoFar, start, end, passages, visited));
				master.tell(this, getSelf());
	}
	
	public void onReceive(Object message) {
		if (message instanceof Integer) {
			
			//solution = labyrinth.solve();
			
			endTime = System.currentTimeMillis();
			System.out.println("Computed sequential solution of length " + /* solution.length + */ " to labyrinth of size " + 
					passages[0].length + "x" + passages[1].length + " in " + (endTime - startTime) + "ms.");
			
			if (labyrinth.smallEnoughToDisplay()) {
				labyrinth.displaySolution(frame);
			    labyrinth.printSolution();
			}

//			if (labyrinth.checkSolution())
//				System.out.println("Solution correct :-)"); 
//			else
//				System.out.println("Solution incorrect :-(");			
			
			getContext().system().shutdown();
		} else if (message instanceof String) {
			System.out.println("Danke");
		} else {
			unhandled(message);			
		}
	}
	
	ListenerActor(Labyrinth labyrinth, byte[][] passages, Point start, Point end, Point[] solution, JFrame frame) {
		this.labyrinth = labyrinth;
		this.passages = passages;
		this.start = start;
		this.end = end;
		this.solution = solution;
		this.frame = frame;
	}
}