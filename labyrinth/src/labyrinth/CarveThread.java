package labyrinth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import labyrinth.Tile.ETileType;

class CarveThread extends Thread {

	private Tile _start;
	private Stack<Tile> _stack = new Stack<Tile>();
	private LabyrinthModel _model;
	private boolean _makeJunction = false;

	public CarveThread(LabyrinthModel labyrinthModel, Tile start) {
		_model = labyrinthModel;
		_start = start;
		_start.setType(ETileType.Empty);
	}

	@Override
	public void run() {
		_stack.add(_start);
		carve();

		pokeExit();

		_model._generating = false;
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