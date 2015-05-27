import java.util.ArrayDeque;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class PathActor extends UntypedActor {

	ArrayDeque<Point> pathSoFar = new ArrayDeque<Point>();
	
	@Override
	public void onReceive(Object arg0) throws Exception {
		System.out.println("BLUBBBBBBB");
		
		ActorRef reporter = getSender();
				reporter.tell(new Object(), getSelf());
		
	}
}
