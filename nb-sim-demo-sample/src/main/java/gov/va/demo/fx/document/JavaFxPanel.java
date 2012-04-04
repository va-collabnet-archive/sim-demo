/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.fx.document;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.lang.Class;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.openide.util.Exceptions;

/**
 *
 * @author kec
 */
public class JavaFxPanel extends JPanel {

    JComponent jFxPanel;

    public JComponent getJFXPanel() {
        
        if (jFxPanel == null) {
            try {
                Class c = Class.forName("javafx.embed.swing.JFXPanel");
                jFxPanel = (JComponent) c.newInstance();
                jFxPanel.setPreferredSize(new Dimension(1500, 1500));
                setLayout(new BorderLayout());
                add(jFxPanel, BorderLayout.CENTER);

            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return jFxPanel;
    }

    public JavaFxPanel() {
        super();
    }
}
