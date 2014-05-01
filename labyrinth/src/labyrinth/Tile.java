package labyrinth;

import java.util.ArrayList;

public class Tile {
	public enum ETileType {
		Wall, Empty
	}

	private ETileType _type;
	private Coordinate _coordinate;

	public Tile(char c, Coordinate coordinate) {
		_coordinate = coordinate;

		switch (c) {
		case '#':
		case '|':
		case '*':
			_type = ETileType.Wall;
			break;
		case ' ':
		case '.':
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

}
