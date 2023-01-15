package ps1;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 *
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 * @author Alex Craig, Winter 2022
 * @author Ben Williams, Winter 2022
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Region brush; 					// the region which will act as the brush
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color brushColor;				// the color to put into the painting from the "brush"
	private boolean brushDown;				// boolean to determine whether brush is down
	private boolean eraser;					// boolean to determine whether eraser is active
	private RegionsTest baker;				// initialized here to allow RegionsTest to run once rather than repeatedly causing a strobing rainbow effect

	private BufferedImage brushStrokes;		// the image which holds the rgb values of pixels of all brush strokes
	private BufferedImage brushed;			// defines what pixels have been brushed (necessary in case rgb of brush is 0)
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		brushStrokes = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		brushed = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		finder = new RegionFinder();
		brushDown = true;
		eraser = false;
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		// Clear painting and brushes
		brushed = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		brushStrokes = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) throws Exception {
		if (displayMode == 'w') {
			g.drawImage(super.image, 0, 0, null);
		}
		if (displayMode == 'p') {
			g.drawImage(this.painting, 0, 0, null);
		}
		if (displayMode == 't') {
			g.drawImage(this.brushStrokes, 0, 0, null);
		}
		if (displayMode == 'r') {
			if (baker != null){
				g.drawImage(baker.getImage(), 0, 0, null);
			}
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */

	@Override
	public void processImage() throws Exception {
		// Wipe finder's previous regions and update the finder's image to current webcam output
		finder.wipeRegions();
		finder.setImage(super.image);

		// Look for regions of target color (if one has been set), and set the largest one to brush
		if (targetColor != null) {
			finder.findRegions(targetColor);
			brush = finder.largestRegion();
		}

		// Brushing / erasing loop (adds brush region to brushStokes)
		if (brush != null) {
			// Loop through all pixels in brush
			for (int i = 0; i < brush.getPoints().size(); i++) {
				Point p = brush.getPoint(i);
				if (eraser) {
					// For every point in brush, set point to unbrushed
					brushStrokes.setRGB(p.x, p.y, 0);
					brushed.setRGB(p.x, p.y, 0);
				} else if (brushDown) {
					// For every point in brush, set the rgb value to brushColor in brushStrokes
					brushStrokes.setRGB(p.x, p.y, brushColor.getRGB());
					brushed.setRGB(p.x, p.y, 1);
				}
			}
		}

		// Painting loop (combines brushStrokes with the webcam output to get painting)
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// If the current pixel has been brushed, set painting's rgb value to whatever it is in brushStrokes, otherwise just set it to the webcam output
				if (brushed.getRGB(x, y) == 1) {
					painting.setRGB(x, y, brushStrokes.getRGB(x, y));
				} else {
					painting.setRGB(x, y, image.getRGB(x, y));
				}
			}
		}
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// Set target color and brush color to whichever color we clicked
		if (image != null) {
			targetColor = new Color(image.getRGB(x, y));
			System.out.println("tracking: " + targetColor);
			if (brush != null) {
				brush.wipeRegion();
			}
			brushColor = targetColor;
		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'w' || k == 't') { // display: painting, recolored image, webcam, or brushStrokes
			displayMode = k;
			System.out.println("Display Mode: " + k);
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'b') { // Toggle brush
			brushDown = !brushDown;
			System.out.println("Brush-Down is: " + brushDown);
		}
		else if (k == 'e') { // Toggle eraser
			eraser = !eraser;
			System.out.println("Eraser is: " + eraser);
		}
		else if (k == 'o') { // save the recolored image
			if (baker != null){
				saveImage(baker.getImage(), "assets/images/recolored.jpeg", "png");
			}
			else{
				System.out.println("Recolored image does not exist yet. Press r to create one first.");
			}
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "assets/images/painting.jpeg", "png");
		}
		else if(k == 'r'){  // creates baker image with brick color as regions with random colors
			baker = new RegionsTest("baker", new RegionFinder(loadImage("assets/images/baker.jpeg")), new Color(130, 100, 100));
			displayMode = 'r';
		}
		else {
			System.out.println("unexpected key " + k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}