package labyrinth;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Main extends JFrame {

	private static final long serialVersionUID = -8129013583057007422L;

	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 800;

	private LabyrinthModel _labyrinthModel;
	private LabyrinthPainter _labyrinthPainter;

	public Main(String mazeFile) {
		setTitle("Labyrinth");
		setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		createGui(mazeFile);

		setVisible(true);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (_labyrinthModel.isDirty()) {
					_labyrinthModel.clean();
					_labyrinthPainter.repaint();
				}
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 100);

	}

	private void createGui(String mazeFile) {
		_labyrinthModel = new LabyrinthModel(mazeFile);
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

		_labyrinthPainter = new LabyrinthPainter(_labyrinthModel);

		_labyrinthPainter.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent event) {
				_labyrinthPainter.start(event.getPoint());
			}

			@Override
			public void mouseDragged(MouseEvent event) {
			}
		});
		_labyrinthPainter.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent event) {
				_labyrinthPainter.toggleTile(event.getPoint());
			}

			@Override
			public void mousePressed(MouseEvent event) {
			}

			@Override
			public void mouseExited(MouseEvent event) {
			}

			@Override
			public void mouseEntered(MouseEvent event) {
			}

			@Override
			public void mouseClicked(MouseEvent event) {
			}
		});

		mainPanel.add(_labyrinthPainter);

		JButton btnRegenerate = new JButton("Regenerate");
		btnRegenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_labyrinthModel.generateLabyrinth(60, 60);
				repaint();
			}
		});
		mainPanel.add(btnRegenerate);

		Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		mainPanel.setBorder(border);

		add(mainPanel);
	}

}
