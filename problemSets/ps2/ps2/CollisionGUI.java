package ps2;

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private ArrayList<Blob> blobs;						// all the blobs
	private ArrayList<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("Collider", width, height);

		blobs = new ArrayList<>();
		colliders = new ArrayList<>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else { // set the type for new blobs
			blobType = k;
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// Ask all the blobs to draw themselves.
        g.setColor(Color.BLACK);
        for (Blob b : blobs) {
            b.draw(g);
        }

		// Ask the colliders to draw themselves in red.
        g.setColor(Color.RED);
        for (Blob c : colliders) {
            c.draw(g);
        }
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// Instantiate tree with first blob in ArrayList as root
		PointQuadtree<Blob> tree = new PointQuadtree<Blob>(blobs.get(0), 0, 0, width, height);
		// Add all the others blobs to tree
		for (Blob b : blobs) {
			tree.insert(b);
		}

		// Clear colliders
		colliders = new ArrayList<>();
		// For each blob, see if anybody else collided with it, and if they have then add both to colliders
		for (Blob b : blobs) {
			boolean bCollided = false;
			for (Blob c : tree.findInCircle(b.getX(), b.getY(), b.getR() * 2)) {
				// Make sure blob isn't Colliding with itself
				if (c != b) {
					if (!bCollided) {
						colliders.add(b);
						bCollided = true;
					}
					colliders.add(c);
				}
			}
		}
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				// Remove all blobs that have collided
				for (Blob blob : colliders){
					blobs.remove(blob);
				}
				colliders = new ArrayList<>();
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
