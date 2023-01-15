package ps6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.NavigableSet;

/**
 * Client-server graphical editor
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI
 */

public class Editor extends JFrame {
	private static String serverIP = "172.26.209.200";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE, TOFRONT
	}
	private Mode mode = ps6.Editor.Mode.DRAW;				// drawing/moving/recoloring/deleting objects

	// Type of shape to draw/work on
	private String shapeType = "ellipse";

	// Current drawing color
	private Color color = Color.black;

	// Current stroke weight
	private int strokeWeight = 3;

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingId = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged

	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};

		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease(event.getPoint());
			}
		});

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});

		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = ps6.Editor.Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = ps6.Editor.Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = ps6.Editor.Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = ps6.Editor.Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Make bring to front Mode
		JRadioButton bringToFront = new JRadioButton("front");
		bringToFront.addActionListener(e -> mode = ps6.Editor.Mode.TOFRONT);
		modes.add(bringToFront);
		modesP.add(bringToFront);

		// Make stroke label
		JLabel strokeLabel = new JLabel("stroke:",  JLabel.CENTER);
		strokeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Make stroke slider
		JSlider strokeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 3);
		strokeSlider.setMajorTickSpacing(1);
		strokeSlider.setPaintLabels(true);
		strokeSlider.setSnapToTicks(true);
		strokeSlider.addChangeListener(e -> strokeWeight = strokeSlider.getValue());

		// Make delete all button
		JButton deleteAll = new JButton("delete all");
		deleteAll.addActionListener(e -> deleteAll());

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		gui.add(strokeLabel);
		gui.add(strokeSlider);
		gui.add(deleteAll);
		return gui;
	}

	private synchronized void deleteAll() {
		for (int ID : sketch.shapes.keySet()) {
			comm.send("DEL " + ID);
		}
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		if (sketch != null && !sketch.getShapes().isEmpty()) {
			NavigableSet<Integer> shapeKeys = sketch.getShapes().navigableKeySet();
			for (Integer ID : shapeKeys) {
				// Prevents NullPointerException when thread is interrupted in the middle of the loop.
				if (sketch.getShapes().get(ID) != null) {
					sketch.getShapes().get(ID).draw(g);
				}
			}
		}
		if (curr != null) curr.draw(g);
	}

	// Helpers for event handlers

	/**
	 * Helper method for press at point
	 * In drawing Mode, start a new object;
	 * in moving Mode, (request to) start dragging if clicked in a shape;
	 * in recoloring Mode, (request to) change clicked shape's color
	 * in deleting Mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {

		// If the Mode is draw and there is no current shape already being drawn, create a new shape according to the shapeType
		if (mode.equals(ps6.Editor.Mode.DRAW) && curr == null) {

			if (shapeType.equals("ellipse")) {
				curr = new Ellipse(p.x, p.y, color);
			}

			if (shapeType.equals("rectangle")) {
				curr = new Rectangle(p.x, p.y, color);
			}

			if (shapeType.equals("freehand")) {
				curr = new Polyline(p.x, p.y, strokeWeight, color);
			}

			if (shapeType.equals("segment")) {
				curr = new Segment(p.x, p.y, strokeWeight, color);
			}

			drawFrom = p;
			repaint();
		}

		NavigableSet<Integer> shapeKeys = sketch.getShapes().descendingKeySet();

		// If the Mode is MOVE, get the most recently drawn shape's ID and the mouse's coordinates and save them
		if (mode.equals(ps6.Editor.Mode.MOVE)) {
			for (Integer ID : shapeKeys) {
				if (sketch.getShapes().get(ID).contains(p.x, p.y)) {
					movingId = ID;
					moveFrom = p;
					// Break out of the loop so that no other shapes override the most recent/top shape
					break;
				}
			}
		}

		// If Mode is DELETE, get the most recently drawn shape's ID that is clicked and send a message to the server to delete it
		if (mode.equals(ps6.Editor.Mode.DELETE)) {
			for (Integer ID : shapeKeys) {
				if (sketch.getShapes().get(ID).contains(p.x, p.y)) {
					comm.send("DEL " + ID);
					repaint();
					// Break out of the loop so that no other shapes override the most recent/top shape
					break;
				}
			}
		}

		// If Mode is TOFRONT, adjust top shape that you click on ID
		if (mode.equals(ps6.Editor.Mode.TOFRONT)) {
			for (Integer ID : shapeKeys) {
				if (sketch.getShapes().get(ID).contains(p.x, p.y)) {
					comm.send("FRONT " + ID);
					// Break out of the loop so that no other shapes override the most recent/top shape
					break;
				}
			}
		}

		// If Mode is DELETE, get the most recently drawn shape's ID that is clicked and send a message to the server to recolor it
		if (mode.equals(ps6.Editor.Mode.RECOLOR)) {
			for (Integer ID : shapeKeys) {
				if (sketch.getShapes().get(ID).contains(p.x, p.y)) {
					comm.send("RCOL " + ID + " " + color.getRGB());
					repaint();
					// Break out of the loop so that no other shapes override the most recent/top shape
					break;
				}
			}
		}
	}

	/**
	 * Helper method for drag to new point
	 * In drawing Mode, update the other corner of the object;
	 * in moving Mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {

		if (mode.equals(ps6.Editor.Mode.DRAW) && curr != null) {

			if (shapeType.equals("ellipse")) {
				curr = new Ellipse(drawFrom.x, drawFrom.y, p.x, p.y, color);
			}

			if (shapeType.equals("rectangle")) {
				curr = new Rectangle(drawFrom.x, drawFrom.y, p.x, p.y, color);
			}

			if (shapeType.equals("freehand")) {
				Polyline line = (Polyline) curr;
				line.addPoint(p.x, p.y);
				curr = line;
			}

			if (shapeType.equals("segment")) {
				curr = new Segment(drawFrom.x, drawFrom.y, p.x, p.y, strokeWeight, color);
			}

			repaint();
		}

		if (mode.equals(ps6.Editor.Mode.MOVE) && movingId != -1) {
			// Get difference to move by
			int moveX = p.x - moveFrom.x;
			int moveY = p.y - moveFrom.y;
			// Update total movement
			comm.send("MOVE " + movingId + " " + moveX + " " +  moveY);
			// Update the moveFrom
			moveFrom.x += moveX;
			moveFrom.y += moveY;
			repaint();
		}
	}

	/**
	 * Helper method for release
	 * In drawing Mode, pass the add new object request on to the server;
	 * in moving Mode, release it
	 */
	private void handleRelease(Point p) {
		if (mode.equals(ps6.Editor.Mode.DRAW) && curr != null) {
			if (shapeType.equals("ellipse") || shapeType.equals("rectangle")) {
				comm.send("DRAW " + shapeType + " " + drawFrom.x + " " + drawFrom.y + " " + p.x + " " + p.y + " " + color.getRGB());
			}

			if (shapeType.equals("freehand")) {
				Polyline line = (Polyline) curr;
				String points = "";
				for (Point point : line.getPoints()) {
					points += " " + point.x + "," + point.y;
				}
				comm.send("DRAW " + shapeType + points + " " + strokeWeight + " " + color.getRGB());
			}

			if (shapeType.equals("segment")) {
				comm.send("DRAW " + shapeType + " " + drawFrom.x + " " + drawFrom.y + " " + p.x + " " + p.y + " " + strokeWeight + " " + color.getRGB());
			}

			drawFrom = null;
			curr = null;
			repaint();
		}

		if (mode.equals(ps6.Editor.Mode.MOVE)) {
			moveFrom = null;
			movingId = -1;
			repaint();
		}
	}

	public int getWidth() {
		return super.getWidth();
	}

	public int getHeight() {
		return super.getHeight();
	}

	public int getStrokeWeight() {
		return strokeWeight;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});
	}
}