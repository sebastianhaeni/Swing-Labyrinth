package labyrinth;

/**
 * Coordinate with x and y.
 * 
 * @author Sebastian Häni <haeni.sebastian@gmail.com>
 * 
 */
public class Coordinate {
	private int _x;
	private int _y;

	public Coordinate(int x, int y) {
		_x = x;
		_y = y;
	}

	public int getX() {
		return _x;
	}

	public int getY() {
		return _y;
	}

	/**
	 * Checks if the coordinates are positive and not under zero.
	 * 
	 * @return
	 */
	public boolean isValid() {
		return getX() >= 0 && getY() >= 0;
	}

	public boolean equals(Coordinate other) {
		return getX() == other.getX() && getY() == other.getY();
	}

	@Override
	public String toString() {
		return String.format("x: %d y: %d", _x, _y);
	}
}
