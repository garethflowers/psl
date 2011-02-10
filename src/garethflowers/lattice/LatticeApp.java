package garethflowers.lattice;

// LatticeApp
// Import Required Libraries
import javax.swing.UIManager;

/**
 * <p>Title: Lattice</p>
 * <p>Description: Starts a new instance of Lattice application</p>
 * <p>Copyright: Gareth Flowers Copyright (c) 2005</p>
 * <p>Company: Leeds Metropolitan University</p>
 * @author Gareth Flowers
 * @version 1.0
 */
final class LatticeApp {

    /**
     * Starts a new instance of 'MainFrame'
     */
    private LatticeApp() {
        try {
            // UI Manager used to set look of program to the current OS
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Create new MainFrame
            new MainFrame();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Main
     * @param arguments String[]
     */
    public static void main(String[] arguments) {
        // Create application frame
        new LatticeApp();
    }
}
