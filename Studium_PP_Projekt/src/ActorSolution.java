import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;


public class ActorSolution {

	private final byte[][] passages;
	private final Point start;
	private final Point end;
	private ActorSystem system;
	
	public CompletableFuture<Point[]> fut = new CompletableFuture<Point[]>();
	
	public ActorSolution(Point start, Point end, byte[][] passages) {
		system = ActorSystem.create("LabyrinthSystem");
		system.eventStream().setLogLevel(0);
		
		this.start = start;
		this.end = end;
		this.passages = passages;
	}

	public Point[] solve() {
		final ActorRef result = system.actorOf(Props.create(ResultActor.class, 
				start, 
				end, 
				passages, fut));
		
		Point[] solution = null;
		
		try {
			solution = fut.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return solution;
	}
}
