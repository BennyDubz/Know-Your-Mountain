package ps6;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Handles communication to/from the server for the editor
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println("RECEIVED: " + line);
				handleMessage(line);
			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			System.out.println("server hung up");
		}
	}

	public void handleMessage(String msg) {
		String[] parts = msg.split(" ");

		/**
		 * If the first part is DRAW, and the second part is ellipse, segment, or rectangle...
		 *  The message comes in the format that the editor sends them, which is:
		 * 	"DRAW " + shapeType + " " + drawFrom.x+ " " +drawFrom.y+ " " + p.x + " " + p.y + " " + color.getRGB()
		 * 	Then, it uses the parts of a message to create a shape in the server's sketch
		 */
		if (parts[0].equals("DRAW")) {

			// Creates an ellipse and adds it to the sketch
			if (parts[1].equals("ellipse")) {
				int rgb = Integer.parseInt(parts[6]);
				Color color = new Color(rgb);
				Ellipse ellipse = new Ellipse(parseInt(parts[2]), parseInt(parts[3]), parseInt(parts[4]), parseInt(parts[5]), color);
				editor.getSketch().addShape(ellipse);
			}

			// Creates a rectangle and adds it to the sketch
			if (parts[1].equals("rectangle")) {
				int rgb = Integer.parseInt(parts[6]);
				Color color = new Color(rgb);
				Rectangle rectangle = new Rectangle(parseInt(parts[2]), parseInt(parts[3]), parseInt(parts[4]), parseInt(parts[5]), color);
				editor.getSketch().addShape(rectangle);
			}

			// Creates a segment and adds it to the sketch.
			if (parts[1].equals("segment")) {
				int rgb = Integer.parseInt(parts[7]);
				Color color = new Color(rgb);
				Segment segment = new Segment(parseInt(parts[2]), parseInt(parts[3]), parseInt(parts[4]), parseInt(parts[5]), parseInt(parts[6]), color);
				editor.getSketch().addShape(segment);
			}

			/**
			 * Creates the freehand by handling its special message from other user's editor
			 * First creates an ArrayList of points from the string, then uses that to make a new freehand for this users editor
			 */
			if (parts[1].equals("freehand")) {
				int rgb = Integer.parseInt(parts[parts.length-1]);
				Color color = new Color(rgb);
				int strokeWeight = Integer.parseInt(parts[parts.length-2]);
				ArrayList<Point> points = new ArrayList<>();
				for (int i = 2; i < parts.length - 2; i++) {
					String[] coords = parts[i].split(",");
					Point point = new Point(parseInt(coords[0]),parseInt(coords[1]));
					points.add(point);
				}
				Polyline polyline = new Polyline(points, strokeWeight, color);
				editor.getSketch().addShape(polyline);
			}
		}

		// Handles the string that the other user's editor sends to move a shape on this user's editor/sketch
		if (parts[0].equals("MOVE")) {
			Shape shape = editor.getSketch().getShapes().get(parseInt(parts[1]));
			// Prevents NullPointerException when other user deletes the shape while another one moves it
			if (shape != null) {
				editor.getSketch().getShapes().get(parseInt(parts[1])).moveBy(parseInt(parts[2]),parseInt(parts[3]));
			}
		}

		// Handles the string that the other user's editor sends to delete a shape on this user's editor/sketch
		if (parts[0].equals("DEL")) {
			editor.getSketch().removeShape((parseInt(parts[1])));
		}

		// Handles the string the user sends to bring a shape to the front by making it have the highest ID in editors's sketch
		if (parts[0].equals("FRONT")) {
			editor.getSketch().toFront(parseInt(parts[1]));
		}

		// Handles the string that the other user's editor sends to recolor a shape on this user's editor/sketch
		if (parts[0].equals("RCOL")) {
			int rgb = Integer.parseInt(parts[2]);
			Color color = new Color(rgb);
			editor.getSketch().getShapes().get(parseInt(parts[1])).setColor(color);
		}

		editor.repaint();

	}
}