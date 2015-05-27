import javax.swing.JFrame;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ListenerActor extends UntypedActor {
	
	private Labyrinth labyrinth = null;
	private long startTime;
	private long endTime;
	private byte[][] passages = null;
	private Point start;
	private Point end;
	private Point[] solution = null;
	public JFrame frame;
	
	public void preStart() {
		startTime = System.currentTimeMillis();
		ActorRef master = getContext().actorOf(
				Props.create(MasterActor.class));
				master.tell(this, getSelf());
	}
	
	public void onReceive(Object message) {
		if (message instanceof Integer) {
			
			solution = labyrinth.solve();
			
			endTime = System.currentTimeMillis();
			System.out.println("Computed sequential solution of length " + solution.length + " to labyrinth of size " + 
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
			if (labyrinth.checkSolution())
				System.out.println("Solution correct :-)"); 
			else
				System.out.println("Solution incorrect :-(");			
			
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