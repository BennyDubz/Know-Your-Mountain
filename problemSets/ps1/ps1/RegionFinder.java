package ps1;

import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * @author Alex Craig, Winter 2022
 * @author Ben Williams, Winter 2022
 */
public class RegionFinder {
	private static final int maxColorDiff = 1200;			// how similar a pixel color must be to the target color, to belong to a region (40 for baker)
	private static final int minRegion = 80; 				// how many points in a region to be worth considering (80 for baker)

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored
	private BufferedImage visited;							// the image which keeps track of which pixels have been visited

	private ArrayList<Region> regions;						// the ArrayList which holds our regions
	private Stack queue;									// Stack which holds points to check for color matches


	public RegionFinder() {
		this.image = null;
		regions = new ArrayList<>();
		queue = new Stack();
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
		visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		regions = new ArrayList<>();
		queue = new Stack();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		this.visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	public void wipeRegions() { regions = new ArrayList<>(); }

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// Loops through all points in the image
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {

				// If the current pixel hasn't been visited
				if ((visited.getRGB(x,y) == 0)){
					// Set current pixel to visited
					visited.setRGB(x,y,1);
					// Gets color at current pixel
					Color colorCheck = new Color(image.getRGB(x,y));

					// If the current pixel color matches the target color, add it to the queue Stack and start a new region
					if (colorMatch(colorCheck,targetColor)) {
						Point firstPoint = new Point(x,y);
						queue.add(firstPoint);
						Region region = new Region();

						// While the queue is not empty continue loop which adds color matches in queue to region and adds their neighbors to queue
						while (!queue.empty()) {
							// Get first point on queue and remove it, then get color at that point
							Point p = (Point) queue.pop();
							Color pColor = new Color(image.getRGB(p.x,p.y));

							// If the color matches
							if (colorMatch(pColor,targetColor)) {
								// Loop through all adjacent neighbors
								for (int ny = Math.max(0, p.y - 1); ny <= Math.min(image.getHeight() - 1, p.y + 1); ny++) {
									for (int nx = Math.max(0, p.x - 1); nx <= Math.min(image.getWidth() - 1, p.x + 1); nx++) {
										// If neighbor is unvisited, then mark as visited and add it to queue
										if (visited.getRGB(nx,ny) == 0) {
											visited.setRGB(nx,ny,1);
											Point np = new Point(nx, ny);
											queue.add(np);
										}
									}
								}

								// Adds the point to the region if the color matches
								region.addPoint(p);
							}
						}

						// Adds the newly created region to regions arrayList if it meets required size
						if (region.getSize() > minRegion) {
							regions.add(region);
						}
					}
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// Use euclidean function to get a difference between two parameter colors
		int diff = (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed())
				+ (c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen())
				+ (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue());

		// If the difference is less than maximum acceptable color difference, return true
		return diff <= maxColorDiff;
	}

	/**
	 * Returns the largest region detected, if no regions, return null
	 */
	public Region largestRegion() {
		// If there aren't any regions, return null
		if (regions.size() <= 0) {
			return null;
		}

		// Declare a Region to store the largest region
		Region largestReg = new Region();

		// Loop through all regions, and if any of them are bigger than the current largest region, then set largestReg to that region
		for (Region reg : regions) {
			if (reg.getSize() > largestReg.getSize()) {
				largestReg = reg;
			}
		}

		// Return the largest region found
		return largestReg;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);

		// Now recolor the regions in it
		for (Region reg : regions) {
			// Set each region to random color
			int v = (int)(Math.random() * 16777217);
			for (Point p : reg.getPoints()) {
				recoloredImage.setRGB(p.x,p.y,v);
			}
		}
	}

}
