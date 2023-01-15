package ps6;

import java.util.TreeMap;

public class Sketch {
    private Integer ID;
    TreeMap<Integer, Shape> shapes;

    public Sketch() {
        ID = 0;
        shapes = new TreeMap<>();
    }
    
    public void addShape(Shape shape) {
        shapes.put(ID, shape);
        ID++;
    }

    public void toFront(int ID) {
        Shape shape = shapes.get(ID);
        shapes.remove(ID);
        this.addShape(shape);
    }

    public void removeShape(Integer ID) {
        shapes.remove(ID);
    }

    public TreeMap<Integer, Shape> getShapes() {
        return shapes;
    }
}
