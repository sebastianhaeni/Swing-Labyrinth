package labyrinth;

import java.util.ArrayList;

/**
 * 
 * @author Sebastian Häni <haeni.sebastian@gmail.com>
 * 
 */
public class Tile {

	public enum ETileType {
		Wall, Empty
	}

	private ETileType _type;
	private Coordinate _coordinate;
	private boolean _isStart;
	private boolean _isPath;

	public Tile(Coordinate coordinate, char c) {
		_coordinate = coordinate;

		switch (c) {
		case '#':
		case '|':
		case '*':
			_type = ETileType.Wall;
			break;
		case ' ':
		case '.':
		case '_':
			_type = ETileType.Empty;
			break;
		default:
			_type = ETileType.Empty;
		}
	}

	public Tile(Coordinate coordinate, ETileType type) {
		_coordinate = coordinate;
		_type = type;
	}

	public ETileType getType() {
		return _type;
	}

	public Coordinate getCoordinate() {
		return _coordinate;
	}

	public Tile getNeighborLeft(ArrayList<Tile> tiles) {
		return searchTile(new Coordinate(getCoordinate().getX() - 1,
				getCoordinate().getY()), tiles);
	}

	public Tile getNeighborTop(ArrayList<Tile> tiles) {
		return searchTile(new Coordinate(getCoordinate().getX(),
				getCoordinate().getY() - 1), tiles);
	}

	public Tile getNeighborRight(ArrayList<Tile> tiles) {
		return searchTile(new Coordinate(getCoordinate().getX() + 1,
				getCoordinate().getY()), tiles);
	}

	public Tile getNeighborBottom(ArrayList<Tile> tiles) {
		return searchTile(new Coordinate(getCoordinate().getX(),
				getCoordinate().getY() + 1), tiles);
	}

	public Tile getNeighborTopLeft(ArrayList<Tile> tiles) {
		return searchTile(new Coordinate(getCoordinate().getX() - 1,
				getCoordinate().getY() - 1), tiles);
	}

	public Tile getNeighborTopRight(ArrayList<Tile> tiles) {
		return searchTile(new Coordinate(getCoordinate().getX() + 1,
				getCoordinate().getY() - 1), tiles);
	}

	public Tile getNeighborBottomRight(ArrayList<Tile> tiles) {
		return searchTile(new Coordinate(getCoordinate().getX() + 1,
				getCoordinate().getY() + 1), tiles);
	}

	public Tile getNeighborBottomLeft(ArrayList<Tile> tiles) {
		return searchTile(new Coordinate(getCoordinate().getX() - 1,
				getCoordinate().getY() + 1), tiles);
	}

	/**
	 * Searches and returns the tile at the given coordinate.
	 * 
	 * @param coordinate
	 * @param tiles
	 * @return
	 */
	private Tile searchTile(Coordinate coordinate, ArrayList<Tile> tiles) {
		if (!coordinate.isValid()) {
			return new Tile(coordinate, ETileType.Empty);
		}

		for (Tile tile : tiles) {
			if (tile.getCoordinate().equals(coordinate)) {
				return tile;
			}
		}

		return new Tile(coordinate, ETileType.Empty);
	}

	public void setType(ETileType type) {
		_type = type;
	}

	public void setStart(boolean isStart) {
		_isStart = isStart;
	}

	public void setPath(boolean isPath) {
		_isPath = isPath;
	}

	public boolean isStart() {
		return _isStart;
	}

	public boolean isPath() {
		return _isPath;
	}

	/**
	 * Determines if the given tile is a neighbor.
	 * @param tile
	 * @return
	 */
	public boolean isNeighbor(Tile tile) {

		if (tile.getCoordinate().getX() == getCoordinate().getX() - 1
				&& tile.getCoordinate().getY() == getCoordinate().getY()) {
			return true;
		}
		if (tile.getCoordinate().getX() == getCoordinate().getX() + 1
				&& tile.getCoordinate().getY() == getCoordinate().getY()) {
			return true;
		}
		if (tile.getCoordinate().getX() == getCoordinate().getX()
				&& tile.getCoordinate().getY() == getCoordinate().getY() - 1) {
			return true;
		}
		if (tile.getCoordinate().getX() == getCoordinate().getX()
				&& tile.getCoordinate().getY() == getCoordinate().getY() + 1) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if this tile is an exit.
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean isExit(int width, int height) {
		int x = getCoordinate().getX();
		int y = getCoordinate().getY();
		return x == 0 || x == width - 1 || y == 0 || y == height - 1;
	}

	/**
	 * Creates a list with all neighbors of a given type.
	 * @param tiles
	 * @param type
	 * @return
	 */
	public ArrayList<Tile> getNeighbors(ArrayList<Tile> tiles, ETileType type) {
		ArrayList<Tile> neighbors = new ArrayList<>();

		for (Tile tile : tiles) {

			if (tile.equals(this) || tile.getType() != type) {
				continue;
			}

			if (isNeighbor(tile)) {
				neighbors.add(tile);
			}

		}

		return neighbors;
	}

}
