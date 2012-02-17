package garethflowers.lattice;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * MainFrame Main frame for all views
 * 
 * @author GarethFlowers
 * @version 1.0
 */
class MainFrame extends JFrame implements ActionListener,
        ListSelectionListener, ChangeListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int[][] array_nodes;
    private double[][] array_results;
    private final JTabbedPane graph_tab_pane = new JTabbedPane();
    private boolean is_3d;
    private final Lattice lattice = new Lattice();
    private int lattice_numbers;
    // Create new variables and objects
    private int lattice_size;
    private final ArrayList<int[][]> list_array_nodes = new ArrayList<int[][]>(
            0);
    private final ArrayList<int[][]> list_array_results = new ArrayList<int[][]>(
            0);
    private final JList list_clusters = new JList();
    private final JList list_lattices = new JList();
    private final JPanel list_panel = new JPanel(new GridLayout(2, 1));
    private final JTabbedPane main_tab_pane = new JTabbedPane();
    private final JMenuBar menu_bar = new JMenuBar();
    private final JMenu menu_file = new JMenu("File");
    private final JMenuItem menu_file_exit = new JMenuItem("Exit");
    // new MenuShortcut(KeyEvent.VK_E));
    private final JMenuItem menu_file_load = new JMenuItem("Load Results");
    // new MenuShortcut(KeyEvent.VK_L));
    private final JMenuItem menu_file_new = new JMenuItem(
            "Generate New Lattices");
    // new MenuShortcut(KeyEvent.VK_N));
    private final JMenuItem menu_file_save = new JMenuItem("Save Results");
    // new MenuShortcut(KeyEvent.VK_S));
    private final JMenu menu_view = new JMenu("View");
    private final JMenuItem menu_view_cluster = new JMenuItem("Cluster");
    // new MenuShortcut(KeyEvent.VK_C));
    private final JMenu menu_view_graph = new JMenu("Graphs");
    private final JMenuItem menu_view_graph_1 = new JMenuItem(
            "Radius of Gyration / Length of Cluster");
    // new MenuShortcut(KeyEvent.VK_1));
    private final JMenuItem menu_view_graph_2 = new JMenuItem("Height / Width");
    // new MenuShortcut(KeyEvent.VK_2));
    private final JMenuItem menu_view_lattice = new JMenuItem("Lattice");
    private final JMenuItem menu_view_output = new JMenuItem("Show Output");
    // new MenuShortcut(KeyEvent.VK_O));
    private int percolating_cluster;
    private final JScrollPane scroll = new JScrollPane(this.view_output,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    private final Settings settings = new Settings(this, 10, 1);
    private final String spacer = "                    ";
    private ViewCluster view_cluster;
    private final ViewGraph view_graph_1 = new ViewGraph();
    private final ViewGraph view_graph_2 = new ViewGraph();
    private final ViewLattice view_lattice = new ViewLattice();
    // GUI Objects
    private final JTextArea view_output = new JTextArea();
    private JDialog wait_dialog;
    private final JButton wait_stop = new JButton("Stop Generating!");

    /**
     * Builds a new instance of the main frame user interface
     */
    public MainFrame() {
        try {
            KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_P,
                    InputEvent.CTRL_MASK);
            this.menu_view_output.setAccelerator(ctrlP);

            // Add Menu Items
            this.menu_file_new.addActionListener(this);
            this.menu_file.add(this.menu_file_new);
            this.menu_file.addSeparator();
            this.menu_file_load.addActionListener(this);
            this.menu_file.add(this.menu_file_load);
            this.menu_file_save.addActionListener(this);
            this.menu_file.add(this.menu_file_save);
            this.menu_file.addSeparator();
            this.menu_file_exit.addActionListener(this);
            this.menu_file.add(this.menu_file_exit);
            this.menu_bar.add(this.menu_file);
            this.menu_view_graph_1.addActionListener(this);
            this.menu_view_graph.add(this.menu_view_graph_1);
            this.menu_view_graph_2.addActionListener(this);
            this.menu_view_graph.add(this.menu_view_graph_2);
            this.menu_view.add(this.menu_view_graph);
            this.menu_view_lattice.addActionListener(this);
            this.menu_view.add(this.menu_view_lattice);
            this.menu_view_cluster.addActionListener(this);
            this.menu_view.add(this.menu_view_cluster);
            this.menu_view.addSeparator();
            this.menu_view_output.addActionListener(this);
            this.menu_view.add(this.menu_view_output);
            this.menu_bar.add(this.menu_view);

            // Create wait dialog
            this.wait_dialog = new JDialog(this, "");
            final JPanel wait_panel = new JPanel(new GridLayout(2, 1));
            final JLabel label = new JLabel(
                    "Please wait while the lattice information is being generated...");
            label.setFont(new Font("Arial", Font.BOLD, 14));
            wait_panel.add(label);
            // Added after evaluation
            this.wait_stop.addActionListener(this);
            wait_panel.add(this.wait_stop);
            this.wait_dialog.add(wait_panel);
            this.wait_dialog.pack();

            // Add lists down the left had side for lattices and clusters
            final JPanel lattice_panel = new JPanel(new BorderLayout());
            lattice_panel.add(new JLabel("Lattices"), BorderLayout.NORTH);
            lattice_panel.add(this.list_lattices);
            this.list_lattices.addListSelectionListener(this);
            final JPanel clusters_panel = new JPanel(new BorderLayout());
            clusters_panel.add(new JLabel("Clusters"), BorderLayout.NORTH);
            clusters_panel.add(this.list_clusters);
            this.list_clusters.addListSelectionListener(this);
            this.list_panel.add(lattice_panel);
            this.list_panel.add(clusters_panel);
            this.add(this.list_panel, BorderLayout.WEST);
            this.list_panel.setVisible(false);

            // Create tabs for different views
            this.main_tab_pane.addTab(this.spacer + "Output" + this.spacer,
                    this.scroll);
            this.main_tab_pane.addTab(this.spacer + "Graph" + this.spacer,
                    this.graph_tab_pane);
            this.graph_tab_pane.addTab(this.spacer
                    + "Radius of Gyration vs Length" + this.spacer,
                    this.view_graph_1);
            this.graph_tab_pane.addTab(this.spacer + "Height vs Width"
                    + this.spacer, this.view_graph_2);
            this.main_tab_pane.addTab(this.spacer + "2D Lattice" + this.spacer,
                    this.view_lattice);
            this.main_tab_pane.addTab(this.spacer + "3D Cluster" + this.spacer,
                    this.view_cluster);
            this.main_tab_pane.setSelectedIndex(0);
            this.main_tab_pane.addChangeListener(this);
            this.add(this.main_tab_pane);

            // Apply settings to the frame
            this.setJMenuBar(this.menu_bar);
            this.setTitle("Percolation Theory - Square Lattice Simulation");
            this.setResizable(true);
            this.setExtendedState(Frame.MAXIMIZED_BOTH);
            this.setVisible(true);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            this.get_lattice_info(10, 1, false);
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Run when a button or menu item is clicked
     * 
     * @param actionevent ActionEvent
     */
    @Override
    public void actionPerformed(final ActionEvent actionevent) {
        // Run when a menu item is selected
        if (actionevent.getSource() == this.menu_file_exit) {
            System.exit(0);
        } else if (actionevent.getSource() == this.menu_file_new) {
            this.settings.open();
            if (this.settings.get_changed()) {
                this.get_lattice_info(this.settings.get_size(),
                        this.settings.get_num(), this.settings.get_is_3d());
            }
        } else if (actionevent.getSource() == this.menu_file_save) {
            this.save_file();
        } else if (actionevent.getSource() == this.menu_file_load) {
            this.load_file();
        } else if (actionevent.getSource() == this.menu_view_output) {
            this.main_tab_pane.setSelectedIndex(0);
        } else if (actionevent.getSource() == this.menu_view_lattice) {
            this.main_tab_pane.setSelectedIndex(2);
        } else if (actionevent.getSource() == this.menu_view_cluster) {
            this.main_tab_pane.setSelectedIndex(3);
        } else if (actionevent.getSource() == this.menu_view_graph_1) {
            this.main_tab_pane.setSelectedIndex(1);
            this.graph_tab_pane.setSelectedIndex(0);
        } else if (actionevent.getSource() == this.menu_view_graph_2) {
            this.main_tab_pane.setSelectedIndex(1);
            this.graph_tab_pane.setSelectedIndex(1);
        } else if (actionevent.getSource() == this.wait_stop) {
            System.exit(0);
        }

    }

    /**
     * Creates new instance of each view
     */
    private void create_views() {
        this.get_lattice_results(0);
        this.array_nodes = this.list_array_nodes.get(0);

        this.view_cluster = new ViewCluster(this.array_nodes,
                this.lattice_size, this.percolating_cluster, this.is_3d);
        this.view_lattice.set(this.lattice_size, this.percolating_cluster,
                this.array_nodes);
        if (this.is_3d) {
            this.view_graph_1.set(this.lattice_size * this.lattice_size
                    * this.lattice_size, this.graph_tab_pane.getHeight(), 1,
                    this.list_array_results);
        } else {
            this.view_graph_1
                    .set(this.lattice_size * this.lattice_size,
                            this.graph_tab_pane.getHeight(), 1,
                            this.list_array_results);
        }
        this.view_graph_2.set(this.lattice_size,
                this.graph_tab_pane.getHeight(), 2, this.list_array_results);

        this.output();
    }

    /**
     * Empties cuurent arraylists and lists
     */
    private void empty_objects() {
        this.percolating_cluster = -1;
        this.list_lattices.removeAll();
        this.list_clusters.removeAll();
        this.list_array_nodes.clear();
        this.list_array_results.clear();
    }

    /**
     * Creates new lattices and gets all results using values from Settings()
     * 
     * @param size
     * @param num
     * @param dim
     */
    private void get_lattice_info(final int size, final int num,
            final boolean dim) {
        this.hide_gui();
        // Reset lattice variables to default
        this.lattice_size = size;
        this.lattice_numbers = num;
        this.is_3d = dim;
        this.empty_objects();

        for (int i = 0; i < this.lattice_numbers; i++) {
            // Add lattice information to arraylists
            this.lattice.generate(this.lattice_size, this.is_3d);
            this.array_nodes = this.lattice.get_nodes();
            this.list_array_nodes.add(this.array_nodes);
            this.array_results = this.lattice.get_results(this.array_nodes,
                    this.lattice_size);
            this.list_array_results.add(this.array_results);
            this.list_lattices.add((i + 1) + " (" + this.array_results.length
                    + ")");
        }

        this.create_views();
        this.show_gui();
    }

    /**
     * Gets lattice results from ViewLattice class
     * 
     * @param num int
     */
    private void get_lattice_results(final int num) {
        double temp_size = 0;
        this.array_results = this.list_array_results.get(num);
        this.list_clusters.removeAll();

        for (int i = 0; i < this.array_results.length; i++) {
            if ((this.array_results[i][9] > 0)
                    && (this.array_results[i][1] > temp_size)) {
                this.percolating_cluster = (int) this.array_results[i][0];
                temp_size = this.array_results[i][1];
            }
            this.list_clusters.add((i + 1) + " ("
                    + (int) this.array_results[i][1] + ")");
        }
    }

    /**
     * Hides all GUI before lattice is generated
     */
    private void hide_gui() {
        this.wait_dialog.setLocation(
                (this.getWidth() / 2) - (this.wait_dialog.getWidth() / 2),
                (this.getHeight() / 2) - (this.wait_dialog.getHeight() / 2));
        this.wait_dialog.setFont(new Font("Arial", Font.BOLD, 12));
        this.wait_dialog.setResizable(false);
        this.wait_dialog.setVisible(true);
        this.menu_file_new.setEnabled(false);
        this.menu_file_load.setEnabled(false);
        this.menu_file_save.setEnabled(false);
        this.menu_view.setEnabled(false);
        this.list_panel.setVisible(false);
        this.main_tab_pane.setVisible(false);
        this.repaint();
    }

    /**
     * Perform action when list selection changes
     * 
     * @param itemevent ItemEvent
     * @param itemevent thing
     */
    public void itemStateChanged(ListSelectionEvent itemevent) {
        int index = 0;

        // Run when an item in one of the list boxes is selected
        if (itemevent.getSource() == this.list_lattices) {
            index = this.list_lattices.getSelectedIndex();
            this.list_clusters.setSelectedIndex(0);
            this.array_nodes = this.list_array_nodes.get(index);
            this.get_lattice_results(index);

            // Update current view with new lattice
            this.view_lattice.set(this.lattice_size, this.percolating_cluster,
                    this.array_nodes);
            this.view_cluster = new ViewCluster(this.array_nodes,
                    this.lattice_size, (int) this.array_results[0][0],
                    this.is_3d);
        } else if (itemevent.getSource() == this.list_clusters) {
            index = this.list_clusters.getSelectedIndex();
            // Update current view with new lattice
            this.view_lattice
                    .set_cluster_number((int) this.array_results[index][0]);
            this.view_cluster = new ViewCluster(this.array_nodes,
                    this.lattice_size, (int) this.array_results[index][0],
                    this.is_3d);
        }

        final int current_view = this.main_tab_pane.getSelectedIndex();
        this.main_tab_pane.remove(3);
        this.main_tab_pane.add(this.view_cluster, this.spacer + "3D Cluster"
                + this.spacer);
        this.main_tab_pane.setSelectedIndex(current_view);

    }

    /**
     * Opens a load file dialog then populates nodes array from file information
     */
    private void load_file() {
        // Open a load file dialog
        final FileDialog filedialog = new FileDialog(this,
                "Load a saved Lattice...", FileDialog.LOAD);
        filedialog.setFile("*.lattice");
        filedialog.setLocation(50, 50);
        filedialog.setVisible(true);

        if (filedialog.getFile() != null) {
            try {
                this.hide_gui();

                // Reset lattice variables to default
                this.empty_objects();

                final FileReader filereader = new FileReader(
                        filedialog.getFile());
                final BufferedReader bufferedreader = new BufferedReader(
                        filereader);

                // Load information from file
                this.lattice_numbers = Integer.parseInt(bufferedreader
                        .readLine());
                this.lattice_size = Integer.parseInt(bufferedreader.readLine());
                int lattice_total_nodes = this.lattice_size * this.lattice_size;
                if (bufferedreader.readLine().equals("true")) {
                    this.is_3d = true;
                    lattice_total_nodes *= this.lattice_size;
                } else {
                    this.is_3d = false;
                }

                this.array_nodes = new int[lattice_total_nodes][2];

                for (int i = this.lattice_numbers; --i >= 0;) {
                    for (int j = this.array_nodes.length; --j >= 0;) {
                        this.array_nodes[j][0] = Integer
                                .parseInt(bufferedreader.readLine());
                        this.array_nodes[j][1] = Integer
                                .parseInt(bufferedreader.readLine());
                    }

                    this.list_array_nodes.add(this.array_nodes);
                    this.list_array_results.add(this.lattice.get_results(
                            this.array_nodes, this.lattice_size));
                    this.list_lattices.add(" Lattice - " + (i + 1) + " ");
                }

                bufferedreader.close();
                filereader.close();

                this.create_views();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
        this.show_gui();
    }

    /**
     * Outputs results to textarea
     */
    private void output() {
        final DecimalFormat dp1 = new DecimalFormat("0.##");
        final DecimalFormat dp0 = new DecimalFormat("0");
        final String bl = "\n";
        final String tb = "\t";
        final String line = "------------------------------------------"
                + "-----------------------------------------";
        final String dline = "=========================================="
                + "=========================================";

        this.view_output
                .setText("Number of Lattices :: "
                        + tb
                        + tb
                        + this.lattice_numbers
                        + bl
                        + "Lattice size :: "
                        + tb
                        + tb
                        + this.lattice_size
                        + bl
                        + "Number of nodes per latice :: "
                        + tb
                        + (this.lattice_size * this.lattice_size)
                        + bl
                        + "Total nodes analysed :: "
                        + tb
                        + (this.lattice_size * this.lattice_size * this.lattice_numbers)
                        + bl + bl + bl + dline + bl + "Lattice" + tb
                        + "Clusters" + tb + "Perc Clusters");

        for (int i = 0; i < this.lattice_numbers; i++) {
            int perc_cluster = 0;
            this.array_results = this.list_array_results.get(i);

            for (int j = this.array_results.length; --j >= 0;) {
                if (this.array_results[j][9] > 0) {
                    perc_cluster++;
                }
            }
            this.view_output.append(bl + (i + 1) + tb
                    + this.array_results.length + tb + tb + perc_cluster);
        }

        this.view_output.append(bl + dline + bl + bl);

        for (int i = 0; i < this.lattice_numbers; i++) {
            this.array_results = this.list_array_results.get(i);
            this.view_output.append(bl + "Lattice Number :: " + (i + 1) + bl
                    + bl + "Cluster" + tb + "Length" + tb + "Mass X" + tb
                    + "Mass Y" + tb + "Mass Z" + tb + "Width" + tb + "Height"
                    + tb + "Depth" + tb + "RadG" + tb + "Percolating");
            for (int j = 0; j < this.array_results.length; j++) {
                this.view_output.append(bl + (j + 1) + tb
                        + dp0.format(this.array_results[j][1]) + tb
                        + dp1.format(this.array_results[j][2]) + tb
                        + dp1.format(this.array_results[j][3]) + tb
                        + dp0.format(this.array_results[j][4]) + tb
                        + dp0.format(this.array_results[j][5]) + tb
                        + dp1.format(this.array_results[j][6]) + tb
                        + dp1.format(this.array_results[j][7]) + tb
                        + dp1.format(this.array_results[j][8]) + tb
                        + dp0.format(this.array_results[j][9]));
            }
            this.view_output.append(bl + bl + line + bl + bl);
        }
    }

    /**
     * Opens a saave file dialog, then saves the lattice arrays to file
     */
    private void save_file() {
        // Open save dialog
        final FileDialog filedialog = new FileDialog(this,
                "Save Lattice information ...", FileDialog.SAVE);
        filedialog.setLocation(50, 50);
        filedialog.setVisible(true);

        if (filedialog.getFile() != null) {
            try {
                final File file = new File(filedialog.getFile() + ".lattice");
                final FileOutputStream output = new FileOutputStream(file);
                final PrintStream printstream = new PrintStream(output);

                // print information to file
                printstream.println(this.lattice_numbers);
                printstream.println(this.lattice_size);
                printstream.println(this.is_3d);

                for (int i = this.lattice_numbers; --i >= 0;) {
                    this.array_nodes = this.list_array_nodes.get(i);
                    for (int j = this.array_nodes.length; --j >= 0;) {
                        printstream.println(this.array_nodes[j][0]);
                        printstream.println(this.array_nodes[j][1]);
                    }
                }

                // Close file after all information is in
                printstream.close();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Shows all GUI after lattice is generated
     */
    private void show_gui() {
        this.menu_file_new.setEnabled(true);
        this.menu_file_load.setEnabled(true);
        this.menu_file_save.setEnabled(true);
        this.menu_view.setEnabled(true);
        this.list_clusters.setSelectedIndex(0);
        this.list_lattices.setSelectedIndex(0);
        this.main_tab_pane.setVisible(true);
        this.main_tab_pane.setSelectedIndex(0);

        if (this.is_3d) {
            this.main_tab_pane.setEnabledAt(2, false);
            this.menu_view_lattice.setEnabled(false);
        } else {
            this.main_tab_pane.setEnabledAt(2, true);
            this.menu_view_lattice.setEnabled(true);
        }
        this.list_panel.setVisible(false);
        this.wait_dialog.setVisible(false);
    }

    /**
     * Run when the tabbed pane is changed
     * 
     * @param changeevent ChangeEvent
     */
    @Override
    public void stateChanged(final ChangeEvent changeevent) {
        final JTabbedPane pane = (JTabbedPane) changeevent.getSource();
        int index = this.list_clusters.getSelectedIndex();

        if (index < 0) {
            index = 0;
        }
        if (pane.getSelectedIndex() == 3) {
            this.view_cluster = new ViewCluster(this.array_nodes,
                    this.lattice_size, (int) this.array_results[index][0],
                    this.is_3d);
            this.view_cluster.repaint();
            this.list_panel.setVisible(true);
        } else if (pane.getSelectedIndex() == 2) {
            this.list_panel.setVisible(true);
        } else {
            this.list_panel.setVisible(false);
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub

    }
}
