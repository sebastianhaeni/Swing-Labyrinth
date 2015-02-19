package labyrinth;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.Iterator;

import javax.swing.JPanel;

import labyrinth.Tile.ETileType;

/**
 * Paints a labyrinth model.
 * 
 * @author Sebastian Häni <haeni.sebastian@gmail.com>
 * 
 */
public class LabyrinthPainter extends JPanel {

	private static final long serialVersionUID = 7860248262127656628L;
	private static final int TILE_TO_BORDER_RATIO = 8;
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final Color STARTER_TILE_BACKGROUND_COLOR = Color.RED;
	private static final Color PATH_TILE_BACKGROUND_COLOR = Color.YELLOW;
	private static final Color WALL_COLOR = Color.BLACK;
	private static final Color OUTLINE_COLOR = Color.GRAY;

	private int _borderWidth;
	private int _width;
	private int _height;
	private LabyrinthModel _labyrinth;
	private int _tileSize;
	private boolean _showOutline = true;
	private boolean _fancyGraphcis = true;

	public LabyrinthPainter(LabyrinthModel labyrinth) {
		_labyrinth = labyrinth;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		if (_fancyGraphcis) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		calculateSizes();

		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, _width, _height);

		Iterator<Tile> it = _labyrinth.getTiles().iterator();

		// draw tile content
		while (it.hasNext()) {
			Tile tile = it.next();

			int x = tile.getCoordinate().getX() * _tileSize;
			int y = tile.getCoordinate().getY() * _tileSize;

			switch (tile.getType()) {
			case Empty:
				paintEmpty(g2, x, y, tile);
				break;
			case Wall:
				paintWall(g2, x, y, tile);
				break;
			}
		}

		if (_showOutline) {
			// create outline first so it will not overdraw tile content

			it = _labyrinth.getTiles().iterator();
			while (it.hasNext()) {
				Tile tile = it.next();

				int x = tile.getCoordinate().getX() * _tileSize;
				int y = tile.getCoordinate().getY() * _tileSize;

				paintOutline(g2, x, y);
			}

		}

	}

	/**
	 * Searches the tile and then starts the path finding algorithm.
	 * 
	 * @param point
	 *            Position of starter tile in component.
	 */
	public void searchPath(Point point) {
		if (_labyrinth.isGenerating()) {
			return;
		}
		Iterator<Tile> it = _labyrinth.getTiles().iterator();
		Tile startTile = null;
		while (it.hasNext()) {
			Tile tile = it.next();

			if (tile.getType() != Tile.ETileType.Empty) {
				continue;
			}

			tile.setPath(false);

			int x = tile.getCoordinate().getX() * _tileSize;
			int y = tile.getCoordinate().getY() * _tileSize;

			Rectangle rect = new Rectangle(x, y, _tileSize, _tileSize);

			if (rect.contains(point)) {
				tile.setStart(true);
				startTile = tile;
			} else {
				tile.setStart(false);
			}
		}

		if (startTile != null) {
			_labyrinth.findPathFrom(startTile);
		}

		repaint();
	}

	/**
	 * Searches the tile and toggles type of tile from empty to wall.
	 * 
	 * @param point
	 *            Position of tile in component.
	 */
	public void toggleTile(Point point) {
		Iterator<Tile> it = _labyrinth.getTiles().iterator();
		while (it.hasNext()) {
			Tile tile = it.next();

			int x = tile.getCoordinate().getX() * _tileSize;
			int y = tile.getCoordinate().getY() * _tileSize;

			Rectangle rect = new Rectangle(x, y, _tileSize, _tileSize);

			if (rect.contains(point)) {
				tile.setType(tile.getType() == ETileType.Empty ? ETileType.Wall
						: ETileType.Empty);
				break;
			}
		}
		repaint();
	}

	/**
	 * Set labyrinth model.
	 * 
	 * @param model
	 */
	public void setModel(LabyrinthModel model) {
		_labyrinth = model;
	}

	public void setShowOutline(boolean showOutline) {
		_showOutline = showOutline;
		repaint();
	}

	public void setFancyGraphics(boolean fancyGraphics) {
		_fancyGraphcis = fancyGraphics;
		repaint();
	}

	/**
	 * Calculates the tile size, border width and the total width and height of
	 * the labyrinth based on how much space is available.
	 */
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

	/**
	 * Paints a tile outline to make a grid.
	 * 
	 * @param g
	 *            Graphics
	 * @param x
	 *            coordinate in model
	 * @param y
	 *            coordinate in model
	 */
	private void paintOutline(Graphics2D g, int x, int y) {
		g.setColor(OUTLINE_COLOR);
		g.setStroke(new BasicStroke(1));
		g.drawRect(x, y, _tileSize, _tileSize);
	}

	/**
	 * Draws an empty pane. It detects if its the starter tile or a path tile.
	 * 
	 * @param g
	 *            Graphics
	 * @param x
	 *            coordinate in model
	 * @param y
	 *            coordinate in model
	 * @param tile
	 *            The tile
	 */
	private void paintEmpty(Graphics2D g, int x, int y, Tile tile) {
		if (tile.isStart()) {
			g.setColor(STARTER_TILE_BACKGROUND_COLOR);
			g.fillOval(x + 1, y + 1, _tileSize - 2, _tileSize - 2);
		} else if (tile.isPath()) {
			g.setColor(PATH_TILE_BACKGROUND_COLOR);
			g.fillOval(x + 1, y + 1, _tileSize - 2, _tileSize - 2);
		}
	}

	/**
	 * Draws a single wall tile.
	 * 
	 * @param g
	 *            Graphics
	 * @param x
	 *            coordinate in model
	 * @param y
	 *            coordinate in model
	 * @param tile
	 *            The tile
	 */
	private void paintWall(Graphics2D g, int x, int y, Tile tile) {
		g.setColor(WALL_COLOR);
		g.setStroke(new BasicStroke(_borderWidth));

		int halfTileSize = _tileSize / 2;

		// middle dot
		int dx = x + halfTileSize - _borderWidth;
		int dy = y + halfTileSize - _borderWidth;
		g.fillOval(dx, dy, 2 * _borderWidth, 2 * _borderWidth);

		// straight lines
		if (tile.getNeighborLeft(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x;
			int ay = y + halfTileSize;
			int bx = x + halfTileSize;
			int by = ay;
			g.draw(new Line2D.Float(ax, ay, bx, by));
		}

		if (tile.getNeighborTop(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + halfTileSize;
			int ay = y;
			int bx = ax;
			int by = y + halfTileSize;
			g.draw(new Line2D.Float(ax, ay, bx, by));
		}

		if (tile.getNeighborRight(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + halfTileSize;
			int ay = y + halfTileSize;
			int bx = x + _tileSize;
			int by = ay;
			g.draw(new Line2D.Float(ax, ay, bx, by));
		}

		if (tile.getNeighborBottom(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + halfTileSize;
			int ay = y + halfTileSize;
			int bx = ax;
			int by = y + _tileSize;
			g.draw(new Line2D.Float(ax, ay, bx, by));
		}

		// diagonal lines
		if (tile.getNeighborTopLeft(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + halfTileSize;
			int ay = y + halfTileSize;
			int bx = x;
			int by = y;
			g.draw(new Line2D.Float(ax, ay, bx, by));
		}

		if (tile.getNeighborTopRight(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + halfTileSize;
			int ay = y + halfTileSize;
			int bx = x + _tileSize;
			int by = y;
			g.draw(new Line2D.Float(ax, ay, bx, by));
		}

		if (tile.getNeighborBottomLeft(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + halfTileSize;
			int ay = y + halfTileSize;
			int bx = x;
			int by = y + _tileSize;
			g.draw(new Line2D.Float(ax, ay, bx, by));
		}

		if (tile.getNeighborBottomRight(_labyrinth.getTiles()).getType() == ETileType.Wall) {
			int ax = x + halfTileSize;
			int ay = y + halfTileSize;
			int bx = x + _tileSize;
			int by = y + _tileSize;
			g.draw(new Line2D.Float(ax, ay, bx, by));
		}

	}
}
