import java.util.ArrayDeque;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;


public class MasterActor extends UntypedActor {

	private final Point start;
	private final Point end;
	private final byte[][] passages;
	private final boolean[][] visited;
	private final ArrayDeque<Point> pathSoFar;
	
	public MasterActor(Point start, Point end, byte[][] passages, boolean[][] visited) {
		this.start = start;
		this.end = end;
		this.passages = passages;
		this.visited = visited;
		pathSoFar = new ArrayDeque<Point>();
	}
	
	public void preStart() {		
		ActorRef solver = getContext().actorOf(Props.create(SolverActor.class, 
				start,
				end,
				passages, 
				visited.clone(),
				pathSoFar.clone()));
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof CheckMessage) {
			CheckMessage check = (CheckMessage) msg;
			ActorRef solver = getContext().actorOf(Props.create(SolverActor.class, 
					check.start,
					check.end,
					passages, 
					check.visited,
					check.pathSoFar));
		}
		else if(msg instanceof ResultMessage) {
			getContext().parent().tell(msg, getSelf());
		} else {
			unhandled(msg);
		}
	}
}
