package labyrinth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

import labyrinth.Tile.ETileType;

class CarveThread extends Thread {

	private Stack<Tile> _stack = new Stack<Tile>();
	private LabyrinthModel _model;
	private boolean _makeJunction = false;
	private Random _random = new Random();

	public CarveThread(LabyrinthModel labyrinthModel) {
		_model = labyrinthModel;
	}

	@Override
	public void run() {
		createMass();

		_stack.add(getStarterTile());
		carve();

		pokeExit();

		_model._generating = false;
	}

	private Tile getStarterTile() {
		Tile start = _model.getTiles().get(
				_random.nextInt(_model.getTiles().size() - 1));
		boolean notGood = true;
		while (notGood) {
			if (start.isExit(_model.getWidth(), _model.getHeight())) {
				start = _model.getTiles().get(
						_random.nextInt(_model.getTiles().size() - 1));
				continue;
			}
			notGood = false;
		}
		start.setType(ETileType.Empty);
		return start;
	}

	private void createMass() {
		for (int x = 0; x < _model.getWidth(); x++) {
			for (int y = 0; y < _model.getHeight(); y++) {
				Tile tile = new Tile(new Coordinate(x, y), ETileType.Wall);
				_model.getTiles().add(tile);
			}
		}
	}

	private void pokeExit() {
		ArrayList<Tile> shuffled = _model.getTiles();
		Collections.shuffle(shuffled);
		for (Tile tile : shuffled) {
			if (tile.isExit(_model.getWidth(), _model.getHeight())) {
				if (tile.getNeighbors(_model.getTiles(), ETileType.Empty)
						.size() > 0) {
					tile.setType(ETileType.Empty);
					break;
				}
			}
		}
	}

	private boolean carve() {
		_model._dirty = true;
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ArrayList<Tile> neighbors = _stack.lastElement().getNeighbors(
				_model.getTiles(), Tile.ETileType.Wall);

		boolean carved = false;
		if (neighbors.size() > 0) {

			Collections.shuffle(neighbors);

			for (Tile neighbor : neighbors) {
				int neighborCount = neighbor.getNeighbors(_model.getTiles(),
						ETileType.Wall).size();

				if (neighborCount >= 3) {
					_makeJunction = false;
					neighbor.setType(Tile.ETileType.Empty);
					_stack.add(neighbor);
					carved = true;
					break;
				}
			}

		}

		if (!carved && !_makeJunction) {
			_makeJunction = true;
		}

		if (!carved && _makeJunction) {
			_stack.pop();
		}

		if (_stack.size() == 0) {
			return true;
		} else {
			carve();
		}

		return false;
	}
}