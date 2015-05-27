import java.util.ArrayDeque;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.dsl.Inbox.Get;

public class MasterActor extends UntypedActor {

	ArrayDeque<Point> pathSoFar = new ArrayDeque<Point>();
	
	@Override
	public void onReceive(Object message) throws Exception {
		
		if ( message instanceof ListenerActor ) {
			ActorRef reporter = getSender();
			reporter.tell(new Integer(2), getSelf());
			
		} else {
			unhandled(message);
		}
	}
}
