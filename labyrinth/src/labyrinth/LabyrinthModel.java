package labyrinth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import labyrinth.Tile.ETileType;

public class LabyrinthModel {

	ArrayList<Tile> _tiles = new ArrayList<>();
	private int _width;
	private int _height;
	boolean _generating = false;
	
	boolean _dirty;

	private ArrayList<ArrayList<Tile>> _paths = new ArrayList<>();

	public LabyrinthModel(String mazeFile) {
		if (mazeFile == null) {
			generateLabyrinth(60, 60);
			return;
		}
		parse(mazeFile);
	}

	public void generateLabyrinth(int width, int height) {
		if (_generating) {
			return;
		}
		_width = width;
		_height = height;
		_tiles.clear();

		Thread thread = new CarveThread(this);
		_generating = true;
		thread.start();
	}

	private void parse(String mazeFile) {
		BufferedReader br = null;

		try {
			String sCurrentLine;

			br = new BufferedReader(new FileReader(mazeFile));

			int row = 0;
			while ((sCurrentLine = br.readLine()) != null) {

				for (int col = 0; col < sCurrentLine.length(); col++) {
					_tiles.add(new Tile(new Coordinate(col, row), sCurrentLine
							.charAt(col)));

					if (col + 1 > _width) {
						_width = col + 1;
					}
				}

				row++;
			}

			_height = row;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public ArrayList<Tile> getTiles() {
		return _tiles;
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public boolean findPathFrom(Tile start) {
		_paths.clear();

		if (start.isExit(getWidth(), getHeight())) {
			return true;
		}

		ArrayList<Tile> visited = new ArrayList<>();
		visited.add(start);

		try {
			findPathFrom(start, visited);
		} catch (StackOverflowError ex) {
			System.out.println("Labyrinth to big to solve recursively!");
		}

		if (_paths.size() > 0) {
			// finding shortest path
			ArrayList<Tile> shortest = null;

			for (ArrayList<Tile> path : _paths) {

				if (shortest == null) {
					shortest = path;
					continue;
				}

				if (shortest.size() > path.size()) {
					shortest = path;
				}

			}

			for (Tile tile : shortest) {
				tile.setIsPath();
			}

			return true;
		}

		return false;
	}

	private boolean findPathFrom(Tile tile, ArrayList<Tile> visited)
			throws StackOverflowError {

		ArrayList<Tile> neighbors = tile.getNeighbors(getTiles(),
				Tile.ETileType.Empty);

		for (Tile neighbor : neighbors) {
			if (visited.contains(neighbor)) {
				continue;
			}

			visited.add(neighbor);

			if (neighbor.isExit(getWidth(), getHeight())) {
				_paths.add(visited);
				return true;
			}

			if (!findPathFrom(neighbor, new ArrayList<>(visited))) {
				visited.remove(neighbor);
			}
		}

		return false;
	}

	public boolean isGenerating() {
		return _generating;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public void clean() {
		_dirty = false;
	}

	public void save(File selectedFile) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(selectedFile));

			int row = 0;
			while (row < _height) {
				int col = 0;
				while (col < _width) {
					for (Tile tile : _tiles) {
						if (tile.getCoordinate().getY() == row
								&& tile.getCoordinate().getX() == col) {
							writer.write(tile.getType() == ETileType.Empty ? '.'
									: '#');
						}
					}
					col++;
				}
				row++;
				writer.write(String.format("%n"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
