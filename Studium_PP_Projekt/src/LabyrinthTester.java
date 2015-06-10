import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;


public class LabyrinthTester {
	private final int iterations = 10;
	
	private ArrayList<Class<?>> strategyClasses = new ArrayList<Class<?>>();
	
	private final byte[][] passages;
	private final Point start;
	private final Point end;
	private final int width;
	private final int height;
		
	public LabyrinthTester(byte[][] passages, Point start, Point end) {
		this.passages = passages;
		this.start = start;
		this.end = end;
		
		this.width = passages.length;
		this.height = passages[0].length;

		strategyClasses.add(Actors.class);
		strategyClasses.add(Sequentiell.class);
	}
	
	public void startAllTests() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		for(Class<?> strategyClass : strategyClasses) {
			
			System.out.println("------------------------------------------------------");
			System.out.println( String.format("| %-50s |", strategyClass.getSimpleName()) );
			System.out.println("|----------------|----------------|------------------|");
			System.out.println("| solve()        | Konstruktor    | Laenge d. Loesung|");
			System.out.println("|----------------|----------------|------------------|");
			
			long sum = 0;
			long sumC = 0;
			String warning = "";
            int lengthOfSolution= -1;
            long[] allSolutions = new long[iterations];
			
			for(int x = 0; x < iterations; x++) {
				
				Constructor<?> constructor = strategyClass.getConstructor(byte[][].class, Point.class, Point.class);
				Object[] arguments = {passages, start, end};
				
				long startTimeC = System.currentTimeMillis();
				Strategy strategy = (Strategy) constructor.newInstance(arguments);
				long endTimeC = System.currentTimeMillis();
				
				long startTime = System.currentTimeMillis();
				Point[] solution = strategy.perform();
				long endTime = System.currentTimeMillis();
				
				sum += endTime-startTime;
                allSolutions[x] = endTime-startTime;
				sumC += endTimeC-startTimeC;

                lengthOfSolution = solution.length;

				if(!checkSolution(solution)) {
					warning = "Keine Loesung gefunden!";
				}
				
				System.out.println("| " + String.format("%11d", endTime-startTime) + 
						" ms | " + String.format("%11d", endTimeC-startTimeC) + 
						" ms " + "| " + String.format("%17d", lengthOfSolution) + "|" + warning);
			}
            Arrays.sort(allSolutions);
			System.out.println("|----------------|----------------|------------------|");
			System.out.println("|"+String.format(" Mittelwert: %35d", (sum/iterations)) + " ms |");
            System.out.println("|"+String.format(" Median: %39d", allSolutions[iterations/2]) + " ms |");
            System.out.println("-----------------------------------------------------|");
			System.out.println();
		}
	}
	
	private boolean checkSolution(Point[] solution) {
		Point from = solution[0];
		if (!from.equals(start)) {
			//System.out.println("checkSolution fails because the first cell is" + from + ", but not  " + start);
			return false;
		}

		for (int i = 1; i < solution.length; ++i) {
			Point to = solution[i];
			if (!hasPassage(from, to)) {
				//System.out.println("checkSolution fails because there is no passage from " + from + " to " + to);
				return false;
			}
			from = to;
		}
		if (!from.equals(end)) {
			//System.out.println("checkSolution fails because the last cell is" + from + ", but not  " + end);
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
		return 0 <= p.getX() && p.getX() < width && 
				0 <= p.getY() && p.getY() < height;
	}
}

interface Strategy {
	public Point[] perform();
}

class Actors implements Strategy {
	
	private final ActorSolution solver;

	public Actors(byte[][] passages, Point start, Point end) {
		solver = new ActorSolution(start, end, passages);
	}

	public Point[] perform() {
		return solver.solve();
	}
}

class Sequentiell implements Strategy {
	
	private final SequentiellSolution solver;

	public Sequentiell(byte[][] passages, Point start, Point end) {
		solver = new SequentiellSolution(start, end, passages);
	}

	public Point[] perform() {
		return solver.solve();
	}
}
