/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.startup;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.stage.DirectoryChooser;
import org.ihtsdo.tk.api.TerminologyStoreDI;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author kec
 */
public class Installer extends ModuleInstall {

    public static CountDownLatch dbReadyLatch = new CountDownLatch(1);
    private static JFXPanel fxPanel;

    @Override
    public void close() {
        System.out.println("Installer::close");
        super.close();
    }

    @Override
    public boolean closing() {
        System.out.println("Installer::closing");
        return super.closing();
    }

    @Override
    public void restored() {
        Task<Void> loadDbTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                ProgressHandle p = ProgressHandleFactory.createHandle("Loading database...");
                p.setDisplayName("BDB Load");
                p.start();
                p.switchToIndeterminate();
                Preferences node = NbPreferences.forModule(TerminologyStoreDI.class);
                String bdbLoc = node.get(TerminologyStoreDI.DatabaseOptionPreferences.DB_LOCATION.name(), "unset");
                File fallBack = new File("/Users/kec/NetBeansProjects/nb-sim-demo/application/berkeley-db");
                if (bdbLoc.equalsIgnoreCase("unset")) {
                    if (fallBack.exists()) {
                        node.put(TerminologyStoreDI.DatabaseOptionPreferences.DB_LOCATION.name(), fallBack.getAbsolutePath());
                    } else {
                        DirectoryChooser dc = new DirectoryChooser();
                        dc.setTitle("Select Berkeley DB directory:");
//                    File defaultDirectory = new File("c:/dev/javafx");
//                    dc.setInitialDirectory(defaultDirectory);
                        File selectedDirectory = dc.showDialog(null);
                        System.out.println("Selected: " + selectedDirectory.getAbsolutePath());
                        node.put(TerminologyStoreDI.DatabaseOptionPreferences.DB_LOCATION.name(), selectedDirectory.getAbsolutePath());
                    }

                }
                TerminologyStoreDI ts = Lookup.getDefault().lookup(TerminologyStoreDI.class);
                p.finish();
                System.out.println("BDB Load complete. ");
                StatusDisplayer.getDefault().setStatusText("BDB Load complete.");
                dbReadyLatch.countDown();
                return null;
            }
        };
        new Thread(loadDbTask).start();

        super.restored();
    }

    @Override
    public void uninstalled() {
        System.out.println("Installer::uninstalled");
        super.uninstalled();
    }

    @Override
    public void validate() throws IllegalStateException {
        super.validate();
        fxPanel = new JFXPanel();
    }
}
