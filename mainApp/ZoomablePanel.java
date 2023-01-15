
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderableImage;
import javax.swing.JPanel;

/**
 * @author Thanasis1101
 *
 * @author Ben Williams, March 2022
 * - Added max zoom in/out
 * - Added max drag to left and right to prevent going too far off-screen
 */
public class ZoomablePanel extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {

    protected BufferedImage image;
    private double zoomFactor = 0.25;
    private double prevZoomFactor = 0.25;
    private boolean zoomer;
    private boolean dragger;
    private boolean released;
    private double xOffset = 0;
    private double yOffset = 0;
    private int xDiff;
    private int yDiff;
    private Point startPoint;

    public ZoomablePanel(BufferedImage image) {
        //zoomFactor = zoomFactor/(getWidth()/image.getWidth());
        this.image = image;
        initComponent();

    }

    private void initComponent() {
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        //super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (zoomer) {
            AffineTransform at = new AffineTransform();
            double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();
            double zoomDiv = zoomFactor / prevZoomFactor;

            xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
            yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;

            at.translate(xOffset, yOffset);
            at.scale(zoomFactor, zoomFactor);
            prevZoomFactor = zoomFactor;
            g2.transform(at);

            zoomer = false;
        } else if (dragger) {
            AffineTransform at = new AffineTransform();

            // Max xOffset -  left of the screen
            if (xOffset >= (zoomFactor * image.getWidth())){
                xOffset = (zoomFactor * image.getWidth());
            }

            // Min xOffset - right of the screen
            if (xOffset <= ((zoomFactor * -image.getWidth()))) {
                xOffset = ((zoomFactor * -image.getWidth()));
            }

            // Max yOffset to the top of the screen
            if (yOffset >= (zoomFactor * image.getHeight())){
                yOffset = (zoomFactor * image.getHeight());
            }
            // Max yOffset to the bottom of the screen
            if (yOffset <= (zoomFactor * -image.getHeight())){
                yOffset = (zoomFactor * -image.getHeight());
            }

            at.translate(xOffset + xDiff, yOffset + yDiff);
            if (zoomFactor != 1) at.scale(zoomFactor, zoomFactor);
            g2.transform(at);
            if (released) {

                xOffset += xDiff;
                yOffset += yDiff;

                dragger = false;
            }
        } else {
            AffineTransform at = new AffineTransform();
            at.translate(xOffset, yOffset);
            at.scale(zoomFactor, zoomFactor);
            g2.transform(at);
        }

        // All drawings go here
        g2.drawImage(image, 0, 0, this);

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        zoomer = true;
        //Tweaked to add max zooms
        //Zoom in
        if (e.getWheelRotation() < 0 && zoomFactor <= 6) {
            zoomFactor *= 1.1;
            repaint();
        }
        //Zoom out
        if (e.getWheelRotation() > 0 && zoomFactor > 0.25) {
            zoomFactor /= 1.1;
            if (zoomFactor < 0.25) zoomFactor = 0.25;
            repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point curPoint = e.getLocationOnScreen();
        xDiff = curPoint.x - startPoint.x;
        yDiff = curPoint.y - startPoint.y;

        dragger = true;
        repaint();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Used to prevent the teleporting back to original position, even if a click is not actually a drag
        Point curPoint = e.getLocationOnScreen();
        xDiff = curPoint.x - startPoint.x;
        yDiff = curPoint.y - startPoint.y;
        zoomer = false;
        dragger = true;
        repaint();

    }

    @Override
    public void mousePressed(MouseEvent e) {
        released = false;
        startPoint = MouseInfo.getPointerInfo().getLocation();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        released = true;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public double getxOffset() {
        return xOffset;
    }

    public double getyOffset() {
        return yOffset;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    public void resetZoomAndOffset() {
        zoomFactor = 0.5;
        prevZoomFactor = 0.5;
        xOffset = 0;
        yOffset = 0;
        repaint();
    }

}