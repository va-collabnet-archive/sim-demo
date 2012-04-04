
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.sim.demo;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.demo.nb.sim.demo.sample.ExpressionTopComponent;

import javafx.application.Platform;

import javafx.embed.swing.JFXPanel;

import javafx.stage.FileChooser;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

//~--- JDK imports ------------------------------------------------------------

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.SwingUtilities;

@ActionID(
   category    = "Edit",
   id          = "gov.va.sim.demo.ProcessAssertionFile"
)
@ActionRegistration(
   iconBase    = "gov/va/sim/demo/brick_add.png",
   displayName = "#CTL_ProcessAssertionFile"
)
@ActionReferences({ @ActionReference(
   path        = "Menu/File",
   position    = 1200
) , @ActionReference(
   path        = "Toolbars/File",
   position    = 300
) })
@Messages("CTL_ProcessAssertionFile=Process Assertion File...")
public final class ProcessAssertionFile implements ActionListener {
   private static JFXPanel fxPanel;

   //~--- methods -------------------------------------------------------------

   @Override
   public void actionPerformed(ActionEvent e) {
      if (fxPanel == null) {
         fxPanel = new JFXPanel();
      }

      Platform.runLater(new Runnable() {
         @Override
         public void run() {
            FileChooser fc = new FileChooser();

            System.out.println("Filters: " + fc.getExtensionFilters());
            fc.setTitle("Select Assertion Definition File...");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Filter for bdb properties",
                    "*.xml"));

            final File f = fc.showOpenDialog(null);

            if (f != null) {
               SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     ExpressionTopComponent exp = new ExpressionTopComponent();

                     exp.setAssertionDefFile(f);
                     exp.setDisplayName(f.getName());
                     exp.open();
                  }
               });
            }
         }
      });
   }
}
