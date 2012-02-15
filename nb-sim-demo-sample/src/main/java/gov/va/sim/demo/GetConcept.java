/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.sim.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.ihtsdo.tk.api.TerminologyStoreDI;
import org.ihtsdo.tk.api.concept.ConceptChronicleBI;
import org.ihtsdo.tk.binding.Taxonomies;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit",
id = "gov.va.sim.demo.GetConcept")
@ActionRegistration(displayName = "#CTL_GetConcept")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1300)
})
@Messages("CTL_GetConcept=Get Concept")

public final class GetConcept implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            TerminologyStoreDI ts = Lookup.getDefault().lookup(TerminologyStoreDI.class);
            if (ts != null) {
                ConceptChronicleBI cc = ts.getConcept(Taxonomies.WB_AUX.getUuids());
                System.out.println("Found: " + cc);
                
                cc = ts.getConceptFromAlternateId("58840004");
                System.out.println("Found: " + cc);
                
                cc = ts.getConceptFromAlternateId("272741003");
                System.out.println("Found: " + cc);
                
                cc = ts.getConceptFromAlternateId("7771000");
                System.out.println("Found: " + cc);
                
            } else {
                System.out.println("Lookup cannot find TerminologyStoreDI.class");
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
