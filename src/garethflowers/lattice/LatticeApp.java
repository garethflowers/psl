package garethflowers.lattice;

import javax.swing.UIManager;

/**
 * LatticeApp Starts a new instance of Lattice application
 *
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
	 *
	 * @param arguments String[]
	 */
	public static void main(String[] arguments) {
		// Create application frame
		new LatticeApp();
	}
}
