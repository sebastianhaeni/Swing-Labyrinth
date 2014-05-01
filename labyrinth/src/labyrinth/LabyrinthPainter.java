package labyrinth;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;

import javax.swing.JPanel;

import labyrinth.Tile.ETileType;

public class LabyrinthPainter extends JPanel {

	private static final long serialVersionUID = 5392339854809820720L;
	private static final int TILE_TO_BORDER_RATIO = 8;
	private int _borderWidth;
	private int _width;
	private int _height;
	private LabyrinthModel _labyrinth;
	private int _tileSize;

	public LabyrinthPainter(LabyrinthModel labyrinth) {
		_labyrinth = labyrinth;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		calculateSizes();

		Iterator<Tile> it = _labyrinth.getTiles().iterator();

		while (it.hasNext()) {
			Tile tile = it.next();

			int x = tile.getCoordinate().getX() * _tileSize;
			int y = tile.getCoordinate().getY() * _tileSize;

			paintOutline(g, x, y);

			switch (tile.getType()) {
			case Empty:
				g.setColor(Color.WHITE);
				break;
			case Wall:
				paintWall(g, x, y, tile);
				break;
			}
		}

	}

	private void calculateSizes() {
		_width = getSize().width;
		_height = getSize().height;

		int tileWidth = _width / _labyrinth.getWidth();
		int tileHeight = _height / _labyrinth.getHeight();

		if (tileWidth > tileHeight) {
			_tileSize = tileHeight;
		} else {
			_tileSize = tileWidth;
		}

		_borderWidth = _tileSize / TILE_TO_BORDER_RATIO;
	}

	private void paintOutline(Graphics g, int x, int y) {
		g.setColor(Color.GRAY);
		g.drawRect(x, y, _tileSize, _tileSize);
	}

	private void paintWall(Graphics g, int x, int y, Tile tile) {
		g.setColor(Color.BLACK);

		int bx = x + (_tileSize / 2) - (_borderWidth / 2);
		int by = y + (_tileSize / 2) - (_borderWidth / 2);
		g.fillRect(bx, by, _borderWidth, _borderWidth);

		if (tile.getNeighborLeft(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x;
			int ay = y + (_tileSize / 2) - (_borderWidth / 2);
			g.fillRect(ax, ay, _tileSize / 2, _borderWidth);
		}

		if (tile.getNeighborTop(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + (_tileSize / 2) - (_borderWidth / 2);
			int ay = y;
			g.fillRect(ax, ay, _borderWidth, _tileSize / 2);
		}

		if (tile.getNeighborRight(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + (_tileSize / 2) - (_borderWidth / 2);
			int ay = y + (_tileSize / 2) - (_borderWidth / 2);
			g.fillRect(ax, ay, _tileSize / 2, _borderWidth);
		}

		if (tile.getNeighborBottom(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + (_tileSize / 2) - (_borderWidth / 2);
			int ay = y + (_tileSize / 2) - (_borderWidth / 2);
			g.fillRect(ax, ay, _borderWidth, _tileSize / 2);
		}

	}
}
