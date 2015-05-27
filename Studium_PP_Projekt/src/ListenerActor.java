import java.util.ArrayDeque;

import javax.swing.JFrame;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.IndirectActorProducer;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ListenerActor extends UntypedActor implements IndirectActorProducer {
	
	Labyrinth labyrinth = null;
	private long startTime;
	private long endTime;
	byte[][] passages = null;
	final Point start;
	final Point end;
	Point[] solution = null;
	public final JFrame frame;
	
	public void preStart() {
		
		final ArrayDeque<Point> pathSoFar = new ArrayDeque<Point>();
		final boolean[][] visited = new boolean[passages[0].length][passages[1].length];
		startTime = System.currentTimeMillis();
		ActorRef master = getContext().actorOf(
				Props.create(MasterActor.class,pathSoFar,start,end,passages,visited));
				master.tell(this, getSelf());
	}
	
	public void onReceive(Object message) {
		if (message instanceof Integer) {
			
			//System.out.println(labyrinth);
			
			//solution = labyrinth.solve();
			
			endTime = System.currentTimeMillis();
			System.out.println("Computed sequential solution of length " + //solution.length + " to labyrinth of size " + 
					passages[0].length + "x" + passages[1].length + " in " + (endTime - startTime) + "ms.");
			
			if (labyrinth.smallEnoughToDisplay()) {
				labyrinth.displaySolution(frame);
			    labyrinth.printSolution();
			}

			/**
			 * Wenn du das hier einkommentierst kommen ganz viele null-pointer. das liegt daran das wir das labyrinth
			 * nicht, bzw. das grid glaub ich nicht bekommen. 
			 * doof ist auch das wir das gesamte grid auch nicht in den actor bekommen, deshalb hab ich start und end und so ruebergezogen
			 * das ist alles irgendwie bescheiden. hast du eine bessere idee?
			 */
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

	@Override
	public Class<? extends Actor> actorClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Actor produce() {
		// TODO Auto-generated method stub
		return null;
	}
}