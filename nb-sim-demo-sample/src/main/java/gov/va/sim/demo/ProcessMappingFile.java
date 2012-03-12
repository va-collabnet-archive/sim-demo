/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.sim.demo;

import gov.va.demo.dom.PncsLegoMapGenerator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.w3c.dom.Document;

@ActionID(category = "Edit",
id = "gov.va.sim.demo.ProcessMappingFile")
@ActionRegistration(iconBase = "gov/va/sim/demo/map_add.png",
displayName = "#CTL_ProcessMappingFile")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1100),
    @ActionReference(path = "Toolbars/File", position = 200)
})
@Messages("CTL_ProcessMappingFile=Process mapping file...")
public final class ProcessMappingFile implements ActionListener {

    private static JFXPanel fxPanel;

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
                fc.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Filter for bdb properties", "*.xml"));
                final File f = fc.showOpenDialog(null);
                if (f != null) {
                    FileInputStream fis = null;
                    try {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        fis = new FileInputStream(f);
                        Document mapDoc = builder.parse(fis);
                        PncsLegoMapGenerator mapGenerator = new PncsLegoMapGenerator();
                        mapGenerator.addToMap(mapDoc);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        try {
                            fis.close();
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        });
    }
}
