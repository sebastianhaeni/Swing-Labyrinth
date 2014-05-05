package labyrinth;

import java.awt.BorderLayout;
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

/**
 * 
 * @author Sebastian Häni <haeni.sebastian@gmail.com>
 * 
 */
public class Main extends JFrame {

	private static final long serialVersionUID = -8129013583057007422L;

	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 800;

	private LabyrinthModel _labyrinthModel;
	private LabyrinthPainter _labyrinthPainter;
	private boolean _painting = false;

	public Main(String mazeFile) {
		setTitle("Labyrinth");
		setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		_labyrinthModel = new LabyrinthModel(mazeFile);
		createGui(mazeFile);

		setVisible(true);

		// Timer to repaint the labyrinth when another thread is changing it.
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (!_painting && _labyrinthModel.isDirty()) {
					_painting = true;
					_labyrinthModel.setDirty(false);
					_labyrinthPainter.repaint();
					_painting = false;
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
				_labyrinthPainter.searchPath(event.getPoint());
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

	/**
	 * Yay, Spaghetti Code!
	 */
	private void createMenuBar() {
		final JFrame frame = this;

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

		menu = new JMenu("Tools");

		final JCheckBoxMenuItem showOutline = new JCheckBoxMenuItem(
				"Show outline", true);
		showOutline.setMnemonic(KeyEvent.VK_O);
		showOutline.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				KeyEvent.CTRL_MASK));
		showOutline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_labyrinthPainter.setShowOutline(showOutline.isSelected());
			}
		});
		menu.add(showOutline);

		final JCheckBoxMenuItem fancyGraphics = new JCheckBoxMenuItem(
				"Fancy Graphics", true);
		fancyGraphics.setMnemonic(KeyEvent.VK_F);
		fancyGraphics.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				KeyEvent.CTRL_MASK));
		fancyGraphics.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_labyrinthPainter.setFancyGraphics(fancyGraphics.isSelected());
			}
		});
		menu.add(fancyGraphics);

		final JDialog dialog = new JDialog(this, "Random labyrinth", true);
		dialog.setResizable(false);
		dialog.setSize(450, 80);

		JPanel dialogPanel = new JPanel(); // Flow layout will center button.
		Border border = BorderFactory.createEmptyBorder(15, 15, 15, 15);
		dialogPanel.setBorder(border);
		dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.LINE_AXIS));

		JLabel widthLabel = new JLabel("Width:", JLabel.RIGHT);
		final JTextField widthTextfield = new JTextField("50");
		JLabel heightLabel = new JLabel("Height:", JLabel.RIGHT);
		final JTextField heightTextfield = new JTextField("50");
		final JCheckBox checkbox = new JCheckBox("Show animation");

		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				dialog.setVisible(false);
				dialog.dispose();

				_labyrinthModel.generateLabyrinth(
						Integer.parseInt(widthTextfield.getText()),
						Integer.parseInt(heightTextfield.getText()),
						checkbox.isSelected());
			}
		});

		dialogPanel.add(widthLabel, BorderLayout.EAST);
		dialogPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		dialogPanel.add(widthTextfield, BorderLayout.WEST);
		dialogPanel.add(Box.createRigidArea(new Dimension(15, 0)));
		dialogPanel.add(heightLabel, BorderLayout.EAST);
		dialogPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		dialogPanel.add(heightTextfield, BorderLayout.WEST);
		dialogPanel.add(Box.createRigidArea(new Dimension(15, 0)));
		dialogPanel.add(checkbox);
		dialogPanel.add(Box.createRigidArea(new Dimension(15, 0)));
		dialogPanel.add(btnRun);

		dialog.getContentPane().add(dialogPanel);
		dialog.setLocationRelativeTo(this);

		JMenuItem generate = new JMenuItem("Generate random labyrinth");
		generate.setMnemonic(KeyEvent.VK_G);
		generate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				KeyEvent.CTRL_MASK));

		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (_labyrinthModel.isGenerating()) {
					JOptionPane.showMessageDialog(frame,
							"Wait for running generation to finish!");
					return;
				}
				dialog.setVisible(true);
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
