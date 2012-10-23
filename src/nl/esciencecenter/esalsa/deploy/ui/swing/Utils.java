package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.Insets;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Utils {

    /** Returns an JLabel, or null if the path was invalid. */
    public static JLabel createImageLabel(String path, String description) {
        JLabel result = new JLabel(createImageIcon(path, description));
        result.setToolTipText(description);
        return result;
    }

    /**
     * Returns an JButton, or null if the path was invalid.
     * 
     * @param buttonText
     */
    public static JButton createImageButton(Action action, String path,
            String description, String buttonText) {
        JButton result = new JButton(action);
        result.setText(buttonText);
        result.setIcon(createImageIcon(path, description));
        if (buttonText == null) {
            result.setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            result.setHorizontalAlignment(SwingConstants.LEFT);
        }
        result.setMargin(new Insets(2, 2, 2, 2));
        result.setVerticalTextPosition(AbstractButton.CENTER);
        result.setHorizontalTextPosition(AbstractButton.TRAILING);
        result.setToolTipText(description);
        return result;
    }

    /**
     * Returns an JButton, or null if the path was invalid.
     * 
     * @param buttonText
     */
    public static JButton createImageButton(String path, String description,
            String buttonText) {
        JButton result = new JButton(buttonText, createImageIcon(path,
                description));
        result.setHorizontalAlignment(SwingConstants.CENTER);
        result.setMargin(new Insets(2, 2, 2, 2));
        result.setVerticalTextPosition(AbstractButton.CENTER);
        result.setHorizontalTextPosition(AbstractButton.TRAILING);
        result.setToolTipText(description);
        result.setFocusPainted(false);
        return result;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path, String description) {
        URL imgURL = null;

        if (path != null) {
            imgURL = ClassLoader.getSystemClassLoader().getResource(path);
        }

        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            return null;
        }
    }

    public static int defaultFieldHeight = 26; // this is the default height for
    // comboboxes
    public static int defaultLabelWidth = 150;
    public static int gapHeight = 3;
    public static int buttonWidth = 80;

    /**
     * truncates a double value to only 4 decimals
     * 
     * @param number
     *            - the value to be truncated
     * @return - the value truncated to 4 decimals
     */
    public static double truncate(double number) {
        if (number > 0) {
            return Math.floor(number * 10000) / 10000.0;
        } else {
            return Math.ceil(number * 10000) / 10000.0;
        }
    }

}
