import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ResultActor extends UntypedActor {
	
	private long startTime;
	private long endTime;
	
	private final byte[][] passages;
	private final Point start;
	private final Point end;
	private Point[] solution = null;
	
	public ResultActor(Point start, Point end, byte[][] passages) {
		this.passages = passages;
		this.start = start;
		this.end = end;
	}
	
	public void preStart() {
		startTime = System.currentTimeMillis();
		
		ActorRef master = getContext().actorOf(Props.create(MasterActor.class, 
				start, 
				end, 
				passages.clone(), 
				new boolean[passages[0].length][passages[1].length]));
	}

	public void onReceive(Object msg) {
		if (msg instanceof ResultMessage) {
			endTime = System.currentTimeMillis();
			
			ResultMessage result = (ResultMessage) msg;
			solution = result.pathSoFar.toArray(new Point[0]);
			
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
			
			getSelf().tell(new AbortMessage(), getSelf());
		} else if(msg instanceof AbortMessage) {
			getContext().system().shutdown();
		} else {
			unhandled(msg);			
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