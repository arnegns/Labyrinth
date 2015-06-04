import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;


public final class ResultMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	public final ArrayDeque<Point> pathSoFar;
	
	public ResultMessage(ArrayDeque<Point> arrayDeque) {
		this.pathSoFar = arrayDeque;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pathSoFar == null) ? 0 : pathSoFar.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultMessage other = (ResultMessage) obj;
		if (pathSoFar == null) {
			if (other.pathSoFar != null)
				return false;
		} else if (!pathSoFar.equals(other.pathSoFar))
			return false;
		return true;
	}
}
