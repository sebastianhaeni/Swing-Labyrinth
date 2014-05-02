package labyrinth;

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
