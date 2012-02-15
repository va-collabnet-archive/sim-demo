/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.sim.demo;

import java.io.IOException;
import java.util.UUID;
import org.ihtsdo.tk.api.TerminologyStoreDI;
import org.openide.util.Lookup;
/**
 *
 * @author kec
 */
public class TestOpenDb {
    public static void testOpen() throws IOException {
        Lookup.getDefault().lookup(TerminologyStoreDI.class).getConcept(UUID.randomUUID());
    }
}
