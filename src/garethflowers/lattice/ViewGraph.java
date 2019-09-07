package garethflowers.lattice;

// ViewGraph
// Import Required Libraries
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.util.ArrayList;

/**
 * ViewGraph Draw a graph using input results
 *
 * @author Gareth Flowers
 * @version 1.0
 */
class ViewGraph extends Panel {

	private static final long serialVersionUID = 1L;
	private int graph_type;
	private float graph_size;
	final private float graph_offset = 60f;
	private int lattice_size;
	private ArrayList list_array_results;
	private double[][] array_results;
	private Graphics graphics;

	/**
	 * Creates new instance of ViewGraph
	 */
	public ViewGraph() {
		try {
			setBackground(Color.WHITE);
			setLayout(new BorderLayout());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Set the graph variables
	 *
	 * @param size int
	 * @param height int
	 * @param type int
	 * @param results ArrayList
	 */
	protected void set(int size, int height, int type, ArrayList<?> results) {
		// Set variables
		lattice_size = size;
		list_array_results = results;
		graph_size = (height - (3f * graph_offset)) / lattice_size;
		graph_type = type;
	}

	/**
	 * Draw graph on graphics component
	 */
	private void draw_graph() {
		float xc = graph_offset;
		float yc = (lattice_size * graph_size) + graph_offset;
		int size = 0;

		switch (graph_type) {
			case 1:
				// Display Length vs Radius Gyration Graph
				axis((int) xc, (int) yc, "Length", "Radius of Gyration");
				size = list_array_results.size();
				for (int i = size; --i >= 0;) {
					array_results = (double[][]) list_array_results.get(i);
					int length = array_results.length;
					for (int j = length; --j >= 0;) {
						graphics.fillOval((int) (xc + array_results[j][1]),
								(int) ((yc - array_results[j][8]) - 1.5), 3, 3);
					}
				}
				break;
			case 2:
				// Display Width vs Height Graph
				axis((int) xc, (int) yc, "Width", "Height");
				size = list_array_results.size();
				for (int i = size; --i >= 0;) {
					array_results = (double[][]) list_array_results.get(i);
					int length = array_results.length;
					for (int j = length; --j >= 0;) {
						graphics.fillOval((int) (xc + array_results[j][5]
								* graph_size), (int) (yc - array_results[j][6]
								* graph_size), 3, 3);
					}
				}
				break;
			default:
		}
	}

	/**
	 * Draw and label axis
	 *
	 * @param xc int
	 * @param yc int
	 * @param x_label String
	 * @param y_label String
	 */
	private void axis(float xc, float yc, String x_label, String y_label) {
		// Draw x and y axis
		graphics.drawLine((int) xc, (int) yc, (int) xc,
				(int) (yc - (lattice_size * graph_size)));
		graphics.drawLine((int) xc, (int) yc,
				(int) (xc + (lattice_size * graph_size)), (int) yc);

		// Add Labels to axis
		graphics.drawString(x_label, (int) xc, (int) (yc + 30f));
		draw_string_vert(y_label, xc - 30f, graph_offset);

		// Add values to axis
		graphics.drawString("0", (int) xc, (int) (yc + 15f));
		graphics.drawString("" + lattice_size,
				(int) (xc + (lattice_size * graph_size)), (int) (yc + 10f));
		graphics.drawString("0", (int) (xc - 15f), (int) yc);
		graphics.drawString("" + lattice_size, (int) (xc - 15f),
				(int) (yc - (lattice_size * graph_size)));
		graphics.drawString("Graph showing " + x_label + " vs " + y_label, 20,
				20);
	}

	/**
	 * Converts a string into seperate letters and writes each letter in a
	 * vertical line (from top to bottom)
	 *
	 * @param text String
	 * @param xc int
	 * @param yc int
	 */
	private void draw_string_vert(String text, float xc, float yc) {
		//
		float font_height = graphics.getFontMetrics(getFont()).getHeight() + 1f;
		int j = 0;
		int k = text.length();

		while (j < k + 1) {
			if (j == k) {
				graphics.drawString(text.substring(j), (int) xc,
						(int) (yc + (j * font_height)));
			} else {
				graphics.drawString(text.substring(j, j + 1), (int) xc,
						(int) (yc + (j * font_height)));
			}
			j++;
		}
	}

	/**
	 * Update Graphics
	 *
	 * @param graphic Graphics
	 */
	@Override
	public void paint(Graphics graphic) {
		// On object repaint
		graphics = graphic;
		draw_graph();
	}
}
