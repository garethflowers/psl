package garethflowers.lattice;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Settings Settings dialog used when regenerating lattices
 * 
 * @author Gareth Flowers
 * @version 1.0
 */
class Settings extends Dialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private int lattice_size;
    private int lattice_numbers;
    private JTextField nl = new JTextField(10);
    private JTextField ls = new JTextField(10);
    private JCheckBox is_3d = new JCheckBox();
    private JButton apply = new JButton("Recreate New Lattices...");
    private JButton cancel = new JButton("Cancel");
    private boolean change = false;

    /**
     * Creates new instance of settings dialog
     * 
     * @param frame Frame
     * @param size int
     * @param num int
     */
    public Settings(Frame frame, int size, int num) {
        super(frame, true);
        lattice_size = size;
        lattice_numbers = num;

        Panel row1 = new Panel();
        row1.add(new Label("Lattice Size (width in nodes)"));
        ls.setText("" + lattice_size);
        row1.add(ls);
        add(row1);

        Panel row2 = new Panel();
        row2.add(new Label("Number of Lattices"));
        nl.setText("" + lattice_numbers);
        row2.add(nl);
        add(row2);

        Panel row3 = new Panel();
        row3.add(new Label("3 Dimensional"));
        is_3d.setSelected(false);
        row3.add(is_3d);
        add(row3);

        Panel row4 = new Panel();
        apply.addActionListener(this);
        row4.add(apply);
        cancel.addActionListener(this);
        row4.add(cancel);
        add(row4);

        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        setTitle("Generate New Lattice");
        setResizable(false);
        setSize(290, 180);
        setModal(true);
        setLocation((screen_size.width / 2) - (getWidth() / 2),
                (screen_size.height / 2) - (getHeight() / 2));
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setVisible(false);
    }

    /**
     * Called by buttons on the dialog
     * 
     * @param actionevent ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent actionevent) {
        if (actionevent.getSource() == apply) {
            try {
                if ((get_num() <= 0) || (get_num() > 9999) || (get_size() <= 0)
                        || (get_size() > 9999)) {
                    JOptionPane.showMessageDialog(this,
                            "You must enter a value between 1 and 9999.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    change = true;
                    setVisible(false);
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this,
                        "You must enter a value between 1 and 9999.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            setVisible(false);
        }
    }

    /**
     * Returns the lattice size
     * 
     * @return int
     */
    protected int get_size() {
        String ls_value = ls.getText();
        return Integer.parseInt(ls_value);
    }

    /**
     * Returns number of lattices
     * 
     * @return int
     */
    protected int get_num() {
        String nl_value = nl.getText();
        return Integer.parseInt(nl_value);
    }

    /**
     * Returns the dimension (3D)
     * 
     * @return boolean
     */
    protected boolean get_is_3d() {
        return is_3d.isSelected();
    }

    /**
     * Returns true if the apply button is pressed
     * 
     * @return boolean
     */
    protected boolean get_changed() {
        return change;
    }

    /**
     * Reset variables and displays dialog
     */
    protected void open() {
        change = false;
        setVisible(true);
    }
}
