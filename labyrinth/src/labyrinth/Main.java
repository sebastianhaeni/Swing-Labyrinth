package labyrinth;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
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

		_labyrinthModel = new LabyrinthModel(mazeFile);
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
		timer.scheduleAtFixedRate(task, 0, 50);

	}

	private void createGui(String mazeFile) {
		createMenuBar();
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

		Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		mainPanel.setBorder(border);

		add(mainPanel);
	}

	private void createMenuBar() {
		JMenuBar bar = new JMenuBar();

		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);

		JMenuItem open = new JMenuItem("Open");
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				KeyEvent.CTRL_MASK));
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooseMazeFile();
			}
		});

		JMenuItem save = new JMenuItem("Save");
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.CTRL_MASK));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveMazeFile();
			}
		});

		menu.add(open);
		menu.add(save);
		bar.add(menu);

		menu = new JMenu("Actions");
		JMenuItem generate = new JMenuItem("Generate random labyrinth");
		generate.setMnemonic(KeyEvent.VK_G);
		generate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				KeyEvent.CTRL_MASK));
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_labyrinthModel.generateLabyrinth(50, 50);
			}
		});
		menu.add(generate);
		bar.add(menu);

		setJMenuBar(bar);
	}

	private void chooseMazeFile() {
		JFileChooser chooser = new JFileChooser();

		if (chooser.showDialog(this, "Open maze") == JFileChooser.APPROVE_OPTION) {
			_labyrinthModel = new LabyrinthModel(chooser.getSelectedFile()
					.getAbsolutePath());
			_labyrinthPainter.setModel(_labyrinthModel);
		}
	}

	private void saveMazeFile() {
		JFileChooser chooser = new JFileChooser();

		if (chooser.showDialog(this, "Save maze") == JFileChooser.APPROVE_OPTION) {
			_labyrinthModel.save(chooser.getSelectedFile());
		}
	}
}
