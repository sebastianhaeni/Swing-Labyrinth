package labyrinth;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Main extends JFrame {

	private static final long serialVersionUID = -8129013583057007422L;
	private static final String MazeFile = "maze1.txt";

	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 800;

	private LabyrinthModel _labyrinthModel;
	private LabyrinthPainter _labyrinthPainter;

	public Main() {
		setTitle("Labyrinth");
		setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		loadMaze();
		createGui();

		setVisible(true);
	}

	private void loadMaze() {
		_labyrinthModel = new LabyrinthModel(MazeFile);
	}

	private void createGui() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

		_labyrinthPainter = new LabyrinthPainter(_labyrinthModel);

		mainPanel.add(_labyrinthPainter, BorderLayout.CENTER);

		Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		mainPanel.setBorder(border);

		add(mainPanel);
	}

}
