package garethflowers.lattice;

// ViewLattice

// Import Required Libraries
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * ViewLattice Panel object added to the main frame Draws a 2d lattice and
 * allows cluster highlighting
 * 
 * @author Gareth Flowers
 * @version 1.0
 */
class ViewLattice extends Canvas implements MouseListener {

    static final long serialVersionUID = 1L;
    private final int display_node_distance = 5; // Distance between node points
                                                 // (pixels)
    private final int display_offset = 10; // Offset from top left (pixels)
    private int view_cluster_number = -666;
    private int lattice_size;
    private int percolating_cluster;
    private final int select_distance = 3;
    private Graphics graphics;
    private int[][] array_nodes;

    /**
     * Creates new instance of ViewLattice
     */
    public ViewLattice() {
        try {
            // Initialise object
            setBackground(Color.WHITE);
            addMouseListener(this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param size
     * @param perc_cluster
     * @param nodes
     */
    protected void set(int size, int perc_cluster, int[][] nodes) {
        // Set variables
        array_nodes = nodes;
        lattice_size = size;
        percolating_cluster = perc_cluster;
        repaint();
    }

    /**
     * Run on mouse click
     * 
     * @param mouseevent MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent mouseevent) {
        // Set cluster as the one nearest the mouse click
        for (int i = array_nodes.length; --i >= 0;) {
            if ((mouseevent.getX() > display_x(i) - select_distance)
                    && (mouseevent.getX() < display_x(i) + select_distance)
                    && (mouseevent.getY() > display_y(i) - select_distance)
                    && (mouseevent.getY() < display_y(i) + select_distance)) {
                view_cluster_number = array_nodes[i][1];
            }
        }
        repaint();
    }

    /**
     * Run when mouse button pressed
     * 
     * @param mouseevent MouseEvent
     */
    @Override
    public void mousePressed(MouseEvent mouseevent) {
    }

    /**
     * Run when mouse button released
     * 
     * @param mouseevent MouseEvent
     */
    @Override
    public void mouseReleased(MouseEvent mouseevent) {
    }

    /**
     * Run when mouse leaves area
     * 
     * @param mouseevent MouseEvent
     */
    @Override
    public void mouseExited(MouseEvent mouseevent) {
    }

    /**
     * Run when mouse enters area
     * 
     * @param mouseevent MouseEvent
     */
    @Override
    public void mouseEntered(MouseEvent mouseevent) {
    }

    /**
     * Return the X coordinate from the node
     * 
     * @param node int
     * @return int
     */
    private int find_x(int node) {
        // Return the X coordinate for the position number
        return node % lattice_size;
    }

    /**
     * Return the Y coordinate from the node
     * 
     * @param node int
     * @return int
     */
    private int find_y(int node) {
        // Return the Y coordinate for the position number
        return node / lattice_size;
    }

    /**
     * Return the display position of X from the node
     * 
     * @param node int
     * @return int
     */
    private int display_x(int node) {
        // Return the X coordinate onscreen display position for the position
        // number
        return display_offset + (find_x(node) * display_node_distance);
    }

    /**
     * Return the display position of Y from the node
     * 
     * @param node int
     * @return int
     */
    private int display_y(int node) {
        // Return the Y coordinate onscreen display position for the position
        // number
        return display_offset + (find_y(node) * display_node_distance);
    }

    /**
     * Draw the lattice on the graphics area
     * 
     * @param cluster int
     */
    protected void draw_lattice(int cluster) {
        for (int i = array_nodes.length; --i >= 0;) {
            if (array_nodes[i][0] != -666) {
                // Draw lines as links between nodes
                if (array_nodes[i][1] == cluster) {
                    // Draw selected cluster in red
                    graphics.setColor(Color.RED);
                    graphics.drawLine(display_x(i), display_y(i),
                            display_x(array_nodes[i][0]),
                            display_y(array_nodes[i][0]));
                } else if ((array_nodes[i][1] == percolating_cluster)) {
                    // Draw percolating cluster in blue
                    graphics.setColor(Color.BLUE);
                    graphics.drawLine(display_x(i), display_y(i),
                            display_x(array_nodes[i][0]),
                            display_y(array_nodes[i][0]));
                } else {
                    // Draw all other clusters in black
                    graphics.setColor(Color.BLACK);
                    graphics.drawLine(display_x(i), display_y(i),
                            display_x(array_nodes[i][0]),
                            display_y(array_nodes[i][0]));
                }
            }
        }
    }

    /**
     * Update the graphics area
     * 
     * @param graphic Graphics
     */
    @Override
    public void paint(Graphics graphic) {
        // Called when this object is repainted
        graphics = graphic;
        draw_lattice(view_cluster_number);
    }

    /**
     * Return the current cluster number
     * 
     * @return int
     */
    protected int get_cluster_number() {
        // Return the current selected cluster
        return view_cluster_number;
    }

    /**
     * Set the current cluster number
     * 
     * @param cluster_number int
     */
    protected void set_cluster_number(int cluster_number) {
        // Set the current selected cluster
        view_cluster_number = cluster_number;
        repaint();
    }
}
