/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.terminology;

import java.io.IOException;
import org.ihtsdo.tk.api.*;
import org.ihtsdo.tk.api.coordinate.EditCoordinate;
import org.ihtsdo.tk.api.coordinate.ViewCoordinate;
import org.ihtsdo.tk.binding.Snomed;
import org.ihtsdo.tk.binding.TermAux;
import org.openide.util.Lookup;

/**
 *
 * @author kec
 */
public class TerminologyService {
    private static TerminologyStoreDI    ts;
    private static TerminologySnapshotDI    tSnap;
    private static TerminologyService service = null;
    private static TerminologyBuilderBI builder;

    public static TerminologyBuilderBI getBuilder() {
        return builder;
    }

    private TerminologyService() throws IOException {
      ts      = Lookup.getDefault().lookup(TerminologyStoreDI.class);
      ViewCoordinate        vc      = new ViewCoordinate(ts.getMetadataVC());
      vc.getIsaTypeNids().add(Snomed.IS_A.getLenient().getConceptNid());
      
      PathBI path = ts.getPath(Snomed.SNOMED_RELEASE_PATH.getLenient().getConceptNid());
      Position position = new Position(Long.MAX_VALUE, path);
      vc.setPositionSet(new PositionSet(position));
      tSnap   = ts.getSnapshot(vc);
      EditCoordinate ec = new EditCoordinate(TermAux.USER.getLenient().getConceptNid(), path.getConceptNid());
      builder = ts.getTerminologyBuilder(ec, vc);
    }
    
    public static TerminologyStoreDI getStore() throws IOException {
        if (service == null) {
            service = new TerminologyService();
        }
        return ts;
    }
  
    public static TerminologySnapshotDI getSnapshot() throws IOException {
        if (service == null) {
            service = new TerminologyService();
        }
        return tSnap;
    }
    
    public static void commit() throws IOException {
        getStore().commit();
    }

}
