package labyrinth;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import labyrinth.Tile.ETileType;

public class LabyrinthModel {

	ArrayList<Tile> _tiles = new ArrayList<>();
	private int _width;
	private int _height;
	boolean _generating = false;
	private Random _random = new Random();
	boolean _dirty;

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

		// generating mass
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Tile tile = new Tile(new Coordinate(x, y), ETileType.Wall);
				_tiles.add(tile);
			}
		}

		// carving out paths
		Tile start = _tiles.get(_random.nextInt(_tiles.size() - 1));
		boolean notGood = true;
		while (notGood) {
			if (start.isExit(width, height)) {
				start = _tiles.get(_random.nextInt(_tiles.size() - 1));
				continue;
			}
			notGood = false;
		}
		Thread thread = new CarveThread(this, start);
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
					_tiles.add(new Tile(sCurrentLine.charAt(col),
							new Coordinate(col, row)));

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
		if (start.isExit(getWidth(), getHeight())) {
			return true;
		}

		ArrayList<Tile> visited = new ArrayList<>();
		visited.add(start);
		return findPathFrom(start, visited, 0);
	}

	private boolean findPathFrom(Tile start, ArrayList<Tile> visited,
			int counter) {
		ArrayList<Tile> neighbors = start.getNeighbors(getTiles(),
				Tile.ETileType.Empty);

		if (counter > _width * _height) {
			System.out.println("Way not found");
			return false;
		}

		for (Tile neighbor : neighbors) {
			if (visited.contains(neighbor)) {
				continue;
			}

			neighbor.setIsPath();
			neighbor.setNumber(counter);
			visited.add(neighbor);

			if (neighbor.isExit(getWidth(), getHeight())) {
				return true;
			}

			if (findPathFrom(neighbor, visited, counter + 1)) {
				return true;
			}
			neighbor.clearPath();
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

}
