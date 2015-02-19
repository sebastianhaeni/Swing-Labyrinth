package labyrinth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

import labyrinth.Tile.ETileType;

/**
 * This class can generate a new labyrinth by carving it out.
 * 
 * @author Sebastian Häni <haeni.sebastian@gmail.com>
 * 
 */
class CarveThread extends Thread {

	private Stack<Tile> _stack = new Stack<Tile>();
	private LabyrinthModel _model;
	private boolean _makeJunction = false;
	private Random _random = new Random();
	private boolean _slow;

	/**
	 * Create carver.
	 * 
	 * @param labyrinthModel
	 * @param slow
	 *            If the carving should be slow so it can be visualized.
	 */
	public CarveThread(LabyrinthModel labyrinthModel, boolean slow) {
		_model = labyrinthModel;
		_slow = slow;x
	}

	@Override
	public void run() {
		createMass();

		if (_model.getWidth() < 3 || _model.getHeight() < 3) {
			_model.setGenerating(false);
			return;
		}

		_stack.clear();
		_stack.add(getStarterTile());

		try {
			carve();
		} catch (StackOverflowError ex) {
			_model.setGenerating(false);
			throw ex;
		}
		pierceExit();

		_model.setGenerating(false);
	}

	/**
	 * Chooses a random tile inside the labyrinth that is not a border tile.
	 * 
	 * @return Starter tile
	 */
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

	/**
	 * Fills the whole labyrinth with walls.
	 */
	private void createMass() {
		for (int x = 0; x < _model.getWidth(); x++) {
			for (int y = 0; y < _model.getHeight(); y++) {
				Tile tile = new Tile(new Coordinate(x, y), ETileType.Wall);
				_model.getTiles().add(tile);
			}
		}
	}

	/**
	 * Creates a random exit tile that has an adjacent empty tile.
	 */
	private void pierceExit() {
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

	/**
	 * Carves out the paths recursively. It takes the last element on the stack
	 * and fetches its wall neighbors. It goes through the neighbors in a random
	 * order. If the neighbor has 3 walls as neighbors it carves it out. When a
	 * position is reached where no more carving is possible to the stack gets
	 * popped and it goes back the carved out path to search a tile that can be
	 * carved out until the whole labyrinth is carved out.
	 * 
	 * @return True when exit found
	 * @throws StackOverflowError
	 */
	private boolean carve() throws StackOverflowError {
		_model.setDirty(true);

		if (_slow) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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