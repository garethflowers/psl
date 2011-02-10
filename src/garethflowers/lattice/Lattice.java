package garethflowers.lattice;

// Lattice

// Import Required Libraries
import java.util.ArrayList;

/**
 * <p>Title: Lattice</p>
 * <p>Description: Generates a new lattice and adds random clusters to it.</p>
 * <p>Copyright: Gareth Flowers Copyright (c) 2005</p>
 * <p>Company: Leeds Metropolitan University</p>
 * @author Gareth Flowers
 * @version 1.0
 */
class Lattice {

    public Lattice() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    final private int lattice_incoming_nodes = 2; // Total incoming nodes per node
    private int lattice_size; // Size of lattice in nodes
    private int lattice_total_nodes;
    private boolean is_3d;
    private int[][] nodes;
    private double[][] cluster;
    private ArrayList clusters = new ArrayList(0);

    /**
     * When run creates a new lattice and adds links to it
     * @param size int
     * @param d3 boolean
     */
    protected void generate(int size, boolean d3) {
        lattice_size = size;
        lattice_total_nodes = size * size;
        is_3d = d3;
        if (is_3d) {
            lattice_total_nodes *= size;
        }

        nodes = new int[lattice_total_nodes][3];

        // Creates new lattice
        for (int i = lattice_total_nodes; --i >= 0;) {
            // Set up nodes with empty data (-1)
            nodes[i][0] = -1;
            nodes[i][1] = -1;
            nodes[i][2] = 0;
            clusters.add(new Integer(i));
        }

        // Add links between nodes
        add_links();
    }

    /**
     * Returns the nodes array
     *
     * <br /> nodes[0] = Node Number Connected To
     * <br /> nodes[1] = Cluster Number
     *
     * @return int[][]
     */
    protected int[][] get_nodes() {
        return nodes;
    }

    /**
     * Returns a random number between low and high
     * @param low int
     * @param high int
     * @return int
     */
    private static int rand(int low, int high) {
        return (int) (Math.random() * (high - low + 1) + low);
    }

    /**
     * Return the X coordinate for the position number
     * @param node int
     * @return int
     */
    private int find_x(int node) {
        return node % lattice_size;
    }

    /**
     * Return the Y coordinate for the position number
     * @param node int
     * @return int
     */
    private int find_y(int node) {
        if (is_3d) {
            return (node
                    - ((lattice_size * lattice_size)
                    * (node / (lattice_size * lattice_size))))
                    / lattice_size;
        } else {
            return node / lattice_size;
        }
    }

    /**
     * Return the Z coordinate for the position number
     * @param node int
     * @return int
     */
    private int find_z(int node) {
        return node / (lattice_size * lattice_size);
    }

    /**
     * Return the node number from the x,y coordinates
     * @param point_x int
     * @param point_y int
     * @return int
     */
    private int find_node(int point_x, int point_y) {
        return (point_y * lattice_size) + point_x;
    }

    /**
     * Return the node number from the x,y coordinates
     * @param point_x int
     * @param point_y int
     * @param point_z int
     * @return int
     */
    private int find_node(int point_x, int point_y, int point_z) {
        return (point_z * lattice_size * lattice_size) + (point_y * lattice_size)
                + point_x;
    }

    /**
     * Check for a valid coordinate
     * @param px int
     * @param py int
     * @param node_from int
     * @return boolean
     */
    private boolean valid_coordinate(int px, int py, int node_from) {
        int node_to = find_node(px, py);

        try {
            if ((px >= lattice_size) || (py >= lattice_size) || (py < 0)
                    || (px < 0) || (nodes[node_to][0] == node_from)
                    || (nodes[node_to][2] >= lattice_incoming_nodes)
                    || ((nodes[node_from][1] == nodes[node_to][1])
                    && (nodes[node_to][1] != -1))) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check for a valid coordinate
     * @param px int
     * @param py int
     * @param pz int
     * @param node_from int
     * @return boolean
     */
    private boolean valid_coordinate(int px, int py, int pz, int node_from) {
        int node_to = find_node(px, py, pz);

        try {
            if (((nodes[node_from][1] == nodes[node_to][1])
                    && (nodes[node_to][1] != -1)) || (px >= lattice_size)
                    || (py >= lattice_size) || (py < 0) || (px < 0)
                    || (nodes[node_to][0] == node_from)
                    || (nodes[node_to][2] >= lattice_incoming_nodes)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the cluster results array
     *
     * <br /> cluster[0] = Cluster Number
     * <br /> cluster[1] = Weight (number nodes)
     * <br /> cluster[2] = Mass X
     * <br /> cluster[3] = Mass Y
     * <br /> cluster[4] = Mass Z
     * <br /> cluster[5] = Width
     * <br /> cluster[6] = Height
     * <br /> cluster[7] = Depth
     * <br /> cluster[8] = Radius Gyration
     * <br /> cluster[9] = Percolating
     *
     * @param nodes int[][]
     * @param size int
     * @return double[][]
     */
    protected double[][] get_results(int[][] nodes, int size) {
        lattice_size = size;
        clusters.clear();

        for (int i = nodes.length; --i >= 0;) {
            if (!clusters.contains(new Integer(nodes[i][1]))) {
                clusters.add(new Integer(nodes[i][1]));
            }
        }

        int cluster_size = clusters.size();
        cluster = new double[cluster_size][10];

        for (int i = cluster_size; --i >= 0;) {
            int total = 0, number_nodes = 0;
            int min_x = -1, min_y = -1, min_z = -1;
            int max_x = -1, max_y = -1, max_z = -1;
            int point_x = 0, point_y = 0, point_z = 0;
            double sum_x = 0, sum_y = 0, sum_z = 0;
            double mass_x = 0, mass_y = 0, mass_z = 0;
            cluster[i][0] = ((Integer) clusters.get(i)).intValue();

            for (int j = nodes.length; --j >= 0;) {
                if (nodes[j][1] == (int) cluster[i][0]) {
                    point_x = find_x(j);
                    point_y = find_y(j);
                    point_z = find_z(j);

                    sum_x += point_x;
                    sum_y += point_y;
                    sum_z += point_z;
                    number_nodes++;

                    if (max_x == -1) {
                        min_x = point_x;
                        min_y = point_y;
                        min_z = point_z;
                        max_x = point_x;
                        max_y = point_y;
                        max_z = point_z;
                    }

                    if (point_x > max_x) {
                        max_x = point_x;
                    } else if (point_x < min_x) {
                        min_x = point_x;
                    }
                    if (point_y > max_y) {
                        max_y = point_y;
                    } else if (point_y < min_y) {
                        min_y = point_y;
                    }
                    if (point_z > max_z) {
                        max_z = point_z;
                    } else if (point_z < min_z) {
                        min_z = point_z;
                    }
                }
            }

            mass_x = sum_x / number_nodes;
            mass_y = sum_y / number_nodes;
            mass_z = sum_z / number_nodes;
            cluster[i][1] = number_nodes;
            cluster[i][2] = mass_x;
            cluster[i][3] = mass_y;
            cluster[i][4] = mass_z;
            cluster[i][5] = max_x - min_x + 1;
            cluster[i][6] = max_y - min_y + 1;
            cluster[i][7] = max_z - min_z + 1;

            // Radius of Gyration
            for (int j = nodes.length; --j >= 0;) {
                if (nodes[j][1] == (int) cluster[i][0]) {
                    total += ((find_x(j) - mass_x) * (find_x(j) - mass_x))
                            + ((find_y(j) - mass_y) * (find_y(j) - mass_y));
                    if (is_3d) {
                        total += ((find_z(j) - mass_z) * (find_z(j) - mass_z));
                    }
                }
            }
            // Square results for radius of gyration
            cluster[i][8] = Math.sqrt(total / number_nodes);

            // Record a '1' if a percolating cluster
            // otherwise '0' if not
            if (((min_x == 0) && (max_x == lattice_size - 1)) || ((min_y == 0)
                    && (max_y == lattice_size - 1))) {
                cluster[i][9] = 1;
            } else {
                cluster[i][9] = 0;
            }
        }
        clusters.clear();

        return cluster;
    }

    /**
     *  Creates new links between nodes and assigns the relevant cluster number
     */
    private void add_links() {
        // function to search random nodes and add links
        int cluster_number = 0;

        while (clusters.size() > 0) {
            // Find a node from list of nodes left to have links added
            int node_from_index = rand(0, clusters.size() - 1);
            int node_from = ((Integer) clusters.get(node_from_index)).intValue();

            ArrayList valid_points = new ArrayList(0);

            int point_x = find_x(node_from);
            int point_y = find_y(node_from);

            // Check for valid surrounding nodes
            if (is_3d) {
                int point_z = find_z(node_from);
                if (valid_coordinate(point_x + 1, point_y, point_z, node_from)) {
                    valid_points.add(new Integer(find_node(point_x + 1, point_y,
                            point_z)));
                }
                if (valid_coordinate(point_x - 1, point_y, point_z, node_from)) {
                    valid_points.add(new Integer(find_node(point_x - 1, point_y,
                            point_z)));
                }
                if (valid_coordinate(point_x, point_y + 1, point_z, node_from)) {
                    valid_points.add(new Integer(find_node(point_x, point_y + 1,
                            point_z)));
                }
                if (valid_coordinate(point_x, point_y - 1, point_z, node_from)) {
                    valid_points.add(new Integer(find_node(point_x, point_y - 1,
                            point_z)));
                }
                if (valid_coordinate(point_x, point_y, point_z + 1, node_from)) {
                    valid_points.add(new Integer(find_node(point_x, point_y,
                            point_z + 1)));
                }
                if (valid_coordinate(point_x, point_y, point_z - 1, node_from)) {
                    valid_points.add(new Integer(find_node(point_x, point_y,
                            point_z - 1)));
                }
            } else {
                if (valid_coordinate(point_x + 1, point_y, node_from)) {
                    valid_points.add(new Integer(find_node(point_x + 1, point_y)));
                }
                if (valid_coordinate(point_x - 1, point_y, node_from)) {
                    valid_points.add(new Integer(find_node(point_x - 1, point_y)));
                }
                if (valid_coordinate(point_x, point_y + 1, node_from)) {
                    valid_points.add(new Integer(find_node(point_x, point_y + 1)));
                }
                if (valid_coordinate(point_x, point_y - 1, node_from)) {
                    valid_points.add(new Integer(find_node(point_x, point_y - 1)));
                }
            }

            if (valid_points.size() > 0) {
                // If there is a valid node
                int node_to = ((Integer) valid_points.get(rand(0,
                        valid_points.size() - 1))).intValue();

                nodes[node_from][0] = node_to;

                // Assign a cluster number
                if ((nodes[node_from][1] > -1) && (nodes[node_to][1] > -1)) {
                    if (nodes[node_to][1] > nodes[node_from][1]) {
                        replace_nodes(nodes[node_to][1], node_from);
                    } else if (nodes[node_to][1] < nodes[node_from][1]) {
                        replace_nodes(nodes[node_from][1], node_to);
                    }
                } else if ((nodes[node_from][1] > -1)
                        && (nodes[node_to][1] == -1)) {
                    nodes[node_to][1] = nodes[node_from][1];
                } else if ((nodes[node_from][1] == -1)
                        && (nodes[node_to][1] > -1)) {
                    nodes[node_from][1] = nodes[node_to][1];
                } else {
                    cluster_number++;
                    nodes[node_from][1] = cluster_number;
                    nodes[node_to][1] = cluster_number;
                }
                nodes[node_to][2]++;
            } else {
                // if there is no valid node
                nodes[node_from][0] = -666;
            }

            // Remove node number from list of nodes left to add links to
            clusters.remove(node_from_index);
        }
    }

    /**
     * Replaces all instances of 'from' with 'to' in the 'nodes' array
     * @param from int
     * @param to int
     */
    private void replace_nodes(int from, int to) {
        for (int i = lattice_total_nodes; --i >= 0;) {
            if (nodes[i][1] == from) {
                nodes[i][1] = nodes[to][1];
            }
        }
    }

    private void jbInit() throws Exception {
    }
}
