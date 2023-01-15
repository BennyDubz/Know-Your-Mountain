package ps6;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NavigableSet;

import static java.lang.Integer.parseInt;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			NavigableSet<Integer> shapeKeys = server.getSketch().getShapes().navigableKeySet();

			/**
			 * Loops through all the keys, then mimics editor's sending messages for every shape that is in sketch
			 */

			for (Integer ID : shapeKeys) {
				String shapeType = server.getSketch().getShapes().get(ID).getType();
				String[] parts = server.getSketch().getShapes().get(ID).toString().split(" ");

				if (shapeType.equals("ellipse") || shapeType.equals("rectangle")) {
					send("DRAW " + shapeType + " " + parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5]);
				}

				if (shapeType.equals("segment")) {
					send("DRAW " + shapeType + " " + parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[6]);
				}

				if (shapeType.equals("freehand")) {
					Polyline line = (Polyline) server.getSketch().getShapes().get(ID);
					String points = "";
					int colorRGB = server.getSketch().getShapes().get(ID).getColor().getRGB();
					int strokeWeight = ((Polyline) server.getSketch().getShapes().get(ID)).getStrokeWeight();
					for (Point point : line.getPoints()) {
						points += " " + point.x + "," + point.y;
					}
					send("DRAW " + shapeType + points + " " + strokeWeight +" " + colorRGB);
				}
			}

			// Keep getting and handling messages from the client
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println("BROADCAST: " + line);
				handleMessage(line);
				server.broadcast(line);
			}

			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void handleMessage(String msg) {
		String[] parts = msg.split(" ");

		/**
		 * If the first part is DRAW, and the second part is ellipse, segment, or rectangle...
		 *  The message comes in the format that the editor sends them, which is:
		 * 	"DRAW " + shapeType + " " + drawFrom.x+ " " +drawFrom.y+ " " + p.x + " " + p.y + " " + color.getRGB()
		 * 	Then, it uses the parts of a message to create a shape in the editor's sketch
		 */
		if (parts[0].equals("DRAW")) {

			// Creates an ellipse and adds it to the sketch
			if (parts[1].equals("ellipse")) {
				int rgb = parseInt(parts[6]);
				Color color = new Color(rgb);
				Ellipse ellipse = new Ellipse(parseInt(parts[2]), parseInt(parts[3]), parseInt(parts[4]), parseInt(parts[5]), color);
				server.getSketch().addShape(ellipse);
			}

			// Creates a rectangle and adds it to the sketch
			if (parts[1].equals("rectangle")) {
				int rgb = parseInt(parts[6]);
				Color color = new Color(rgb);
				Rectangle rectangle = new Rectangle(parseInt(parts[2]), parseInt(parts[3]), parseInt(parts[4]), parseInt(parts[5]), color);
				server.getSketch().addShape(rectangle);
			}

			// Creates a segment and adds it to the sketch.
			if (parts[1].equals("segment")) {
				int rgb = parseInt(parts[7]);
				Color color = new Color(rgb);
				Segment segment = new Segment(parseInt(parts[2]), parseInt(parts[3]), parseInt(parts[4]), parseInt(parts[5]), parseInt(parts[6]), color);
				server.getSketch().addShape(segment);
			}

			/**
			 * Creates the freehand by handling its special message from other user's editor
			 * First creates an ArrayList of points from the string, then uses that to make a new freehand for this users editor
			 */
			if (parts[1].equals("freehand")) {
				int rgb = parseInt(parts[parts.length-1]);
				Color color = new Color(rgb);
				ArrayList<Point> points = new ArrayList<>();
				int strokeWeight = Integer.parseInt(parts[parts.length-2]);
				for (int i = 2; i < parts.length - 2; i++) {
					String[] coords = parts[i].split(",");
					Point point = new Point(parseInt(coords[0]),parseInt(coords[1]));
					points.add(point);
				}
				Polyline polyline = new Polyline(points, strokeWeight, color);
				server.getSketch().addShape(polyline);
			}
		}

		// Handles the string that the other user's editor sends to move a shape on the server's sketch
		if (parts[0].equals("MOVE")) {
			Shape shape = server.getSketch().getShapes().get(parseInt(parts[1]));
			// Prevents NullPointerException when other user deletes the shape while another one moves it
			if (shape != null) {
				server.getSketch().getShapes().get(parseInt(parts[1])).moveBy(parseInt(parts[2]),parseInt(parts[3]));
			}
		}

		// Handles the string that the other user's editor sends to delete a shape on the server's sketch
		if (parts[0].equals("DEL")) {
			server.getSketch().removeShape(parseInt(parts[1]));
		}

		// Handles the string the user sends to bring a shape to the front by making it have the highest ID in this server's sketch
		if (parts[0].equals("FRONT")) {
			server.getSketch().toFront(parseInt(parts[1]));
		}


		// Handles the string that the other user's editor sends to recolor a shape on this server's sketch
		if (parts[0].equals("RCOL")) {
			int rgb = parseInt(parts[2]);
			Color color = new Color(rgb);
			server.getSketch().getShapes().get(parseInt(parts[1])).setColor(color);
		}
	}
}
