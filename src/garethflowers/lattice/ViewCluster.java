package garethflowers.lattice;

// ViewCluster
// Import Required Libraries
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;

/**
 * ViewCluster Produces a 3D view of a cluster
 *
 * @author Gareth Flowers
 * @version 1.0
 */
class ViewCluster extends Panel {

    private static final long serialVersionUID = 1L;
    private int lattice_size;
    private int cluster;
    private int[][] array_nodes;
    private boolean is_3d;

    /**
     * Creates new instance of ViewCluster and sets up VirtualEnvironment
     *
     * @param nodes int[][]
     * @param size int
     * @param num int
     * @param d3 boolean
     */
    public ViewCluster(int[][] nodes, int size, int num, boolean d3) {
        array_nodes = nodes;
        lattice_size = size;
        cluster = num;
        is_3d = d3;

        GraphicsConfigTemplate3D gcTemplate = new GraphicsConfigTemplate3D();
        GraphicsEnvironment local = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screen = local.getDefaultScreenDevice();
        GraphicsConfiguration configuration = screen.getBestConfiguration(gcTemplate);
        Canvas3D canvas_3d = new Canvas3D(configuration);

        Locale locale = new Locale(new VirtualUniverse());
        locale.addBranchGraph(build_view(canvas_3d));
        locale.addBranchGraph(build_content());
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        Panel info_panel = new Panel(new GridLayout(2, 1));
        info_panel.setBackground(Color.WHITE);
        info_panel.setFont(new Font("Arial", Font.BOLD, 12));
        Panel info_panel_1 = new Panel();
        info_panel_1.add(new Label(
                "Select a Lattice, then a cluster from the list on the left"));
        info_panel.add(info_panel_1);
        Panel info_panel_2 = new Panel(new GridLayout(1, 3));
        info_panel_2.add(new Label("Left-Mouse button = Rotate Cluster"));
        info_panel_2.add(new Label("Right-Mouse button = Move cluster"));
        info_panel_2.add(new Label(
                "Left-Mouse button + Left-Alt key = Zoom In or Out"));
        info_panel.add(info_panel_2);
        add(BorderLayout.NORTH, info_panel);
        add(BorderLayout.CENTER, canvas_3d);
    }

    /**
     * Returns the X coordinate of the given node
     *
     * @param node int
     * @return int
     */
    private int find_x(int node) {
        return node % lattice_size;
    }

    /**
     * Returns the Y coordinate of the given node
     *
     * @param node int
     * @return int
     */
    private int find_y(int node) {
        if (is_3d) {
            // Return the 2D Y coordinate for the position number
            return (node - ((lattice_size * lattice_size) * (node / (lattice_size * lattice_size))))
                    / lattice_size;

        } else {
            // Return the 3D Y coordinate for the position number
            return node / lattice_size;
        }
    }

    /**
     * Returns the Z coordinate of the given node
     *
     * @param node int
     * @return int
     */
    private int find_z(int node) {
        // Return the Z coordinate for the position number
        return node / (lattice_size * lattice_size);
    }

    /**
     * Sets up a view for the 3D viewing
     *
     * @param canvas Canvas3D
     * @return BranchGroup
     */
    protected BranchGroup build_view(Canvas3D canvas) {
        BranchGroup view = new BranchGroup();
        Transform3D view_transform = new Transform3D();
        view_transform.set(new Vector3f(0.0f, 0.0f, 5.0f));
        TransformGroup group = new TransformGroup(view_transform);
        ViewPlatform platform = new ViewPlatform();
        PhysicalBody body = new PhysicalBody();
        PhysicalEnvironment environment = new PhysicalEnvironment();

        group.addChild(platform);
        view.addChild(group);
        View my_view = new View();
        my_view.addCanvas3D(canvas);
        my_view.attachViewPlatform(platform);
        my_view.setPhysicalBody(body);
        my_view.setPhysicalEnvironment(environment);
        return view;
    }

    /**
     * Sets up a view for the 3D content
     *
     * @return BranchGroup
     */
    private BranchGroup build_content() {
        int max_x = -1, max_y = -1, max_z = -1;
        int min_x = -1, min_y = -1, min_z = -1;
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                100.0);
        Appearance appearance = new Appearance();
        BranchGroup content = new BranchGroup();
        TransformGroup group = new TransformGroup(new Transform3D());
        group.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        group.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        appearance.setMaterial(new Material(new Color3f(1f, 0f, 0f),
                new Color3f(1f, 0f, 0f), new Color3f(1f, 1f, 1f), new Color3f(
                1f, 0f, 0f), 80f));

        for (int i = 0; i < array_nodes.length; i++) {
            if (array_nodes[i][1] == cluster) {
                if (max_x == -1) {
                    max_x = find_x(i);
                    max_y = find_y(i);
                    max_z = find_z(i);
                    min_x = find_x(i);
                    min_y = find_y(i);
                    min_z = find_z(i);
                }
                if (find_x(i) > max_x) {
                    max_x = find_x(i);
                }
                if (find_x(i) < min_x) {
                    min_x = find_x(i);
                }
                if (find_y(i) > max_y) {
                    max_y = find_y(i);
                }
                if (find_y(i) < min_y) {
                    min_y = find_y(i);
                }
                if (find_z(i) > max_z) {
                    max_z = find_z(i);
                }
                if (find_z(i) < min_z) {
                    min_z = find_z(i);
                }

            }

        }

        float x_set = min_x + ((max_x - min_x) / 2.0f);
        float y_set = min_y + ((max_y - min_y) / 2.0f);
        float z_set = min_z + ((max_z - min_z) / 2.0f);
        float cylinder_radius = 0.007f;
        float sphere_radius = 0.035f;
        float distance = 0.175f;
        float half_distance = 0.0875f;

        for (int i = 0; i < array_nodes.length; i++) {
            if ((array_nodes[i][1] == cluster)) {
                float xc = (find_x(i) - x_set) * distance;
                float yc = (find_y(i) - y_set) * distance;
                float zc = 0;

                if (is_3d) {
                    zc = (find_z(i) - z_set) * distance;
                }

                // Create a sphere to represent nodes
                Transform3D trans_sphere = new Transform3D();
                trans_sphere.setTranslation(new Vector3f(xc, yc, zc));
                TransformGroup spheres = new TransformGroup(trans_sphere);
                spheres.addChild(new Sphere(sphere_radius, appearance));
                group.addChild(spheres);

                // Creates a cyclinder to represent link
                // Rotate cylinder to correct orientation
                if (array_nodes[i][0] != -666) {
                    Transform3D trans_cylinder = new Transform3D();
                    if (array_nodes[i][0] <= i - (lattice_size * lattice_size)) {
                        trans_cylinder.setTranslation(new Vector3f(xc, yc, zc
                                - half_distance));
                        trans_cylinder.setRotation(new AxisAngle4f(0, 1, 1,
                                3.14159f));
                    } else if (array_nodes[i][0] >= i
                            + (lattice_size * lattice_size)) {
                        trans_cylinder.setTranslation(new Vector3f(xc, yc, zc
                                + half_distance));
                        trans_cylinder.setRotation(new AxisAngle4f(0, 1, 1,
                                9.42477f));
                    } else if (array_nodes[i][0] < i - 1) {
                        trans_cylinder.setTranslation(new Vector3f(xc, yc
                                - half_distance, zc));
                        trans_cylinder.setRotation(new AxisAngle4f(1, 1, 0,
                                6.28318f));
                    } else if (array_nodes[i][0] > i + 1) {
                        trans_cylinder.setTranslation(new Vector3f(xc, yc
                                + half_distance, zc));
                    } else if (array_nodes[i][0] < i) {
                        trans_cylinder.setTranslation(new Vector3f(xc
                                - half_distance, yc, zc));
                        trans_cylinder.setRotation(new AxisAngle4f(1, 1, 0,
                                9.42477f));
                    } else if (array_nodes[i][0] > i) {
                        trans_cylinder.setTranslation(new Vector3f(xc
                                + half_distance, yc, zc));
                        trans_cylinder.setRotation(new AxisAngle4f(1, 1, 0,
                                3.14159f));
                    }

                    TransformGroup cylinder = new TransformGroup(trans_cylinder);
                    cylinder.addChild(new Cylinder(cylinder_radius, distance,
                            appearance));
                    group.addChild(cylinder);
                }
            }
        }

        content.addChild(group);

        // Create the rotate behavior node
        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(group);
        content.addChild(behavior);
        behavior.setSchedulingBounds(bounds);

        // Create the zoom behavior node
        MouseZoom behavior2 = new MouseZoom();
        behavior2.setTransformGroup(group);
        content.addChild(behavior2);
        behavior2.setSchedulingBounds(bounds);

        // Create the translate behavior node
        MouseTranslate behavior3 = new MouseTranslate();
        behavior3.setTransformGroup(group);
        content.addChild(behavior3);
        behavior3.setSchedulingBounds(bounds);

        // Add lights and compile (for speed)
        add_lights(content);
        content.compile();

        return content;
    }

    /**
     * Add a directional light to the 3D scene
     *
     * @param branchgroup BranchGroup
     */
    private static void add_lights(BranchGroup branchgroup) {
        DirectionalLight light = new DirectionalLight(new Color3f(0.1f, 1.4f,
                0.1f), new Vector3f(4.0f, -7.0f, -12.0f));
        light.setEnable(true);
        light.setInfluencingBounds(new BoundingSphere(
                new Point3d(0.0, 0.0, 0.0), 100.0));
        branchgroup.addChild(light);
    }
}
